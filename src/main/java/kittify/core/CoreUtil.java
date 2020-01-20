package kittify.core;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class CoreUtil {
    public static boolean OBFUSCATED = !FMLLaunchHandler.isDeobfuscatedEnvironment();
    private static final ImmutableSet<String> PRIMATIVE_DESCRIPTORS = new ImmutableSet.Builder<String>()
            .add("V", "Z", "C", "B", "S", "I", "F", "J", "D")
            .build();

    public static MethodNode getMethodNode(ClassNode cn, String name, String desc) {
        return cn.methods.stream()
                .filter((m) -> m.name.equals(name) && m.desc.equals(desc))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method with name " + name + " desc " + desc + " not found"));
    }

    public static boolean isLabelOrLine(AbstractInsnNode n) {
        return n.getType() == AbstractInsnNode.LABEL || n.getType() == AbstractInsnNode.LINE;
    }

    public static boolean isNotLabelOrLine(AbstractInsnNode n) {
        return !isLabelOrLine(n);
    }

    public static String typeToPath(String typeName) {
        return typeName.replace('.', '/');
    }

    public static String pathToType(String pathName) {
        return pathName.replace('/', '.');
    }

    private static String typeToDescriptor(String typeName) {
        if (PRIMATIVE_DESCRIPTORS.contains(typeName)) return typeName;
        return "L" + typeToPath(typeName) + ";";
    }

    public static String createMethodDescriptor(String returnType, String... paramTypes) {
        return "(" +
                Arrays.stream(paramTypes).map(CoreUtil::typeToDescriptor).collect(Collectors.joining()) +
                ")" + typeToDescriptor(returnType);
    }

    public static String deobfPath(String pathName) {
        return FMLDeobfuscatingRemapper.INSTANCE.map(pathName);
    }

    public static String deobfType(String typeName) {
        return pathToType(deobfPath(typeToPath(typeName)));
    }

    public static String obfPath(String pathName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(pathName);
    }

    public static String obfType(String typeName) {
        return pathToType(obfPath(typeToPath(typeName)));
    }

    /**
     * Wraps {@link ClassWriter} so that it uses the correct ClassLoader (this class' one).
     * Yes, this does at first glance seem like it does nothing.
     * This makes it so the call to {@code Object.getClass().getClassloader()} inside the method
     * {@link ClassWriter#getCommonSuperClass(String, String)} returns the correct value.
     */
    public static class WrappedClassWriter extends ClassWriter {
        public WrappedClassWriter(int flags) {
            super(flags);
        }

        public WrappedClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            // Just here for reference, don't actually need to override :P
            return super.getCommonSuperClass(type1, type2);
        }
    }
}
