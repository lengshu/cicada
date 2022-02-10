/**
 * 
 */
package org.aquarius.cicada.core.helper.filter;

import org.aquarius.cicada.core.model.MovieChannel;

/**
 * 
 * All movie channels will be processed.<BR>
 * 
 * @author aquarius.github@gmail.com
 *
 */
public class NullMovieChannelFilter implements MovieChannelFilter {

	private static final NullMovieChannelFilter instance = new NullMovieChannelFilter();

	/**
	 * @return the instance
	 */
	public static NullMovieChannelFilter getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private NullMovieChannelFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isFilter(MovieChannel movieChannel) {
		return false;
	}

}
