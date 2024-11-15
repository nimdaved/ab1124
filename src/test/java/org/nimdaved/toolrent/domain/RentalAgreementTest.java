package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.RentalAgreementTestSamples.*;
import static org.nimdaved.toolrent.domain.RentalTestSamples.*;

import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class RentalAgreementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RentalAgreement.class);
        RentalAgreement rentalAgreement1 = getRentalAgreementSample1();
        RentalAgreement rentalAgreement2 = new RentalAgreement();
        assertThat(rentalAgreement1).isNotEqualTo(rentalAgreement2);

        rentalAgreement2.setId(rentalAgreement1.getId());
        assertThat(rentalAgreement1).isEqualTo(rentalAgreement2);

        rentalAgreement2 = getRentalAgreementSample2();
        assertThat(rentalAgreement1).isNotEqualTo(rentalAgreement2);
    }

    @Test
    void rentalTest() {
        RentalAgreement rentalAgreement = getRentalAgreementRandomSampleGenerator();
        Rental rentalBack = getRentalRandomSampleGenerator();

        rentalAgreement.setRental(rentalBack);
        assertThat(rentalAgreement.getRental()).isEqualTo(rentalBack);

        rentalAgreement.rental(null);
        assertThat(rentalAgreement.getRental()).isNull();
    }
}
