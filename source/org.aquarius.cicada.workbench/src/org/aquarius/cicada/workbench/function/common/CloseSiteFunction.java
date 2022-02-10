/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * Close the current sit editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CloseSiteFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public CloseSiteFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public CloseSiteFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(CloseSiteFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {
		SwtUtil.closeActiveEditor(true);

		return BrowserUtil.Success;
	}

}