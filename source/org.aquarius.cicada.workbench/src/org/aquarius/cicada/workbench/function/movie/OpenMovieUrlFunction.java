/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractBatchMovieListFunction;
import org.eclipse.swt.browser.Browser;

/**
 * Open movie url in the system browsers.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenMovieUrlFunction extends AbstractBatchMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public OpenMovieUrlFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public OpenMovieUrlFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(OpenMovieUrlFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object doCallFunction(List<Movie> selectedMovieList, Object[] arguments) {

		return BrowserUtil.Success;

	}

}