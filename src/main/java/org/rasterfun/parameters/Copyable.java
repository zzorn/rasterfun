package org.rasterfun.parameters;

/**
 * Interface for objects that can copy themselves.
 */
public interface Copyable<T> {

    /**
     * @return a copy of this object.
     */
    T copy();

}
