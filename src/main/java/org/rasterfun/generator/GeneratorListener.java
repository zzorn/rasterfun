package org.rasterfun.generator;

/**
 * A listener that is notified about changes to the picture generator.
 */
public interface GeneratorListener {

    /**
     * Called when a property or something else changed in the generator.
     */
    void onGeneratorChanged(PictureGenerator generator);

}
