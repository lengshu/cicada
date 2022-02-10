/**
 * 
 */
package org.aquarius.cicada.core.helper.filter;

import org.aquarius.cicada.core.model.MovieChannel;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class AutoRefreshMovieChannelFilter implements MovieChannelFilter {

	private static final AutoRefreshMovieChannelFilter instance = new AutoRefreshMovieChannelFilter();

	/**
	 * @return the instance
	 */
	public static AutoRefreshMovieChannelFilter getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private AutoRefreshMovieChannelFilter() {
		super();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isFilter(MovieChannel movieChannel) {
		return !movieChannel.isAutoRefreshList();
	}

}
