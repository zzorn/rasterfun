package org.rasterfun.core.compiler;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.utils.ParameterChecker;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.rasterfun.core.compiler.SourceLocation.*;

/**
 * Used to collect data about a PixelCalculator,
 * convert it to java source code, and then compile and return a PixelCalculator.
 */
public class CalculatorBuilder {

    public static final int PROGRESS_REPORT_STEPS = Math.max(2, 50 / Runtime.getRuntime().availableProcessors());
    public static final String[] DEFAULT_CHANNELS = new String[]{"red", "green", "blue", "alpha"};

    public static final String PIXEL_DATA = "pixelData";
    public static final String VAR_PREFIX = "var_";
    public static final String PIXEL_INDEX = "pixelIndex";
    public static final String X_NAME = "x";
    public static final String Y_NAME = "x";
    public static final String RELATIVE_X = "relX";
    public static final String RELATIVE_Y = "relY";

    private static final int DEFAULT_SIZE = 128;
    private static final String DEFAULT_NAME = "Picture";

    private final Parameters parameters;

    private final SimpleCompiler janinoCompiler = new SimpleCompiler();

    private final Map<SourceLocation, StringBuilder> inputSources = new HashMap<SourceLocation, StringBuilder>();

    private String source;

    private final String generatorName;

    public CalculatorBuilder(Parameters parameters) {
        // Take a copy of the parameters so that if they are changed after calculation started the calculation is not affected
        this.parameters = parameters.copy();

        generatorName = parameters.get(PictureGenerator.NAME, null);

        // Ensure channel names are valid
        int channelIndex = 0;
        for (String channelName : getChannels()) {
            ParameterChecker.checkIsIdentifier(channelName, "name for channel number " + (channelIndex++));
        }

        // Initialize input sources
        for (SourceLocation location : values()) {
            inputSources.put(location, new StringBuilder());
        }

        // Add default imports
        addImport(PixelCalculator.class);
        addImport(CalculationListener.class);
        addImport(Parameters.class);

        // Add code to get the channel values
        channelIndex = 0;
        for (String channel : getChannels()) {
            final String expression = PIXEL_DATA+"[" + PIXEL_INDEX + " + " + channelIndex + "]";
            addVariable(BEFORE_PIXEL, channel, expression, "float", false);
            channelIndex++;
        }

        // Add code to write the updated channel values
        channelIndex = 0;
        for (String channel : getChannels()) {
            addSourceLine(AFTER_PIXEL,
                          PIXEL_DATA + "[" + PIXEL_INDEX + " + " + channelIndex + "] = " + VAR_PREFIX + channel);
            channelIndex++;
        }

        /*
        // TODO: Remove, debug
        Random r = new Random();
        for (String channel : getChannels()) {
            addVariable(BEFORE_LOOP, "channelValue_"+ channel, "" + r.nextFloat() + "f");
        }
        int i = 2;
        for (String channel : getChannels()) {
            setVariable(AT_PIXEL, channel, getVariableName("channelValue_"+ channel) + " * ((float)x / endX) * (y % "+(i++)+")", true);
        }
        */
    }

    public String getChannelVariable(int channelIndex) {
        if (channelIndex < 0 || channelIndex >= getChannelCount()) {
            throw new IllegalArgumentException("There is no channel with the index " + channelIndex + ", " +
                                               "valid channels indexes are: 0.." + (getChannelCount() - 1));
        }
        return getChannelVariable(getChannels()[channelIndex]);
    }

    public String getChannelVariable(String channelName) {
        for (String existingChannelName : getChannels()) {
            if (channelName.equals(existingChannelName)) {
                return VAR_PREFIX + existingChannelName;
            }
        }
        throw new IllegalArgumentException("There is no channel with the name '" + channelName + "', valid channels are: " +
                                           Arrays.toString(getChannels()));
    }

    // Helper methods for adding variables, contexts, etc.


    /**
     * Directly adds some code that gets inserted inside the pixel calculation loop, after previously inserted code.
     */
    public void addPixelCalculationLine(String pixelCalculationLine) {
        addSourceLine(AT_PIXEL, pixelCalculationLine);
    }

    /**
     * Adds the specified line to the source at the specified location.
     * Indentation and terminating semicolon will be added to the line.
     */
    public void addSourceLine(SourceLocation location, String line) {
        ParameterChecker.checkNotNull(location, "location");
        ParameterChecker.checkNotNull(line, "line");

        inputSources.get(location).append(location.getIndent()).append(line).append(";\n");
    }

    /**
     * Adds a non final float variable with the specified initialization at the specified location.
     * The variable name will have the variable prefix appended in front.
     */
    public void addVariable(SourceLocation location,
                            String variableName,
                            String initializationExpression) {
        addVariable(location, variableName, initializationExpression, "float", false);
    }

