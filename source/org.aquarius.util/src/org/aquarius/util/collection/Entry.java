/**
 *
 */
package org.aquarius.util.collection;

/**
 *
 * Entry element like Map.Entry
 *
 * @author aquarius.github@gmail.com
 *
 */
public class Entry<K, V> {

	private K key;

	private V value;

	/**
	 *
	 */
	public Entry() {
		// Default constructor
	}

	/**
	 * @param key
	 * @param value
	 */
	public Entry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public K getKey() {
		return this.key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String toString() {
		return "Entry [key=" + this.key + ", value=" + this.value + "]";
	}

}
