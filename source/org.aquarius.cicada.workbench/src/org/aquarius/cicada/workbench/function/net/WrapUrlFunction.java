/**
 *
 */
package org.aquarius.cicada.workbench.function.net;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.aquarius.cicada.core.spi.AbstractDownloadUrlAnalyser;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * Use JSoup to get or post Data.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class WrapUrlFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public WrapUrlFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public WrapUrlFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(WrapUrlFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {

		if (ArrayUtils.isEmpty(arguments)) {
			return null;
		}

		if (arguments.length != 2) {
			return "This function should has 2 arguments,the first is url ,the second is segment to replace.";
		}

		String url = (String) arguments[0];
		String mark = (String) arguments[1];

		if (UrlValidator.getInstance().isValid(url)) {
			return AbstractDownloadUrlAnalyser.wrapUrl(url, mark);
		} else {
			return url + "  is not a valid url.";
		}
	}

}