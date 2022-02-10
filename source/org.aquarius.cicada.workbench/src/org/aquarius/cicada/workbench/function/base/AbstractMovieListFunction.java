/**
 *
 */
package org.aquarius.cicada.workbench.function.base;

import org.aquarius.cicada.core.service.IMovieListService;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * The function for being injected into browser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractMovieListFunction extends BrowserFunction {

	/**
	 *
	 */
	private final IMovieListService movieListSerivce;

	/**
	 *
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public AbstractMovieListFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, name);
		this.movieListSerivce = movieListSerivce;

	}

	@Override
	public Object function(Object[] arguments) {
		return callFunction(this.movieListSerivce, arguments);

	}

	/**
	 * @return the movieListSerivce
	 */
	protected IMovieListService getMovieListSerivce() {
		return this.movieListSerivce;
	}

	/**
	 *
	 * @param movieListSerivce
	 * @param arguments
	 * @return
	 */
	protected abstract Object callFunction(IMovieListService movieListSerivce, Object[] arguments);
}