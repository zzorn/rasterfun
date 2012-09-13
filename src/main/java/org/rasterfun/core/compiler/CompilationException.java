package org.rasterfun.core.compiler;

/**
 * Exception thrown by RendererBuilder in case of errors.
 */
public class CompilationException extends Exception {

    private final String generatorName;
    private final String source;
    private final String longExplanation;

    public CompilationException(Throwable cause, String generatorName,
                                String source,
                                String message,
                                String longExplanation) {
        super(message, cause);
        this.generatorName = generatorName;
        this.source = source;
        this.longExplanation = longExplanation;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public String getSource() {
        return source;
    }

    public String getLongExplanation() {
        return longExplanation;
    }
}
