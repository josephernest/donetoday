package com.als.util;

public class ObjectUtils {

    public static String toString(final Object o) {
        return o == null ? null : o.toString();
    }

    public static String toString(final Object o, final String defaultString) {
        return o == null ? defaultString : o.toString();
    }

    public static boolean equals(final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }

        if (o1 == null || o2 == null) {
            return false;
        }

        return o1.equals(o2);
    }

    public static <O> O coalesce(final O o, final O nullReplacement) {
        return o != null ? o : nullReplacement;
    }

    @SafeVarargs
    public static <O> O coalesce(final O o, final O or, final O... nullReplacement) {

        if (o != null) {
            return o;
        }

        if (or != null) {
            return or;
        }

        for (final O orr : nullReplacement) {
            if (orr != null) {
                return orr;
            }
        }

        return null;
    }

    public static int hashCode(final Object o) {
        return o == null ? 0 : o.hashCode();
    }
}
