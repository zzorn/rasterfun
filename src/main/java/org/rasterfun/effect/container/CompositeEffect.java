package org.rasterfun.effect.container;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.AbstractEffect;
import org.rasterfun.effect.Effect;
import org.rasterfun.effect.variable.InputOutputVariable;
import org.rasterfun.effect.variable.InputVariable;
import org.rasterfun.effect.variable.OutputVariable;
import org.rasterfun.library.GeneratorElement;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that consist of a number of other effects, applied in sequence.
 */
public final class CompositeEffect extends AbstractEffect {


    private final EffectContainer effectContainer;


    public CompositeEffect() {
        this(new EffectContainerImpl());
    }

    private CompositeEffect(EffectContainer effectContainer) {
        this.effectContainer = effectContainer;

        // Listen to changes and forward them.
        this.effectContainer.addListener(new EffectContainerListener() {
            @Override
            public void onContainerChanged(EffectContainer container) {
                notifyEffectChanged();
            }

            @Override
            public void onEffectChanged(Effect effect) {
                notifyEffectChanged();
            }
        });
    }

    @Override
    public void generateCode(RendererBuilder builder, String effectNamespace, EffectContainer container) {
        effectContainer.buildSource(builder, effectNamespace, container);
    }

    public EffectContainer getEffectContainer() {
        return effectContainer;
    }

    @Override
    public List<InputVariable> getInputVariables() {
        List<InputVariable> inputVars = new ArrayList<InputVariable>();
        for (InputOutputVariable inputOutputVariable : effectContainer.getInputs()) {
            inputVars.add(inputOutputVariable.getInputVariable());
        }
        return inputVars;
    }

    @Override
    public List<OutputVariable> getOutputVariables() {
        List<OutputVariable> outputVars = new ArrayList<OutputVariable>();
        for (InputOutputVariable inputOutputVariable : effectContainer.getOutputs()) {
            outputVars.add(inputOutputVariable.getOutputVariable());
        }
        return outputVars;
    }



    @Override
    public GeneratorElement copy() {
        return new CompositeEffect(effectContainer.copy());
    }

    public void addEffect(Effect effect) {getEffectContainer().addEffect(effect);}
    public void addEffect(Effect effect, int position) {getEffectContainer().addEffect(effect, position);}
    public void moveEffect(Effect effect, int position) {getEffectContainer().moveEffect(effect, position);}
    public void removeEffect(Effect effect) {getEffectContainer().removeEffect(effect);}
    public List<Effect> getEffects() {return getEffectContainer().getEffects();}



}
