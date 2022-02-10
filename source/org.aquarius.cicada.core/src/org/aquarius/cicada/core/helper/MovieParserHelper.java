/**
 *
 */
package org.aquarius.cicada.core.helper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.filter.MovieChannelFilter;
import org.aquarius.cicada.core.helper.filter.NullMovieChannelFilter;
import org.aquarius.cicada.core.impl.condition.DuplicatedUrlsCondition;
import org.aquarius.cicada.core.impl.condition.ICondition;
import org.aquarius.cicada.core.impl.condition.SiteChannelCondition;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.VisitHistory;
import org.aquarius.cicada.core.model.result.MovieListResult;
import org.aquarius.cicada.core.model.result.MovieRecursionResult;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.spi.AbstractMovieInfoProcssor;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.util.NumberUtil;
import org.aquarius.util.net.UrlUtil;

/**
 * Helper class to simple parser operation.
 *
 * @author aquarius.github@gmail.com
 *
 */

public class MovieParserHelper {

	public static final int NextPageJumpStep = 10;

	public static final int ExhausePageJumpStep = 200;

	/**
	 *
	 */
	private MovieParserHelper() {
		// No instances needed
	}

	/**
	 * parse movie info .<BR>
	 *
	 * @param includeDownloadInfo the parameter to parse download info.<BR>
	 *                            Some site needed to navigate new urls to parse
	 *                            download info.
	 * @param movie
	 * @param processMonitor
	 */
	public static boolean parseMovieDetailInfo(boolean includeDownloadInfo, Movie movie, IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		AbstractMovieParser movieParser = RuntimeManager.getInstance().findMovieParserByUrl(movie.getPageUrl());

		if (StringUtils.isNotEmpty(movie.getSite())) {
			String message = RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.MovieParserHelper_ResourceParseMovieInfo);
			String title = movie.getTitle();
			String site = movie.getSite();

			if (null == site) {
				site = "";
			}

			if (null == title) {
				title = "";
			}

			processMonitor.setName(MessageFormat.format(message, site, title, UrlUtil.decode(movie.getPageUrl())));
		} else {
			String message = RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.MovieParserHelper_ResourceParseMovieInfo2);
			processMonitor.setName(MessageFormat.format(message, movie.getPageUrl()));
		}

		return movieParser.parseMovieDetailPage(movie, processMonitor, includeDownloadInfo);
	}

	/**
	 * Parse movie detail info
	 *
	 * @param includeDownloadInfo
	 * @param movieList
	 * @param processMonitor
	 */
	public static String parseMovieDetailInfo(boolean includeDownloadInfo, List<Movie> movieList, IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);
		int maxErrorCount = RuntimeManager.getInstance().getConfiguration().getMaxErrorCount();

		for (Movie movie : movieList) {
			if (processMonitor.isCanceled()) {
				return null;
			}
			boolean success = parseMovieDetailInfo(includeDownloadInfo, movie, processMonitor);
			if (!success) {
				maxErrorCount--;
			}

			try {
				AbstractMovieInfoProcssor movieTagProcssor = RuntimeManager.getInstance().findMovieTagProcessorByUrl(movie.getPageUrl());
				if (null != movieTagProcssor) {
					movieTagProcssor.process(movie);
				}
			} catch (Exception e) {
				// Nothing to do
			}

			if (maxErrorCount < 0) {
				return RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.MovieParserHelper_MayBeBlocked);
			}
		}

		return null;
	}

	/**
	 * Parse a page to get movie list.<BR>
	 *
	 * @param currentPageUrl
	 * @param processMonitor
	 * @return
	 */
	public static MovieRecursionResult parseMovieList(String currentPageUrl, IProcessMonitor processMonitor) {
		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		AbstractMovieParser movieParser = RuntimeManager.getInstance().findMovieParserByUrl(currentPageUrl);

		MovieRecursionResult movieListResult = new MovieRecursionResult();
		MovieListResult movieListWebPage = movieParser.parseMovieListPage(currentPageUrl, null, processMonitor);

		movieListResult.addMovies(movieListWebPage.getMovieList());

		return movieListResult;
	}

	/**
	 * Parse a site to import movie list.<BR>
	 *
	 * @param site
	 * @param processMonitor
	 */
	public static void parseSite(Site site, IProcessMonitor processMonitor) {
		parseSite(site, processMonitor, null);
	}

	/**
	 * Parse a site to import movie list.<BR>
	 *
	 * @param site
	 * @param processMonitor
	 * @param movieChannelFilter
	 */
	public static void parseSite(Site site, IProcessMonitor processMonitor, MovieChannelFilter movieChannelFilter) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);
		AbstractMovieParser movieParser = site.getMovieParser();

		List<MovieChannel> channelList = movieParser.getChannels();

		if (null == movieChannelFilter) {
			movieChannelFilter = NullMovieChannelFilter.getInstance();
		}

		for (MovieChannel movieChannel : channelList) {

			if (movieChannelFilter.isFilter(movieChannel)) {
				continue;
			}

			VisitHistory visitHistory = parseChannel(site, movieChannel, processMonitor);
			// Save history to avoid parse unnecessary update.

			if (processMonitor.isCanceled()) {
				break;
			}

			if (null != visitHistory) {
				visitHistory.setLastUpdate(new Date());

				if (visitHistory.getLastVisitPageNumber() != null) {
					RuntimeManager.getInstance().getStoreService().insertOrUpdateVisitHistory(visitHistory);
				}
			}

			if (processMonitor.isCanceled()) {
				break;
			}
		}
	}

	/**
	 * Parse a channel.<BR>
	 *
	 * @param site
	 * @param channel
	 * @param processMonitor
	 * @return
	 */
	public static VisitHistory parseChannel(Site site, MovieChannel channel, IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);
		AbstractMovieParser movieParser = site.getMovieParser();
		VisitHistory visitHistory = prepare(site, channel, processMonitor);
		// Query history to avoid full update.

		if ((processMonitor.isCanceled()) || (null == visitHistory)) {
			return null;
		}

		List<Range> rangeList = buildRangeList(visitHistory, NextPageJumpStep);
		// There are so many pages to query.
		// So all the pages will be split in much ranges as order desc.
		// The range should be like [300,400] [200,300],[100,200]
		// There a page cross the near ranges to solve movie update problem because some
		// site may update movies while parsing.

		ICondition urlsMonitor = prepareInitCondition(site, channel, movieParser);
		// Prepare some urls to judge while the updates is over.

		boolean strictCheckStrategy = RuntimeManager.getInstance().getConfiguration().isStrictCheckStrategy();

		for (Range range : rangeList) {

			if (processMonitor.isCanceled()) {
				return visitHistory;
			}

			List<Movie> movieList = doParseRange(site, channel, range, urlsMonitor, processMonitor);

			if (null == movieList) {
				// No more movies available.
				break;
			} else {
				visitHistory.setLastVisitPageNumber(range.getStart());

				if (!movieList.isEmpty()) {

					if (strictCheckStrategy) {
						urlsMonitor = DuplicatedUrlsCondition.create(movieList, movieParser.getCheckDuplicationCount());
					}
				}

				doSave(site, movieList, visitHistory);
			}
		}

		return visitHistory;
	}

	/**
	 *
	 * @param site
	 * @param channel
	 * @param range
	 * @param conditionChecker
	 * @param processMonitor
	 * @return
	 */
	private static List<Movie> doParseRange(Site site, MovieChannel channel, Range range, ICondition conditionChecker, IProcessMonitor processMonitor) {

		String message = RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.MovieParserHelper_ParseChannelTaskName);

		AbstractMovieParser movieParser = site.getMovieParser();
		List<Movie> movieList = new ArrayList<>();
		String urlPattern = channel.getUrlPattern();

		for (int currentPage = range.getStart(); currentPage <= range.getEnd(); currentPage++) {

			if (processMonitor.isCanceled()) {
				return null;
			}

			String currentPageString = currentPage + "";
			String currentPageUrl = MessageFormat.format(urlPattern, currentPageString);

			String taskName = MessageFormat.format(message, site.getSiteName(), channel.getDisplayName(), currentPage);
			processMonitor.setName(taskName);

			Movie lastVisitedMovie = findLastVisitedMovie(site, channel);

			MovieListResult movieListWebPage = movieParser.parseMovieListPage(currentPageUrl, lastVisitedMovie, processMonitor);

			if (null == movieListWebPage || CollectionUtils.isEmpty(movieListWebPage.getMovieList())) {
				return null;
			}

			MovieUtil.updateInfo(movieListWebPage.getMovieList(), channel.getName(), site.getSiteName());

			if (!conditionChecker.checkShouldContinue(movieListWebPage)) {
				movieList.addAll(movieListWebPage.getMovieList());
				return movieList;
			}
			// If the urls monitor found duplicated movie, them will be removed.
			// The process will be interrupted;

			movieList.addAll(movieListWebPage.getMovieList());

			boolean isLastPage = movieListWebPage.isLastPage();

			if (!isLastPage) {
				if (!shouldContinue(movieListWebPage)) {
					return null;
				}
			}
		}

		return movieList;
	}

	/**
	 * @param visitHistory
	 * @return
	 */
	public static List<Range> buildRangeList(VisitHistory visitHistory, int step) {

		if (step < 5) {
			step = NextPageJumpStep;
		}

		List<Range> rangeList = new ArrayList<>();

		if (NumberUtil.getIntValue(visitHistory.getLastVisitPageNumber()) < step) {
			Range range = new Range();

			range.setStart(1);
			range.setEnd(visitHistory.getLastTotalPageCount());
			rangeList.add(range);
			return rangeList;
		}

		int lastVisitPageNumber = NumberUtil.getIntValue(visitHistory.getLastVisitPageNumber());

		if (NumberUtil.getIntValue(visitHistory.getId()) == 0) {
			lastVisitPageNumber = visitHistory.getLastTotalPageCount();
		}

		int rangeCount = lastVisitPageNumber / step;

		if (NumberUtil.getIntValue(visitHistory.getId()) == 0) {
			if ((lastVisitPageNumber % step) != 0) {
				rangeCount++;
			}
		}

		for (int i = 0; i < rangeCount; i++) {
			Range range = new Range();

			range.setStart(i * step + 1);
			range.setEnd((i + 1) * step + 1);

			if (range.getEnd() > lastVisitPageNumber) {
				range.setEnd(lastVisitPageNumber);
			}

			rangeList.add(range);
		}

		if (NumberUtil.getIntValue(visitHistory.getId()) != 0) {
			Range range = new Range();

			range.setStart(lastVisitPageNumber);
			range.setEnd(visitHistory.getLastTotalPageCount());

			rangeList.add(range);
		}

		Collections.reverse(rangeList);
		return rangeList;
	}

	/**
	 * Prepare a visit history for a specified channel.<BR>
	 * If the history is null. After querying the last page number, Try to create a
	 * history.
	 *
	 * @param site
	 * @param channel
	 * @param processMonitor
	 * @return
	 */
	private static VisitHistory prepare(Site site, MovieChannel channel, IProcessMonitor processMonitor) {
		AbstractMovieParser movieParser = site.getMovieParser();

		VisitHistory visitHistory = RuntimeManager.getInstance().getStoreService().queryVisitHistory(site.getSiteName(), channel.getName());

		if (null == visitHistory) {

			visitHistory = new VisitHistory();

			visitHistory.setChannel(channel.getName());
			visitHistory.setSiteName(site.getSiteName());

			int lastPageCount = findLastPage(movieParser, channel, 1, processMonitor);

			if (lastPageCount > 0) {
				visitHistory.setLastVisitPageNumber(lastPageCount);
				visitHistory.setLastTotalPageCount(lastPageCount);
			} else {
				return null;
			}
		}

		if (NumberUtil.getIntValue(visitHistory.getLastTotalPageCount()) < 0) {
			int lastPageCount = findLastPage(movieParser, channel, 1, processMonitor);
			visitHistory.setLastTotalPageCount(lastPageCount);
		}

		return visitHistory;

	}

	/**
	 * Create a condition to judge whether the query process will be continue or
	 * not.<BR>
	 * If duplicated movies found,them will be removed and the process will be
	 * interrupted.
	 *
	 * @param site
	 * @param channel
	 * @param movieParser
	 * @return
	 */
	private static ICondition prepareInitCondition(Site site, MovieChannel channel, AbstractMovieParser movieParser) {

		boolean strictCheckStrategy = RuntimeManager.getInstance().getConfiguration().isStrictCheckStrategy();

		if (strictCheckStrategy) {
			List<Movie> preloadMovieList = site.getMovieList().stream().filter(movie -> StringUtils.equals(movie.getChannel(), channel.getName()))
					.collect(Collectors.toList());

			DuplicatedUrlsCondition urlsMonitor = DuplicatedUrlsCondition.create(preloadMovieList, movieParser.getCheckDuplicationCount());
			return urlsMonitor;
		} else {
			return new SiteChannelCondition(site, channel);
		}

	}

	private static Movie findLastVisitedMovie(Site site, MovieChannel channel) {

		for (Movie movie : site.getMovieList()) {

			if (StringUtils.equals(movie.getChannel(), channel.getName())) {
				return movie;
			}

		}

		return null;

	}

	/**
	 * Judge the visite process should continue or not.
	 *
	 * @param movieListWebPage
	 * @return
	 */
	private static boolean shouldContinue(MovieListResult movieListWebPage) {

		if (null == movieListWebPage) {
			return false;
		}

		if (CollectionUtils.isEmpty(movieListWebPage.getMovieList())) {
			return false;
		}

		if (movieListWebPage.isHasError()) {
			return movieListWebPage.isShouldRetry();
		}

		boolean ignoreError = RuntimeManager.getInstance().getConfiguration().isIgnoreError();
		if (movieListWebPage.isHasError() && (!ignoreError)) {
			return false;
		}

		return true;
	}

	/**
	 * Save the new movies to database.<BR>
	 *
	 * @param site
	 * @param visitHistory
	 * @param movieListWebPage
	 */
	private static void doSave(Site site, List<Movie> movieList, VisitHistory visitHistory) {

		if (CollectionUtils.isNotEmpty(movieList)) {

			List<Movie> sourceMovieList = new ArrayList<>(movieList);

			Collections.reverse(movieList);
			RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(movieList);

			site.addMoviesToHeader(sourceMovieList);
			// The site order the movies by desc.
			// So the new movies should be added to the header.
		}

		visitHistory.setLastUpdate(new Date());
		RuntimeManager.getInstance().getStoreService().insertOrUpdateVisitHistory(visitHistory);

	}

	/**
	 *
	 * @param movieParser
	 * @param channel
	 * @param startPosition
	 * @param processMonitor
	 * @return
	 */
	private static int findLastPage(AbstractMovieParser movieParser, MovieChannel channel, int startPosition, IProcessMonitor processMonitor) {

		String urlPattern = channel.getUrlPattern();

		if (!movieParser.supportPaging()) {
			return 1;
		}

		if (!channel.isSupportPaging()) {
			return 1;
		}

		if (movieParser.supportJumpToLast()) {
			String currentListPageUrl = MessageFormat.format(urlPattern, 1);
			MovieListResult movieListWebPage = movieParser.parseMovieListPage(currentListPageUrl, null, processMonitor);

			if (null == movieListWebPage) {
				throw new RuntimeException("Can't access the last page. " + currentListPageUrl);
			} else {
				return movieListWebPage.getTotalPageCount();
			}
		} else {
			return exhaustiveFindLastPage(urlPattern, startPosition, processMonitor);
		}
	}

	/**
	 * Exhaustive pages to find the last page.<BR>
	 *
	 * @param urlPattern
	 * @param startPosition
	 * @param processMonitor
	 * @return
	 */
	public static int exhaustiveFindLastPage(String urlPattern, int startPosition, IProcessMonitor processMonitor) {

		processMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

		AbstractMovieParser movieParser = RuntimeManager.getInstance().findMovieParserByUrl(urlPattern);

		int retryCount = 20;
		int currentPosition = startPosition;
		int step = ExhausePageJumpStep;

		if (currentPosition > 1) {
			step = 20;
		}

		boolean outOfLastPage = false;
		int lastValidPostion = -1;

		while (retryCount > 0) {
			retryCount--;

			if (lastValidPostion == currentPosition) {
				return currentPosition;
			}

			String currentListPageUrl = MessageFormat.format(urlPattern, currentPosition + "");

			MovieListResult movieListWebPage = movieParser.parseMovieListPage(currentListPageUrl, null, processMonitor);

			if (processMonitor.isCanceled()) {
				return -1;
			}

			if (movieListWebPage.isEmpty() || movieListWebPage.isLastPage()) {
				outOfLastPage = true;
				step = halfStep(step);

				if (currentPosition == (lastValidPostion + 1)) {
					return lastValidPostion;
				}

				currentPosition = lastValidPostion + step;
			} else {
				if (outOfLastPage) {
					step = halfStep(step);
				}
				lastValidPostion = currentPosition;
				currentPosition = currentPosition + step;
			}
		}

		return -1;

	}

	/**
	 * @param step
	 * @return
	 */
	private static int halfStep(int step) {
		step = step / 2;
		if (step < 4) {
			step = 1;
		}
		return step;
	}

}
