package org.nimdaved.toolrent.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RentalAgreementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RentalAgreement getRentalAgreementSample1() {
        return new RentalAgreement().id(1L);
    }

    public static RentalAgreement getRentalAgreementSample2() {
        return new RentalAgreement().id(2L);
    }

    public static RentalAgreement getRentalAgreementRandomSampleGenerator() {
        return new RentalAgreement().id(longCount.incrementAndGet());
    }
}
