package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.ToolInventoryTestSamples.*;
import static org.nimdaved.toolrent.domain.ToolTestSamples.*;

import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class ToolTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tool.class);
        Tool tool1 = getToolSample1();
        Tool tool2 = new Tool();
        assertThat(tool1).isNotEqualTo(tool2);

        tool2.setCode(tool1.getCode());
        assertThat(tool1).isEqualTo(tool2);

        tool2 = getToolSample2();
        assertThat(tool1).isNotEqualTo(tool2);
    }

    @Test
    void toolInventoryTest() {
        Tool tool = getToolRandomSampleGenerator();
        ToolInventory toolInventoryBack = getToolInventoryRandomSampleGenerator();

        tool.setToolInventory(toolInventoryBack);
        assertThat(tool.getToolInventory()).isEqualTo(toolInventoryBack);

        tool.toolInventory(null);
        assertThat(tool.getToolInventory()).isNull();
    }
}
