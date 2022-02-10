/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
public class IsAcceptablePixelFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public IsAcceptablePixelFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public IsAcceptablePixelFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(IsAcceptablePixelFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {
		if (ArrayUtils.isEmpty(arguments)) {
			return null;
		}

		String pixel = ObjectUtils.toString(arguments[0]);

		if (StringUtils.isEmpty(pixel)) {
			return null;
		}

		if (!StringUtils.endsWithIgnoreCase(pixel, "P")) {
			pixel = pixel + "P";
		}

		return RuntimeManager.getInstance().getConfiguration().getPixelList().contains(pixel);
	}

}