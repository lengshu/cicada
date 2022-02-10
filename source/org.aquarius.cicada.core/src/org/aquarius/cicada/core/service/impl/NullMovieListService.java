/**
 * 
 */
package org.aquarius.cicada.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class NullMovieListService implements IMovieListService {

	public static final NullMovieListService Instance = new NullMovieListService();

	/**
	 * 
	 */
	private NullMovieListService() {
		// Just singleton
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Movie> subList(int start, int end) {
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMovieSize() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Movie> findMovies(Set<Integer> idList) {
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getRequestHeader() {
		return null;
	}

}
