package org.nimdaved.toolrent.domain.enumeration;

/**
 * The ToolType enumeration.
 */
public enum ToolType {
    LADDER("Ladder"),
    CHINSAW("Chainsaw"),
    JACKHUMMER("Jackhummer");

    private final String value;

    ToolType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