    /**
     * Adds a variable with initialization at the specified location.
     * The variable name will have the variable prefix appended in front.
     */
    public void addVariable(SourceLocation location,
                            String variableName,
                            String initializationExpression,
                            String variableType,
                            boolean isFinal) {
        ParameterChecker.checkNotNull(location, "location");
        ParameterChecker.checkIsIdentifier(variableType, "variableType");
        ParameterChecker.checkIsIdentifier(variableName, "variableName");
        ParameterChecker.checkNotNull(initializationExpression, "initializationExpression");
        if (!location.isValidVariableLocation()) throw new IllegalArgumentException("Variables can not be added to the location " + location);

        addSourceLine(location,
                      (location == FIELDS ? "private " : "") +
                      (isFinal ? "final " : "") +
                      variableType + " " +
                      VAR_PREFIX + variableName + " = " +
                      initializationExpression
                     );
    }

    /**
     * Adds an assignment to the specified variable at the specified location.
     * The variable name will have the variable prefix appended in front.
     */
    public void setVariable(SourceLocation location, String variableName, String expression) {
        ParameterChecker.checkNotNull(location, "location");
        ParameterChecker.checkIsIdentifier(variableName, "variableName");
        ParameterChecker.checkNotNull(expression, "expression");
        if (!location.isValidAssignmentLocation()) throw new IllegalArgumentException("Variables can not be assigned in the location " + location);

        addSourceLine(location,
                      VAR_PREFIX + variableName + " = " +
                      expression
                     );
    }

    public String getVariableName(String baseName) {
        ParameterChecker.checkIsIdentifier(baseName, "baseName");
        return VAR_PREFIX + baseName;
    }

    /**
     * Adds an import for the specified class or interface.
     */
    public void addImport(Class<?> classToImport) {
        addSourceLine(IMPORTS, "import " + classToImport.getName());
    }

    /**
     * @return compiles the source provided and generates a picture calculator, or throws an error if it could not be done.
     */
    public PixelCalculator compilePixelCalculator() throws CompilationException {

        final String className = "GeneratedPixelCalculator";
        final String packageName = "org.rasterfun.generated";
        String fullCalculatorName = packageName +"."+ className;

        source = "\n// Generated Pixel Calculator source: \n" +
                 "package " + packageName + ";\n" +
                 sourcesFor(IMPORTS) +
                 "public final class "+className+" implements PixelCalculator {\n" +
                 "  \n" +
                 "  private boolean running = true;\n" +
                 "  \n" +
                 "  public final void stop() {\n" +
                 "    running = false;\n" +
                 "  }\n" +
                 sourcesFor(FIELDS) +
                 "  // Read input parameters\n"+
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
                 "    // Check that the passed in picture has the correct number of channels\n"+
                 "    if (channelNames.length != "+getChannelCount()+") \n" +
                 "      throw new IllegalArgumentException(\"The channel count should be correct, expected "+getChannelCount()+", but got \"+channelNames.length+\".\");\n"+
                 sourcesFor(BEFORE_LOOP) +
                 "    // Set up progress reporting\n"+
                 "    final int progressReportInterval = (endY - startY) / "+PROGRESS_REPORT_STEPS+";\n" +
                 "    int completedScanLines = 0;\n" +
                 "    \n"+
                 "    // Loop the pixels\n" +
                 "    final float relXStep = (width == 1) ? 0 : 1f / (width - 1);\n"+
                 "    final float relYStep = (height == 1) ? 0 : 1f / (height - 1);\n"+
                 "    float relX;\n"+
                 "    float relY = (height == 1) ? 0.5f : (float)startY / (height - 1);\n"+
                 "    int pixelIndex = (startY * width + startX) * "+getChannelCount()+";\n" +
                 "    for (int y = startY; (y < endY) && running; y++) {\n" +
//                             "       try {Thread.sleep(1);} catch (Exception e) {}\n" +
                 sourcesFor(BEFORE_LINE) +
                 "      relX = (width == 1) ? 0.5f : (float)startX / (width - 1);"+
                 "      for (int x = startX; (x < endX) && running; x++) {\n" +
                 "\n" +
                 sourcesFor(BEFORE_PIXEL) +
                 sourcesFor(AT_PIXEL) +
                 sourcesFor(AFTER_PIXEL) +
                 "        "+PIXEL_INDEX+" += "+getChannelCount()+";\n" +
                 "        relX += relXStep;\n" +
                 "      }\n" +
                 sourcesFor(AFTER_LINE) +
                 "      relY += relYStep;\n" +
                 "\n" +
                 "      // Report progress\n" +
                 "      completedScanLines++;\n" +
                 "      if ((completedScanLines >= progressReportInterval || y == endY - 1) && listener != null) {\n" +
                 "        listener.onCalculationProgress(calculatorIndex, width * completedScanLines);\n" +
                 "        completedScanLines = 0;\n" +
                 "      }\n" +
                 "    }\n"+
                 sourcesFor(AFTER_LOOP) +
                 "  }\n" +
                 sourcesFor(METHODS) +
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


    private String sourcesFor(final SourceLocation location) {
        return "\n" +
               location.getIndent() + "// " + location.toString() + "\n" +
               inputSources.get(location).toString() +
               "\n";
    }

}
