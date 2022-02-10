/**
 * 
 */
package org.aquarius.ui.util;

import org.aquarius.util.AssertUtil;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Function provider for adapter.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class AdapterUtil {

	/**
	 * 
	 */
	private AdapterUtil() {
		super();
	}

	/**
	 * 
	 * @param <T>
	 * @param element
	 * @param adapterClass
	 * @return
	 */
	public static <T> T getAdapter(Object element, Class<T> adapterClass) {
		if (element == null) {
			return null;
		}

		AssertUtil.assertNotNull(adapterClass);

		if (adapterClass.isAssignableFrom(element.getClass())) {
			return (T) element;
		}

		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			return adaptable.getAdapter(adapterClass);
		}

		return null;

	}

}
