package org.rasterfun.library;

import org.rasterfun.parameters.Copyable;

/**
 * Something that can be stored in a library for future use, and copied to a picture generator.
 */
public interface GeneratorElement extends Copyable<GeneratorElement> {

    // TODO: Add listener support, so that if a gradient is changed, the generators it is used in will be updated as well.

}
