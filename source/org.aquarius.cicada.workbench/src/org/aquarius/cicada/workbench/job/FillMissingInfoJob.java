/**
 * 
 */
package org.aquarius.cicada.workbench.job;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class FillMissingInfoJob extends AbstractCancelableJob {

	private Site site;

	private List<Movie> movieList;

	private boolean forceRefill;

	/**
	 * 
	 * @param name
	 * @param site
	 * @param movieList
	 */
	public FillMissingInfoJob(String name, Site site, List<Movie> movieList, boolean forceRefill) {
		super(name);

		this.site = site;
		this.movieList = movieList;
		this.forceRefill = forceRefill;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		if (CollectionUtils.isEmpty(this.movieList)) {
			this.movieList = this.site.getMovieList();
		}

		ProcessMonitorProxy monitorProxy = new ProcessMonitorProxy(monitor);

		List<Movie> resultMovieList = MovieHelper.fillActors(this.movieList, this.forceRefill, monitorProxy);
		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(resultMovieList);

		resultMovieList = MovieHelper.fillPublishDate(this.site, this.movieList, monitorProxy);
		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(resultMovieList);

		return Status.OK_STATUS;
	}

}
