/**
 * #protocol
 */
package org.aquarius.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.util.StringUtil;

/**
 * Http function provider.<BR>
 *
 * @author auqarius.github@hotmail.com
 *
 */
public class HttpUtil {

	// private static Logger logger = LogUtil.getLogger(HttpUtil.class);

	public static final String Location = "Location";

	public static final String Get = "GET";

	public static final String Post = "POST";

	public static final String SetCookie = "Set-Cookie";

	public static final String Cookie = "Cookie";

	public static final String Referer = "Referer";

	/**
	 * Access a url with get ,then return value in byte array format.<BR>
	 *
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static byte[] doGetByteArray(String urlString, Map<String, String> headers) throws IOException {

		return doGetByteArray(urlString, 15000, headers);
	}

	/**
	 * Access a url with get ,then return value in byte array format.<BR>
	 *
	 * @param urlString
	 * @param timeout
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static byte[] doGetByteArray(String urlString, int timeout, Map<String, String> headers) throws IOException {

		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(Get);
			connection.setReadTimeout(timeout);

			updateHttpConnectionHeader(connection, headers);

			connection.connect();
			if (connection.getResponseCode() == 200) {
				inputStream = connection.getInputStream();
				return IOUtils.toByteArray(inputStream);
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.close(connection);
		}

		return null;
	}

	/**
	 * Access a url with get ,then return value in string format.<BR>
	 *
	 * @param urlString
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static String doGet(String urlString, Map<String, String> headers) throws IOException {
		return new String(doGetByteArray(urlString, headers));
	}

	/**
	 * Access a url with get ,then return value in string format with specified
	 * encoding.<BR>
	 * 
	 * @param urlString
	 * @param encoding
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static String doGet(String urlString, String encoding, Map<String, String> headers) throws IOException {
		return new String(doGetByteArray(urlString, headers), encoding);
	}

	/**
	 * Access a url with post and parameter ,then return value in string format.<BR>
	 *
	 * @param urlString
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public static String doPost(String urlString, Map<String, String> headers) throws IOException {
		return doPost(urlString, null, null, headers);
	}

	/**
	 * Access a url with post and parameter ,then return value in string format with
	 * specified encoding.<BR>
	 *
	 * @param urlString
	 * @param param
	 * @param encoding
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static String doPost(String urlString, String param, String encoding, Map<String, String> headers) throws IOException {
		StringBuilder result = new StringBuilder();
		HttpURLConnection httpConnection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod(Post);
			httpConnection.setConnectTimeout(15000);
			httpConnection.setReadTimeout(15000);
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);

			// 设置通用的请求属性
			updateHttpConnectionHeader(httpConnection, headers);

			// 拼装参数
			if (StringUtils.isNotEmpty(param)) {
				// 设置参数
				outputStream = httpConnection.getOutputStream();
				// 拼装参数
				outputStream.write(param.getBytes(StringUtil.CODEING_UTF8));
			}
			// 设置权限
			// 设置请求头等
			// 开启连接
			httpConnection.connect();
			// 读取响应
			if (httpConnection.getResponseCode() == 200) {
				inputStream = httpConnection.getInputStream();

				if (StringUtils.isEmpty(encoding)) {
					return IOUtils.toString(inputStream);
				} else {
					return IOUtils.toString(inputStream, encoding);
				}

			}

		} finally {

			IOUtils.closeQuietly(inputStream);
			IOUtils.close(httpConnection);
		}

		return result.toString();
	}

	/**
	 * Get the file length for the url.<BR>
	 *
	 * @param urlString
	 * @param headers
	 * @return
	 */
	public static long getRemoteFileLength(String urlString, Map<String, String> headers) {
		HttpURLConnection httpConnection = null;

		try {

			httpConnection = prepareHttpURLConnection(urlString, Get, headers);

			return httpConnection.getContentLengthLong();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.close(httpConnection);
		}
	}

	public static HttpURLConnection prepareHttpURLConnection(String urlString, String method, Map<String, String> headers) throws IOException {
		HttpURLConnection httpConnection = null;

		URL url = new URL(urlString);
		// 创建连接
		httpConnection = (HttpURLConnection) url.openConnection();

		if (StringUtils.isNotBlank(method)) {
			httpConnection.setRequestMethod(method);
		} else {
			httpConnection.setRequestMethod(Get);
		}

		updateHttpConnectionHeader(httpConnection, headers);

		httpConnection.setInstanceFollowRedirects(true); // you still need to handle redirect manully.

		httpConnection.connect();

		boolean redirect = false;

		int status = httpConnection.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
		}

