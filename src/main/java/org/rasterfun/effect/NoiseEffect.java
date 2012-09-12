package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.utils.PerlinNoise;

import static org.rasterfun.core.compiler.CalculatorBuilder.RELATIVE_X;
import static org.rasterfun.core.compiler.CalculatorBuilder.RELATIVE_Y;

/**
 *
 */
public class NoiseEffect extends EffectBase {

    private String outChannel = "value";

    private int  seed = 42;

    private float scale;
    private float amplitude = 1;
    private float offset = 0;

    @Override
    protected void initVariables() {

        // TODO: Ability to put some variables outside the loop

        // TODO: Need references to the default variables available in the pixel renderer, such as x, y, pictureSeed, generatorSeed, localSeed, and channel values.
        final InputVariable x = addInput("x", 0f, "x coordinate to get the noise at", Float.class);
        final InputVariable y = addInput("y", 0f, "y coordinate to get the noise at", Float.class);

        final InputVariable seedVar = addInput("seed", seed, "random seed for the noise", Integer.class);
        final InputVariable scaleVar = addInput("scale", scale, "frequency of the noise", Float.class);
        final InputVariable amplitudeVar = addInput("amplitude", amplitude, "contrast of the noise", Float.class);
        final InputVariable offsetVar = addInput("offset", offset, "brightness / darkness of the noise", Float.class);

        final String expression = offsetVar+" + "+amplitudeVar+" * (float)PerlinNoise.noise("+scaleVar+" * "+RELATIVE_X+", "+scaleVar+" * "+RELATIVE_Y+", "+seedVar+")";

        addOutput("noise", "the created noise", Float.class, expression, outChannel);

    }

    @Override
    public void onBuildSource(CalculatorBuilder builder) {

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

}
