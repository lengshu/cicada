/**
 * 
 */
package org.aquarius.cicada.core.impl.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.result.MovieListResult;
import org.aquarius.cicada.core.util.MovieUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class SiteChannelCondition implements ICondition {

	private Set<String> videoIdSet = new TreeSet<>();

	/**
	 * 
	 */
	public SiteChannelCondition(Site site, MovieChannel movieChannel) {
		List<Movie> movieList = site.getMovieList();

		for (Movie movie : movieList) {

			if (StringUtils.equalsIgnoreCase(movie.getChannel(), movieChannel.getName())) {
				String movieId = MovieUtil.parseMovieId(movie);
				this.videoIdSet.add(movieId);
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean checkShouldContinue(MovieListResult model) {

		if (this.videoIdSet.isEmpty()) {
			return true;
		}

		List<Movie> duplicatedMovieList = new ArrayList<>();

		List<Movie> movieList = model.getMovieList();
		for (Movie movie : movieList) {
			String videoId = MovieUtil.parseMovieId(movie);
			if (this.videoIdSet.contains(videoId)) {
				duplicatedMovieList.add(movie);
			}
		}

		double ratio = ((double) duplicatedMovieList.size()) / movieList.size();

		if (!duplicatedMovieList.isEmpty()) {
			movieList.removeAll(duplicatedMovieList);
			duplicatedMovieList.clear();
		}

		if (ratio > 0.3) {
			return false;
		}

		return true;
	}

}
