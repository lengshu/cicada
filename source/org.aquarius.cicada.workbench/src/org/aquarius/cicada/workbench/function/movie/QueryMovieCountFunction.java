/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractMovieListFunction;
import org.eclipse.swt.browser.Browser;

/**
 * Query all movie count.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class QueryMovieCountFunction extends AbstractMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public QueryMovieCountFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public QueryMovieCountFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(QueryMovieCountFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object callFunction(IMovieListService movieListSerivce, Object[] arguments) {
		return movieListSerivce.getMovieSize();
	}

}