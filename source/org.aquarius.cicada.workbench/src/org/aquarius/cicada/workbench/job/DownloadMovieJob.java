/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.MovieDownloadAnalyserHelper;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.listener.DynamicUrlParserProgressListener;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Download the specified movies.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadMovieJob extends AbstractCancelableJob {

	private List<Movie> movieList;

	private boolean saveToDatabase;
	// Sometime , user can download movies which are not in database.

	private boolean chooseSource = false;

	/**
	 * @param name
	 */
	public DownloadMovieJob(String name, List<Movie> movieList, boolean saveToDatabase, boolean chooseSource) {
		super(name);

		this.movieList = movieList;
		this.saveToDatabase = saveToDatabase;
		this.chooseSource = chooseSource;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);

		List<DownloadTask> taskList = new ArrayList<DownloadTask>();

		MovieParserHelper.parseMovieDetailInfo(true, this.movieList, processMonitor);

		MovieDownloadAnalyserHelper.analyseMovieDownloadUrls(this.movieList, false, processMonitor);

		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}

		for (Movie movie : this.movieList) {
			List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();

			if (CollectionUtils.isEmpty(downloadInfoList)) {
				String message = MessageFormat.format(Messages.DownloadMovieJob_NoDownloadLinkAvaiable, movie.getTitle());
				TooltipUtil.showErrorTip(Messages.DownloadMovieJob_TitleError, message);
				continue;
			}

			for (int i = 0; i < downloadInfoList.size(); i++) {

				DownloadInfo downloadInfo = downloadInfoList.get(i);

				String tagId = MovieUtil.getDownloadTag(movie, downloadInfo, i);

				DownloadTask existDownloadTask = DownloadManager.getInstance().findTaskByTagId(tagId);
				if (null != existDownloadTask) {

					if (existDownloadTask.getState() == DownloadTask.StateFinish) {
						String message = MessageFormat.format(Messages.DownloadMovieJob_ReloadTasks, movie.getTitle());

						if (SwtUtil.openConfirm(Messages.WarnDialogTitle, message)) {
							List<DownloadTask> newTaskList = new ArrayList<>();
							newTaskList.add(existDownloadTask);

							DownloadManager.getInstance().reloadTasks(newTaskList);
						}
					} else {
						String message = MessageFormat.format(Messages.DownloadMovieJob_DuplicatedTasks, movie.getTitle());
						TooltipUtil.showErrorTip(Messages.DownloadMovieJob_TitleError, message);

					}

					continue;
				}

				DownloadTask downloadTask = DynamicUrlParserProgressListener.update(movie, null, downloadInfo, i);
				if (null == downloadTask) {
					continue;
				}

				if (WorkbenchActivator.getDefault().getConfiguration().isAutoFinish()) {
					movie.setState(Movie.StateFinished);
				} else {
					movie.setState(Movie.StateDownloading);
				}

				taskList.add(downloadTask);
			}
		}

		if (this.saveToDatabase) {
			RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(this.movieList);
		}

		DownloadTask[] tasks = taskList.toArray(new DownloadTask[taskList.size()]);

		DownloadManager.getInstance().addDownloadTask(tasks);

		monitor.done();

		return Status.OK_STATUS;
	}

}
