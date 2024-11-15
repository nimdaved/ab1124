package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.ChargeTestSamples.*;

import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class ChargeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Charge.class);
        Charge charge1 = getChargeSample1();
        Charge charge2 = new Charge();
        assertThat(charge1).isNotEqualTo(charge2);

        charge2.setId(charge1.getId());
        assertThat(charge1).isEqualTo(charge2);

        charge2 = getChargeSample2();
        assertThat(charge1).isNotEqualTo(charge2);
    }
}
