package kittify.core;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

import static kittify.core.CoreUtil.*;
import static org.objectweb.asm.Opcodes.*;

public class KittifyTransformer implements IClassTransformer {
    public static final String KITTIFY_HOOKS = "kittify.core.KittifyHooks";
    public static final String ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";

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

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformers.containsKey(transformedName)) return basicClass;

        final ClassNode node = new ClassNode();
        final ClassReader reader = new ClassReader(basicClass);
        reader.accept(node, 0);

        transformers.get(transformedName).forEach(transformer -> transformer.accept(node));

        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        final byte[] outputClass = writer.toByteArray();
        if (!OBFUSCATED) {
            final File folder = new File("./KittifyTransformer/");
            if (folder.exists() || folder.mkdirs()) {
                final File file = new File(folder, transformedName + ".class");
                try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(outputClass);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save: " + file, e);
                }
            } else {
                throw new RuntimeException("Could not create all folder(s): " + folder);
            }
        }
        return outputClass;
    }

    /**
     * Hook to determine if a player can eat or not.
     *
     * @see EntityPlayer#canEat(boolean)
     * @see KittifyHooks#canEat(EntityPlayer, boolean);
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
