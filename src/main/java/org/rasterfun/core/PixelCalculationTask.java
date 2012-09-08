package org.rasterfun.core;

import org.rasterfun.parameters.Parameters;
import org.rasterfun.picture.PictureImpl;

/**
 * Task that calculates a part of the pixels in an image.
 */
public class PixelCalculationTask implements Runnable {

    private final PictureImpl picture;
    private final Parameters parameters;
    private final int startY;
    private final int endY;
    private final PixelCalculator calculator;
    private final CalculationListener listener;
    private final int calculatorIndex;

    public PixelCalculationTask(PictureImpl picture, Parameters parameters, int startY, int endY, PixelCalculator calculator, CalculationListener listener, int calculatorIndex) {
        this.picture = picture;
        this.parameters = parameters;
        this.startY = startY;
        this.endY = endY;
        this.calculator = calculator;
        this.listener = listener;
        this.calculatorIndex = calculatorIndex;
    }

    @Override
    public void run() {
        calculator.calculatePixels(parameters,
                                   picture.getWidth(),
                                   picture.getHeight(),
                                   picture.getData(),
                                   0,
                                   startY,
                                   picture.getWidth(),
                                   endY,
                                   listener,
                                   calculatorIndex);
    }

    public void stop() {
        calculator.stop();
    }
}
