/**
 *
 */
package org.aquarius.cicada.workbench.helper;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.model.VirtualSite;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.util.nls.NlsResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The helper for movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieHelper {

	/**
	 *
	 */
	private MovieHelper() {
		// No instances needed.
	}

	/**
	 * 
	 * @param siteConfig
	 * @return
	 */
	public static Map<String, String> buildChannelNameMapping(SiteConfig siteConfig) {
		Map<String, String> mapping = new HashMap<>();

		List<MovieChannel> channelList = siteConfig.getMovieChannelList();

		for (MovieChannel movieChannel : channelList) {
			mapping.put(movieChannel.getName(), movieChannel.getDisplayName());
		}
		return mapping;

	}

	/**
	 * @return
	 */
	public static Map<String, String> buildChannelNameMapping(Site site, boolean includeUnknownChannel) {

		if (site instanceof VirtualSite) {
			return ((VirtualSite) site).getChannelNameMapping();
		}

		Map<String, String> nameMapping = doBuildChannelNameMapping(site);

		if (includeUnknownChannel) {
			List<Movie> movieList = site.getMovieList();
			for (Movie movie : movieList) {
				String channelName = movie.getChannel();
				nameMapping.putIfAbsent(channelName, channelName);
			}
		}

		return nameMapping;
	}

	private static Map<String, String> doBuildChannelNameMapping(Site site) {
		SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfig(site.getSiteName());

		if (null == siteConfig) {
			Map<String, String> nameMapping = new HashMap<String, String>();

			List<String> channelNameList = site.getChannelNames();

			for (String channelName : channelNameList) {
				nameMapping.put(channelName, channelName);
			}

			return nameMapping;
		}

		final Map<String, String> nameMapping = MovieHelper.buildChannelNameMapping(siteConfig);
		return nameMapping;
	}

	/**
	 * Return the states map.<BR>
	 * The key is the state.<BR>
	 * The value is the display string.<BR>
	 *
	 * @return
	 */
	public static Map<Integer, String> getMovieStateMap() {
		Map<Integer, String> statesMap = new TreeMap<>();

		NlsResource nlsResource = RuntimeManager.getInstance().getNlsResource();

		statesMap.put(Movie.StateInit, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateInit));
		statesMap.put(Movie.StateParsed, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateParsed));
		statesMap.put(Movie.StateFinished, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateFinished));
		// statesMap.put(Movie.StateDeleted,
		// nlsResource.getString(DownloadNlsMessageConstant.MovieStateDeleted));
		statesMap.put(Movie.StateDownloading, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateDownloading));
		statesMap.put(Movie.StateDuplicated, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateDuplicated));
		statesMap.put(Movie.StateError, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateError));
		statesMap.put(Movie.StateIgnored, nlsResource.getValue(MovieNlsMessageConstant.Movie_StateIgnored));

		return statesMap;
	}

	/**
	 * Return the movie state as list.<BR>
	 *
	 * @return
	 */
	public static List<Integer> getMovieStateList() {
		List<Integer> stateList = new ArrayList<>();

		stateList.add(Movie.StateInit);
		stateList.add(Movie.StateParsed);
		stateList.add(Movie.StateFinished);
		// stateList.add(Movie.StateDeleted);
		stateList.add(Movie.StateDownloading);
		stateList.add(Movie.StateDuplicated);
		stateList.add(Movie.StateError);
		stateList.add(Movie.StateIgnored);

		return stateList;
	}

	/**
	 * Find the string with the specified keyword.<BR>
	 * But the result should be less than max match number.<BR>
	 * Except the max match number < 0.<BR>
	 *
	 * @param site
	 * @param keyword
	 * @param maxMatchNumber
	 * @return
	 */
	public static List<String> findKeywords(Site site, String keyword, int maxMatchNumber) {

		List<String> resultList = new ArrayList<>();

		for (String mark : site.getAllMarks()) {

			if (StringUtils.containsIgnoreCase(mark, keyword)) {
				resultList.add(mark);
			}

			if ((maxMatchNumber > 0) && (resultList.size() > maxMatchNumber)) {
				return resultList;
			}
		}

		return resultList;
	}

	/**
	 * Copy the movie info.<BR>
	 *
	 * @param selectedMovieList
	 * @param property
	 * @return
	 */
	public static String copyMovieInfo(List<Movie> selectedMovieList, String property) {
		StringJoiner stringJoiner = new StringJoiner("\r\n"); //$NON-NLS-1$

		for (Movie movie : selectedMovieList) {

			String value = null;

			if (StringUtils.isBlank(property)) {
				value = movie.toString();
			} else {
				try {
					value = ObjectUtils.toString(PropertyUtils.getProperty(movie, property));
				} catch (Exception e) {
					value = e.getLocalizedMessage();
				}
			}

			stringJoiner.add(value);
		}

		String content = stringJoiner.toString();
		return content;
	}

	/**
	 * Get the info of a movie for tooltip.<BR>
	 *
	 * @param movie
	 * @return
	 */
	public static String getInfoForTooltip(Movie movie) {
		if (null == movie) {
			return ""; //$NON-NLS-1$
		}

		String message = Messages.MovieHelper_TooltipMessage;

		String title = ObjectUtils.toString(movie.getTitle(), ""); //$NON-NLS-1$
		String uniId = ObjectUtils.toString(movie.getUniId(), ""); //$NON-NLS-1$
		String actor = ObjectUtils.toString(movie.getActor(), ""); //$NON-NLS-1$
		String tag = ObjectUtils.toString(movie.getTag(), ""); //$NON-NLS-1$
		String category = ObjectUtils.toString(movie.getCategory(), ""); //$NON-NLS-1$
		String producer = ObjectUtils.toString(movie.getProducer(), ""); //$NON-NLS-1$
		String publishDate = ""; //$NON-NLS-1$
		if (null != movie.getPublishDate()) {
			publishDate = DateFormat.getDateInstance().format(movie.getPublishDate());
		}
		return MessageFormat.format(message, title, uniId, actor, tag, category, producer, publishDate);
	}

	/**
	 * 
	 * @param site
	 * @param movieList
	 * @return
	 */
	private static List<Movie> fillActors(Site site, List<Movie> movieList) {

		List<Movie> resultList = new ArrayList<>();

		if (null == site || null == movieList) {
			return resultList;
		}

		List<String> actorList = site.getActors();

		for (Movie movie : movieList) {

			if (StringUtils.isBlank(movie.getActor())) {
				StringJoiner stringJoiner = new StringJoiner(",");

				for (String actor : actorList) {

					if (StringUtils.isBlank(actor)) {
						continue;
					}

					if (StringUtils.containsIgnoreCase(movie.getTitle(), actor)) {
						stringJoiner.add(actor);
					}

					if (StringUtils.containsIgnoreCase(movie.getTag(), actor)) {
						stringJoiner.add(actor);
					}

					if (StringUtils.containsIgnoreCase(movie.getCategory(), actor)) {
						stringJoiner.add(actor);
					}
				}

				if (stringJoiner.length() > 0) {
					movie.setActor(stringJoiner.toString());
					resultList.add(movie);
				}
			}
		}

		return resultList;
	}

	/**
	 * 
	 * @param site
	 * @param processMonitor
	 * @return
	 */
	public static List<Movie> fillActors(Site site, IProcessMonitor processMonitor) {

		if (null == site) {
			return new ArrayList<Movie>();
		}

		List<Movie> movieList = site.getMovieList();

		return fillActors(movieList, false, processMonitor);
	}

	/**
	 * 
	 * @param movieList
	 * @param force
	 * @param processMonitor
	 * @return
	 */
	public static List<Movie> fillActors(List<Movie> movieList, boolean force, IProcessMonitor processMonitor) {

		if (CollectionUtils.isEmpty(movieList)) {
			return new ArrayList<Movie>();
		}

		List<Movie> resultList = new ArrayList<>();
		List<String> actorList = RuntimeManager.getInstance().getStoreService().queryAllActors();

		for (Movie movie : movieList) {

			if (processMonitor.isCanceled()) {
				return resultList;
			}

			if (StringUtils.isBlank(movie.getActor()) || force) {
				StringJoiner stringJoiner = new StringJoiner(",");

				for (String actor : actorList) {

					if (StringUtils.isBlank(actor)) {
						continue;
					}

					String title = movie.getTitle();
					String newTitle = StringUtils.remove(title, " ");

					if (StringUtils.containsIgnoreCase(title, actor) || StringUtils.containsIgnoreCase(newTitle, actor)) {
						stringJoiner.add(actor);
					}

					if (StringUtils.containsIgnoreCase(movie.getTag(), actor)) {
						stringJoiner.add(actor);
					}

					if (StringUtils.containsIgnoreCase(movie.getCategory(), actor)) {
						stringJoiner.add(actor);
					}
				}

				if (stringJoiner.length() > 0) {
					movie.setActor(stringJoiner.toString());
					resultList.add(movie);
				}
			}

		}

		return resultList;

	}

	/**
	 * 
	 * @param site
	 * @return
	 */
	public static List<Movie> fillPublishDate(Site site) {

		if (null == site) {
			return new ArrayList<Movie>();
		}

		List<Movie> movieList = site.getMovieList();
		return fillPublishDate(site, movieList);
	}

	/**
	 * 
	 * @param site
	 * @return
	 */
	public static List<Movie> fillPublishDate(Site site, List<Movie> movieList) {

		if (null == site) {
			return new ArrayList<Movie>();
		}

		List<Movie> resultList = new ArrayList<>();

		Date publishDate = null;

		for (Movie movie : movieList) {
			if (null != movie.getPublishDate()) {
				publishDate = movie.getPublishDate();
			} else {
				if (null != publishDate) {
					movie.setPublishDate(publishDate);
					resultList.add(movie);
				}
			}
		}

		return resultList;
	}

	public static List<Movie> getMovieList(ISelection selection) {
		List<Movie> movieList = new ArrayList<>();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			List<?> objectList = structuredSelection.toList();

			for (Object object : objectList) {
				if (object instanceof Movie) {
					movieList.add((Movie) object);
				}
			}
		}

		return movieList;
	}

	/**
	 * 
	 * @param site
	 * @return
	 */
	public static boolean isVirtualSite(Site site) {
		return null == site.getMovieParser();
	}

}
