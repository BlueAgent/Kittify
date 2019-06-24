package kittify.common.util;

public class ReflectionUtil {
    public static Class getClassOrNull(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Ignored
        }
        return null;
    }
}
