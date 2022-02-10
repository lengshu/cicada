/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.MovieDownloadAnalyserHelper;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.control.FixedMovieDownloadComposite;
import org.aquarius.cicada.workbench.model.FixedMovieListService;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.job.OrderedSchedulingRule;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.net.HttpUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Load and parse a movie to download.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ParseMovieJob extends AbstractCancelableJob {

	private Logger logger = LogUtil.getLogger(this.getClass());

	private boolean directDownload;

	private List<String> urlStringList;

	/**
	 * 
	 * @param name
	 * @param directDownload
	 * @param urlStringList
	 */
	public ParseMovieJob(String name, boolean directDownload, List<String> urlStringList) {
		super(name);
		this.urlStringList = urlStringList;
		this.setRule(new OrderedSchedulingRule(this.getClass().getName()));

		this.directDownload = directDownload;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		return doRun(monitor);
	}

	/**
	 * @param monitor
	 * @return
	 */
	private IStatus doRun(IProgressMonitor monitor) {
		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		ProcessMonitorProxy monitorProxy = new ProcessMonitorProxy(monitor);

		List<IStatus> statusList = new ArrayList<IStatus>();

		List<Movie> movieList = new ArrayList<Movie>();

		for (int i = 0; i < this.urlStringList.size(); i++) {

			String urlString = this.urlStringList.get(i);

			AbstractMovieParser movieParser = RuntimeManager.getInstance().findMovieParserByUrl(urlString);

			if (null != movieParser) {
				Movie movie = new Movie();
				movie.setId(i + 1);
				movie.setPageUrl(urlString);
				movie.setSite(movieParser.getName());

				try {
					MovieParserHelper.parseMovieDetailInfo(true, movie, monitorProxy);

				} catch (Exception e) {
					this.logger.error("Parse movie detail info ", e); //$NON-NLS-1$
				}

				movieList.add(movie);
				statusList.add(Status.OK_STATUS);
			} else {
				String host = HttpUtil.getHost(urlString);
				String errorMessage = MessageFormat.format(Messages.ParseMovieJob_UrlNotSupported, host);
				TooltipUtil.showErrorTip(Messages.ErrorDialogTitle, errorMessage);
			}
		}

		if (movieList.size() > 0) {

			if (this.directDownload) {
				DownloadMovieJob downloadMovieJob = new DownloadMovieJob(Messages.DownloadAction_DownloadMovieJobName, movieList, false, false);

				downloadMovieJob.schedule();
			} else {

				for (Movie movie : movieList) {
					MovieDownloadAnalyserHelper.analyseDownloadUrls(movie.getDownloadInfoList(), true, monitorProxy);
				}

				SwtUtil.findDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						Shell shell = new Shell(SwtUtil.findDisplay(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);

						shell.setLayout(new FillLayout());

						FixedMovieListService queryMovieList = new FixedMovieListService(movieList);
						FixedMovieDownloadComposite composite = new FixedMovieDownloadComposite(shell, SWT.FLAT);

						composite.setService(queryMovieList);

						shell.setSize(1200, 800);
						shell.open();
					}
				});
			}
		}

//		MultiStatus multiStatus = new MultiStatus(WorkbenchActivator.PLUGIN_ID, code,
//				statusList.toArray(new IStatus[statusList.size()]), Messages.ErrorDialogTitle, null);
//
//		return multiStatus;

		return Status.OK_STATUS;
	}

}
