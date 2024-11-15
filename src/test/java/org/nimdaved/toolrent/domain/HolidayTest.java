package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.HolidayTestSamples.*;

import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class HolidayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Holiday.class);
        Holiday holiday1 = getHolidaySample1();
        Holiday holiday2 = new Holiday();
        assertThat(holiday1).isNotEqualTo(holiday2);

        holiday2.setId(holiday1.getId());
        assertThat(holiday1).isEqualTo(holiday2);

        holiday2 = getHolidaySample2();
        assertThat(holiday1).isNotEqualTo(holiday2);
    }
}
