package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.CustomerTestSamples.*;

import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }
}