		if (redirect) {

			// get redirect url from "location" header field
			String newUrl = httpConnection.getHeaderField(Location);

			// get the cookie if need, for login
			String cookies = httpConnection.getHeaderField(SetCookie);

			httpConnection.disconnect();

			// open the new connnection again
			httpConnection = (HttpURLConnection) new URL(newUrl).openConnection();
			httpConnection.setRequestProperty(Cookie, cookies);

			updateHttpConnectionHeader(httpConnection, headers);

		}

		return httpConnection;
	}

	/**
	 * @param httpConnection
	 * @param headers
	 */
	private static void updateHttpConnectionHeader(HttpURLConnection httpConnection, Map<String, String> headers) {
		httpConnection.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		httpConnection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		httpConnection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
		// httpConnection.setRequestProperty("Connection", "keep-alive");
		httpConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

		// httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");

		updateHttpConnectionProperty(httpConnection, headers);

	}

	/**
	 * @param httpConnection
	 * @param headers
	 */
	public static void updateHttpConnectionProperty(HttpURLConnection httpConnection, Map<String, String> headers) {
		if (null != headers) {
			for (Entry<String, String> entry : headers.entrySet()) {

				if (StringUtils.isNotEmpty(entry.getValue())) {
					httpConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	public static String getRedirectUrl(String urlString) throws IOException {

		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(5000);
		return connection.getHeaderField(Location);
	}

	/**
	 * Generate parameters for post data.<BR>
	 *
	 * @param parameters
	 * @return
	 */
	public static String generateParameters(Map<String, String> parameters) {

		if (null == parameters) {
			return null;
		}

		StringJoiner stringJoiner = new StringJoiner("&");
		for (Map.Entry<String, String> entry : parameters.entrySet()) {

			if (StringUtils.isEmpty(entry.getValue())) {
				try {
					stringJoiner.add(
							URLEncoder.encode(entry.getKey(), StringUtil.CODEING_UTF8) + "=" + URLEncoder.encode(entry.getValue(), StringUtil.CODEING_UTF8));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}

		}
		return stringJoiner.toString();
	}

	/**
	 * 
	 * @param urlString
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static String queryRedirectUrl(String urlString, Map<String, String> headers) throws IOException {
		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(Get);
			connection.setReadTimeout(5000);

			updateHttpConnectionHeader(connection, headers);

			connection.connect();
			connection.setInstanceFollowRedirects(false);

			if (connection.getResponseCode() == 302) {
				return connection.getHeaderField(Location);
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.close(connection);
		}

		return null;
	}

	/**
	 *
	 * To validate whether the remote file is bigger the min length.<BR>
	 *
	 * @param urlString
	 * @param minRemoteLength
	 * @return if the return value is <code>false</code>,that means the url is not
	 *         accessible or less then the specified length.<BR>
	 */
	public static boolean validateUrlLength(String urlString, long minRemoteLength, Map<String, String> headers) {
		try {
			long remoteLength = HttpUtil.getRemoteFileLength(urlString, headers);
			// return remoteLength > minRemoteLength;
			if (remoteLength < 0) {
				return true;
			} else {
				return remoteLength > minRemoteLength;
			}
		} catch (Exception e) {
			// NOthing to do

			return false;
		}

	}

	/**
	 * Whether the url string is hls.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public static boolean isHls(String urlString) {
		try {
			URL url = new URL(urlString);

			String protocol = url.getProtocol();
			String path = url.getPath();

			boolean flag = StringUtils.equalsIgnoreCase(protocol, "http") || StringUtils.equalsIgnoreCase(protocol, "https");
			flag = flag && StringUtil.containsAnyIgnoreCase(path, "m3u8", "hls");
			return flag;

		} catch (Exception e) {
			boolean flag = StringUtils.startsWithAny(urlString, new String[] { "http://", "https://" });
			flag = flag && StringUtil.containsAnyIgnoreCase(urlString, "m3u8", "hls");
			return flag;
		}
	}

	/**
	 * Return whether the system is using proxy or not.<BR>
	 *
	 * @return
	 */
	public static boolean isUsingProxy() {
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");

		return StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort);
	}

	/**
	 * Get the host of a url.<BR>
	 * 
	 * @param urlString
	 * @return
	 */
	public static String getHost(String urlString) {
		try {
			URL url = new URL(urlString);
			return url.getHost();

		} catch (Exception e) {
			return urlString;
		}
	}
}