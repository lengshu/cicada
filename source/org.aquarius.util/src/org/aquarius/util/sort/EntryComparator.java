/**
 *
 */
package org.aquarius.util.sort;

import java.util.Comparator;

import org.apache.commons.collections4.MultiSet.Entry;

/**
 * @author aquarius.github@gmail.com
 * @param <V>
 *
 */
public final class EntryComparator<K> implements Comparator<Entry<K>> {

	/**
	 *
	 */
	public EntryComparator() {
		super();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int compare(Entry<K> object1, Entry<K> object2) {
		if ((null == object1) && (null == object2)) {
			return 0;
		}

		if (null == object1) {
			return -1;
		}

		if (null == object2) {
			return 1;
		}

		if (object1.equals(object2)) {
			return 0;
		}

		int value = object2.getCount() - object1.getCount();
		if (value == 0) {
			return -1;
		} else {
			return value;
		}
	}

}
