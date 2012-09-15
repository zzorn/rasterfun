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

    public static String getPrimitiveTypeNameOrNull(Class<?> type) {
        checkNotNull(type, "type");

        if (Boolean.class.equals(type)) return "boolean";
        else if (Byte.class.equals(type)) return "byte";
        else if (Short.class.equals(type)) return "short";
        else if (Integer.class.equals(type)) return "int";
        else if (Long.class.equals(type)) return "long";
        else if (Float.class.equals(type)) return "float";
        else if (Double.class.equals(type)) return "double";
        else if (Character.class.equals(type)) return "char";

        else if (Boolean.TYPE.equals(type)) return "boolean";
        else if (Byte.TYPE.equals(type)) return "byte";
        else if (Short.TYPE.equals(type)) return "short";
        else if (Integer.TYPE.equals(type)) return "int";
        else if (Long.TYPE.equals(type)) return "long";
        else if (Float.TYPE.equals(type)) return "float";
        else if (Double.TYPE.equals(type)) return "double";
        else if (Character.TYPE.equals(type)) return "char";
        else return null;
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
