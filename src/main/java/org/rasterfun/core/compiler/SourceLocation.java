package org.rasterfun.core.compiler;

/**
 * Indicates where in the generated source some code should be placed.
 * Used when adding source to CalculationBuilder.
 */
public enum SourceLocation {

    IMPORTS(0, false, false),
    FIELDS(1, true, false),
    METHODS(1, false, false),
    BEFORE_LOOP(2, true, true),
    BEFORE_LINE(3, true, true),
    BEFORE_PIXEL(4, true, true),
    AT_PIXEL(4, true, true),
    AFTER_PIXEL(4, true, true),
    AFTER_LINE(3, true, true),
    AFTER_LOOP(2, true, true)
    ;

    private String indent = "";
    private final boolean validVariableLocation;
    private final boolean validAssignmentLocation;

    private SourceLocation(int indentSize, boolean validVariableLocation, boolean validAssignmentLocation) {
        this.validVariableLocation = validVariableLocation;
        this.validAssignmentLocation = validAssignmentLocation;

        for (int i = 0; i < indentSize; i++) {
            indent += "  ";
        }
    }

    public String getIndent() {
        return indent;
    }

    public boolean isValidVariableLocation() {
        return validVariableLocation;
    }

    public boolean isValidAssignmentLocation() {
        return validAssignmentLocation;
    }
}
