package org.rasterfun.library;

import org.rasterfun.parameters.Copyable;

/**
 * Something that can be stored in a library for future use, and copied to a picture generator.
 */
public interface GeneratorElement extends Copyable<GeneratorElement> {

    String getName();

}
