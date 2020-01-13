package kittify.core;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

public class KittifyTransformer implements IClassTransformer {

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

        return writer.toByteArray();
    }

    /**
     * Makes it so that players can always eat.
     *
     * @see net.minecraft.entity.player.EntityPlayer#canEat(boolean)
     */
    @Transformer(transformedName = "net.minecraft.entity.player.EntityPlayer")
    public void playerAlwaysCanEat(ClassNode cn) {
        final MethodNode mn = cn.methods.stream()
                .filter((m) -> (m.name.equals("func_71043_e") || m.name.equals("canEat")) && m.desc.equals("(Z)Z"))
                .findFirst().orElseThrow(() -> new RuntimeException("Method not found"));
        // TODO: Find a better way to do this... Maybe delegate to a method?
        final AbstractInsnNode instructionLoadParam1 = Arrays.stream(mn.instructions.toArray())
                .filter((i) -> i.getOpcode() == Opcodes.ILOAD)
                .findFirst().orElseThrow(() -> new RuntimeException("Instruction ILOAD not found"));
        final AbstractInsnNode insertionTarget = instructionLoadParam1.getPrevious();
        mn.instructions.remove(instructionLoadParam1);
        mn.instructions.insert(insertionTarget, new InsnNode(Opcodes.ICONST_1));
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Transformer {
        String transformedName();
    }
}
