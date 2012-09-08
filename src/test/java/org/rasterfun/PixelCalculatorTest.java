package org.rasterfun;

import org.junit.Test;
import org.rasterfun.core.*;
import org.rasterfun.parameters.ParametersImpl;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;

import java.util.Arrays;

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
        pixelCalculator.calculatePixels(null, 10, 10, new String[] {"red", "blue"},new float[]{}, 0, 0, 10, 10, new CalculationListener() {

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
    public void testPictureCalculation() throws CalculatorCompilationException {
        // Create builder with some output
        CalculatorBuilder calculatorBuilder = new CalculatorBuilder();
        calculatorBuilder.addEvaluationLoopSource("        pixelData[pixelIndex]     = x;\n" +
                                                  "        pixelData[pixelIndex + 1] = y;\n");

        // Create empty picture to draw on
        final PictureImpl picture = new PictureImpl("TestPic", 100, 100, Arrays.asList("xs", "ys"));

        // Create calculation task and start it
        final PictureCalculation calculation = new PictureCalculation(new ParametersImpl(), picture, calculatorBuilder);
        calculation.start();

        // Get result picture
        final Picture result = calculation.getPictureAndWait();

        // Check generated picture
        assertEquals("Number of channels should be correct", 2, result.getChannelCount());
        assertPixelCorrect(result, "xs",  0,  0,  0);
        assertPixelCorrect(result, "xs",  1,  0,  1);
        assertPixelCorrect(result, "ys",  1,  0,  0);
        assertPixelCorrect(result, "xs",  1,  1,  1);
        assertPixelCorrect(result, "xs",  4,  9,  4);
        assertPixelCorrect(result, "ys",  4,  9,  9);
        assertPixelCorrect(result, "xs", 99, 99, 99);
        assertPixelCorrect(result, "ys", 99, 99, 99);
    }

    @Test
    public void testErrorCalculation() throws CalculatorCompilationException {

        // Lets make a division by zero halfway through
        CalculatorBuilder calculatorBuilder = new CalculatorBuilder();
        calculatorBuilder.addEvaluationLoopSource("int w = 1 / (50 - y); // Oops!\n");

        // Create empty picture to draw on
        final PictureImpl picture = new PictureImpl("TestPic", 100, 100, Arrays.asList("roses", "violets"));

        final PictureCalculation calculation = new PictureCalculation(new ParametersImpl(), picture, calculatorBuilder);

        final boolean[] errorReported = {false};
        final float[] progressMade = {0};
        calculation.addListener(new ProgressListener() {
            @Override
            public void onProgress(float progress) {
                System.out.println("progress = " + progress);
                progressMade[0] = progress;
            }

            @Override
            public void onStatusChanged(String description) {
                System.out.println("description = " + description);
            }

            @Override
            public void onError(String description, Throwable cause) {
                System.out.println("Error description = " + description);
                errorReported[0] = true;
            }
        });

        calculation.start();

        // Wait for boom
        final Picture finalPicture = calculation.getPictureAndWait();

        delay(10);

        System.out.println("calculation.getProblemDescription() = " + calculation.getProblemDescription());

        assertNull("The picture should be null", finalPicture);
        assertNotNull("Some error message should have been reported", calculation.getProblemDescription());
        assertTrue("An error should have been reported", errorReported[0]);
        assertTrue("Some progress should have been made, but not complete, but progress was " +progressMade[0], progressMade[0] > 0.05 && progressMade[0] < 0.95);
    }

    @Test
    public void testStop() throws CalculatorCompilationException {
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


    private void assertPixelCorrect(Picture picture, String channel, int x, int y, float expected) {
        assertEquals("We should get the correct pixel value at channel " + channel + ", location (x: "+x+", y: "+y+")",
                     expected, picture.getPixel(channel, x, y), 0.0001);
    }

}
