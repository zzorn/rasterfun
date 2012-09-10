package org.rasterfun.core.compiler;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.rasterfun.core.PixelCalculator;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.parameters.Parameters;

import java.io.IOException;
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

    private final String generatorName;

    public CalculatorBuilder(Parameters parameters) {
        // Take a copy of the parameters so that if they are changed after calculation started the calculation is not affected
        this.parameters = parameters.copy();
        generatorName = parameters.get(PictureGenerator.NAME, null);
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
//                             "       try {Thread.sleep(1);} catch (Exception e) {}\n" +
                             "       for (int x = startX; (x < endX) && running; x++) {\n" +
                             "         pixelData[pixelIndex] = ((float)x / endX) * (y % 2);\n" +
                                       evaluationLoopSource.toString() +
                             "         \n" +
                             "         pixelIndex += channelCount;\n" +
                             "       }\n" +
                             "       \n" +
                             "       completedScanLines++;\n" +
                             "       if ((completedScanLines >= progressReportInterval || y == endY - 1) && listener != null) {\n" +
                             "         listener.onCalculationProgress(calculatorIndex, width * completedScanLines);\n" +
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

        } catch (CompileException e) {
            throw new CompilationException(e, generatorName, source,
                                           "Could not compile the renderer because incorrect source code was generated",
                                           "There was a compile error in the generated renderer source code.\n" +
                                           "The compile error is: \n" + e.getMessage() + "\n\n" +
                                           "And the complete source of the renderer is:\n\n" + source
            );
        } catch (ClassNotFoundException e) {
            throw new CompilationException(e, generatorName, source,
                                           "Could not compile the renderer because a requested class was not found",
                                           "There was an attempt to access a non-existing or unavailable class \n" +
                                           "in the generated renderer source code.  The class that was not found was:\n" +
                                           e.getMessage() + "\nThe exception was " + e
            );
        } catch (InstantiationException e) {
            throw new CompilationException(e, generatorName, source,
                                           "Could not could not instantiate the compiled renderer",
                                           "Could not create an instance of the compiled renderer.  \n" +
                                           "The reason was '" +e.getMessage() + "'."
            );
        } catch (IllegalAccessException e) {
            throw new CompilationException(e, generatorName, source,
                                           "Could not access the compiled renderer",
                                           "There was a problem in accessing the compiled renderer. \n" +
                                           "The problematic access was '"+e.getMessage() + "'"
            );
        } catch (IOException e) {
            throw new CompilationException(e, generatorName, source,
                                           "Could not read the renderer source or other resource",
                                           "There was a problem accessing the renderer source, \n" +
                                           "or some other resources needed by the renderer.\n" +
                                           "The problematic resource was '" +e.getMessage()+"'"
            );
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
