/**
 *
 */
package org.aquarius.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Property reflection provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class PropertyUtil {

	/**
	 *
	 */
	private PropertyUtil() {
		// Just no instances needed.
	}

	/**
	 * Copy the specified property values from the source object to the target
	 * object.<BR>
	 * But if the property value is null,it won't be copied.<BR>
	 *
	 *
	 * @param source
	 * @param target
	 * @param names
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static void copyPropertiesWithoutNull(Object source, Object target, String... names)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		AssertUtil.assertNotNull(names);

		for (String name : names) {
			Object sourceValue = PropertyUtils.getProperty(source, name);

			if (null != sourceValue) {
				BeanUtils.copyProperty(target, name, sourceValue);
			}
		}
	}

}
