package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertToolAllPropertiesEquals(Tool expected, Tool actual) {
        assertToolAutoGeneratedPropertiesEquals(expected, actual);
        assertToolAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertToolAllUpdatablePropertiesEquals(Tool expected, Tool actual) {
        assertToolUpdatableFieldsEquals(expected, actual);
        assertToolUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertToolAutoGeneratedPropertiesEquals(Tool expected, Tool actual) {
        // empty method
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertToolUpdatableFieldsEquals(Tool expected, Tool actual) {
        assertThat(expected)
            .as("Verify Tool relevant properties")
            .satisfies(e -> assertThat(e.getCode()).as("check code").isEqualTo(actual.getCode()))
            .satisfies(e -> assertThat(e.getToolType()).as("check toolType").isEqualTo(actual.getToolType()))
            .satisfies(e -> assertThat(e.getBrand()).as("check brand").isEqualTo(actual.getBrand()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertToolUpdatableRelationshipsEquals(Tool expected, Tool actual) {
        assertThat(expected)
            .as("Verify Tool relationships")
            .satisfies(e -> assertThat(e.getToolInventory()).as("check toolInventory").isEqualTo(actual.getToolInventory()));
    }
}
