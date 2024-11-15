package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.CustomerTestSamples.*;
import static org.nimdaved.toolrent.domain.RentalAgreementTestSamples.*;
import static org.nimdaved.toolrent.domain.RentalTestSamples.*;
import static org.nimdaved.toolrent.domain.ToolTestSamples.*;

import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class RentalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rental.class);
        Rental rental1 = getRentalSample1();
        Rental rental2 = new Rental();
        assertThat(rental1).isNotEqualTo(rental2);

        rental2.setId(rental1.getId());
        assertThat(rental1).isEqualTo(rental2);

        rental2 = getRentalSample2();
        assertThat(rental1).isNotEqualTo(rental2);
    }

    @Test
    void customerTest() {
        Rental rental = getRentalRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        rental.setCustomer(customerBack);
        assertThat(rental.getCustomer()).isEqualTo(customerBack);

        rental.customer(null);
        assertThat(rental.getCustomer()).isNull();
    }

    @Test
    void toolTest() {
        Rental rental = getRentalRandomSampleGenerator();
        Tool toolBack = getToolRandomSampleGenerator();

        rental.setTool(toolBack);
        assertThat(rental.getTool()).isEqualTo(toolBack);

        rental.tool(null);
        assertThat(rental.getTool()).isNull();
    }

    @Test
    void rentalAgreementTest() {
        Rental rental = getRentalRandomSampleGenerator();
        RentalAgreement rentalAgreementBack = getRentalAgreementRandomSampleGenerator();

        rental.setRentalAgreement(rentalAgreementBack);
        assertThat(rental.getRentalAgreement()).isEqualTo(rentalAgreementBack);
        assertThat(rentalAgreementBack.getRental()).isEqualTo(rental);

        rental.rentalAgreement(null);
        assertThat(rental.getRentalAgreement()).isNull();
        assertThat(rentalAgreementBack.getRental()).isNull();
    }
}
