/**
 *
 */
package org.aquarius.downloader.core.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.util.StringUtil;
import org.aquarius.util.net.HttpUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class NetUtil {

	/**
	 * Fetch http info to build a download task.<BR>
	 *
	 * @param downloadTask
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static DownloadTask fetchHttpInfo(DownloadTask downloadTask, String urlString) throws IOException {
		HttpURLConnection httpConnection = prepareConnection(downloadTask, urlString);

		downloadTask.setDownloadUrl(urlString);
		downloadTask.setRemoteFileLength(httpConnection.getContentLengthLong());

		if (StringUtils.isBlank(downloadTask.getFileName())) {
			String contentDisposition = httpConnection.getHeaderField("Content-Disposition");

			if (StringUtils.isNotEmpty(downloadTask.getFileName())) {
				String fileName = StringUtils.substringAfter(contentDisposition, "filename=");

				if (StringUtils.isBlank(fileName)) {
					fileName = FilenameUtils.getName(urlString);
					fileName = StringUtil.getTitleForFile(fileName);
				}

				downloadTask.setFileName(fileName);
			}
		}

		IOUtils.close(httpConnection);

		return downloadTask;
	}

	/**
	 * Prepare a http connection.<BR>
	 *
	 * @param downloadTask
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection prepareConnection(DownloadTask downloadTask, String urlString) throws IOException {

//		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection();
//
//		httpURLConnection.setRequestMethod(HttpUtil.Get);
//
//		HttpUtil.updateHttpConnectionProperty(httpURLConnection, downloadTask.getRequestHeaders());
//
//		httpURLConnection.setInstanceFollowRedirects(true); // you still need to handle redirect manully.
//
//		return httpURLConnection;

		return HttpUtil.prepareHttpURLConnection(urlString, HttpUtil.Get, downloadTask.getRequestHeaders());
	}

	/**
	 * Parse and return the host.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public static String getHost(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		String hostString = url.getHost();
		return hostString;
	}

	private static final Pattern extraDotSegmentsPattern = Pattern.compile("^/((\\.{1,2}/)+)");

	private static final Pattern validUriScheme = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+-.]*:");

	/**
	 * Create a new absolute URL, from a provided existing absolute URL and a
	 * relative URL component.
	 *
	 * @param baseUrl the existing absolute base URL
	 * @param relUrl  the relative URL to resolve. (If it's already absolute, it
	 *                will be returned)
	 * @return an absolute URL if one was able to be generated, or the empty string
	 *         if not
	 */
	public static String resolve(final String baseUrl, final String relUrl) {
		try {
			URL base;
			try {
				base = new URL(baseUrl);
			} catch (MalformedURLException e) {
				// the base is unsuitable, but the attribute/rel may be abs on its own, so try
				// that
				URL abs = new URL(relUrl);
				return abs.toExternalForm();
			}
			return resolve(base, relUrl).toExternalForm();
		} catch (MalformedURLException e) {
			// it may still be valid, just that Java doesn't have a registered stream
			// handler for it, e.g. tel
			// we test here vs at start to normalize supported URLs (e.g. HTTP -> http)
			return validUriScheme.matcher(relUrl).find() ? relUrl : "";
		}
	}

	/**
	 * Create a new absolute URL, from a provided existing absolute URL and a
	 * relative URL component.
	 *
	 * @param base   the existing absolute base URL
	 * @param relUrl the relative URL to resolve. (If it's already absolute, it will
	 *               be returned)
	 * @return the resolved absolute URL
	 * @throws MalformedURLException if an error occurred generating the URL
	 */
	public static URL resolve(URL base, String relUrl) throws MalformedURLException {
		// workaround: java resolves '//path/file + ?foo' to '//path/?foo', not
		// '//path/file?foo' as desired
		if (relUrl.startsWith("?"))
			relUrl = base.getPath() + relUrl;
		// workaround: //example.com + ./foo = //example.com/./foo, not
		// //example.com/foo
		URL url = new URL(base, relUrl);
		String fixedFile = extraDotSegmentsPattern.matcher(url.getFile()).replaceFirst("/");
		if (url.getRef() != null) {
			fixedFile = fixedFile + "#" + url.getRef();
		}
		return new URL(url.getProtocol(), url.getHost(), url.getPort(), fixedFile);
	}

}
