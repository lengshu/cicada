/**
 *
 */
package org.aquarius.cicada.core.model.result;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.model.Movie;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class MovieRecursionResult {

	private int totalPageCount;

	private List<Movie> movieList = new ArrayList<Movie>();

	private boolean hasError = false;

	private List<String> errorPages = new ArrayList<String>();

	public List<String> getErrorPages() {
		return this.errorPages;
	}

	public void addErrorPages(String... errorPages) {
		CollectionUtils.addAll(this.errorPages, errorPages);
	}

	public void addMovies(Movie... movies) {
		CollectionUtils.addAll(this.movieList, movies);
	}

	public void addMovies(List<Movie> movies) {
		this.movieList.addAll(movies);
	}

	/**
	 * @return the hasError
	 */
	public boolean isHasError() {
		return this.hasError;
	}

	/**
	 * @param hasError the hasError to set
	 */
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	/**
	 * @return the movieList
	 */
	public List<Movie> getMovieList() {
		return this.movieList;
	}

	/**
	 * @param movieList the movieList to set
	 */
	public void setMovieList(List<Movie> movieList) {
		this.movieList = movieList;
	}

	/**
	 * @return the totalPageCount
	 */
	public int getTotalPageCount() {
		return this.totalPageCount;
	}

	/**
	 * @param totalPageCount the totalPageCount to set
	 */
	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

}
