/**
 *
 */
package org.aquarius.cicada.core.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.result.AnalyserResult;
import org.aquarius.cicada.core.spi.AbstractDownloadUrlAnalyser;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.log.LogUtil;
import org.slf4j.Logger;

/**
 * Helper to simplify download anlyser operation.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieDownloadAnalyserHelper {

	private static Logger logger = LogUtil.getLogger(MovieDownloadAnalyserHelper.class);

	/**
	 *
	 */
	private MovieDownloadAnalyserHelper() {
		// No instances needed
	}

	/**
	 *
	 * @param dynamicUrl
	 * @param refererUrl
	 * @param forExternal
	 * @param processMonitor TODO
	 * @return
	 * @throws IOException
	 */
	public static AnalyserResult analyseDownloadUrl(String dynamicUrl, String refererUrl, boolean forExternal, IProcessMonitor processMonitor)
			throws IOException {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		AbstractDownloadUrlAnalyser downloadUrlAnalyser = RuntimeManager.getInstance().findDownloadUrlAnalyserr(dynamicUrl);

		if (null == downloadUrlAnalyser) {
			return new AnalyserResult(dynamicUrl, null);
		}

		return downloadUrlAnalyser.analyseDownloadUrl(dynamicUrl, refererUrl, forExternal, processMonitor);
	}

	/**
	 *
	 * @param downloadUrls
	 * @param refererUrl
	 * @param forExternal
	 * @param processMonitor TODO
	 * @return
	 */
	public static List<AnalyserResult> analyseDownloadUrlList(List<String> downloadUrls, String refererUrl, boolean forExternal,
			IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		List<AnalyserResult> resultList = new ArrayList<>();

		for (String downloadUrl : downloadUrls) {
			AnalyserResult analyserResult;
			try {
				analyserResult = analyseDownloadUrl(downloadUrl, refererUrl, forExternal, processMonitor);
				resultList.add(analyserResult);
			} catch (IOException e) {
				logger.error("Analyse Download Url List ", e);
			}
		}

		return resultList;
	}

	/**
	 *
	 * @param link
	 * @param forExternal
	 * @param processMonitor TODO
	 * @throws IOException
	 */
	public static void analyseDownloadUrl(Link link, boolean forExternal, IProcessMonitor processMonitor) throws IOException {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		String dynamicUrl = MovieUtil.getDownloadUrl(link);

		AbstractDownloadUrlAnalyser downloadUrlAnalyser = RuntimeManager.getInstance().findDownloadUrlAnalyserr(dynamicUrl);

		if (null == downloadUrlAnalyser) {
			return;
		}

		downloadUrlAnalyser.analyseDownloadUrl(link, forExternal, processMonitor);
	}

	/**
	 *
	 * @param downloadInfoList
	 * @param forExternal
	 * @param processMonitor
	 */
	public static void analyseDownloadUrls(List<DownloadInfo> downloadInfoList, boolean forExternal, IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		for (DownloadInfo downloadInfo : downloadInfoList) {

			List<Link> links = new ArrayList<>(downloadInfo.getDownloadLinks());

			for (Link link : links) {
				link.setConverted(false);

				try {
					analyseDownloadUrl(link, forExternal, processMonitor);
				} catch (Exception e) {
					logger.error("analyse download urlPattern", e);
				}
			}

			Collections.sort(downloadInfo.getDownloadLinks());
		}
	}

	/**
	 *
	 * @param movieList
	 * @param forExternal
	 * @param processMonitor
	 */
	public static void analyseMovieDownloadUrls(List<Movie> movieList, boolean forExternal, IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		for (Movie movie : movieList) {
			List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();
			analyseDownloadUrls(downloadInfoList, forExternal, processMonitor);
		}

	}
}
