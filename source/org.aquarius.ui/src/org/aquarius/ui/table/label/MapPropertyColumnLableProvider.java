/**
 *
 */
package org.aquarius.ui.table.label;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.aquarius.ui.table.base.AbstractPropertyColumnLabelProvider;

/**
 * Get a value for a property.<BR>
 * Then use a format to display it.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MapPropertyColumnLableProvider extends AbstractPropertyColumnLabelProvider {

	/**
	 * @param propertyName
	 * @param mapping
	 * @param pattern
	 */
	public MapPropertyColumnLableProvider(String propertyName, Map<Object, Object> mapping, String pattern) {
		super(propertyName, mapping, pattern);
	}

	/**
	 * @param propertyName
	 * @param mapping
	 */
	public MapPropertyColumnLableProvider(String propertyName, Map<Object, Object> mapping) {
		super(propertyName, mapping);
	}

	/**
	 * @param propertyName
	 * @param pattern
	 */
	public MapPropertyColumnLableProvider(String propertyName, String pattern) {
		super(propertyName, pattern);
	}

	/**
	 * @param propertyName
	 */
	public MapPropertyColumnLableProvider(String propertyName) {
		super(propertyName);
	}

	/**
	 * @param element
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	protected Object doGetPropertyValue(Object element) throws Exception {
		if (element instanceof Map) {
			Map<?, ?> map = (Map) element;
			return map.get(this.getPropertyName());
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getToolTipText(Object element) {
		if (this.isUseTooltip()) {
			return this.getText(element);
		} else {
			return super.getToolTipText(element);
		}
	}

}
