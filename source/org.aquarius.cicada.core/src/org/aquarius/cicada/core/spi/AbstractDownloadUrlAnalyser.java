/**
 *
 */
package org.aquarius.cicada.core.spi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.result.AnalyserResult;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.service.INameService;
import org.aquarius.util.base.AbstractComparable;
import org.aquarius.util.mark.IUrlAcceptable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Some download urls needed to be anlysed to get real address.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractDownloadUrlAnalyser extends AbstractComparable<AbstractDownloadUrlAnalyser>
		implements IUrlAcceptable, INameService<AbstractDownloadUrlAnalyser> {

	/**
	 *
	 */
	public AbstractDownloadUrlAnalyser() {
		super();
		this.setPriority(Low);
	}

	/**
	 * Judge where the url can be anlysed to not.
	 *
	 * @param url
	 * @return
	 */
	public boolean isAcceptable(String url) {
		return StringUtils.containsIgnoreCase(url, this.getName());
	}

	/**
	 *
	 * @param link
	 * @param forExternal
	 * @param processMonitor TODO
	 * @throws IOException
	 */
	public void analyseDownloadUrl(Link link, boolean forExternal, IProcessMonitor processMonitor) throws IOException {

		String dynamicUrl = MovieUtil.getDownloadUrl(link);
		String referUrl = link.getRefererUrl();

		AnalyserResult analyserResult = this.analyseDownloadUrl(dynamicUrl, referUrl, forExternal, processMonitor);

		if (analyserResult.isHasError()) {
			link.setPriority(AbstractComparable.Lowest);
			link.setSelected(false);
			link.setValid(false);
			link.setErrorMessage(analyserResult.getErrorMessage());

		} else {

			if (StringUtils.equals(analyserResult.getUrl(), dynamicUrl)) {

				link.setPriority(AbstractComparable.Lowest);
				link.setSelected(false);

			} else {

				link.setPriority(this.getPriority());

				link.setDownloadUrl(analyserResult.getUrl());
				link.setHls(analyserResult.isHls());
				link.setConverted(true);
			}

			link.mergeRequestHeaders(analyserResult.getRequestHeaders());
		}
	}

	/**
	 *
	 * @param dynamicUrl
	 * @param referUrl
	 * @param forExternal
	 * @param processMonitor TODO
	 * @return
	 * @throws IOException
	 */
	public final AnalyserResult analyseDownloadUrl(String dynamicUrl, String referUrl, boolean forExternal, IProcessMonitor processMonitor) throws IOException {

		return this.doAnalyseDownloadUrl(dynamicUrl, referUrl, forExternal, processMonitor);
	}

	public abstract AnalyserResult doAnalyseDownloadUrl(String dynamicUrl, String referUrl, boolean forExternal, IProcessMonitor processMonitor)
			throws IOException;

	/**
	 *
	 * @param urlString
	 * @param referUrl
	 * @param retryCount
	 * @return
	 * @throws IOException
	 */
	protected Document loadPage(String urlString, String referUrl, int retryCount) throws IOException {

		retryCount = Integer.max(retryCount, 1);
		int count = 0;

		while (count < retryCount) {
			count++;

			try {
				return doLoadPage(urlString, referUrl);
			} catch (Exception e) {

				if (count == retryCount) {
					throw e;
				}
			}
		}

		return null;

	}

	/**
	 *
	 * @param urlString
	 * @param referUrl
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private Document doLoadPage(String urlString, String referUrl) throws UnsupportedEncodingException, IOException {

		Connection conn = Jsoup.connect(urlString).timeout(15000);

		if (StringUtils.isNotEmpty(referUrl)) {
			conn.referrer(referUrl);
		}

		conn.ignoreContentType(true);

		conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		conn.header("Accept-Language", "en-US,en;q=0.8");
		conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

		this.updateCookie(conn);
		this.updateHeader(conn);

		return conn.get();
	}

	/**
	 * @param conn
	 */
	protected void updateHeader(Connection conn) {
		// Nothing to do
	}

	/**
	 * @param conn
	 */
	protected void updateCookie(Connection conn) {
		// Nothing to do
	}

	/**
	 *
	 * @param dynamicUrl
	 * @return
	 */
	public static String wrapUrl(String dynamicUrl, String newSegment) {

		try {
			URL url = new URL(dynamicUrl);

			String path = url.getPath();
			String[] segments = StringUtils.split(path, "/");

			if (!newSegment.startsWith("/")) {
				newSegment = "/" + newSegment;
			}

			if (!newSegment.endsWith("/")) {
				newSegment = newSegment + "/";
			}

			return StringUtils.replace(dynamicUrl, "/" + segments[0] + "/", newSegment);

		} catch (MalformedURLException e) {
			return null;
		}
	}

}
