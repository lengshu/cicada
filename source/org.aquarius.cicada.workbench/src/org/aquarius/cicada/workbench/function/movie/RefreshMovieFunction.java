/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractBatchMovieListFunction;
import org.aquarius.cicada.workbench.job.RefreshMovieJob;
import org.eclipse.swt.browser.Browser;

/**
 * Refresh movie details.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshMovieFunction extends AbstractBatchMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public RefreshMovieFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public RefreshMovieFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(RefreshMovieFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object doCallFunction(List<Movie> selectedMovieList, Object[] arguments) {

		new RefreshMovieJob(Messages.RefreshMovieJob_Name, selectedMovieList).schedule();

		return BrowserUtil.Success;

	}

}