package org.rasterfun;

import org.junit.Test;
import org.rasterfun.core.*;
import org.rasterfun.parameters.ParametersImpl;
import org.rasterfun.picture.PictureImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests core aspect of the pixel calculator.
 */
public class PixelCalculatorTest {

    @Test
    public void testCalculateNoPixels() throws CalculatorCompilationException {

        CalculatorBuilder calculatorBuilder = new CalculatorBuilder();
        final PixelCalculator pixelCalculator = calculatorBuilder.compilePixelCalculator();

        //System.out.println("Source:\n" + calculatorBuilder.getSource());

        final int[] p = {0};
        final int testCalculatorIndex = 3;
        pixelCalculator.calculatePixels(null, 10, 10, new float[]{}, 0, 0, 10, 10, new CalculationListener() {

            @Override
            public void onCalculationProgress(int calculationIndex, int completedLines) {

                assertEquals("Calculator index should be passed back correctly", testCalculatorIndex, calculationIndex);

                //System.out.println("completedLines = " + completedLines);
                p[0] = completedLines;
            }
        }, testCalculatorIndex);

        assertTrue("Should have gotten some progress", p[0] > 0);
    }

    @Test
    public void testCancel() throws CalculatorCompilationException {
        // Create builder with sleep
        CalculatorBuilder calculatorBuilder = new CalculatorBuilder();
        calculatorBuilder.addEvaluationLoopSource("        try {\n" +
                                                  "            Thread.sleep(10);\n" +
                                                  "        } \n" +
                                                  "        catch (InterruptedException e) {\n" +
                                                  "            \n" +
                                                  "        }\n");


        // Create empty picture to draw on
        final PictureImpl picture = new PictureImpl("TestPic", 100, 100, Arrays.asList("roses", "violets"));

        // Create calculation task and start it
        final PictureCalculation calculation = new PictureCalculation(new ParametersImpl(), picture, calculatorBuilder);
        calculation.start();

        // Should be running
        assertFalse("The calculation should not be done yet", calculation.isDone());

        // Wait a bit for the calculation to start
        delay(50);

        // Stop calculation
        calculation.stop();

        // Wait a bit for the calculation to stop
        delay(50);

        // Should be stopped now
        assertTrue("The calculation should have stopped now", calculation.isDone());
    }


    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            // Ignore
        }
    }

}
