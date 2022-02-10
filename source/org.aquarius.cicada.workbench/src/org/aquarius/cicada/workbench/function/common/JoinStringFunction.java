/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 *
 * Join string.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class JoinStringFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public JoinStringFunction(Browser browser) {
		super(browser, "joinString");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object function(Object[] arguments) {

		return StringUtils.join(arguments, "\r\n");
	}

}
