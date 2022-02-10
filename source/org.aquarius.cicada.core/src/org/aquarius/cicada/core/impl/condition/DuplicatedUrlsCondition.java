/**
 *
 */
package org.aquarius.cicada.core.impl.condition;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.result.MovieListResult;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.collection.CollectionUtil;

/**
 *
 * The movies of a site is updated continuously.<BR>
 * For example ,"XXX" in 17 page ,but it will appear in 18page.<BR>
 * So you can't parse all movies just by pages.<BR>
 * Use last updated movies as right condition instead.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DuplicatedUrlsCondition implements ICondition {

	private Set<String> videoIdSet = new TreeSet<>();

	private int duplicatedCount = 2;

	/**
	 * @param videoIdSet
	 */
	public DuplicatedUrlsCondition(List<Movie> movieList, int duplicatedCount) {
		super();
		AssertUtil.assertNotNull(movieList);

		if (duplicatedCount >= 1 && duplicatedCount < 6) {
			this.duplicatedCount = duplicatedCount;
		}

		for (int i = 0; i < movieList.size(); i++) {
			if (i >= this.duplicatedCount) {
				break;
			}

			Movie movie = movieList.get(i);
			String videoId = MovieUtil.parseMovieId(movie);
			this.videoIdSet.add(videoId);
		}

	}

	/**
	 *
	 */
	public static DuplicatedUrlsCondition create(List<Movie> movieList, int duplicatedCount) {
		return new DuplicatedUrlsCondition(movieList, duplicatedCount);
	}

	/**
	 *
	 */
	public static DuplicatedUrlsCondition create(List<Movie> movieList) {
		return create(movieList, 2);
	}

	/**
	 *
	 * @param movieListResult
	 * @return
	 */
	@Override
	public boolean checkShouldContinue(MovieListResult movieListResult) {
		if (this.videoIdSet.isEmpty()) {
			return true;
		}

		List<Movie> movieList = movieListResult.getMovieList();

		if (movieList.isEmpty()) {
			return false;
		}

		int index = -1;
		int count = 0;

		for (int i = 0; i < movieList.size(); i++) {
			Movie movie = movieList.get(i);

			String videoId = MovieUtil.parseMovieId(movie);

			if (this.videoIdSet.contains(videoId)) {

				count++;

				if (index == -1) {
					index = i;
				}

				if (count >= this.duplicatedCount) {
					// index = this.urls.indexOf(movie.getPageUrl());
					break;
				}
			} else {
				count = 0;
			}
		}

		if (index >= 0) {

			List<Movie> tempList = CollectionUtil.subList(movieList, 0, index);
			movieList.clear();
			movieList.addAll(tempList);

			return false;
		}

		return true;
	}
}
