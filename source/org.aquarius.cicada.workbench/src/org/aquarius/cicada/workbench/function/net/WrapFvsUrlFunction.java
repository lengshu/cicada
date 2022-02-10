/**
 *
 */
package org.aquarius.cicada.workbench.function.net;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.aquarius.cicada.core.spi.base.AbstractFvsDownloadUrlAnalyser;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * Use JSoup to get or post Data.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class WrapFvsUrlFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public WrapFvsUrlFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public WrapFvsUrlFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(WrapFvsUrlFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {

		if (ArrayUtils.isEmpty(arguments)) {
			return null;
		}

		String content = (String) arguments[0];

		if (UrlValidator.getInstance().isValid(content)) {
			return AbstractFvsDownloadUrlAnalyser.wrapUrl(content);
		} else {
			return content + "  is not a valid url.";
		}
	}

}