package com.hillayes.mensa.auditor.utils;

public class EnumUtils {
    public static <T extends Enum<T>> T safeValueOf(Class<T> enums, String name) {
        return safeValueOf(enums.getEnumConstants(), name);
    }

    public static <T extends Enum<T>> T safeValueOf(T[] enums, String name) {
        for (T constant : enums) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }

        return null;
    }
}
