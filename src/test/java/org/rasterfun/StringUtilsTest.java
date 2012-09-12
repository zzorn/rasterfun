package org.rasterfun;

import org.junit.Test;
import org.rasterfun.utils.ClassUtils;
import org.rasterfun.utils.StringUtils;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class StringUtilsTest {

    @Test
    public void testIdentifierFromName() throws Exception {
        assertEquals("a", StringUtils.identifierFromName("a"));
        assertEquals("A", StringUtils.identifierFromName(" a"));
        assertEquals("_12", StringUtils.identifierFromName("12"));
        assertEquals("_1a", StringUtils.identifierFromName("1a"));
        assertEquals("a1", StringUtils.identifierFromName("a1"));
        assertEquals("__", StringUtils.identifierFromName(" ?! "));
        assertEquals("HelloWorld_", StringUtils.identifierFromName("Hello world!"));
        assertEquals("HelloWorldQHöwMayIHälpYouQQ", StringUtils.identifierFromName("Hello world! Höw may I hälp you^?",
                                                                                   'Q'));
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(4, StringUtils.countCharacters("[asdfg[sd[sdasd[", '['));
        assertEquals(0, StringUtils.countCharacters(" asdf sdfjh ] sd ", '['));
    }

    @Test
    public void testCreateTypeDeclaration() throws Exception {
        assertEquals("org.rasterfun.StringUtilsTest", ClassUtils.getTypeDeclaration(StringUtilsTest.class));
        assertEquals("java.lang.Float", ClassUtils.getTypeDeclaration(Float.class));
        assertEquals("float", ClassUtils.getTypeDeclaration(Float.TYPE));
        assertEquals("float[]", ClassUtils.getTypeDeclaration((new float[]{}).getClass()));
        assertEquals("float[][]", ClassUtils.getTypeDeclaration((new float[][]{}).getClass()));
        assertEquals("float[][][]", ClassUtils.getTypeDeclaration((new float[][][]{}).getClass()));
    }
}
