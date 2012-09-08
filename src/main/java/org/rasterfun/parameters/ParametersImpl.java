package org.rasterfun.parameters;

/**
 *
 */
public class ParametersImpl implements Parameters {

    @Override
    public int getInt(String name, int defaultValue) {
        // TODO
        return defaultValue;
    }

    @Override
    public String getString(String name, String defaultValue) {
        // TODO
        return defaultValue;
    }

    @Override
    public String[] getStringArray(String name, String[] defaultValue) {
        // TODO: Implement
        return defaultValue;
    }

    @Override
    public Parameters copy() {
        // TODO: Implement
        return null;
    }

    @Override
    public Parameters snapshot() {
        // TODO: Implement
        // TODO: If this is a snapshot, return itself, and keep a reference count
        return this;
    }

    @Override
    public void release() {
        // If this is a snapshot, and we are the last reference, release any temporary memory structures kept
        // TODO: Implement
    }

    @Override
    public void addParameter(Parameter parameter) {
        // TODO: Implement

    }
}
