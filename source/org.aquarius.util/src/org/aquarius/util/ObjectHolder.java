/**
 *
 */
package org.aquarius.util;

/**
 * Sometime ,you can't return the value from a thread.<BR>
 * So a holder is designed to pass value between different threads.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class ObjectHolder<T> {

	private T value;

	private boolean updated;

	/**
	 * 
	 */
	public ObjectHolder() {
		super();
	}

	/**
	 * @param value
	 */
	public ObjectHolder(T value) {
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(T value) {
		this.value = value;
		this.updated = true;
	}

	/**
	 * @return the updated
	 */
	public boolean isUpdated() {
		return this.updated;
	}

}
