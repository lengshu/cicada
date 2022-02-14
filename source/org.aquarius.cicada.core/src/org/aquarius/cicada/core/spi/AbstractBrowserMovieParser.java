/**
 *
 */
package org.aquarius.cicada.core.spi;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.ScriptDefinition;
import org.aquarius.cicada.core.model.result.AbstractResult;
import org.aquarius.cicada.core.model.result.LinkResult;
import org.aquarius.cicada.core.model.result.MovieListResult;
import org.aquarius.cicada.core.model.result.MovieResult;
import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.aquarius.cicada.core.spi.web.IWebAccessorService;
import org.aquarius.cicada.core.spi.web.IWebAccessorService.Visitor;
import org.aquarius.util.PropertyUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.net.UrlUtil;

import com.alibaba.fastjson.JSON;

/**
 * Use browser to parse movie info.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractBrowserMovieParser extends AbstractMovieParser {

	private static int DefaultRetryCount = 10;

	private static final String[] MovieNames = new String[] { "title", "uniId", "category", "tag", "name", "actor", "publishDate", "imageUrl" };

	private int defaultWaitTime = 3 * SystemUtil.TimeSecond;

	protected abstract ScriptDefinition getParseListScriptDefinition();

	protected abstract ScriptDefinition getParseDetailScriptDefinition();

	protected abstract ScriptDefinition getParseLinkScriptDefinition();

	private static final String PreferedPixelsTemplate = "window.preferedPixels=\"{0}\";";

	private static final String IncludeDownloadInfoTemplate = "window.includeDownloadInfo={0};";

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public MovieListResult parseMovieListPage(String pageUrl, Movie lastVisitedMovie, final IProcessMonitor processMonitor) {

		final IProcessMonitor realProcessMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		Visitor<MovieListResult> visitor = (new IWebAccessorService.Visitor<MovieListResult>() {

			@Override
			public MovieListResult visit(IWebAccessor webAccessor) {

				String currrentPageUrl = MessageFormat.format(pageUrl, 1);

				List<String> parameterList = new ArrayList<>();

				if (null != lastVisitedMovie) {
					String lastUrl = lastVisitedMovie.getPageUrl();
					String lastUrlScript = "window['_last_url_']='" + lastUrl + "'";

					parameterList.add(lastUrlScript);
				}

				String parseListScript = getParseListScriptDefinition().getScript();
				parameterList.add(parseListScript);

				String[] parameters = new String[parameterList.size()];
				parameterList.toArray(parameters);

				MovieListResult movieListResult = parsePage(webAccessor, currrentPageUrl, parameters, null, MovieListResult.class, DefaultRetryCount,
						realProcessMonitor);

				return movieListResult;
			}

		});

		return WebAccessorHelper.visit(visitor);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean parseMovieDetailPage(Movie movie, final IProcessMonitor processMonitor, boolean includeDownloadInfo) {

		final IProcessMonitor realProcessMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		String preferedPixelsScript = MessageFormat.format(PreferedPixelsTemplate, RuntimeManager.getInstance().getConfiguration().getPixels());
		String includeDownloadInfoScript = MessageFormat.format(IncludeDownloadInfoTemplate, includeDownloadInfo);

		Visitor<Boolean> visitor = new Visitor<Boolean>() {

			@Override
			public Boolean visit(IWebAccessor webAccessor) {

				String moviePageUrl = movie.getPageUrl();
				String parseDetailScript = getParseDetailScriptDefinition().getScript();

				String[] scripts = new String[] { preferedPixelsScript, includeDownloadInfoScript, parseDetailScript };

				MovieResult movieResult = parsePage(webAccessor, moviePageUrl, scripts, null, MovieResult.class, DefaultRetryCount, realProcessMonitor);

				if (null == movieResult) {
					movie.setState(Movie.StateError);
					return false;
				}

				if (movieResult.isHasError()) {
					movie.setState(Movie.StateError);
					movie.setMemo(movieResult.getErrorMessage());
					movie.setDetailed(true);

					return false;
				}

				Movie newMovie = movieResult.getMovie();
				try {
					PropertyUtil.copyPropertiesWithoutNull(newMovie, movie, MovieNames);
				} catch (Exception e) {
					AbstractBrowserMovieParser.this.logger.error("copyPropertiesWithoutNull", e);
				}

				if (StringUtils.isNotBlank(movie.getName())) {
					movie.setName(movie.getTitle());
				}

				for (DownloadInfo downloadInfo : movieResult.getMovie().getDownloadInfoList()) {

					Set<URL> linkSet = new ListOrderedSet<>();
					List<Link> linkList = new ArrayList<>();

					for (Link link : downloadInfo.getDownloadLinks()) {
						try {

							String urlString = link.getSourceUrl();

							if (!StringUtils.startsWith(urlString, "http")) {
								break;
							}

							URL url = new URL(urlString);

							if (!linkSet.contains(url)) {
								linkList.add(link);
							} else {
								linkSet.add(url);
							}
						} catch (Exception e) {
							linkList.add(link);
							AbstractBrowserMovieParser.this.logger.error("remove duplicated links", e);
						}
					}

					downloadInfo.setDownloadLinks(linkList);
				}

				movie.setDownloadInfoList(movieResult.getMovie().getDownloadInfoList(), includeDownloadInfo);

				if (includeDownloadInfo) {
					doParseMovieDownloadInfo(webAccessor, movie);
				}

				if (movie.getState() == Movie.StateInit) {
					movie.setState(Movie.StateParsed);
				}

				movie.setDetailed(true);
				movie.setPageUrl(moviePageUrl);

				return true;
			}

			/**
			 *
			 * @param webAccessor
			 * @param movie
			 */
			private void doParseMovieDownloadInfo(IWebAccessor webAccessor, Movie movie) {
				List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();
				doParseDownloadLink(webAccessor, downloadInfoList, movie);

				if (processMonitor.isCanceled()) {
					return;
				}

				movie.setAlbumCount(downloadInfoList.size());
				Set<String> fileNames = new HashSet<>();

				MovieConfiguration movieConfiguration = RuntimeManager.getInstance().getConfiguration();

				for (int i = 0; i < downloadInfoList.size(); i++) {
					DownloadInfo downloadInfo = downloadInfoList.get(i);

					if (StringUtils.isEmpty(downloadInfo.getTitle())) {
						if (downloadInfoList.size() == 1) {
							downloadInfo.setTitle(movie.getTitle());
						} else {
							String title = movie.getTitle();
							char c = (char) (i + 65);
							title = title + "_" + Character.valueOf(c);
							downloadInfo.setTitle(title);
						}
					}

					if (StringUtils.isEmpty(downloadInfo.getMp4File()) || movieConfiguration.isUseTitleAsFileName()) {
						downloadInfo.setMp4File(downloadInfo.getTitle() + ".mp4");
					}

					int count = 0;
					String fileName = downloadInfo.getMp4File();
					while (fileNames.contains(fileName)) {

						fileName = FilenameUtils.removeExtension(fileName);
						fileName = fileName + Integer.toString(i) + "_" + count + ".mp4";

						downloadInfo.setMp4File(fileName);
					}

					fileNames.add(downloadInfo.getMp4File());
				}
			}

			/**
			 *
			 * @param webAccessor
			 * @param downloadInfoList
			 * @param movie
			 */
			private void doParseDownloadLink(IWebAccessor webAccessor, List<DownloadInfo> downloadInfoList, Movie movie) {

				if (!isParseLink()) {
					return;
				}

				String parseLinkScript = getParseLinkScriptDefinition().getScript();

				for (DownloadInfo downloadInfo : downloadInfoList) {
					List<Link> linkList = downloadInfo.getDownloadLinks();

					for (Link link : linkList) {

						if (processMonitor.isCanceled()) {
							return;
						}

						if (link.isIgnoreParseLink()) {
							continue;
						}

						doParseDownloadLink(processMonitor, webAccessor, parseLinkScript, link);
					}
				}
			}

			/**
			 * @param processMonitor
			 * @param webAccessor
			 * @param parseLinkScript
			 * @param link
			 * @return
			 */
			private void doParseDownloadLink(final IProcessMonitor processMonitor, IWebAccessor webAccessor, String parseLinkScript, Link link) {

				LinkResult linkResult = parsePage(webAccessor, link.getSourceUrl(), new String[] { parseLinkScript }, null, LinkResult.class, DefaultRetryCount,
						processMonitor);

				if ((null == linkResult) || linkResult.isHasError()) {
					link.setConverted(false);
					link.setSelected(false);

					if (null != linkResult) {
						link.setErrorMessage(linkResult.getErrorMessage());
					}
					return;
				}

				if (StringUtils.isNotEmpty(linkResult.getSourceUrl())) {
					link.setSourceUrl(linkResult.getSourceUrl());
				}

				if (StringUtils.isNotEmpty(linkResult.getErrorMessage())) {
					link.setErrorMessage(linkResult.getErrorMessage());
				}

				if (StringUtils.isNotEmpty(linkResult.getRefererUrl())) {
					link.setRefererUrl(linkResult.getRefererUrl());
				}

				if (StringUtils.isNotEmpty(linkResult.getDownloadUrl())) {
					link.setDownloadUrl(linkResult.getDownloadUrl());
				}

				link.mergeRequestHeaders(linkResult.getRequestHeaders());

			};
		};

		return WebAccessorHelper.visit(visitor);
	}

	/**
	 * @param movie
	 * @param includeDownloadInfo
	 */
	private static boolean isCacheValid(Movie movie, boolean includeDownloadInfo) {
		if (!movie.isOutOfValidTime()) {
			if (movie.isIncludeDownloadInfo() == includeDownloadInfo) {
				if (RuntimeManager.getInstance().getConfiguration().isUseDownloadInfoCache()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 *
	 * @param webAccessor
	 * @param urlString
	 * @param processMonitor
	 */
	protected void loadPage(IWebAccessor webAccessor, String urlString, IProcessMonitor processMonitor, int count) {

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("try " + count + " time to load page " + UrlUtil.decode(urlString));
		}

		webAccessor.get(urlString, processMonitor);

		WebAccessorHelper.sleepQuietly(this.getWaitTime(), processMonitor);
	}

	protected <T extends AbstractResult> T parsePage(IWebAccessor webAccessor, String urlString, String[] scripts, String checkFlag, Class<T> clazz,
			int retryCount, IProcessMonitor processMonitor) {

		retryCount = Math.max(1, retryCount);
		retryCount = Math.min(retryCount, DefaultRetryCount);
		boolean prohibitReloadPage = false;
		long waitTime = 3000;

		for (int i = 0; i < retryCount; i++) {

			try {

				if (processMonitor.isCanceled()) {
					return null;
				}

				if (!prohibitReloadPage) {
					loadPage(webAccessor, urlString, processMonitor, (i + 1));
				}

				String json = toString(webAccessor.syncExecuteScript(scripts));

				T result = JSON.parseObject(json, clazz);

				if (null == result) {
					break;
				}

				waitTime = result.getWaitTime();
				waitTime = Math.max(waitTime, 2000);

				String redirectUrl = result.getRedirectUrl();

				if (StringUtils.isNotBlank(redirectUrl)) {
					prohibitReloadPage = false;
					urlString = redirectUrl;
					continue;
				}

				prohibitReloadPage = result.isProhibitReloadPage();

				if (!result.isHasError()) {
					return result;
				}

				if (!result.isShouldRetry()) {
					return result;
				}

				SystemUtil.sleepQuietly(waitTime);

			} catch (Exception e) {
				e.printStackTrace();
				SystemUtil.sleepQuietly(waitTime);
			}

		}

		return null;
	}

	public static String toString(Object webResult) {
		String resultString = ObjectUtils.toString(webResult);
		resultString = StringUtils.replace(resultString, "‎", "");

		return resultString;
	}

	/**
	 *
	 * Some time, the browser need more time to load page.<BR>
	 * So the thread need to wait some time.<BR>
	 *
	 * @return
	 */
	protected int getWaitTime() {
		return this.defaultWaitTime;
	}

	/**
	 * Some site need to navigate new urls and parse it.
	 *
	 * @return
	 */
	protected boolean isParseLink() {
		return false;
	}

}
