/**
 *
 */
package org.aquarius.ui.table.label;

import java.text.DecimalFormat;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.log.LogUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.slf4j.Logger;

/**
 * Get a number value for a property.<BR>
 * Then use a format to display it.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NumberPropertyColumnLableProvider extends ColumnLabelProvider {

	private static Logger log = LogUtil.getLogger(NumberPropertyColumnLableProvider.class);

	private int unit = 0;

	private String propertyName;

	private DecimalFormat decimalFormat;

	/**
	 *
	 * @param propertyName
	 * @param pattern
	 */
	public NumberPropertyColumnLableProvider(String propertyName, String pattern) {
		this(propertyName, pattern, 0);
	}

	/**
	 *
	 * @param propertyName
	 * @param pattern
	 * @param unit
	 */
	public NumberPropertyColumnLableProvider(String propertyName, String pattern, int unit) {
		this.propertyName = propertyName;
		this.unit = unit;

		if (StringUtils.isNotBlank(pattern)) {
			this.decimalFormat = new DecimalFormat(pattern);
		}
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {
		Object propertyValue = doGetPropertyValue(element);

		if (null == this.decimalFormat) {
			return ObjectUtils.toString(propertyValue);
		}

		if (null == propertyValue) {
			return "null";
		}

		double numberValue = NumberUtils.toDouble(propertyValue.toString());
		if (this.unit != 0) {
			numberValue = numberValue / this.unit;
		}

		return this.decimalFormat.format(numberValue);

	}

	/**
	 * @param element
	 * @return
	 */
	private Object doGetPropertyValue(Object element) {
		if (null == element) {
			return null;
		} else {
			try {
				Object object = PropertyUtils.getProperty(element, this.propertyName);

				return object;

			} catch (Exception e) {
				log.error("doGetPropertyValue", e);
				return null;
			}
		}
	}

}
