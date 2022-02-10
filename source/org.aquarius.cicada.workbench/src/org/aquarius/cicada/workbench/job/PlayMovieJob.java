/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.text.MessageFormat;
import java.util.List;

import org.aquarius.cicada.core.helper.MovieDownloadAnalyserHelper;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class PlayMovieJob extends AbstractCancelableJob {

	private Movie movie;

	/**
	 *
	 * @param name
	 * @param movie
	 */
	public PlayMovieJob(String name, Movie movie) {
		super(name);
		this.movie = movie;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		String mediaPlayerCommand = WorkbenchActivator.getDefault().getConfiguration().getMediaPlayerCommand();

		ProcessMonitorProxy processMonitor = new ProcessMonitorProxy(monitor);

		MovieParserHelper.parseMovieDetailInfo(true, this.movie, processMonitor);

		MovieDownloadAnalyserHelper.analyseDownloadUrls(this.movie.getDownloadInfoList(), true, processMonitor);

		List<DownloadInfo> downloadInfoList = this.movie.getDownloadInfoList();

		for (int i = 0; i < downloadInfoList.size(); i++) {

			DownloadInfo downloadInfo = downloadInfoList.get(i);

			Link link = MovieUtil.find(downloadInfo, false);

			if (null == link) {

				String title = MessageFormat.format(Messages.PlayMovieAction_ParserErrorMessage, this.movie.getTitle());
				String message = MovieUtil.getLinkErrorMessages(downloadInfo.getDownloadLinks());

				TooltipUtil.showErrorTip(title, message);

				continue;
			} else {
				String urlString = MovieUtil.getDownloadUrl(link);

				String command = MessageFormat.format(mediaPlayerCommand, urlString); // $NON-NLS-1$

				try {
					Runtime.getRuntime().exec(command);
				} catch (Exception e) {
					TooltipUtil.showErrorTip(Messages.PlayMovieAction_ExecuteErrorTitle, e.getLocalizedMessage());
					return new Status(IStatus.ERROR, "unknown", IStatus.ERROR, Messages.PlayMovieAction_ExecuteErrorTitle, e);
				}

				continue;
			}
		}
		return Status.OK_STATUS;
	}

}
