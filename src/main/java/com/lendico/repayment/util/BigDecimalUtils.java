package com.lendico.repayment.util;

import java.math.BigDecimal;

public final class BigDecimalUtils {

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final int HUNDRED = 100;
    public static final int PRECISION = 2;

    public static boolean isNegative(BigDecimal value) {
        return value.compareTo(ZERO) < 0;
    }

    public static boolean isZero(BigDecimal value) {
        return ZERO.equals(round(value, 0));
    }

    public static BigDecimal round(BigDecimal value, int precision) {
        return value.setScale(precision, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal divide(BigDecimal divident, BigDecimal divisor, int precision) {
        return divident.divide(divisor, precision, BigDecimal.ROUND_HALF_EVEN);
    }

    public static boolean isGreaterThan(BigDecimal value1, BigDecimal value2) {
        return value1.compareTo(value2) > 0;
    }

    public static boolean isLessThan(BigDecimal value1, BigDecimal value2) {
        return value1.compareTo(value2) < 0;
    }
}
