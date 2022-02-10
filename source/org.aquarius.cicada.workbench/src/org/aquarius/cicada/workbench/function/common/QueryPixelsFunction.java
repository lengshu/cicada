/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * Query default pixels.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class QueryPixelsFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public QueryPixelsFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public QueryPixelsFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(QueryPixelsFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {
		return RuntimeManager.getInstance().getConfiguration().getPixels();
	}

}