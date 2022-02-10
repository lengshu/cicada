/**
 *
 */
package org.aquarius.cicada.core.helper;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.aquarius.cicada.core.spi.web.IWebAccessorService;
import org.aquarius.cicada.core.spi.web.IWebAccessorService.Visitor;
import org.aquarius.cicada.core.web.WebAccessorManager;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.SystemUtil;

/**
 * Web accessor helper to simply operations.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class WebAccessorHelper {

	/**
	 *
	 */
	private WebAccessorHelper() {
		// No instances needed.
	}

	/**
	 * Sleep quietly without exception
	 *
	 * @param sleepTime
	 * @param processMonitor
	 */
	public static void sleepQuietly(long sleepTime, IProcessMonitor processMonitor) {

		long currentTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - currentTime) <= sleepTime) {

			if (processMonitor.isCanceled()) {
				return;
			}

			SystemUtil.sleepQuietly(SystemUtil.NumberHundred);
		}

	}

	/**
	 * Load a webpage and execute script with get method.
	 * 
	 * @param urlString
	 * @param processMonitor
	 * @param scripts
	 * @return
	 */
	public static String syncExecuteScript(String urlString, IProcessMonitor processMonitor, String... scripts) {
		return syncExecuteScript(null, urlString, processMonitor, scripts);
	}

	/**
	 * Load a webpage and execute script asynchronized.
	 * 
	 * @param urlString
	 * @param processMonitor
	 * @param scripts
	 * @return
	 */
	public static String asyncExecuteScript(String urlString, IProcessMonitor processMonitor, String... scripts) {
		return asyncExecuteScript(null, urlString, IWebAccessor.DefaultWaitTimeMills, IWebAccessor.DefaultResultName, processMonitor, scripts);
	}

	/**
	 * Load a webpage and execute script asynchronized.
	 * 
	 * @param name
	 * @param urlString
	 * @param timeMillis
	 * @param resultName
	 * @param scripts
	 * @return
	 */
	public static String asyncExecuteScript(String name, String urlString, int timeMillis, String resultName, IProcessMonitor processMonitor,
			String... scripts) {
		IWebAccessorService webAccessorService = findWebAccessService(name);

		return webAccessorService.visit(new Visitor<String>() {

			@Override
			public String visit(IWebAccessor webAccessor) {
				webAccessor.get(urlString, processMonitor);

				SystemUtil.sleepQuietly(IWebAccessor.DefaultWaitTimeMills);

				Object result = webAccessor.asyncExecuteScript(timeMillis, resultName, scripts);

				return ObjectUtils.toString(result);
			}
		});
	}

	/**
	 * Return the html content of the url.<BR>
	 * 
	 * @param urlString
	 * @param processMonitor
	 * @return
	 */
	public static String getHtml(String urlString, IProcessMonitor processMonitor) {
		return getHtml(null, urlString, processMonitor);
	}

	/**
	 * Use the specified web accessor to get the html content of the url.<BR>
	 * 
	 * @param name
	 * @param urlString
	 * @param processMonitor
	 * @return
	 */
	public static String getHtml(String name, String urlString, IProcessMonitor processMonitor) {
		IWebAccessorService webAccessorService = findWebAccessService(name);

		return webAccessorService.visit(new Visitor<String>() {

			@Override
			public String visit(IWebAccessor webAccessor) {
				webAccessor.get(urlString, processMonitor);

				SystemUtil.sleepQuietly(IWebAccessor.DefaultWaitTimeMills);

				return webAccessor.getHtmlSource();
			}
		});
	}

	/**
	 * Load a webpage and execute script with get method by a specified web
	 * accessor.
	 *
	 * @param name
	 * @param urlString
	 * @param script
	 * @return
	 */
	public static String syncExecuteScript(String name, String urlString, IProcessMonitor processMonitor, String... script) {

		IWebAccessorService webAccessorService = findWebAccessService(name);

		return webAccessorService.visit(new Visitor<String>() {

			@Override
			public String visit(IWebAccessor webAccessor) {
				webAccessor.get(urlString, processMonitor);

				SystemUtil.sleepQuietly(IWebAccessor.DefaultWaitTimeMills);

				Object result = webAccessor.syncExecuteScript(script);

				return ObjectUtils.toString(result);
			}
		});
	}

	/**
	 * @param name if the parameter is null,the default web accessor will be used.
	 * @return
	 */
	private static IWebAccessorService findWebAccessService(String name) {
		IWebAccessorService webAccessorServie;

		if (StringUtils.isEmpty(name)) {
			webAccessorServie = WebAccessorManager.getInstance().getDefaultWebAccessorService();
		} else {
			webAccessorServie = WebAccessorManager.getInstance().findService(name);
			AssertUtil.assertNotNull(webAccessorServie, "The accessor manager is not founded.");
		}
		return webAccessorServie;
	}

	/**
	 * Load a webpage with post method.
	 *
	 * @param urlString
	 * @param parameters
	 * @return
	 */
	public static String postPage(String urlString, Map<String, String> parameters) {
		return postPage(null, urlString, parameters);
	}

	/**
	 * Load a webpage with post method by a specified web accessor.
	 *
	 * @param name
	 * @param urlString
	 * @param parameters
	 * @return
	 */
	public static String postPage(String name, String urlString, Map<String, String> parameters) {
		IWebAccessorService webAccessorService = findWebAccessService(name);

		return webAccessorService.visit(new Visitor<String>() {

			@Override
			public String visit(IWebAccessor webAccessor) {

				Object result = webAccessor.post(urlString, parameters);

				return ObjectUtils.toString(result);
			}
		});
	}

	/**
	 * Run a visitor by default web accessor.
	 *
	 * @param <T>
	 * @param visitor
	 * @return
	 */
	public static <T> T visit(Visitor<T> visitor) {
		return visit(null, visitor);
	}

	/**
	 * Run a visitor by a specified web accessor.
	 *
	 * @param <T>
	 * @param name
	 * @param visitor
	 * @return
	 */
	public static <T> T visit(String name, Visitor<T> visitor) {
		IWebAccessorService webAccessorService = findWebAccessService(name);

		return webAccessorService.visit(visitor);
	}

	/**
	 * Open a blank browser for setting.
	 */
	public static void openBrowserForSetting() {
		IWebAccessorService webAccessorServie = WebAccessorManager.getInstance().getDefaultWebAccessorService();
		webAccessorServie.openBrowserForSetting();
	}

	/**
	 * Open browsers for setting different pages
	 *
	 * @param urlStrings
	 */
	public static void openBrowserForSetting(String... urlStrings) {

		if (ArrayUtils.isEmpty(urlStrings)) {
			return;
		}

		IWebAccessorService webAccessorServie = WebAccessorManager.getInstance().getDefaultWebAccessorService();
		for (String urlString : urlStrings) {
			webAccessorServie.openBrowserForSetting(urlString);
		}

	}

}
