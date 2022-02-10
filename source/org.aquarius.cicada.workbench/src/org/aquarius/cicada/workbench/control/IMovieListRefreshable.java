/**
 *
 */
package org.aquarius.cicada.workbench.control;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.util.spi.IRefreshable;

/**
 * The interface is to provider update function and select movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IMovieListRefreshable extends IRefreshable {

	/**
	 * Return the selected movies.<BR>
	 *
	 * @return
	 */
	public List<Movie> getSelectedMovieList();

	/**
	 * Update movie list.<BR>
	 *
	 * @param movieList
	 */
	public default void update(List<Movie> movieList) {

	}

	public default void setSelectedMovieList(List<Movie> movieList) {

	}

}
