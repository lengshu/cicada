/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractBatchMovieListFunction;
import org.aquarius.cicada.workbench.job.DownloadMovieJob;
import org.eclipse.swt.browser.Browser;

public class DownloadMovieListFunction extends AbstractBatchMovieListFunction {

	private boolean saveToDatabase;

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public DownloadMovieListFunction(Browser browser, IMovieListService movieListSerivce, String name, boolean saveToDatabase) {
		super(browser, movieListSerivce, name);
		this.saveToDatabase = saveToDatabase;
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public DownloadMovieListFunction(Browser browser, IMovieListService movieListSerivce, boolean saveToDatabase) {
		super(browser, movieListSerivce, BrowserUtil.getShortClassName(DownloadMovieListFunction.class));
		this.saveToDatabase = saveToDatabase;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object doCallFunction(List<Movie> selectedMovieList, Object[] arguments) {

		DownloadMovieJob downloadMovieJob = new DownloadMovieJob("download movies", selectedMovieList, this.saveToDatabase, false);

		downloadMovieJob.schedule();
		return BrowserUtil.Success;
	}

}