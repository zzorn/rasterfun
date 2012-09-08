package org.rasterfun.library;

/**
 * Something that can be stored in a library for future use, and copied to a picture generator.
 */
public interface GeneratorElement {

    String getName();

    /**
     * @return a new unique copy of this element.
     */
    GeneratorElement copy();

}
