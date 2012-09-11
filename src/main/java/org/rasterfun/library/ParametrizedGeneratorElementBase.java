package org.rasterfun.library;

import org.rasterfun.parameters.Parameters;
import org.rasterfun.parameters.ParametersImpl;

/**
 * Base class for generator elements that have parameters.
 * Provides some common functionality.
 */
public abstract class ParametrizedGeneratorElementBase implements GeneratorElement {

    private final Parameters parameters = new ParametersImpl();

    public final Parameters getParameters() {
        return parameters;
    }

    @Override
    public final ParametrizedGeneratorElementBase copy() {
        // Clone instance
        final ParametrizedGeneratorElementBase theCopy = createCopyOfClass();

        // Copy parameters
        theCopy.getParameters().addParameterCopies(parameters);

        // Do instance specific state copying
        initializeCopy(theCopy);

        return theCopy;
    }

    /**
     * Called when a copy has been done of this generator.
     * Provides opportunity for generator implementations to copy over any implementation specific state.
     */
    protected void initializeCopy(ParametrizedGeneratorElementBase theCopy) {}

    protected ParametrizedGeneratorElementBase createCopyOfClass() {
        try {
            return getClass().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Problem when copying a class instance of type "+getClass().getSimpleName()+": " + e, e);
        }
    }

}
