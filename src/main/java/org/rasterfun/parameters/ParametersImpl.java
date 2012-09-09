package org.rasterfun.parameters;

import org.rasterfun.utils.ParameterChecker;

import java.util.*;

/**
 *
 */
public class ParametersImpl implements Parameters {

    private Map<String, Object> values = new LinkedHashMap<String, Object>();
    private List<ParametersListener> listeners = null;

    @Override
    public <T> void set(String name, T value) {
        final Object oldValue = values.get(name);

        // Only do change and notify listeners if value changed.
        if (value != oldValue && (value == null || !value.equals(oldValue))) {
            values.put(name, value);

            // Notify listeners if we have any
            if (listeners != null) {
                for (ParametersListener listener : listeners) {
                    listener.onParameterChanged(this, name, oldValue, value);
                }
            }
        }
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

    @Override
    public void addListener(ParametersListener listener) {
        ParameterChecker.checkNotNull(listener, "listener");
        if (listeners == null) {
            listeners = new ArrayList<ParametersListener>();
        }

        listeners.add(listener);
    }

    @Override
    public void removeListener(ParametersListener listener) {
        ParameterChecker.checkNotNull(listener, "listener");
        if (listeners != null) listeners.remove(listener);
    }
}
