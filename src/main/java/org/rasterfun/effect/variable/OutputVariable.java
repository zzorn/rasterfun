package org.rasterfun.effect.variable;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.Effect;
import org.rasterfun.effect.container.EffectContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.rasterfun.core.compiler.SourceLocation.AT_PIXEL;

/**
 * Represents code fields created by effects, or from the picture generator parameters.
 * Can generate an unique variable name to the source code.
 */
public class OutputVariable extends VariableBase {

    private List<InputVariable> users = new ArrayList<InputVariable>();

    private String codeIdentifier;

    private VariableExpression expression;


    public OutputVariable(Class<?> type, String name, String description, VariableExpression expression) {
        super(type, name, description);
        this.expression = expression;
    }

    public void addUser(InputVariable inputVariable) {
        users.add(inputVariable);
    }

    public void removeUser(InputVariable inputVariable) {
        users.remove(inputVariable);
    }

    public boolean hasUsers() {
        return !users.isEmpty();
    }

    public List<InputVariable> getUsers() {
        return Collections.unmodifiableList(users);
    }

    /**
     * @param codeId an identifier to use for this variable in generated code, or null to clear.
     */
    public void setCodeIdentifier(String codeId) {
        codeIdentifier = codeId;
    }

    /**
     * @return an identifier to use for this variable in generated code, or exception thrown if not yet initialized.
     */
    public String getCodeIdentifier() {
        if (codeIdentifier == null) throw new IllegalStateException("Code id not yet set");
        else return codeIdentifier;
    }

    public final VariableExpression getExpression() {
        return expression;
    }

    /*
    public final void setExpression(VariableExpression expression) {
        this.expression = expression;

        notifyVariableChanged();
    }

    public final String getExpressionString(EffectContainer container, Effect effect) {
        return expression.getExpressionString(container, effect);
    }
    */

    public void buildSource(RendererBuilder builder, EffectContainer container, Effect effect, String namespace) {
        builder.addVariable(AT_PIXEL, getCodeIdentifier(), expression.getExpressionString(container, effect, namespace), getType(), true);
    }

    public void removeAllUsers() {
        for (InputVariable user : users) {
            user.setToVariable(null);
        }
        users.clear();
    }
}
