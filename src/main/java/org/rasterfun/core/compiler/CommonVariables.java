package org.rasterfun.core.compiler;

import org.rasterfun.effect.Effect;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.variable.OutputVariable;
import org.rasterfun.effect.variable.VariableExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.rasterfun.core.compiler.RendererBuilder.*;

/**
 *
 */
public class CommonVariables {

    public static final OutputVariable GENERATOR_SEED = createBuiltinVar("generatorSeed",
                                                                         Integer.class,
                                                                         PARAMETER_PREFIX +
                                                                         RendererBuilder.GENERATOR_SEED,
                                                                         "Random seed that is common for the whole generator"
                                                                        );
    public static final OutputVariable PICTURE_SEED = createBuiltinVar("pictureSeed",
                                                                       Integer.class, PARAMETER_PREFIX +
                                                                       RendererBuilder.PICTURE_SEED,
                                                                       "Random seed that is unique for one picture"
                                                                      );
    public static final OutputVariable RELATIVE_X = createBuiltinVar("relX",
                                                                     Float.class,
                                                                     RendererBuilder.RELATIVE_X, null
                                                                    );
    public static final OutputVariable RELATIVE_Y = createBuiltinVar("relY",
                                                                     Float.class,
                                                                     RendererBuilder.RELATIVE_Y, null
                                                                    );
    public static final OutputVariable ABSOLUTE_X = createBuiltinVar("absoluteX", Float.class, X_NAME, null);
    public static final OutputVariable ABSOLUTE_Y = createBuiltinVar("absoluteY", Float.class, Y_NAME, null);
    public static final OutputVariable WIDTH      = createBuiltinVar("absoluteWidth", Float.class, W_NAME, null);
    public static final OutputVariable HEIGHT     = createBuiltinVar("absoluteHeight", Float.class, H_NAME, null);

    public static List<OutputVariable> BUILT_IN_VARS = Collections.unmodifiableList(new ArrayList<OutputVariable>(Arrays.asList(
            GENERATOR_SEED,
            PICTURE_SEED,
            RELATIVE_X,
            RELATIVE_Y,
            ABSOLUTE_X,
            ABSOLUTE_Y,
            WIDTH,
            HEIGHT
    )));


    public static OutputVariable createBuiltinVar(final String name,
                                                  final Class<?> type,
                                                  final String expression,
                                                  final String description) {
        final OutputVariable var = new OutputVariable(type, name, description, new VariableExpression() {
            @Override
            public String getExpressionString(EffectContainer container,
                                              Effect effect,
                                              String internalVarPrefix) {
                return expression;
            }
        }, false);

        var.setCodeIdentifier(expression);

        return var;
    }

    public static OutputVariable getChannelAsOutputVar(String channelName) {
        return createBuiltinVar(channelName, Float.class, CHANNEL_PREFIX + channelName, "Channel for " + channelName);
    }

    public static OutputVariable getParameterAsOutputVar(String parameterName) {
        return createBuiltinVar(parameterName,
                                Float.class, PARAMETER_PREFIX + parameterName,
                                "Value for parameter " + parameterName);
    }



}
