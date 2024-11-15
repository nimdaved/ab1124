package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.AssertUtils.bigDecimalCompareTo;

public class RentalAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRentalAllPropertiesEquals(Rental expected, Rental actual) {
        assertRentalAutoGeneratedPropertiesEquals(expected, actual);
        assertRentalAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRentalAllUpdatablePropertiesEquals(Rental expected, Rental actual) {
        assertRentalUpdatableFieldsEquals(expected, actual);
        assertRentalUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRentalAutoGeneratedPropertiesEquals(Rental expected, Rental actual) {
        assertThat(expected)
            .as("Verify Rental auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRentalUpdatableFieldsEquals(Rental expected, Rental actual) {
        assertThat(expected)
            .as("Verify Rental relevant properties")
            .satisfies(e -> assertThat(e.getCheckOutDate()).as("check checkOutDate").isEqualTo(actual.getCheckOutDate()))
            .satisfies(e -> assertThat(e.getDayCount()).as("check dayCount").isEqualTo(actual.getDayCount()))
            .satisfies(e -> assertThat(e.getDiscountPercent()).as("check discountPercent").isEqualTo(actual.getDiscountPercent()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()))
            .satisfies(e ->
                assertThat(e.getChargeAmount())
                    .as("check chargeAmount")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getChargeAmount())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRentalUpdatableRelationshipsEquals(Rental expected, Rental actual) {
        assertThat(expected)
            .as("Verify Rental relationships")
            .satisfies(e -> assertThat(e.getCustomer()).as("check customer").isEqualTo(actual.getCustomer()))
            .satisfies(e -> assertThat(e.getTool()).as("check tool").isEqualTo(actual.getTool()));
    }
}
