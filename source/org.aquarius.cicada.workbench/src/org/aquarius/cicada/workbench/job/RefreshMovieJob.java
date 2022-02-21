/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.RuntimeConstant;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.collection.CollectionUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Refresh movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshMovieJob extends AbstractCancelableJob {

	private List<Movie> movieList;

	/**
	 * @param name
	 */
	public RefreshMovieJob(String name, List<Movie> movieList) {
		super(name);

		this.movieList = movieList;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		if (this.movieList.isEmpty()) {
			monitor.done();
			return Status.OK_STATUS;
		}

		IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);

		List<Movie> unparsedMovieList = new ArrayList<>(this.movieList);

		while (!unparsedMovieList.isEmpty()) {

			List<Movie> todoMovieList = CollectionUtil.removeList(unparsedMovieList, 0, RuntimeConstant.RefreshBatchCount);

			String errorMessage = MovieParserHelper.parseMovieDetailInfo(false, todoMovieList, processMonitor);

			if (processMonitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			if (StringUtils.isNotBlank(errorMessage)) {
				TooltipUtil.showErrorTip(Messages.ErrorDialogTitle, errorMessage);
				return new Status(Status.ERROR, WorkbenchActivator.PLUGIN_ID, 1, errorMessage, null); // $NON-NLS-1$
			}

			RefreshMovieJob.doUpdateDetailedMovieList(null, todoMovieList);
			JobUtil.updateMoviesInUI(todoMovieList);
		}

		monitor.done();

		return Status.OK_STATUS;
	}

	/**
	 * @param movieList
	 *
	 */
	static void doUpdateDetailedMovieList(Site site, List<Movie> movieList) {

		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(movieList);

		if (RuntimeManager.getInstance().getConfiguration().isAutoFillActor()) {
			new FillMissingInfoJob(Messages.FillMissingInfoDropDownAction_FillMissingInfoForSelectedMovie, site, movieList, false).schedule();
		}
	}

}
