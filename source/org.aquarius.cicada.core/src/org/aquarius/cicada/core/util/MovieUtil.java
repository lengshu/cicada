/**
 *
 */
package org.aquarius.cicada.core.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.log.LogUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.net.HttpUtil;
import org.slf4j.Logger;

/**
 * Movie function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class MovieUtil {

	// private static Pattern UniIdPattern =
	// Pattern.compile("[^\\x00-\\xff]{0,10}[0-9a-zA-Z]{0,20}[^\\x00-\\xff]{0,10}[
	// -]{0,2}[0-9a-zA-Z_]{1,12}");

	private static Pattern UniIdPattern = Pattern.compile("[0-9a-zA-Z]{2,20}[ -]{0,2}[0-9a-zA-Z_]{2,12}", Pattern.CASE_INSENSITIVE);

	private static Pattern SplitPattern = Pattern.compile(",|\\|");

	private static String[] EmptyStringArrays = new String[] {};

	private static Logger log = LogUtil.getLogger(MovieUtil.class);

	/**
	 *
	 */
	private MovieUtil() {
		// No instances will be created.
	}

	public static boolean findKeyword(Movie movie, String keyword) {
		return StringUtil.findKeywordInRangeIgnoreCase(keyword, movie.getTitle(), movie.getActor(), movie.getTag(), movie.getCategory(), movie.getUniId(),
				movie.getProducer());
	}

	/**
	 * @param movieChannel
	 * @param movieList
	 * @return
	 */
	public static void updateInfo(List<Movie> movieList, String channelName, String siteName) {

		for (Movie movie : movieList) {
			movie.setSite(siteName);
			movie.setChannel(channelName);
		}
	}

	/**
	 * for a string like "1,2,3,4",the result set should be[1,2,3,4]
	 *
	 * @param value
	 * @return
	 */
	public static Set<Integer> extractMovieIdList(String value) {
		String[] ids = split(value);

		Set<Integer> idList = new HashSet<Integer>();

		for (String idString : ids) {
			int id = NumberUtils.toInt(idString, -1);
			if (id > 0) {
				idList.add(id);
			}
		}
		return idList;
	}

	/**
	 * For a string like "usr1,user2,user3,user1",the return value should be
	 * "user1,user2,user3". The duplicated "user1" will be removed.
	 *
	 * @param source
	 * @return
	 */
	public static String removeDuplication(String source) {
		String[] stringArrays = split(source);

		if (ArrayUtils.isEmpty(stringArrays)) {
			return null;
		}

		Set<String> stringSet = new ListOrderedSet<>();
		for (String string : stringArrays) {
			if (StringUtils.isNotBlank(string)) {
				stringSet.add(string.trim());
			}
		}
		return StringUtils.join(stringSet, StringUtil.ContentSeparator);
	}

	/**
	 * Use "|" and "," to split string.
	 *
	 * @param source
	 * @return
	 */
	public static String[] split(String source) {

		if (StringUtils.isBlank(source)) {
			return EmptyStringArrays;
		}

		return SplitPattern.split(source);
	}

	/**
	 * Find uniid in the title.
	 *
	 * @param title
	 * @return
	 */
	public static String findUniId(String title) {
		Matcher matcher = UniIdPattern.matcher(title);
		if (matcher.find()) {
			return matcher.group();
		} else {
			return null;
		}
	}

	/**
	 * Find the uniid and set to the movie.
	 *
	 * @param movie
	 * @return
	 */
	public static boolean updateUniId(Movie movie) {
		if (StringUtils.isEmpty(movie.getUniId())) {
			String uniId = findUniId(movie.getTitle());
			if (null != uniId) {
				movie.setUniId(uniId);
				return true;
			}
		}

		return false;
	}

	/**
	 * Generate a tag for downloader for locate.<BR>
	 *
	 * @param movie
	 * @param downloadInfo
	 * @return
	 */
	public static String getDownloadTag(Movie movie, DownloadInfo downloadInfo, int index) {
		return "cicada:" + movie.getId() + "-" + index;
	}

	/**
	 * Extract the id from the download tag.
	 *
	 * @param downloadTag
	 * @return
	 */
	public static String extractIdFromDownloadTag(String downloadTag) {
		if (StringUtils.isEmpty(downloadTag)) {
			return null;
		}

		downloadTag = StringUtils.substringAfter(downloadTag, "cicada:");
		downloadTag = StringUtils.substringBefore(downloadTag, "-");

		if (NumberUtils.isNumber(downloadTag)) {
			return downloadTag;
		} else {
			return null;
		}

	}

	/**
	 * Generate a suitable file name for the movie by the title.
	 *
	 * @param movie
	 * @return
	 */
	protected static String getTitleForFile(Movie movie) {

		String oldTitle = movie.getTitle();
		String title = oldTitle;

		if (title.startsWith("[中文")) {
			title = StringUtils.substringAfter(title, "]");
		}

		title = StringUtil.getTitleForFile(oldTitle);

		Matcher matcher = UniIdPattern.matcher(title);
		if (matcher.find()) {
			String codeName = matcher.group().toUpperCase();
			String oldInfo = StringUtils.substringAfter(title, codeName);

			String pixel = "720P";

			title = codeName + "-" + pixel + oldInfo;
		}

		if (StringUtils.contains(movie.getTag(), "中文")) {
			title = title + "[中文字幕]";
		}

		if (StringUtils.contains(movie.getCategory(), "中文")) {
			title = title + "[中文字幕]";
		}

		return title;
	}

	/**
	 *
	 * @param downloadInfo
	 * @return
	 */
	public static Link find(DownloadInfo downloadInfo, boolean validateLength) {

		if (null == downloadInfo) {
			return null;
		}

		List<Link> links = downloadInfo.getDownloadLinks();

		for (Link link : links) {

			try {

				String urlString = link.getSourceUrl();

				if (RuntimeManager.getInstance().isFilteredAnalyserSite(urlString)) {
					link.setSelected(false);
					continue;
				}

				urlString = getDownloadUrl(link);

				if (RuntimeManager.getInstance().isFilteredAnalyserSite(urlString)) {
					link.setSelected(false);
					continue;
				}

				if (StringUtils.isNotEmpty(link.getErrorMessage())) {
					continue;
				}

				long minLength = 10 * SystemUtil.DiskSizeInM;

				if (HttpUtil.isHls(urlString) || link.isHls()) {
					minLength = -1;
				}

				Map<String, String> headers = new HashMap<>();

				if (StringUtils.isNotBlank(link.getRefererUrl())) {
					headers.put(HttpUtil.Referer, link.getRefererUrl());
				}

				if (link.getRequestHeaders() != null) {
					headers.putAll(link.getRequestHeaders());
				}

				if (validateLength) {
					if (HttpUtil.validateUrlLength(urlString, minLength, headers)) {
						link.setDownloadUrl(urlString);
						return link;
					} else {
						link.setErrorMessage("NotEnoughLength");
					}
				} else {
					link.setDownloadUrl(urlString);
					return link;
				}

			} catch (Exception e) {
				log.error("find download urls", e);
			}
		}

		return null;
	}

	/**
	 * Find all unparsed movies.
	 *
	 * @param movieList
	 * @return
	 */
	public static List<Movie> findUnparsedMovies(List<Movie> movieList) {

		List<Movie> filteredMovieList = new ArrayList<>();

		for (Movie movie : movieList) {
			if (!BooleanUtils.isTrue(movie.getDetailed())) {
				filteredMovieList.add(movie);
			}
		}

		return filteredMovieList;
	}

	/**
	 * Compute channel id.<BR>
	 *
	 * @param movieChannel
	 * @return
	 */
	public static String computeChannelID(MovieChannel movieChannel) {
		return movieChannel.getName() + ":" + movieChannel.getUrlPattern();
	}

	/**
	 * Return channel names.<BR>
	 *
	 * @param movieChannelList
	 * @return
	 */
	public static List<String> getChannelNames(List<MovieChannel> movieChannelList) {
		if (null != movieChannelList) {
			return movieChannelList.stream().map(MovieChannel::getName).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * @param idList
	 * @return
	 */
	public static List<Movie> findMovies(List<Movie> movieList, Set<Integer> idList) {

		List<Movie> resultList = new ArrayList<Movie>();

		for (Movie movie : movieList) {
			if (idList.contains(movie.getId())) {
				resultList.add(movie);
			}
		}

		return resultList;
	}

	/**
	 * Find the movie channel with the given name.<BR>
	 * 
	 * @param siteConfig
	 * @param channelName
	 * @return
	 */
	public static MovieChannel findMovieChannel(SiteConfig siteConfig, String channelName) {
		List<MovieChannel> movieChannelList = siteConfig.getMovieChannelList();

		for (MovieChannel movieChannel : movieChannelList) {
			if (StringUtils.equals(movieChannel.getName(), channelName)) {
				return movieChannel;
			}
		}
		return null;
	}

	/**
	 * @param idList
	 * @return
	 */
	public static Movie findMovie(List<Movie> movieList, int id) {

		for (Movie movie : movieList) {
			if (id == movie.getId().intValue()) {
				return movie;
			}
		}

		return null;
	}

	/**
	 * Get all error messages.<BR>
	 *
	 * @param linkList
	 * @return
	 */
	public static String getLinkErrorMessages(List<Link> linkList) {
		StringJoiner errorMessage = new StringJoiner("\r\n"); //$NON-NLS-1$

		for (Link link : linkList) {
			if (StringUtils.isNotBlank(link.getErrorMessage())) {
				errorMessage.add(link.getSourceUrl() + ": " + link.getErrorMessage());
			} else {

				String message = RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.MovieUtil_ErrorLinkIsNotAccessible);
				errorMessage.add(MessageFormat.format(message, link.getSourceUrl()));
			}
		}

		return errorMessage.toString();
	}

	/**
	 * @param link
	 * @return
	 */
	public static String getDownloadUrl(Link link) {
		String downloadUrl = link.getDownloadUrl();
		if (StringUtils.isEmpty(downloadUrl)) {
			downloadUrl = link.getSourceUrl();
		}

		return downloadUrl;
	}

	/**
	 * @param fileUrls
	 */
	public static String findDownloadUrlByPixel(Map<String, String> fileUrls) {

		if (null == fileUrls) {
			return null;
		}

		List<String> pixelList = RuntimeManager.getInstance().getConfiguration().getPixelList();

		for (String pixel : pixelList) {
			String fileUrl = fileUrls.get(pixel);

			if (StringUtils.isBlank(fileUrl)) {
				fileUrl = fileUrls.get(pixel.toUpperCase());
			}

			if (StringUtils.isBlank(fileUrl)) {
				fileUrl = fileUrls.get(pixel.toLowerCase());
			}

			if (StringUtils.isNotBlank(fileUrl)) {
				return fileUrl;
			}
		}

		String fileUrl = fileUrls.get(null);
		if (StringUtils.isBlank(fileUrl)) {
			fileUrl = fileUrls.get("");
		}

		if (StringUtils.isBlank(fileUrl)) {
			fileUrl = fileUrls.get(" ");
		}

		return fileUrl;
	}

	/**
	 * Parse the id of the movie.<BR>
	 * 
	 * @param movie
	 * @return
	 */
	public static String parseMovieId(Movie movie) {
		AbstractMovieParser movieParser = RuntimeManager.getInstance().findMovieParserByUrl(movie.getPageUrl());

		if (null == movieParser) {
			return movie.getPageUrl();
		}

		return movieParser.parseMovieId(movie);
	}

}
