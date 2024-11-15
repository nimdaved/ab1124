package org.nimdaved.toolrent.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nimdaved.toolrent.domain.ToolInventoryTestSamples.*;
import static org.nimdaved.toolrent.domain.ToolTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.web.rest.TestUtil;

class ToolInventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToolInventory.class);
        ToolInventory toolInventory1 = getToolInventorySample1();
        ToolInventory toolInventory2 = new ToolInventory();
        assertThat(toolInventory1).isNotEqualTo(toolInventory2);

        toolInventory2.setId(toolInventory1.getId());
        assertThat(toolInventory1).isEqualTo(toolInventory2);

        toolInventory2 = getToolInventorySample2();
        assertThat(toolInventory1).isNotEqualTo(toolInventory2);
    }

    @Test
    void toolTest() {
        ToolInventory toolInventory = getToolInventoryRandomSampleGenerator();
        Tool toolBack = getToolRandomSampleGenerator();

        toolInventory.addTool(toolBack);
        assertThat(toolInventory.getTools()).containsOnly(toolBack);
        assertThat(toolBack.getToolInventory()).isEqualTo(toolInventory);

        toolInventory.removeTool(toolBack);
        assertThat(toolInventory.getTools()).doesNotContain(toolBack);
        assertThat(toolBack.getToolInventory()).isNull();

        toolInventory.tools(new HashSet<>(Set.of(toolBack)));
        assertThat(toolInventory.getTools()).containsOnly(toolBack);
        assertThat(toolBack.getToolInventory()).isEqualTo(toolInventory);

        toolInventory.setTools(new HashSet<>());
        assertThat(toolInventory.getTools()).doesNotContain(toolBack);
        assertThat(toolBack.getToolInventory()).isNull();
    }
}
