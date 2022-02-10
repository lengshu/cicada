/**
 *
 */
package org.aquarius.cicada.core.model.result;

import org.aquarius.cicada.core.model.Movie;

/**
 * Model for query info from movie detail page.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieResult extends AbstractResult {

	private Movie movie;

	private boolean missing;

	/**
	 * 
	 */
	public MovieResult() {
		super();
	}

	/**
	 * @return the movie
	 */
	public Movie getMovie() {
		return this.movie;
	}

	/**
	 * @param movie the movie to set
	 */
	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	/**
	 * @return the missing
	 */
	public boolean isMissing() {
		return this.missing;
	}

	/**
	 * @param missing the missing to set
	 */
	public void setMissing(boolean missing) {
		this.missing = missing;
	}

}
