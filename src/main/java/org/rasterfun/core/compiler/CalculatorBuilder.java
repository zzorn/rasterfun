package org.rasterfun.core.compiler;

import org.codehaus.janino.SimpleCompiler;
import org.rasterfun.core.PixelCalculator;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.parameters.Parameters;

import java.io.StringReader;

/**
 * Used to collect data about a PixelCalculator,
 * convert it to java source code, and then compile and return a PixelCalculator.
 */
public class CalculatorBuilder {

    public static final int PROGRESS_REPORT_STEPS = Math.max(2, 50 / Runtime.getRuntime().availableProcessors());
    public static final String[] DEFAULT_CHANNELS = new String[]{"red", "green", "blue", "alpha"};

    private static final int DEFAULT_SIZE = 128;
    private static final String DEFAULT_NAME = "Picture";

    private final Parameters parameters;

    private final SimpleCompiler janinoCompiler = new SimpleCompiler();
    private final StringBuilder initializerSource = new StringBuilder();
    private final StringBuilder evaluationLoopSource = new StringBuilder();
    private String source;

    public CalculatorBuilder(Parameters parameters) {
        // Take a copy of the parameters so that if they are changed after calculation started the calculation is not affected
        this.parameters = parameters.copy();
    }

    // Helper methods for adding variables, contexts, etc.

    /**
     * @return compiles the source provided and generates a picture calculator, or throws an error if it could not be done.
     */
    public PixelCalculator compilePixelCalculator() throws CompilationException {

        final String className = "GeneratedPixelCalculator";
        final String packageName = "org.rasterfun.generated";
        String fullCalculatorName = packageName +"."+ className;

        source = "// Generated Pixel Calculator source: \n" +
                             "package " + packageName + ";\n" +
                             "\n" +
                             "import org.rasterfun.core.PixelCalculator;\n" +
                             "import org.rasterfun.core.listeners.CalculationListener;\n" +
                             "import org.rasterfun.parameters.Parameters;\n" +
                             "\n" +
                             "public final class "+className+" implements PixelCalculator {\n" +
                             "  \n" +
                             "  private boolean running = true;\n" +
                             "  \n" +
                             "  public final void stop() {\n" +
                             "    running = false;\n" +
                             "  }\n" +
                             "  \n" +
                             "  public void setParameters(Object[] parameters) {\n" +
                             "    \n" + // TODO: Handle parameter assignment here
                             "  }\n" +
                             "  \n" +
                             "  public final void calculatePixels(final int width,\n" +
                             "                                    final int height,\n" +
                             "                                    final String[] channelNames,\n" +
                             "                                    final float[] pixelData,\n" +
                             "                                    final int startX,\n" +
                             "                                    final int startY,\n" +
                             "                                    final int endX,\n" +
                             "                                    final int endY,\n" +
                             "                                    final CalculationListener listener,\n" +
                             "                                    final int calculatorIndex) {\n" +
                             "    \n"+
                             "    final int channelCount = channelNames.length;\n"+
                             "    \n"+
                                  initializerSource.toString() +
                             "    \n" +
                             "    int progressReportInterval = (endY - startY) / "+PROGRESS_REPORT_STEPS+";\n" +
                             "    int completedScanLines = 0;\n" +
                             "    \n"+
                             "    int pixelIndex = (startY * width + startX) * channelCount;\n"+
                             "    for (int y = startY; (y < endY) && running; y++) {\n" +
                             "       for (int x = startX; (x < endX) && running; x++) {\n" +
                                       evaluationLoopSource.toString() +
                             "         \n" +
                             "         pixelIndex += channelCount;\n" +
                             "       }\n" +
                             "       \n" +
                             "       completedScanLines++;\n" +
                             "       if (completedScanLines >= progressReportInterval || y == endY - 1) {\n" +
                             "         listener.onCalculationProgress(calculatorIndex, completedScanLines);\n" +
                             "         completedScanLines = 0;\n" +
                             "       }\n" +
                             "    }\n"+
                             "  }\n" +
                             "}\n\n";


        try {
            // Compile
            janinoCompiler.cook(new StringReader(source));

            // Get compiled class
            Class calculatorClass = janinoCompiler.getClassLoader().loadClass(fullCalculatorName);

            // Create a new instance of it
            final PixelCalculator pixelCalculator = (PixelCalculator) calculatorClass.newInstance();

            // Initialize it with the parameters
            // TODO: Pass in any non-literal parameters that could not be compiled into the code
            pixelCalculator.setParameters(new Object[0]);

            return pixelCalculator;

        } catch (Exception e) {
            throw new CompilationException("Could not compile the pixel calculator '"+ fullCalculatorName +"':\n" + e + "\nSource:\n\n"+ source +"\n", e);
        }

    }

    /**
     * Directly adds some code that gets inserted inside the pixel calculation loop, after previously inserted code.
     */
    public void addEvaluationLoopSource(String additionalEvaluationLoopCode) {
        evaluationLoopSource.append(additionalEvaluationLoopCode);
    }

    /**
     * @return the parameters used when generating the picture.
     */
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * @return the generated source, or null if it has not yet been generated.
     */
    public String getSource() {
        return source;
    }

    /**
     * @return the name for this generated picture.
     */
    public String getName() {
        return parameters.get(PictureGenerator.NAME, DEFAULT_NAME);
    }

    /**
     * @return default width of the generated picture.
     *         The final width is determined by the data array size passed to the PixelCalculator
     *         (this allows us to generate smaller preview pictures easily).
     */
    public int getWidth() {
        return parameters.get(PictureGenerator.WIDTH, DEFAULT_SIZE);
    }

    /**
     * @return default height of the generated picture.
     *         The final height is determined by the data array size passed to the PixelCalculator
     *         (this allows us to generate smaller preview pictures easily).
     */
    public int getHeight() {
        return parameters.get(PictureGenerator.HEIGHT, DEFAULT_SIZE);
    }

    /**
     * @return number of channels in the generated picture.
     */
    public int getChannelCount() {
        return getChannels().length;
    }

    /**
     * @return names of the channels in the generated picture.
     */
    public String[] getChannels() {
        return parameters.get(PictureGenerator.CHANNELS, DEFAULT_CHANNELS);
    }
}
