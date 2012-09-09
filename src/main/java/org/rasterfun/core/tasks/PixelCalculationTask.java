package org.rasterfun.core.tasks;

import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.picture.Picture;

/**
 * Task that calculates a part of the pixels in an image.
 */
public class PixelCalculationTask implements Runnable {

    private final Picture picture;
    private final int startY;
    private final int endY;
    private final PixelCalculator     calculator;
    private final CalculationListener listener;
    private final int calculatorIndex;

    public PixelCalculationTask(Picture picture, int startY, int endY, PixelCalculator calculator, CalculationListener listener, int calculatorIndex) {
        this.picture = picture;
        this.startY = startY;
        this.endY = endY;
        this.calculator = calculator;
        this.listener = listener;
        this.calculatorIndex = calculatorIndex;
    }

    @Override
    public void run() {
        calculator.calculatePixels(picture.getWidth(),
                                   picture.getHeight(),
                                   picture.getChannelNames(),
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
