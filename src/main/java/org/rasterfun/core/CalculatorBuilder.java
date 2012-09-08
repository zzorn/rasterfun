package org.rasterfun.core;

import org.codehaus.janino.SimpleCompiler;

import java.io.StringReader;

/**
 * Used to collect data about a PixelCalculator,
 * convert it to java source code, and then compile and return a PixelCalculator.
 */
public class CalculatorBuilder {

    public static final int PROGRESS_REPORT_STEPS = Math.max(2, 50 / Runtime.getRuntime().availableProcessors());

    private SimpleCompiler janinoCompiler = new SimpleCompiler();
    private StringBuilder initializerSource = new StringBuilder();
    private StringBuilder evaluationLoopSource = new StringBuilder();
    private String source;

    public CalculatorBuilder() {
    }

    // Helper methods for adding variables, contexts, etc.

    /**
     * @return compiles the source provided and generates a picture calculator, or throws an error if it could not be done.
     */
    public PixelCalculator compilePixelCalculator() throws CalculatorCompilationException {

        final String className = "GeneratedPixelCalculator";
        final String packageName = "org.rasterfun.generated";
        String fullCalculatorName = packageName +"."+ className;

        source = "// Generated Pixel Calculator source: \n" +
                             "package " + packageName + ";\n" +
                             "\n" +
                             "import org.rasterfun.core.PixelCalculator;\n" +
                             "import org.rasterfun.core.CalculationListener;\n" +
                             "import org.rasterfun.parameters.Parameters;\n" +
                             "\n" +
                             "public final class "+className+" implements PixelCalculator {\n" +
                             "  \n" +
                             "  public final String[] getChannelNames() {\n" +
                             "    return null;\n" +
                             "  }\n" +
                             "  \n" +
                             "  public final void calculatePixels(final Parameters parameters,\n" +
                             "                                    final int width,\n" +
                             "                                    final int height,\n" +
                             "                                    final float[] pixelData,\n" +
                             "                                    final int startX,\n" +
                             "                                    final int startY,\n" +
                             "                                    final int endX,\n" +
                             "                                    final int endY,\n" +
                             "                                    final CalculationListener listener,\n" +
                             "                                    final int calculatorIndex) {\n" +
                             "    \n"+
                                  initializerSource.toString() +
                             "    \n" +
                             "    int progressReportInterval = (endY - startY) / "+PROGRESS_REPORT_STEPS+";\n" +
                             "    if (progressReportInterval <= 0) progressReportInterval = 1;\n" +
                             "    int progressReportCountdown = progressReportInterval;\n" +
                             "    \n"+
                             "    for (int y = startY; y < endY; y++) {\n" +
                             "       for (int x = startX; x < endX; x++) {\n" +
                                       evaluationLoopSource.toString() +
                             "         \n" +
                             "       }\n" +
                             "       \n" +
                             "       progressReportCountdown--;\n" +
                             "       if (progressReportCountdown <= 0) {\n" +
                             "         progressReportCountdown = progressReportInterval;\n" +
                             "         listener.onCalculationProgress(calculatorIndex, y - startY);\n" +
                             "       }\n" +
                             "    }\n"+
                             "  }\n" +
                             "}\n\n";


        try {
            // Compile
            janinoCompiler.cook(new StringReader(source));

            // Get compiled class
            Class calculatorClass = janinoCompiler.getClassLoader().loadClass(fullCalculatorName);

            // Create and return a new instance of it
            return (PixelCalculator) calculatorClass.newInstance();

        } catch (Exception e) {
            throw new CalculatorCompilationException("Could not compile the pixel calculator '"+ fullCalculatorName +"':\n" + e + "\nSource:\n\n"+ source +"\n", e);
        }

    }

    public String getSource() {
        return source;
    }
}
