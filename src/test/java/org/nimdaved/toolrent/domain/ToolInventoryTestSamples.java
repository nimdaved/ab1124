package org.nimdaved.toolrent.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ToolInventoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ToolInventory getToolInventorySample1() {
        return new ToolInventory().id(1L).location("location1").stockCount(1).checkedOutCount(1).onHoldCount(1);
    }

    public static ToolInventory getToolInventorySample2() {
        return new ToolInventory().id(2L).location("location2").stockCount(2).checkedOutCount(2).onHoldCount(2);
    }

    public static ToolInventory getToolInventoryRandomSampleGenerator() {
        return new ToolInventory()
            .id(longCount.incrementAndGet())
            .location(UUID.randomUUID().toString())
            .stockCount(intCount.incrementAndGet())
            .checkedOutCount(intCount.incrementAndGet())
            .onHoldCount(intCount.incrementAndGet());
    }
}
