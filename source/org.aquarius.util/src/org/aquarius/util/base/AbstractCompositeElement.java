/**
 *
 */
package org.aquarius.util.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Designed to composite pattern.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractCompositeElement<T> {

	private List<T> nestedElements = new ArrayList<>();

	/**
	 *
	 * @return
	 */
	public int size() {
		return this.nestedElements.size();
	}

	/**
	 *
	 * @param t
	 * @return
	 */
	public boolean add(T t) {
		return this.nestedElements.add(t);
	}

	/**
	 *
	 * @param t
	 * @return
	 */
	public boolean remove(T t) {
		return this.nestedElements.remove(t);
	}

	/**
	 *
	 */
	public void clear() {
		this.nestedElements.clear();
	}

	/**
	 *
	 * @param index
	 * @return
	 */
	public T get(int index) {
		return this.nestedElements.get(index);
	}

}
