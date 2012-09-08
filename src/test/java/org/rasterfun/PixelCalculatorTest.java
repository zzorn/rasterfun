package org.rasterfun;

import org.junit.Test;
import org.rasterfun.core.*;

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


}
