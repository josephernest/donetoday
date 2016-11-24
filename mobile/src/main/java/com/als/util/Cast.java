package com.als.util;

public class Cast {

    /**
     * Helps to avoid using {@code @SuppressWarnings({"unchecked"})} when casting to a generic type.
     * see http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T unchecked(final Object obj) {
        return (T) obj;
    }


    /**
     * Helps to avoid using {@code @SuppressWarnings({"unchecked"})} when casting to a generic type.
     * see http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
     */
    public static <T> T unchecked(final Object obj, final T returnInCaseOfExceptions) {
        try {
            return unchecked(obj);
        } catch (Exception e) {
            Logr.ignored(e);
            return returnInCaseOfExceptions;
        }
    }
}
