package org.rasterfun.parameters;

import java.util.*;

/**
 *
 */
public class ParametersImpl implements Parameters {

    private Map<String, Object> values = new LinkedHashMap<String, Object>();

    @Override
    public <T> void set(String name, T value) {
        values.put(name, value);
    }

    @Override
    public <T> T get(String name) {
        return (T) values.get(name);
    }

    @Override
    public <T> T get(String name, T defaultValue) {
        if (!values.containsKey(name)) return defaultValue;
        else return (T) values.get(name);
    }

    @Override
    public <T> T getNotNull(String name, T defaultValue) {
        final T value = get(name, defaultValue);
        if (value == null) return defaultValue;
        else return value;
    }

    @Override
    public Collection<String> getNames() {
        return Collections.unmodifiableCollection(values.keySet());
    }

    @Override
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    public void addParameterCopies(Parameters source) {
        for (Map.Entry<String, Object> entry : source.getValues().entrySet()) {
            Object value = entry.getValue();

            // Copy the value if it supports it
            if (Copyable.class.isInstance(value)) {
                value = ((Copyable)value).copy();
            }

            set(entry.getKey(), value);
        }
    }

    @Override
    public void addParameterReferences(Parameters source) {
        for (Map.Entry<String, Object> entry : source.getValues().entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Parameters copy() {
        final ParametersImpl parametersCopy = new ParametersImpl();
        parametersCopy.addParameterCopies(this);
        return parametersCopy;
    }


}
