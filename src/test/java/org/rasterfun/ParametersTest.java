package org.rasterfun;

import org.junit.Before;
import org.junit.Test;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.parameters.ParametersImpl;
import org.rasterfun.parameters.ParametersListener;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Test parameter class.
 */
public class ParametersTest {

    private Parameters parameters;
    private TestListener listener;

    @Before
    public void setUp() {
        parameters = new ParametersImpl();

        listener = new TestListener();
        parameters.addListener(listener);
    }

    @Test
    public void testNewParameters() throws Exception {
        ParametersImpl parameters1 = new ParametersImpl();
        assertEquals("Should be empty when created", 0, parameters1.getNames().size());
    }

    @Test
    public void testGetAndSet() {
        parameters.set("foo", 4);
        parameters.set("fooF", 4f);
        parameters.set("fooD", 4d);
        parameters.set("bar", true);
        parameters.set("color", Color.WHITE);
        parameters.set("element", new TestElement("testElem", 42));

        assertParameterIs("foo", 4);
        assertParameterIs("fooF", 4f);
        assertParameterIs("fooD", 4d);
        assertParameterIs("bar", true);
        assertParameterIs("color", new Color(1f, 1f, 1f));
        assertParameterIs("element", new TestElement("testElem", 42));
    }

    @Test
    public void testNonExistingKeys() {
        assertParameterIs("nonExistingKey", null);
        assertParameterIs("nonExistingKey", 7, 7);

        assertEquals("Getting no key with notNull should work",
                     Integer.valueOf(2), parameters.getNotNull("nonExistingKey", 2));

        assertEquals("Getting no key with notNull and null default value should work",
                     null, parameters.getNotNull("nonExistingKey", null));
    }

    @Test
    public void testNullValuesAndGetNotNull() {
        parameters.set("bar", null);
        assertParameterIs("bar", null);
        assertEquals("Getting with notNull should work", Integer.valueOf(2), parameters.getNotNull("bar", 2));
    }

    @Test
    public void testAddParameterReferences() throws Exception {
        parameters.set("foo", 1);
        parameters.set("bar", true);

        final TestElement orc = new TestElement("zug", 7);
        Parameters parameters2 = new ParametersImpl();
        parameters2.set("foo", 2);
        parameters2.set("baz", 42.0);
        parameters2.set("orc", orc);

        parameters.addParameterReferences(parameters2);

        assertParameterIs("foo", 2);
        assertParameterIs("bar", true);
        assertParameterIs("baz", 42.0);
        assertParameterIs("orc", new TestElement("zug", 7));

        orc.setValue(8);
        parameters2.set("baz", 24);
        assertParameterIs(parameters2, "orc", new TestElement("zug", 8));
        assertParameterIs(parameters2, "baz", 24);

        // Should have changed the values that were referenced
        assertParameterIs("orc", new TestElement("zug", 8));
        assertParameterIs("baz", 42.0);
    }

    @Test
    public void testAddParameterCopies() throws Exception {
        parameters.set("foo", 1);
        parameters.set("bar", true);

        final TestElement orc = new TestElement("zug", 7);
        Parameters parameters2 = new ParametersImpl();
        parameters2.set("foo", 2);
        parameters2.set("baz", 42.0);
        parameters2.set("orc", orc);

        parameters.addParameterCopies(parameters2);

        assertParameterIs("foo", 2);
        assertParameterIs("bar", true);
        assertParameterIs("baz", 42.0);
        assertParameterIs("orc", new TestElement("zug", 7));

        orc.setValue(8);
        parameters2.set("baz", 24);
        assertParameterIs(parameters2, "orc", new TestElement("zug", 8));
        assertParameterIs(parameters2, "baz", 24);

        // Should not have changed any values in the copy
        assertParameterIs("orc", new TestElement("zug", 7));
        assertParameterIs("baz", 42.0);
    }

    @Test
    public void testCopy() {
        final TestElement testElement = new TestElement("testElement", 42);

        parameters.set("foo", 1);
        parameters.set("bar", testElement);

        final Parameters snapshot = parameters.copy();

        parameters.set("foo", 2);
        testElement.setValue(24);

        assertParameterIs(parameters, "foo", 2);
        assertParameterIs(parameters, "bar", new TestElement("testElement", 24));
        assertParameterIs(snapshot,   "foo", 1);
        assertParameterIs(snapshot,   "bar", new TestElement("testElement", 42));
    }

    @Test
    public void testGetKeys() throws Exception {
        parameters.set("bar", true);
        parameters.set("foo", 4);
        parameters.set("bar", false);

        final Collection<String> names = parameters.getNames();
        assertEquals("Should have expected number of names", 2, names.size());

        final String msg = "Should contain the names of the properties, in the order they were added";
        final Iterator<String> iterator = names.iterator();
        assertEquals(msg, "bar", iterator.next());
        assertEquals(msg, "foo", iterator.next());
    }

