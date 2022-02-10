/**
 *
 */
package org.aquarius.cicada.workbench.function.common;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import com.alibaba.fastjson.JSON;

/**
 * Query all download generator names.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class QueryGeneratorNameListFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public QueryGeneratorNameListFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public QueryGeneratorNameListFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(QueryGeneratorNameListFunction.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object function(Object[] arguments) {
		List<String> nameList = RuntimeManager.getInstance().getAllDownloadListGeneratorNames();

		return JSON.toJSONString(nameList);
	}

}