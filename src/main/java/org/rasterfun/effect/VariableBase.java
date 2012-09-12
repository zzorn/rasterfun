package org.rasterfun.effect;

import org.rasterfun.utils.ParameterChecker;
import org.rasterfun.utils.StringUtils;

/**
 * Common functionality for Variables.
 */
public abstract class VariableBase implements Variable {

    private final Class<?> type;
    private String name;
    private String description;

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

    /**
     * @return generated identifier based on the name
     */
    public String createIdentifierPart() {
        return StringUtils.identifierFromName(getName(), 'Q');
    }

}
