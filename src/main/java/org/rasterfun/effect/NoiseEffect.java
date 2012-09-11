package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.utils.PerlinNoise;

import static org.rasterfun.core.compiler.CalculatorBuilder.*;
import static org.rasterfun.core.compiler.SourceLocation.AT_PIXEL;
import static org.rasterfun.core.compiler.SourceLocation.BEFORE_LOOP;

/**
 *
 */
public class NoiseEffect extends EffectBase {

    private String valueOutVariable = "value";
    private String seedVariable = "seed";
    private int  seed = 42;

    private float scale;
    private float amplitude = 1;
    private float offset = 0;

    public NoiseEffect() {
    }

    public NoiseEffect(String valueOutVariable, float scale) {
        this.valueOutVariable = valueOutVariable;
        this.scale = scale;
    }

    public NoiseEffect(String valueOutVariable, int seed, float scale) {
        this.valueOutVariable = valueOutVariable;
        this.seed = seed;
        this.scale = scale;
    }

    public NoiseEffect(String valueOutVariable, int seed, float scale, float offset, float amplitude) {
        this.valueOutVariable = valueOutVariable;
        this.seed = seed;
        this.scale = scale;
        this.offset = offset;
        this.amplitude = amplitude;
    }

    @Override
    public void buildSource(CalculatorBuilder builder) {

        builder.addImport(PerlinNoise.class);

        // TODO: Check if we have a seed variable, if not use a default value

        // TODO: Support for unique namespaces for effects
        String seedVar = seedVariable + hashCode();

        builder.addVariable(BEFORE_LOOP, seedVar, ""+seed);

        final String expression = offset +"f + " + amplitude + "f * (float)PerlinNoise.noise("+scale+"f * "+RELATIVE_X+", "+scale+"f * "+RELATIVE_Y+", (int)"+VAR_PREFIX+seedVar+")";
        builder.setVariable(AT_PIXEL, valueOutVariable, expression);
    }

    public String getValueOutVariable() {
        return valueOutVariable;
    }

    public void setValueOutVariable(String valueOutVariable) {
        this.valueOutVariable = valueOutVariable;
    }

    public String getSeedVariable() {
        return seedVariable;
    }

    public void setSeedVariable(String seedVariable) {
        this.seedVariable = seedVariable;
    }
}
