/**
 *
 */
package org.aquarius.ui.table.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.log.LogUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.slf4j.Logger;

/**
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractPropertyColumnLabelProvider extends ColumnLabelProvider {

	private static Logger logger = LogUtil.getLogger(AbstractPropertyColumnLabelProvider.class);

	private String propertyName;

	private Map<Object, Object> mapping;

	private String pattern;

	private boolean useTooltip;

	/**
	 *
	 * @param propertyName
	 */
	public AbstractPropertyColumnLabelProvider(String propertyName) {
		this(propertyName, null, null);
	}

	/**
	 *
	 * @param propertyName
	 * @param pattern
	 */
	public AbstractPropertyColumnLabelProvider(String propertyName, String pattern) {
		this(propertyName, null, null);
	}

	/**
	 *
	 * @param propertyName
	 * @param mapping
	 */
	public AbstractPropertyColumnLabelProvider(String propertyName, Map<Object, Object> mapping) {
		this(propertyName, mapping, null);
	}

	/**
	 *
	 * @param propertyName
	 * @param mapping
	 * @param pattern
	 */
	public AbstractPropertyColumnLabelProvider(String propertyName, Map<Object, Object> mapping, String pattern) {
		this.propertyName = propertyName;
		this.mapping = mapping;
		if (null == mapping) {
			this.mapping = new HashMap<Object, Object>();
		}

		this.pattern = pattern;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {

		Object propertyValue;
		try {
			propertyValue = doGetPropertyValue(element);

			if (null == propertyValue) {
				return " ";
			}
			Object value = this.mapping.get(propertyValue);
			if (null == value) {
				return propertyValue.toString();
			}

			if (StringUtils.isEmpty(this.pattern)) {
				return ObjectUtils.toString(value);
			} else {
				return String.format(this.pattern, value);
			}
		} catch (Exception e) {

			logger.error("getText", e);

		}

		return "error";

	}

	/**
	 * @param element
	 * @return
	 * @throws Exception
	 */
	protected abstract Object doGetPropertyValue(Object element) throws Exception;

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getToolTipText(Object element) {
		if (this.useTooltip) {
			return this.getText(element);
		} else {
			return super.getToolTipText(element);
		}
	}

	/**
	 * @return the useTooltip
	 */
	public boolean isUseTooltip() {
		return this.useTooltip;
	}

	/**
	 * @param useTooltip the useTooltip to set
	 */
	public void setUseTooltip(boolean useTooltip) {
		this.useTooltip = useTooltip;
	}

}
