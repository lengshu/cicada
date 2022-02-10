/**
 * 
 */
package org.aquarius.cicada.core.spi;

import org.apache.commons.lang.StringUtils;
import org.aquarius.service.INameService;
import org.aquarius.util.base.AbstractComparable;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public abstract class AbstractUrlRedirector extends AbstractComparable<AbstractUrlRedirector> implements INameService<AbstractUrlRedirector> {

	/**
	 * 
	 */
	public AbstractUrlRedirector() {
		super();
	}

	/**
	 * 
	 * @param urlStrings
	 */
	public abstract void redirect(String... urlStrings);

	/**
	 * 
	 */
	public abstract boolean isUsePrimitiveUrl();

	public static final boolean isUsePrimitive(String name) {
		return StringUtils.containsIgnoreCase(name, "Primitive");
	}
}
