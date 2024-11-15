package org.nimdaved.toolrent.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HolidayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Holiday getHolidaySample1() {
        return new Holiday().id(1L).name("name1").monthNumber(1).dayNumber(1);
    }

    public static Holiday getHolidaySample2() {
        return new Holiday().id(2L).name("name2").monthNumber(2).dayNumber(2);
    }

    public static Holiday getHolidayRandomSampleGenerator() {
        return new Holiday()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .monthNumber(intCount.incrementAndGet())
            .dayNumber(intCount.incrementAndGet());
    }
}
