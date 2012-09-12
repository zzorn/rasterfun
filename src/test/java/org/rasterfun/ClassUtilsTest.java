package org.rasterfun;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.rasterfun.utils.ClassUtils.isWrappedPrimitiveType;
import static org.rasterfun.utils.ClassUtils.wrappedPrimitiveTypeAsConstantString;

/**
 *
 */
public class ClassUtilsTest {

    @Test
    public void testIsWrappedPrimitive() throws Exception {

        assertTrue(isWrappedPrimitiveType(Boolean.class));
        assertTrue(isWrappedPrimitiveType(Byte.class));
        assertTrue(isWrappedPrimitiveType(Short.class));
        assertTrue(isWrappedPrimitiveType(Integer.class));
        assertTrue(isWrappedPrimitiveType(Long.class));
        assertTrue(isWrappedPrimitiveType(Float.class));
        assertTrue(isWrappedPrimitiveType(Double.class));
        assertTrue(isWrappedPrimitiveType(Character.class));

        assertFalse(isWrappedPrimitiveType(String.class));
        assertFalse(isWrappedPrimitiveType(Object.class));
        assertFalse(isWrappedPrimitiveType(List.class));
    }

    @Test
    public void testToConstantString() throws Exception {
        assertEquals("true", wrappedPrimitiveTypeAsConstantString(Boolean.TRUE));
        assertEquals("3", wrappedPrimitiveTypeAsConstantString(3));
        assertEquals("5.0f", wrappedPrimitiveTypeAsConstantString((float) 5));
        assertEquals("-1.0d", wrappedPrimitiveTypeAsConstantString((double) -1));
        assertEquals("'f'", wrappedPrimitiveTypeAsConstantString('f'));
        assertEquals("'_'", wrappedPrimitiveTypeAsConstantString('_'));
    }
}
