package com.als.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionUtils {

	public static String concat(final Collection<?> col, final String delim) {

		final StringBuilder sb = new StringBuilder("");

		final Iterator<?> i = col.iterator();

		while (i.hasNext()) {
			if (sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(i.next());
		}

		return sb.toString();
	}

	public static String concat(final String delim, final int[] col) {

		final StringBuilder sb = new StringBuilder("");

		for (final Object o : col) {
			if (sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(o);
		}

		return sb.toString();
	}

	public static String concat(final String delim, final long[] col) {

		final StringBuilder sb = new StringBuilder("");

		for (final Object o : col) {
			if (sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(o);
		}

		return sb.toString();
	}

	public static String concat(final String delim, final Object... col) {

		final StringBuilder sb = new StringBuilder("");

		for (final Object o : col) {
			if (sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(o);
		}

		return sb.toString();
	}

	public static String concatNonNull(final String delim, final Object... col) {

		final StringBuilder sb = new StringBuilder("");

		for (final Object o : col) {
			if (o != null) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(o);
			}
		}

		return sb.toString();
	}

	public static String concatNonEmpty(final String delim, final Object... col) {

		final StringBuilder sb = new StringBuilder("");

		for (final Object o : col) {
			if (o != null && !StringUtils.isNullSpaceEmpty(o.toString())) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(o);
			}
		}

		return sb.toString();
	}

	public static <E> List<E> collect(final Iterable<E> i) {

		final List<E> ret = new ArrayList<E>();

		for (final E e : i) {
			ret.add(e);
		}

		return ret;

	}
}