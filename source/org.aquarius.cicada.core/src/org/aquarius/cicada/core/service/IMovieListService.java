/**
 *
 */
package org.aquarius.cicada.core.service;

import java.util.List;
import java.util.Set;

import org.aquarius.cicada.core.model.Movie;

/**
 * Movie list service to show table or grid.
 *
 * @author aquarius.github@gmail.com
 */
public interface IMovieListService {

	/**
	 * Get the movies of a specified range.
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Movie> subList(int start, int end);

	/**
	 * Return the size of all movies.<BR>
	 *
	 * @return
	 */
	public int getMovieSize();

	/**
	 * Found all the movies with specified id.
	 *
	 * @param idList
	 * @return
	 */
	public List<Movie> findMovies(Set<Integer> idList);

	/**
	 * Set the filter.
	 *
	 * @param filter
	 */
	public default void setFilter(Filter filter) {

	}

	/**
	 * @return
	 */
	public String[] getRequestHeader();

}
