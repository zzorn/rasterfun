package org.rasterfun.effect.variable;

import org.rasterfun.effect.Effect;
import org.rasterfun.effect.container.EffectContainer;

/**
 * Provides variable expression on demand.
 */
public interface VariableExpression<E extends Effect> {

    String getExpressionString(EffectContainer container, E effect, String internalVarPrefix);

}
