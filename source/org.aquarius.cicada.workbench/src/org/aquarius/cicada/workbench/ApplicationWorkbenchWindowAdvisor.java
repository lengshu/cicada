package org.aquarius.cicada.workbench;

import java.lang.reflect.InvocationTargetException;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.web.WebAccessorManager;
import org.aquarius.cicada.workbench.page.WorkbenchConfiguration;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.services.IDisposable;
import org.slf4j.Logger;

/**
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor implements IDisposable {

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 *
	 * @param configurer
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void postWindowCreate() {
		PreferenceManager preferenceManager = PlatformUI.getWorkbench().getPreferenceManager();

		preferenceManager.remove("org.eclipse.ui.browser.preferencePage");
		preferenceManager.remove("org.eclipse.ui.preferencePages.Workbench");

		super.postWindowCreate();

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		IWorkbenchWindow window = configurer.getWindow();
		window.getShell().setMaximized(true);

	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShellStyle(configurer.getShellStyle() | SWT.MAX);

		configurer.setShowCoolBar(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowStatusLine(true);
		configurer.setInitialSize(new Point(1200, 800));

		configurer.setTitle("cicada");

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void postWindowOpen() {

		super.postWindowOpen();

		this.getWindowConfigurer().getWindow().getActivePage().closeAllEditors(false);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean preWindowShellClose() {

		WebAccessorManager.getInstance().dispose();

		IWorkbenchWindow window = this.getWindowConfigurer().getWindow();

		if (DownloadManager.getInstance().isTaskRunning()) {

			IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

			if (!SwtUtil.openConfirm(window.getShell(), Messages.ApplicationWorkbenchWindowAdvisor_WarnDownloadingDialogTitle,
					Messages.ApplicationWorkbenchWindowAdvisor_WarnDownloadingDialogMessage, store, WorkbenchConfiguration.Key_ConfirmQuit)) {
				return false;
			}
		}

		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(window.getShell());

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				monitor.beginTask(Messages.ApplicationWorkbenchWindowAdvisor_SaveDownloadTasks, IProgressMonitor.UNKNOWN);
				monitor.setTaskName(Messages.ApplicationWorkbenchWindowAdvisor_SaveDownloadTasks);

				closeJobs();

				try {
					RuntimeManager.getInstance().getCacheService().shutdown();

					DownloadManager.getInstance().close();
				} catch (Exception e) {
					ApplicationWorkbenchWindowAdvisor.this.logger.debug("close resources", e);
				} finally {
					monitor.done();
				}
			}
		};

		try {
			progressMonitorDialog.run(false, true, runnable);
		} catch (Exception e) {
			this.logger.error("progress dialog ", e); //$NON-NLS-1$
		}

		this.getWindowConfigurer().getWindow().getActivePage().closeAllEditors(false);

		return super.preWindowShellClose();

	}

	private void closeJobs() {
		IWorkbenchWindow window = this.getWindowConfigurer().getWindow();

		try {
			IJobManager jobManager = window.getService(IJobManager.class);
			jobManager.cancel(AbstractCancelableJob.FamilyName);

		} catch (Exception e) {
			// Nothing to do
		}

	}

}
