package org.rasterfun.utils;

import static org.rasterfun.utils.ParameterChecker.checkNotNull;

/**
 *
 */
public class ClassUtils {

    public static boolean isWrappedPrimitiveType(Class<?> type) {
        return Boolean.class.equals(type) ||
               Byte.class.equals(type) ||
               Short.class.equals(type) ||
               Integer.class.equals(type) ||
               Long.class.equals(type) ||
               Float.class.equals(type) ||
               Double.class.equals(type) ||
               Character.class.equals(type);
    }

    public static String wrappedPrimitiveTypeAsConstantString(Object value) {
        checkNotNull(value, "value");
        if (!isWrappedPrimitiveType(value.getClass())) throw new IllegalArgumentException("Not a wrapped primitive type: " + value.getClass());

        if (Boolean.class.isInstance(value)) return value.toString();
        else if (Byte.class.isInstance(value)) return value.toString();
        else if (Short.class.isInstance(value)) return value.toString();
        else if (Integer.class.isInstance(value)) return value.toString();
        else if (Long.class.isInstance(value)) return value.toString();
        else if (Float.class.isInstance(value)) return value.toString() + "f";
        else if (Double.class.isInstance(value)) return value.toString() + "d";
        else if (Character.class.isInstance(value)) return "'" + value + "'";
        else throw new IllegalStateException("Unhandled type: " + value.getClass());
    }

    public static String getTypeDeclaration(Class<?> type) {
        StringBuilder s = new StringBuilder();

        if (!type.isArray()) {
            // Normal class
            s.append(type.getName());
        }
        else {
            Class<?> componentType = type.getComponentType();
            while  (componentType.isArray()) {
                componentType = componentType.getComponentType();
            }

            s.append(componentType.getName());

            // Handle multidimensional arrays
            int dimensions = StringUtils.countCharacters(type.getName(), '[');
            for (int i = 0; i < dimensions; i++) {
                s.append("[]");
            }
        }

        return s.toString();
    }
}
