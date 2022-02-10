/**
 *
 */
package org.aquarius.ui.binder;

/**
 * Access a object.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IValueAccessor {

	/**
	 * Get value from source object by specified property name.<BR>
	 *
	 * @param object
	 * @param propertyName
	 * @return
	 */
	public Object getValue(Object object, String propertyName);

	/**
	 * Set value to source object by specified property name.<BR>
	 *
	 * @param object
	 * @param propertyName
	 * @param value
	 */
	public void setValue(Object object, String propertyName, Object value);

}
