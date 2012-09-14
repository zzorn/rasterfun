package org.rasterfun.effect.variable.value;

import javax.swing.*;

/**
 * Something that can be assigned to an InputVariable, e.g. a gradient.
 * Supports listening and editing.
 */
public interface VariableValue {

    /**
     * @return a new editor that edits this value.
     */
    JComponent createEditor();

    void addListener(ValueListener listener);

    void removeListener(ValueListener listener);

}
