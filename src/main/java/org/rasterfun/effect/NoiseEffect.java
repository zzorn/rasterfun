package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.container.EffectContainer;
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

    private int  seed = 42;
    private float scale;
    private float amplitude = 1;
    private float offset = 0;
    private OutputVariable output;
    private InputVariable seedVar;
    private InputVariable scaleVar;
    private InputVariable amplitudeVar;
    private InputVariable offsetVar;

    public NoiseEffect() {
        this(1);
    }

    public NoiseEffect(float scale) {
        this(42, scale);
    }

    public NoiseEffect(int seed, float scale) {
        this(seed, scale, 0, 1);
    }

    public NoiseEffect(int seed, float scale, float offset, float amplitude) {
        this.seed = seed;
        this.scale = scale;
        this.offset = offset;
        this.amplitude = amplitude;

        initVariables();
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

    public OutputVariable getOutput() {
        return output;
    }

    private void initVariables() {

        // TODO: Need references to the default variables available in the pixel renderer, such as x, y, pictureSeed, generatorSeed, localSeed, and channel values.
        final InputVariable xVar = addInput("x", 0f, "x coordinate to get the noise at", Float.class);
        final InputVariable yVar = addInput("y", 0f, "y coordinate to get the noise at", Float.class);

        seedVar = addInput("seed", seed, "random seed for the noise", Integer.class);
        scaleVar = addInput("scale", scale, "frequency of the noise", Float.class);
        amplitudeVar = addInput("amplitude", amplitude, "contrast of the noise", Float.class);
        offsetVar = addInput("offset", offset, "brightness / darkness of the noise", Float.class);

        output = addOutput("noise", "the created noise", Float.class, new VariableExpression<NoiseEffect>() {

                    @Override
                    public String getExpressionString(EffectContainer container,
                                                      NoiseEffect effect,
                                                      String internalVarPrefix) {
                        return offsetVar.getExpr() + " + " +
                               amplitudeVar.getExpr() + " * " +
                               "(float)PerlinNoise.noise(" +
                               scaleVar.getExpr() + " * " + RELATIVE_X + ", " +
                               scaleVar.getExpr() + " * " + RELATIVE_Y + ", " +
                               seedVar.getExpr() + ")";
                    }
                });

    }

    @Override
    public void beforeBuildSource(RendererBuilder builder, String s, EffectContainer container) {

        builder.addImport(PerlinNoise.class);

    }


}
