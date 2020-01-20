package kittify.core;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Streams;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

import static kittify.core.CoreUtil.*;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class KittifyTransformer implements IClassTransformer {
    public static final String KITTIFY_HOOKS = "kittify.core.KittifyHooks";
    public static final String ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";
    public static final String FOOD_STATS = "net.minecraft.util.FoodStats";

    public final ImmutableMultimap<String, Consumer<ClassNode>> transformers = collectTransformers();

    private ImmutableMultimap<String, Consumer<ClassNode>> collectTransformers() {
        ImmutableMultimap.Builder<String, Consumer<ClassNode>> build = ImmutableMultimap.builder();
        Arrays.stream(KittifyTransformer.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Transformer.class))
                .forEach(m -> {
                    final Class<?>[] parameterTypes = m.getParameterTypes();
                    if (parameterTypes.length != 1 || !ClassNode.class.isAssignableFrom(parameterTypes[0]))
                        throw new RuntimeException("Method '" + m + "' should only have a single ClassNode parameter");
                    final String className = m.getAnnotation(Transformer.class).transformedName();
                    final Object instance = Modifier.isStatic(m.getModifiers()) ? null : this;
                    final Consumer<ClassNode> transformer = (classNode) -> {
                        try {
                            KittifyCore.log.info("Transforming " + className + " using " + m);
                            m.invoke(instance, classNode);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("Error transforming " + className + " using " + m, e);
                        }
                    };
                    build.put(className, transformer);
                });
        return build.build();
    }

    /**
     * Hook to prevent regen for some time after being hit
     */
    @Transformer(transformedName = FOOD_STATS)
    public static void hookFoodStats(ClassNode cn) {
        final MethodNode mn = getMethodNode(cn,
                OBFUSCATED ? "func_75118_a" : "onUpdate",
                createMethodDescriptor("V", ENTITY_PLAYER)
        );

        final AbstractInsnNode[] originalInstructions = mn.instructions.toArray();
        int i = 0;

        // Right at setting flag: boolean flag = player.world.getGameRules().getBoolean("naturalRegeneration");
        VarInsnNode insnISTORE_flag = null;
        for (; i < originalInstructions.length; i++) {
            AbstractInsnNode insn = originalInstructions[i];
            if (CoreUtil.isLabelOrLine(insn)) continue;
            if (insn.getOpcode() != ISTORE || !(insn instanceof VarInsnNode)) continue;
            if (!(insn.getPrevious() instanceof MethodInsnNode)) continue;
            AbstractInsnNode insn_m2 = insn.getPrevious().getPrevious();
            if (!(insn_m2 instanceof LdcInsnNode) || !((LdcInsnNode) insn_m2).cst.equals("naturalRegeneration"))
                continue;
            insnISTORE_flag = (VarInsnNode) insn;
            break;
        }
        if (insnISTORE_flag == null)
            throw new RuntimeException("ISTORE `flag` after LDC \"naturalRegeneration\"; INVOKEVIRTUAL ...; not found");

        // At: if (flag && ...)
        VarInsnNode insnILOAD_flag = null;
        for (; i < originalInstructions.length; i++) {
            AbstractInsnNode insn = originalInstructions[i];
            if (CoreUtil.isLabelOrLine(insn)) continue;
            if (insn.getOpcode() != ILOAD || !(insn instanceof VarInsnNode) || ((VarInsnNode) insn).var != insnISTORE_flag.var)
                continue;
            if (!(insn.getNext() instanceof JumpInsnNode)) continue;
            insnILOAD_flag = (VarInsnNode) insn;
            break;
        }
        if (insnILOAD_flag == null)
            throw new RuntimeException("ILOAD `flag` followed by IFEQ not found");

        // return at the end of the function, either a label just before the end, or it create and inserts a new one
        InsnNode insnRETURN = null;
        for (; i < originalInstructions.length; i++) {
            AbstractInsnNode insn = originalInstructions[i];
            if (CoreUtil.isLabelOrLine(insn)) continue;
            if (insn.getOpcode() != RETURN || !(insn instanceof InsnNode)) continue;
            insnRETURN = (InsnNode) insn;
            break;
        }
        if (insnRETURN == null)
            throw new RuntimeException("RETURN not found");

        // Represents
        //     -boolean flag = player.world.getGameRules().getBoolean("naturalRegeneration");
        //     +boolean flag = KittifyHooks.shouldNaturalRegen(player.world.getGameRules().getBoolean("naturalRegeneration"), player);
        //     +if(!KittifyHooks.doSpecialRegen(flag, player, this)) {
        //     <rest of the method / regeneration and starvation code>
        //     +}

        final InsnList hook_shouldNaturalRegen = new InsnList();
        // Stack start: flag
        hook_shouldNaturalRegen.add(new VarInsnNode(ALOAD, 1)); // -> player
        //hook_shouldNaturalRegen.add(new VarInsnNode(ALOAD, 0)); // -> this
        // flag, player, this -> flag
        hook_shouldNaturalRegen.add(new MethodInsnNode(INVOKESTATIC,
                typeToPath(KITTIFY_HOOKS), "shouldNaturalRegen",
                createMethodDescriptor("Z", "Z", ENTITY_PLAYER),
                false
        ));
        mn.instructions.insertBefore(insnISTORE_flag, hook_shouldNaturalRegen); // -> flag

        final InsnList hook_doSpecialRegen = new InsnList();
        hook_doSpecialRegen.add(new VarInsnNode(ILOAD, insnISTORE_flag.var)); // -> flag
        hook_doSpecialRegen.add(new VarInsnNode(ALOAD, 1)); // -> player
        //hook_doSpecialRegen.add(new VarInsnNode(ALOAD, 0)); // -> this
        hook_doSpecialRegen.add(new MethodInsnNode(INVOKESTATIC,
                typeToPath(KITTIFY_HOOKS), "doSpecialRegen",
                createMethodDescriptor("Z", "Z", ENTITY_PLAYER),
                false
        ));
        final LabelNode funcReturn = new LabelNode();
        hook_doSpecialRegen.add(new JumpInsnNode(Opcodes.IFNE, funcReturn));
        // IFNE Return (Stack EMPTY)

        mn.instructions.insertBefore(insnILOAD_flag, hook_doSpecialRegen);

        // Return at end of function
        mn.instructions.insertBefore(insnRETURN, funcReturn);
        //mn.instructions.insertBefore(insnRETURN, new FrameNode(F_SAME, 0, null, 0, null));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformers.containsKey(transformedName)) return basicClass;

        final ClassNode node = new ClassNode();
        final ClassReader reader = new ClassReader(basicClass);
        reader.accept(node, 0);

        transformers.get(transformedName).forEach(transformer -> transformer.accept(node));

        final ClassWriter writer = new ClassWriter(COMPUTE_MAXS);
        node.accept(writer);
        final byte[] outputClass = writer.toByteArray();

        // Debug Output
        if (!OBFUSCATED) {
            final File root = new File("./KittifyTransformer/");
            final File classFile = new File(root, typeToPath(transformedName) + ".class");
            final File parentDir = classFile.getParentFile();
            if (parentDir.exists() || parentDir.mkdirs()) {
                try (final FileOutputStream fileOutputStream = new FileOutputStream(classFile)) {
                    fileOutputStream.write(outputClass);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save class: " + classFile, e);
                }
                // Check the ASMifier output vs the output when stack frames differ
                final ASMifier asmifierUnchanged = new ASMifier();
                new ClassReader(outputClass)
                        .accept(new TraceClassVisitor(null, asmifierUnchanged, null), 0);

                final File asmFile = new File(root, typeToPath(transformedName) + ".asm.java");
                try (final FileOutputStream fileOutputStream = new FileOutputStream(asmFile)) {
                    try (final PrintWriter pr = new PrintWriter(fileOutputStream)) {
                        asmifierUnchanged.print(pr);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save asm: " + asmFile, e);
                }

                final ClassWriter writerComputed = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
                node.accept(writerComputed);
                final ASMifier asmifierComputed = new ASMifier();
                new ClassReader(writerComputed.toByteArray())
                        .accept(new TraceClassVisitor(null, asmifierComputed, null), 0);

                // Only output the computed if it differs, otherwise delete if it exists
                boolean isSame = asmifierUnchanged.text.size() == asmifierComputed.text.size() && Streams
                        .zip(asmifierUnchanged.text.stream(), asmifierComputed.text.stream(),
                                (a, b) -> a.toString().equals(b.toString()))
                        .reduce(true, (a, b) -> a && b);

                final File asmComputedFile = new File(root, typeToPath(transformedName) + ".comp.asm.java");
                if (!isSame) {
                    try (final FileOutputStream fileOutputStream = new FileOutputStream(asmComputedFile)) {
                        try (final PrintWriter pr = new PrintWriter(fileOutputStream)) {
                            asmifierComputed.print(pr);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save computed asm: " + asmComputedFile, e);
                    }
                } else {
                    //noinspection ResultOfMethodCallIgnored
                    asmComputedFile.delete();
                }
            } else {
                throw new RuntimeException("Could not create all folder(s): " + parentDir);
            }
        }
        return outputClass;
    }

    /**
     * Hook to determine if a player can eat or not.
     *
     * @see EntityPlayer#canEat(boolean)
     * @see KittifyHooks#canEat(EntityPlayer, boolean)
     */
    @Transformer(transformedName = ENTITY_PLAYER)
    public void hookPlayerCanEat(ClassNode cn) {
        final MethodNode mn = getMethodNode(cn, OBFUSCATED ? "func_71043_e" : "canEat", "(Z)Z");

        final AbstractInsnNode instructionFirst = Arrays.stream(mn.instructions.toArray())
                .filter(CoreUtil::isNotLabelOrLine)
                .findFirst().orElseThrow(() -> new RuntimeException("First instruction not found"));

        // ignoreHunger = KittifyHooks.canEat(this, ignoreHunger);
        InsnList hook = new InsnList();
        hook.add(new VarInsnNode(ALOAD, 0)); // -> this
        hook.add(new VarInsnNode(ILOAD, 1)); // -> ignoreHunger
        // this, ignoreHunger -> ignoreHunger
        hook.add(new MethodInsnNode(INVOKESTATIC,
                typeToPath(KITTIFY_HOOKS), "canEat",
                createMethodDescriptor("Z", ENTITY_PLAYER, "Z"),
                false
        ));
        hook.add(new VarInsnNode(Opcodes.ISTORE, 1)); // ignoreHunger ->

        mn.instructions.insert(instructionFirst.getPrevious(), hook);
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Transformer {
        String transformedName();
    }
}
