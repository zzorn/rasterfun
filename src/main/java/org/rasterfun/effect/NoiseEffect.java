package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.variable.InputVariable;
import org.rasterfun.effect.variable.OutputVariable;
import org.rasterfun.effect.variable.VariableExpression;
import org.rasterfun.utils.PerlinNoise;

import static org.rasterfun.core.compiler.CommonVariables.*;

/**
 *
 */
public class NoiseEffect extends EffectBase {

    private int  seed = 42;
    private float scale;
    private float amplitude = 1;
    private float offset = 0;
    private OutputVariable output;
    private InputVariable fillSeedVar;
    private InputVariable edgeSeedVar;
    private InputVariable scaleVar;
    private InputVariable amplitudeVar;
    private InputVariable offsetVar;
    private InputVariable xVar;
    private InputVariable yVar;

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

        requireValueChannel();

        initVariables();
    }

    public InputVariable getSeedVar() {
        return fillSeedVar;
    }

    public InputVariable getEdgeSeedVar() {
        return edgeSeedVar;
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

        xVar         = addInput("x",         0.5f,      Float.class,   RELATIVE_X, "X coordinate to get the noise at.  Ranges from 0 to 1 normally.");
        yVar         = addInput("y",         0.5f,      Float.class,   RELATIVE_Y, "Y coordinate to get the noise at.  Ranges from 0 to 1 normally.");
        fillSeedVar  = addInput("fillSeed",  seed,      Integer.class, PICTURE_SEED, "Random seed for the noise");
        edgeSeedVar  = addInput("edgeSeed",  seed,      Integer.class, GENERATOR_SEED, "Random seed for the noise along the picture edges.  Noises with the same edgeSeed will tile seamlessly.");
        scaleVar     = addInput("scale",     scale,     Float.class, "Frequency of the noise");
        amplitudeVar = addInput("amplitude", amplitude, Float.class, "Contrast of the noise");
        offsetVar    = addInput("offset",    offset,    Float.class, "Brightness / darkness of the noise");

        output = addOutput("noise", "the created noise", Float.class, new VariableExpression<NoiseEffect>() {

                    @Override
                    public String getExpressionString(EffectContainer container,
                                                      NoiseEffect effect,
                                                      String internalVarPrefix) {
                        return offsetVar.getExpr() + " + " +
                               amplitudeVar.getExpr() + " * " +
                               "(float)PerlinNoise.tilingNoise(" +
                               scaleVar.getExpr() + " * " + xVar.getExpr() + ", " +
                               scaleVar.getExpr() + " * " + yVar.getExpr() + ", " +
                               "0, 0, 1, 1, " +
                               fillSeedVar.getExpr() + ", " +
                               edgeSeedVar.getExpr() + ")";
                    }
                });

        // TODO: Add support to specify default channel assignments for outputs.  Only add the code if the channels are actually in the picture
    }

    @Override
    public void beforeBuildSource(RendererBuilder builder, String s, EffectContainer container) {

        builder.addImport(PerlinNoise.class);

    }


}