    @Test
    public void testGetMap() throws Exception {
        parameters.set("bar", true);
        parameters.set("foo", 4);
        parameters.set("bar", false);
        final Map<String,Object> values = parameters.getValues();

        // Check that contents are as expected
        assertEquals("Should have expected number of names", 2, values.size());

        final String msg = "Should contain the names and values of the properties, in the order they were added";
        final Iterator<Map.Entry<String, Object>> iterator = values.entrySet().iterator();

        Map.Entry<String, Object> entry = iterator.next();
        assertEquals(msg, "bar", entry.getKey());
        assertEquals(msg, false, entry.getValue());

        entry = iterator.next();
        assertEquals(msg, "foo", entry.getKey());
        assertEquals(msg, 4, entry.getValue());

        // Check that we can't change values
        try{
            values.put("foo", 1234);
            fail("Changing values in the returned map should not work");
        } catch (Exception e) {
            // Expected
        }
        assertEquals("value should remain unchanged", 4, values.get("foo"));
    }

    @Test
    public void testListenToChanges() throws Exception {
        listener.assertCalledCount(0);

        parameters.set("bar", "zugzug");
        listener.assertCalledCount(1);
        listener.assertLastParameterChangeIs("bar", null, "zugzug");

        parameters.set("bar", "zug!");
        listener.assertCalledCount(2);
        listener.assertLastParameterChangeIs("bar", "zugzug", "zug!");
    }

    @Test
    public void testListenerShouldNotBeCalledIfValueIsSame() throws Exception {
        parameters.set("foo", 1);
        listener.assertCalledCount(1);

        parameters.set("foo", 1);
        listener.assertCalledCount(1);

        parameters.set("bar", new TestElement("a", 42));
        listener.assertCalledCount(2);

        parameters.set("bar", new TestElement("a", 42));
        listener.assertCalledCount(2);
    }

    @Test
    public void testRemoveListener() throws Exception {
        parameters.set("foo", 1);
        listener.assertCalledCount(1);

        parameters.removeListener(listener);

        parameters.set("bar", 2);
        listener.assertCalledCount(1);
    }

    private <T> void assertParameterIs(String name, T expected) {
        assertParameterIs(parameters,  name, expected);
    }

    private <T> void assertParameterIs(String name, T expected, T defaultValue) {
        assertParameterIs(parameters, name, expected, defaultValue);
    }

    private <T> void assertParameterIs(Parameters parameters, String name, T expected) {
        assertParameterIs(parameters, name, expected, null);

        // Also check plain get
        T actual = parameters.get(name);
        assertEquals("Parameter '" + name + "' should have the expected value when retrieved without default value.", expected, actual);
    }

    private <T> void assertParameterIs(Parameters parameters, String name, T expected, T defaultValue) {
        // Check get with default value
        T actual = parameters.get(name, defaultValue);
        assertEquals("Parameter '" + name + "' should have the expected value.", expected, actual);
    }


    private static class TestElement implements GeneratorElement {
        private final String name;
        private float value;

        private TestElement(String name, float value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setValue(float value) {
            this.value = value;
        }

        @Override
        public TestElement copy() {
            return new TestElement(name, value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestElement that = (TestElement) o;

            if (Float.compare(that.value, value) != 0) return false;
            return !(name != null ? !name.equals(that.name) : that.name != null);

        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
            return result;
        }
    }

    private static class TestListener implements ParametersListener {
        private Parameters parameters;
        private String parameter;
        private Object oldValue;
        private Object newValue;
        private int count = 0;

        @Override
        public void onParameterChanged(Parameters parameters, String name, Object oldValue, Object newValue) {
            this.parameters = parameters;
            this.parameter = name;
            this.oldValue = oldValue;
            this.newValue = newValue;
            count++;
        }

        public void assertCalledCount(int num) {
            assertEquals("The listener should have been called the correct number of times", num, count);
        }

        public void assertLastParameterChangeIs(String expectedParameter,
                                                Object expectedOldValue,
                                                Object expectedNewValue) {
            assertLastParameterChangeIs(parameters, expectedParameter, expectedOldValue, expectedNewValue);
        }
        public void assertLastParameterChangeIs(Parameters expectedParameters,
                                                String expectedParameter,
                                                Object expectedOldValue,
                                                Object expectedNewValue) {
            assertEquals("The last parameter listener call should have the correct parameters instance", expectedParameters, parameters);
            assertEquals("The last parameter listener call should have the correct parameter name", expectedParameter, parameter);
            assertEquals("The last parameter listener call should have the correct old value", expectedOldValue, oldValue);
            assertEquals("The last parameter listener call should have the correct new value", expectedNewValue, newValue);
        }
    }
}
