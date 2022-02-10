/**
 *
 */
package org.aquarius.util.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.StringUtil;

/**
 * Url function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UrlUtil {

	/**
	 *
	 */
	private UrlUtil() {
		// No instances needed
	}

	/**
	 * Return the base path for a url string.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public static String getBasePath(String urlString) {
		return StringUtils.substringBeforeLast(urlString, "/") + "/";
	}

	/**
	 * decode a url.<BR>
	 * 
	 * @param urlString
	 * @return
	 */
	public static String decode(String urlString) {
		if (StringUtils.isEmpty(urlString)) {
			return urlString;
		}

		try {
			return URLDecoder.decode(urlString, StringUtil.CODEING_UTF8);
		} catch (Exception e) {
			return urlString;
		}
	}

	/**
	 * Return the domain name for the given url string.<BR>
	 * 
	 * @param urlString
	 * @return
	 * @throws MalformedURLException
	 */
	public static String getDomain(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);

		String host = url.getHost();

		while (StringUtils.countMatches(host, ".") > 1) {
			host = StringUtils.substringAfter(host, ".");
		}

		return host;
	}

}
