package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public final class Randomiser {

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public static long rangeL(long start, long finish) {
        return RandomUtils.nextLong(start, finish);
    }

    public static long uptoL(long finish) {
        return rangeL(0, finish);
    }

    public static BigDecimal rangeD(BigDecimal start, BigDecimal finish) {
        return rangeD(start.doubleValue(), finish.doubleValue());
    }

    public static BigDecimal rangeD(double start, double finish) {
        return BigDecimal.valueOf(RandomUtils.nextDouble(start, finish));
    }

    public static BigDecimal uptoD(BigDecimal finish) {
        return rangeD(BigDecimal.ZERO, finish);
    }

    public static BigDecimal uptoD(double finish) {
        return rangeD(BigDecimal.ZERO, BigDecimal.valueOf(finish));
    }

    public static BigDecimal variation(BigDecimal start, double variationInPercentage) {
        BigDecimal absoluteVariation = start.multiply(BigDecimal.valueOf(variationInPercentage)).divide(HUNDRED);
        return rangeD(start.subtract(absoluteVariation), start.add(absoluteVariation));
    }

//    public static long percentL(long total, )
}
