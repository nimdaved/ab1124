package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerAllPropertiesEquals(Customer expected, Customer actual) {
        assertCustomerAutoGeneratedPropertiesEquals(expected, actual);
        assertCustomerAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerAllUpdatablePropertiesEquals(Customer expected, Customer actual) {
        assertCustomerUpdatableFieldsEquals(expected, actual);
        assertCustomerUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerAutoGeneratedPropertiesEquals(Customer expected, Customer actual) {
        assertThat(expected)
            .as("Verify Customer auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerUpdatableFieldsEquals(Customer expected, Customer actual) {
        assertThat(expected)
            .as("Verify Customer relevant properties")
            .satisfies(e -> assertThat(e.getFullName()).as("check fullName").isEqualTo(actual.getFullName()))
            .satisfies(e -> assertThat(e.getEmail()).as("check email").isEqualTo(actual.getEmail()))
            .satisfies(e -> assertThat(e.getPhoneNumber()).as("check phoneNumber").isEqualTo(actual.getPhoneNumber()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerUpdatableRelationshipsEquals(Customer expected, Customer actual) {
        // empty method
    }
}
