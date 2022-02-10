/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Shell;

/**
 * Close the current sit editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CloseWindowFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public CloseWindowFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public CloseWindowFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(CloseWindowFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {
		Shell shell = this.getBrowser().getShell();

		SwtUtil.closeShell(shell);

		return BrowserUtil.Success;
	}

}