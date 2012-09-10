package org.rasterfun.core.tasks;

import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.compiler.CompilationException;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.utils.ParameterChecker;

import java.util.concurrent.Callable;

/**
 * Task that compiles a pixel calculator from a picture generator.
 */
public class CompileTask implements Callable<PixelCalculator> {

    private final int calculationIndex;
    private final CalculatorBuilder builder;
    private final CalculationListener listener;

    public CompileTask(int calculationIndex, CalculatorBuilder builder, CalculationListener listener) {
        ParameterChecker.checkNotNull(builder, "builder");

        this.calculationIndex = calculationIndex;
        this.builder = builder;
        this.listener = listener;
    }

    @Override
    public PixelCalculator call() throws Exception {
        try {
            return builder.compilePixelCalculator();
        } catch (CompilationException e) {
            if (listener != null) {
                listener.onError(calculationIndex, e.getMessage(), e.getLongExplanation(), e);
            }
            e.printStackTrace();
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(calculationIndex, "Problem when creating renderer: " + e.getMessage(),
                                 "There was an unexpected problem when creating the renderer \n" +
                                 "used to draw the picture.  The full exception is: \n" + e, e);
            }
            e.printStackTrace();
        }
        return null;
    }
}
