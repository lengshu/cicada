/**
 *
 */
package org.aquarius.cicada.core.spi.web;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.util.SystemUtil;

/**
 * Web pages is really complicated.<BR>
 * A lot of them can just run a web browser.<BR>
 * You can't reach the real contents by jsoup.<BR>
 * So a web accessor is designed to solve the problem.<BR>
 * I provide different implementations to support to parse web pages.<BR>
 * The default implementation is swt browser.<BR>
 * Chrome and jsoup implementations is also supported.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IWebAccessor {

	public static int DefaultWaitTimeMills = 10 * SystemUtil.TimeSecond;

	public static String DefaultResultName = "cicada_result";

	/**
	 * load a page with get method.
	 *
	 * @param urlString
	 * @param processMonitor TODO
	 * @return
	 */
	public boolean get(String urlString, IProcessMonitor processMonitor);

	/**
	 * in a specified page to execute some script to get result synchronized.
	 *
	 * @param scripts
	 * @return
	 */
	public Object syncExecuteScript(String... scripts);

	/**
	 * in a specified page to execute some script to get result asynchronized.
	 * 
	 * @param timeMillis
	 * @param resultName
	 * @param scripts
	 *
	 * @return
	 */
	public Object asyncExecuteScript(int timeMillis, String resultName, String... scripts);

	/**
	 * in a specified page to execute some script to get result asynchronized.
	 * 
	 * @param timeMillis
	 * @param scripts
	 *
	 * @return
	 */
	public default Object asyncExecuteScript(int timeMillis, String... scripts) {
		return asyncExecuteScript(DefaultWaitTimeMills, DefaultResultName, scripts);
	}

	/**
	 * in a specified page to execute some script to get result asynchronized.
	 *
	 * @param scripts
	 * @param timeMillis
	 * @return
	 */
	public default Object asyncExecuteScript(String... scripts) {
		return asyncExecuteScript(DefaultWaitTimeMills, DefaultResultName, scripts);
	}

	/**
	 * Return the html of the current page.<BR>
	 * 
	 * @return
	 */
	public String getHtmlSource();

	/**
	 * Return the html of the current page.<BR>
	 * 
	 * @return
	 */
	public default String getCurrentHtmlSource() {
		Object result = syncExecuteScript("return document.documentElement.outerHTML;");
		return ObjectUtils.toString(result);
	}

	/**
	 * load a page with post method and parameters.
	 *
	 * @param urlString
	 * @param parameters
	 * @return
	 */
	public Object post(String urlString, Map<String, String> parameters);

	/**
	 *
	 * @param name
	 * @param url
	 * @return
	 */
	public default String getCookie(String name, String url) {
		return null;
	}

	/**
	 *
	 * @param value
	 * @param url
	 * @return
	 */
	public default boolean setCookie(String value, String url) {
		return false;
	}

}
