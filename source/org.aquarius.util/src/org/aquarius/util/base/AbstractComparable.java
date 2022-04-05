/**
 *
 */
package org.aquarius.util.base;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Provide basic comparable functions.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AbstractComparable<T> implements Comparable<T> {

	public static int Greatest = 16000;

	public static int Greater = 14000;

	public static int Great = 12000;

	public static int Highest = 10000;

	public static int Higher = 8000;

	public static int High = 6000;

	public static int Medium = 5000;

	public static int Low = 4000;

	public static int Lower = 2000;

	public static int Default = 1000;

	public static int Lowest = 0;

	private int priority = Default;

	/**
	 * @return the priority
	 */
	public final int getPriority() {
		return this.priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public final void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int compareTo(T otherObject) {

		if (!(otherObject instanceof AbstractComparable)) {
			return -1;
		}

		if (this.equals(otherObject)) {
			return 0;
		}

		int value = ((AbstractComparable<?>) otherObject).priority - this.priority;
		if (0 == value) {
			return 1;
		} else {
			return value;
		}
	}

	/**
	 * Return the priority for the name.<BR>
	 * 
	 * @param name
	 * @return
	 */
	public static int getPriority(String name) {

		return getPriority(name, Medium);
	}

	/**
	 * Return the priority for the name.<BR>
	 * 
	 * @param name
	 * @return
	 */
	public static int getPriority(String name, int defaultValue) {

		if (StringUtils.equalsIgnoreCase(name, "Greatest")) {
			return Greatest;
		}

		if (StringUtils.equalsIgnoreCase(name, "Greater")) {
			return Greater;
		}

		if (StringUtils.equalsIgnoreCase(name, "Great")) {
			return Great;
		}

		if (StringUtils.equalsIgnoreCase(name, "Highest")) {
			return Highest;
		}

		if (StringUtils.equalsIgnoreCase(name, "Higher")) {
			return Higher;
		}

		if (StringUtils.equalsIgnoreCase(name, "High")) {
			return High;
		}

		if (StringUtils.equalsIgnoreCase(name, "Low")) {
			return Low;
		}

		if (StringUtils.equalsIgnoreCase(name, "Lower")) {
			return Lower;
		}

		if (StringUtils.equalsIgnoreCase(name, "Lowest")) {
			return Lowest;
		}

		return NumberUtils.toInt(name, defaultValue);

	}

}
