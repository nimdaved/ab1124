package org.nimdaved.toolrent.domain;

import java.util.UUID;

public class ToolTestSamples {

    public static Tool getToolSample1() {
        return new Tool().code("code1").brand("brand1");
    }

    public static Tool getToolSample2() {
        return new Tool().code("code2").brand("brand2");
    }

    public static Tool getToolRandomSampleGenerator() {
        return new Tool().code(UUID.randomUUID().toString()).brand(UUID.randomUUID().toString());
    }
}
