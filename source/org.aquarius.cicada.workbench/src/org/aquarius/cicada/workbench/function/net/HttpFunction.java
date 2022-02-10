/**
 *
 */
package org.aquarius.cicada.workbench.function.net;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.util.net.HttpUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;

/**
 * Use JSoup to get or post Data.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HttpFunction extends BrowserFunction {

	/**
	 * @param browser
	 * @param name
	 */
	public HttpFunction(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * @param browser
	 */
	public HttpFunction(Browser browser) {
		this(browser, BrowserUtil.getShortClassName(HttpFunction.class));
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
		SimpleHttpRequest request = null;

		try {
			request = JSON.parseObject(content, SimpleHttpRequest.class);
		} catch (Exception e) {
			// Nothing to do
		}

		if (null == request) {
			if (UrlValidator.getInstance().isValid(content)) {
				request = new SimpleHttpRequest();
				request.setUrl(content);
			} else {
				return BrowserUtil.Error;
			}
		}

		Connection connection = Jsoup.connect(request.getUrl());
		connection.ignoreHttpErrors(true);
		connection.ignoreContentType(true);

		if (MapUtils.isNotEmpty(request.getHeaders())) {
			connection.headers(request.getHeaders());
		}

		if (StringUtils.equalsIgnoreCase(HttpUtil.Post, request.getMethod())) {
			connection.method(Method.POST);
		} else {
			connection.method(Method.GET);
		}

		try {
			Response response = connection.execute();
			return response.body();
		} catch (Exception e) {
			return ExceptionUtils.getFullStackTrace(e);
		}
	}

}