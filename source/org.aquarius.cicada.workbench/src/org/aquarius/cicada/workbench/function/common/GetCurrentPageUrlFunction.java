/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 *
 * Get the current page url.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class GetCurrentPageUrlFunction extends BrowserFunction {

	private String currentPageUrl;

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public GetCurrentPageUrlFunction(Browser browser, String name, String currentPageUrl) {
		super(browser, name);
		this.currentPageUrl = currentPageUrl;
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public GetCurrentPageUrlFunction(Browser browser, String currentPageUrl) {
		this(browser, BrowserUtil.getShortClassName(CloseSiteFunction.class), currentPageUrl);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object function(Object[] arguments) {
		return this.currentPageUrl;
	}

}
