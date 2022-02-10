/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.util.List;

import org.aquarius.cicada.core.helper.MovieDownloadAnalyserHelper;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.ui.dialog.ListInfoDialog;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * Generate the download urls for the specified movies.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class GenerateDownloadUrlJob extends AbstractCancelableJob {

	private AbstractDownloadListGenerator downloadListGenerator;

	private List<Movie> selectedMovieList;

	/**
	 * @param name
	 */
	public GenerateDownloadUrlJob(String name, AbstractDownloadListGenerator downloadListGenerator, List<Movie> selectedMovieList) {
		super(name);
		this.downloadListGenerator = downloadListGenerator;
		this.selectedMovieList = selectedMovieList;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		IProcessMonitor monitorProxy = new ProcessMonitorProxy(monitor);

		if (!this.downloadListGenerator.isUsePrimitiveUrl()) {
			MovieParserHelper.parseMovieDetailInfo(true, this.selectedMovieList, monitorProxy);

			MovieDownloadAnalyserHelper.analyseMovieDownloadUrls(this.selectedMovieList, true, monitorProxy);

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
		}

		String defaultDownloadFolder = DownloadManager.getInstance().getConfiguration().getDefaultDownloadFolder();
		String downloadUrls = this.downloadListGenerator.generateDownloadList(this.selectedMovieList, defaultDownloadFolder);

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = new Shell(SwtUtil.findDisplay(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
				new ListInfoDialog(shell, Messages.GenerateDownloadUrlJob_DialogTitle, downloadUrls).open();
			}
		});

		return Status.OK_STATUS;
	}

}
