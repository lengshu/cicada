/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractBatchMovieListFunction;
import org.aquarius.cicada.workbench.job.GenerateDownloadUrlJob;
import org.eclipse.swt.browser.Browser;

/**
 * Generate urls for specified movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class GenerateMovieDownloadUrlsFunction extends AbstractBatchMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public GenerateMovieDownloadUrlsFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public GenerateMovieDownloadUrlsFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(GenerateMovieDownloadUrlsFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object doCallFunction(List<Movie> selectedMovieList, Object[] arguments) {

		if (arguments.length < 2) {
			return BrowserUtil.Error;
		}

		String generatorName = (String) arguments[1];
		AbstractDownloadListGenerator downloadListGenerator = RuntimeManager.getInstance().findDownloadListGenerator(generatorName);

		if (null != downloadListGenerator) {
			new GenerateDownloadUrlJob(Messages.GenerateDownloadUrlsAction_GenerateDownloadUrlsJob, downloadListGenerator, selectedMovieList).schedule();

			return BrowserUtil.Success;
		} else {
			return "the specified generator name can't be found.";
		}

	}

}