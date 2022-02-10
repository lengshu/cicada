/**
 *
 */
package org.aquarius.cicada.workbench.model;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.util.collection.CollectionUtil;

/**
 * Use a fixed movie list to show movie list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FixedMovieListService implements IMovieListService {

	private List<Movie> movieList;

	/**
	 * @param movieList
	 */
	public FixedMovieListService(List<Movie> movieList) {
		super();
		this.movieList = movieList;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> subList(int start, int end) {
		return CollectionUtil.subList(this.movieList, start, end);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getMovieSize() {
		return this.movieList.size();
	}

	/**
	 * @param idList
	 * @return
	 */
	@Override
	public List<Movie> findMovies(Set<Integer> idList) {
		return MovieUtil.findMovies(this.movieList, idList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getRequestHeader() {

		Set<String> siteNames = new TreeSet<String>();

		for (Movie movie : this.movieList) {

			SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfig(movie.getSite());

			if (null != siteConfig) {
				siteNames.add(siteConfig.getMainPage());
			} else {
				siteNames.add(getHost(movie.getPageUrl()));
			}
		}

		return new String[] { "Referer: " + StringUtils.join(siteNames, ",") };
	}

	/**
	 * Get the host of a url.<BR>
	 * 
	 * @param urlString
	 * @return
	 */
	private static String getHost(String urlString) {
		try {
			URL url = new URL(urlString);
			return url.getProtocol() + "://" + url.getHost();

		} catch (Exception e) {
			return urlString;
		}
	}

}
