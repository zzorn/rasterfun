package org.rasterfun.effect.variable;

import org.rasterfun.utils.ParameterChecker;

import java.util.HashSet;
import java.util.Set;

/**
 * Common functionality for Variables.
 */
public abstract class VariableBase implements Variable {

    private final Class<?> type;
    private String name;
    private String description;
    private Set<VariableListener> listeners = new HashSet<VariableListener>(3);


    protected VariableBase(Class<?> type) {
        ParameterChecker.checkNotNull(type, "type");
        this.type = type;
    }

    protected VariableBase(Class<?> type, String name, String description) {
        ParameterChecker.checkNotNull(type, "type");
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public final void addListener(VariableListener listener) {
        listeners.add(listener);

    }

    @Override
    public final void removeListener(VariableListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify listeners that this variable has changed.
     */
    public final void notifyVariableChanged() {
        for (VariableListener listener : listeners) {
            listener.onVariableChanged(this);
        }
    }


}
