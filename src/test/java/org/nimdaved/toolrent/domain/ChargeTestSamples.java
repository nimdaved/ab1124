package org.nimdaved.toolrent.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ChargeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Charge getChargeSample1() {
        return new Charge().id(1L);
    }

    public static Charge getChargeSample2() {
        return new Charge().id(2L);
    }

    public static Charge getChargeRandomSampleGenerator() {
        return new Charge().id(longCount.incrementAndGet());
    }
}
