package com.als.util;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class StringUtils {

    public static <S extends CharSequence> S coalesceEmpty(final S cs, final S nullReplacement) {
        return isNullSpaceEmpty(cs) ? nullReplacement : cs;
    }

    public static int compare(final String s1, final String s2) {

        if (s1 == s2) {
            return 0;
        }

        if (s1 == null) {
            return -1;
        }

        if (s2 == null) {
            return 1;
        }

        return s1.compareTo(s2);
    }

    public static boolean equals(final String s1, final String s2) {
        return compare(s1, s2) == 0;
    }

    public static String upperCase(final String s) {
        return s == null ? null : s.toUpperCase(Locale.getDefault());
    }

    public static String lowerCase(final String s) {
        return s == null ? null : s.toLowerCase(Locale.getDefault());
    }

    public static String prependToSize(final CharSequence s, final char c, final int size) {

        final StringBuilder sb = new StringBuilder(Math.max(size, s == null ? 0 : s.length()));

        final int fill = size - (s == null ? 0 : s.length());
        for (int i = 0; i < fill; i++) {
            sb.append(c);
        }

        sb.append(s == null ? "" : s);

        return sb.toString();
    }

    public static String prependIfNotEmpty(final CharSequence c, final CharSequence s) {

        if (s == null) {
            return null;
        }

        final String sstr = s.toString();
        if (isNullSpaceEmpty(sstr)) {
            return sstr;
        }
        return "" + c + sstr;
    }

    public static String appendIfNotEmpty(final CharSequence s, final char c) {

        if (s == null) {
            return null;
        }

        final String sstr = s.toString();
        if (isNullSpaceEmpty(sstr)) {
            return sstr;
        }
        return sstr + c;
    }

    public static String appendToSize(final CharSequence s, final char c, final int size) {

        final StringBuilder sb = new StringBuilder(Math.max(size, s == null ? 0 : s.length()));

        sb.append(s == null ? "" : s);

        final int fill = size - (s == null ? 0 : s.length());
        for (int i = 0; i < fill; i++) {
            sb.append(c);
        }

        return sb.toString();
    }

    public static String lowerInitial(final String s) {
        return s == null ? null //
                : s.length() == 0 ? s //
                        : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String upperInitial(final String s) {
        return s == null ? null //
                : s.length() == 0 ? s //
                        : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String limit(final String s, final int limit) {
        if (s.length() <= limit) {
            return s;
        }

        return s.substring(0, limit);
    }

    public static String urlEncode(final String s) {

        return Uri.encode(s) //
                .replaceAll("'", "%27")

        ;

        // _-!.~'()*
    }

    public static boolean isNullSpaceEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0 || cs.toString().trim().length() == 0;
    }

    public static String times(final String string, final String delim, final int size) {
        final StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < size; i++) {
            if (sb.length() > 0) {
                sb.append(delim);
            }
            sb.append(string);
        }

        return sb.toString();
    }

    public static String trim(final String name) {
        if (name == null) {
            return null;
        }
        return name.trim();
    }

    public static byte[] toAsciiBytes(String s) {
        try {
            return s.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            Logr.ignored(e);
        }
        return null;
    }
}
