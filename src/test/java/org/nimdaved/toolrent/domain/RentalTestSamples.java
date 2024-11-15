package org.nimdaved.toolrent.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RentalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Rental getRentalSample1() {
        return new Rental().id(1L).dayCount(1).discountPercent(1);
    }

    public static Rental getRentalSample2() {
        return new Rental().id(2L).dayCount(2).discountPercent(2);
    }

    public static Rental getRentalRandomSampleGenerator() {
        return new Rental()
            .id(longCount.incrementAndGet())
            .dayCount(intCount.incrementAndGet())
            .discountPercent(intCount.incrementAndGet());
    }
}
