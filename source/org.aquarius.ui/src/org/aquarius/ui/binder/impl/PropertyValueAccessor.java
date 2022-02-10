/**
 *
 */
package org.aquarius.ui.binder.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.aquarius.ui.binder.IValueAccessor;

/**
 * Use system reflection to get value of source.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class PropertyValueAccessor implements IValueAccessor {

	/**
	 * Thread safe.<BR>
	 */
	public static final PropertyValueAccessor Instance = new PropertyValueAccessor();

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object getValue(Object object, String propertyName) {
		try {
			return PropertyUtils.getProperty(object, propertyName);
		} catch (Exception e) {
			throw new UnsupportedOperationException("can't access the property of " + propertyName, e);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(Object object, String propertyName, Object value) {
		try {
			PropertyUtils.setProperty(object, propertyName, value);
		} catch (Exception e) {
			throw new UnsupportedOperationException("can't access the property of " + propertyName, e);
		}
	}

}
