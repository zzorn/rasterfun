package org.rasterfun.effect.variable;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.utils.ParameterChecker;
import org.rasterfun.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Common functionality for Variables.
 */
public abstract class VariableBase implements Variable {

    private final Class<?> type;
    private String name;
    private String description;
    private String namespace;
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

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void buildSource(RendererBuilder builder) {
    }

    /**
     * @return generated identifier based on the name
     */
    public final String getIdentifier() {
        if (namespace == null) throw new IllegalStateException("Can not calculate the identifier, as the namespace was not yet initialized.");

        // Create variable id part that is guaranteed to have no underscores (so that it can't collide with namespaces).
        final String variableIdPart = StringUtils.identifierFromName(getName().replace('_', ' '), 'Q');
        final String identifier = namespace + "_" + variableIdPart;

        // Sanity check // NOTE: it gets var prefix added automatically
        //ParameterChecker.checkIsIdentifier(identifier, "generated variable identifier");
        return identifier;
    }


    public String getVarIdentifier() {
        return RendererBuilder.VAR_PREFIX + getIdentifier();
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
