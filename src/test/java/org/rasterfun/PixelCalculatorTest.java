package org.rasterfun;

import org.junit.Before;
import org.junit.Test;
import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.compiler.CompilationException;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.core.listeners.PictureCalculationsListenerAdapter;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.parameters.ParametersImpl;
import org.rasterfun.picture.Picture;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests core aspect of the pixel calculator, and the PictureCalculation.
 */
public class PixelCalculatorTest {

    private Parameters parameters;

    @Before
    public void setUp() throws Exception {
        parameters = new ParametersImpl();
        parameters.set(PictureGenerator.NAME, "TestPic");
        parameters.set(PictureGenerator.WIDTH, 100);
        parameters.set(PictureGenerator.HEIGHT, 100);
        parameters.set(PictureGenerator.CHANNELS, new String[]{"roses", "violets"});
    }

    @Test
    public void testCalculateNoPixels() throws CompilationException {

        CalculatorBuilder calculatorBuilder = new CalculatorBuilder(parameters);
        final PixelCalculator pixelCalculator = calculatorBuilder.compilePixelCalculator();

        //System.out.println("Source:\n" + calculatorBuilder.getSource());

        final int[] p = {0};
        final int testCalculatorIndex = 3;
        pixelCalculator.calculatePixels(10, 10, new String[] {"red", "blue"},new float[]{}, 0, 0, 10, 10, new CalculationListener() {

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
    public void testPictureCalculation() throws CompilationException {
        // Create builder with some output
        parameters.set(PictureGenerator.CHANNELS, new String[]{"xs", "ys"});
        CalculatorBuilder calculatorBuilder = new CalculatorBuilder(parameters);
        calculatorBuilder.addEvaluationLoopSource("        pixelData[pixelIndex]     = x;\n" +
                                                  "        pixelData[pixelIndex + 1] = y;\n");

        // Create calculation task and start it
        final PictureCalculations calculation = new PictureCalculations(calculatorBuilder);
        calculation.start();

        // Get result picture
        final List<Picture> results = calculation.getPicturesAndWait();
        assertEquals("We should get one picture", 1, results.size());
        Picture result = results.get(0);

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

        // Check generated preview
        assertEquals("We should get one preview", 1, calculation.getPreviews().size());
        final Picture preview = calculation.getPreviews().get(0);
        assertEquals("Number of channels should be correct in preview", 2, preview.getChannelCount());
        assertPixelCorrect(preview, "xs",  0,  0,  0);
        assertPixelCorrect(preview, "xs",  1,  0,  1);
        assertPixelCorrect(preview, "ys",  1,  0,  0);
        assertPixelCorrect(preview, "xs",  1,  1,  1);
    }

    @Test
    public void testErrorCalculation() throws CompilationException {

        // Lets make a division by zero halfway through
        CalculatorBuilder calculatorBuilder = new CalculatorBuilder(parameters);
        calculatorBuilder.addEvaluationLoopSource("int w = 1 / (50 - y); // Oops!\n");

        final PictureCalculations calculation = new PictureCalculations(calculatorBuilder);

        final boolean[] errorReported = {false};
        final float[] progressMade = {0};
        final boolean[] readyCalled = {false};
        calculation.addListener(new PictureCalculationsListenerAdapter() {
            @Override
            public void onProgress(float progress) {
                //System.out.println("progress = " + progress);
                progressMade[0] = progress;
            }

            @Override
            public void onError(String description, Throwable cause) {
                //System.out.println("Error description = " + description);
                errorReported[0] = true;
            }

            @Override
            public void onReady(List<Picture> pictures) {
                readyCalled[0] = true;
            }
        });

        calculation.start();

        // Wait for boom
        final List<Picture> finalPictures = calculation.getPicturesAndWait();

        delay(10);

        assertNull("The pictures list should be null", finalPictures);
        assertTrue("An error should have been reported", errorReported[0]);
        assertTrue("Some progress should have been made, but not complete, but progress was " + progressMade[0], progressMade[0] > 0.05 && progressMade[0] < 0.95);
        assertFalse("onReady should not have been called", readyCalled[0]);
    }

    @Test
    public void testStop() throws CompilationException {
        // Create builder with sleep
        CalculatorBuilder calculatorBuilder = new CalculatorBuilder(parameters);
        calculatorBuilder.addEvaluationLoopSource("        try {\n" +
                                                  "            Thread.sleep(10);\n" +
                                                  "        } \n" +
                                                  "        catch (InterruptedException e) {\n" +
                                                  "            \n" +
                                                  "        }\n");


        // Create calculation task and start it
        final PictureCalculations calculation = new PictureCalculations(calculatorBuilder);
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
