/**
 *
 */
package org.aquarius.ui.table.label;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.aquarius.log.LogUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;

/**
 * Use a map to store the relation between value and image.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ImageLabelProvider extends ColumnLabelProvider {

	private static Logger log = LogUtil.getLogger(ImageLabelProvider.class);

	private String propertyName;

	private Map<Object, Image> mapping;

	/**
	 * @param mapping
	 */
	public ImageLabelProvider(String propertyName, Map<Object, Image> mapping) {
		super();
		this.propertyName = propertyName;
		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Image getImage(Object element) {

		Object propertyValue = this.doGetPropertyValue(element);

		return this.mapping.get(propertyValue);
	}

	public Object doGetPropertyValue(Object element) {
		if (null == element) {
			return "";
		} else {
			try {
				Object object = PropertyUtils.getProperty(element, this.propertyName);

				return object;

			} catch (Exception e) {
				log.error("doGetPropertyValue", e);
				return "error";
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {
		return "";
	}

}
