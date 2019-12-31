package com.lendico.repayment.util;

public final class ObjectUtils {

    public static boolean allNotNull(Object... values) {
        if (values == null) {
            return false;
        } else {
            for (Object val : values) {
                if (val == null) {
                    return false;
                }
            }
            return true;
        }
    }
}
