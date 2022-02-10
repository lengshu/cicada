/**
 *
 */
package org.aquarius.cicada.core.model.result;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.model.Movie;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class MovieListResult extends AbstractResult {

	private int totalPageCount;

	private int toalMovieCount;

	private String previousPageUrl;

	private String nextPageUrl;

	private String lastPageUrl;

	private List<Movie> movieList;

	private boolean lastPage = false;

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
	 * @return the nextPageUrl
	 */
	public String getNextPageUrl() {
		return this.nextPageUrl;
	}

	/**
	 * @param nextPageUrl the nextPageUrl to set
	 */
	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}

	/**
	 * @return the previousPageUrl
	 */
	public String getPreviousPageUrl() {
		return this.previousPageUrl;
	}

	/**
	 * @param previousPageUrl the previousPageUrl to set
	 */
	public void setPreviousPageUrl(String previousPageUrl) {
		this.previousPageUrl = previousPageUrl;
	}

	/**
	 * @return the lastPageUrl
	 */
	public String getLastPageUrl() {
		return this.lastPageUrl;
	}

	/**
	 * @param lastPageUrl the lastPageUrl to set
	 */
	public void setLastPageUrl(String lastPageUrl) {
		this.lastPageUrl = lastPageUrl;
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

	/**
	 * @return the toalMovieCount
	 */
	public int getToalMovieCount() {
		return this.toalMovieCount;
	}

	/**
	 * @param toalMovieCount the toalMovieCount to set
	 */
	public void setToalMovieCount(int toalMovieCount) {
		this.toalMovieCount = toalMovieCount;
	}

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(this.movieList);
	}

	/**
	 * @return the lastPage
	 */
	public boolean isLastPage() {
		return this.lastPage;
	}

	/**
	 * @param lastPage the lastPage to set
	 */
	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

}
