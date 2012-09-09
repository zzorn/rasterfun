package org.rasterfun.core.compiler;

/**
 * Exception thrown by CalculatorBuilder in case of errors.
 */
public class CompilationException extends Exception {

    public CompilationException(String message) {
        super(message);
    }

    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
