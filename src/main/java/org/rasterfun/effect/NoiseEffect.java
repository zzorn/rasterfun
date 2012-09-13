package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.variable.InputVariable;
import org.rasterfun.effect.variable.OutputVariable;
import org.rasterfun.effect.variable.VariableExpression;
import org.rasterfun.utils.PerlinNoise;

import static org.rasterfun.core.compiler.RendererBuilder.RELATIVE_X;
import static org.rasterfun.core.compiler.RendererBuilder.RELATIVE_Y;

/**
 *
 */
public class NoiseEffect extends EffectBase {

    private String outChannel = "value";

    private int  seed = 42;

    private float scale;
    private float amplitude = 1;
    private float offset = 0;
    private InputVariable seedVar;
    private InputVariable scaleVar;
    private InputVariable amplitudeVar;
    private InputVariable offsetVar;
    private InputVariable y;
    private InputVariable x;
    private OutputVariable noiseOut;

    @Override
    protected void initVariables() {

        // TODO: Need references to the default variables available in the pixel renderer, such as x, y, pictureSeed, generatorSeed, localSeed, and channel values.
        x = addInput("x", 0f, "x coordinate to get the noise at", Float.class);
        y = addInput("y", 0f, "y coordinate to get the noise at", Float.class);

        seedVar      = addInput("seed", seed, "random seed for the noise", Integer.class);
        scaleVar     = addInput("scale", scale, "frequency of the noise", Float.class);
        amplitudeVar = addInput("amplitude", amplitude, "contrast of the noise", Float.class);
        offsetVar    = addInput("offset", offset, "brightness / darkness of the noise", Float.class);

        System.out.println("scaleVar.toString() = " + scaleVar.toString());

        final VariableExpression expression = new VariableExpression() {
            @Override
            public String getExpressionString() {
                return offsetVar+" + "+amplitudeVar+" * (float)PerlinNoise.noise("+scaleVar+" * "+RELATIVE_X+", "+scaleVar+" * "+RELATIVE_Y+", "+seedVar+")";
            }
        };
        noiseOut = addOutput("noise", "the created noise", Float.class, expression, outChannel);

    }

    @Override
    public void onBuildSource(RendererBuilder builder) {

        builder.addImport(PerlinNoise.class);

    }

    public NoiseEffect(String outChannel, float scale) {
        this.outChannel = outChannel;
        this.scale = scale;
    }

    public NoiseEffect(String outChannel, int seed, float scale) {
        this.outChannel = outChannel;
        this.seed = seed;
        this.scale = scale;
    }

    public NoiseEffect(String outChannel, int seed, float scale, float offset, float amplitude) {
        this.outChannel = outChannel;
        this.seed = seed;
        this.scale = scale;
        this.offset = offset;
        this.amplitude = amplitude;
    }

    public InputVariable getSeedVar() {
        return seedVar;
    }

    public InputVariable getScaleVar() {
        return scaleVar;
    }

    public InputVariable getAmplitudeVar() {
        return amplitudeVar;
    }

    public InputVariable getOffsetVar() {
        return offsetVar;
    }

    public InputVariable getY() {
        return y;
    }

    public InputVariable getX() {
        return x;
    }

    public OutputVariable getNoiseOut() {
        return noiseOut;
    }
}
