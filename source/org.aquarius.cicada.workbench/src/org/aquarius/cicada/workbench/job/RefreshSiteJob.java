/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.text.MessageFormat;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.SiteEditorInput;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.job.OrderedSchedulingRule;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * Refresh a site.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshSiteJob extends AbstractCancelableJob {

	private String siteName;

	private boolean forceOpenEditor;

	private static Logger logger = LogUtil.getLogger(RefreshSiteJob.class);

	/**
	 * @param name
	 */
	public RefreshSiteJob(String name, String siteName, boolean forceOpenEditor) {
		super(name);
		this.siteName = siteName;
		this.forceOpenEditor = forceOpenEditor;

		this.setRule(new OrderedSchedulingRule(getFamilyName(siteName)));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		Site site = RuntimeManager.getInstance().loadSite(this.siteName);

		try {
			site.setRefreshing(this, true);
			doRun(site, monitor);
		} finally {
			site.setRefreshing(this, false);
		}

		monitor.done();

		return Status.OK_STATUS;
	}

	/**
	 * @param site
	 */
	private void doRun(Site site, IProgressMonitor monitor) {

		int oldSize = site.getMovieSize();

		IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);

		try {
			MovieParserHelper.parseSite(site, processMonitor);
		} catch (Exception e) {

			if (monitor.isCanceled()) {
				return;
			}

			logger.error("refresh site", e);
		}

		int sizeInterval = site.getMovieSize() - oldSize;

		String title = MessageFormat.format(Messages.AutoRefreshJob_RefreshSiteTitle, RefreshSiteJob.this.siteName);
		String message = MessageFormat.format(Messages.AutoRefreshJob_RefreshSiteMessage, sizeInterval);

		TooltipUtil.showInfoTip(title, message);

		refreshEditorForSite(site, this.forceOpenEditor);
	}

	/**
	 * @param site
	 */
	public static void refreshEditorForSite(Site site, boolean forceOpenEditor) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				SiteEditorInput input = new SiteEditorInput(site);

				IWorkbenchPage workbenchPage = WorkbenchUtil.getActivePage();
				if (null == workbenchPage) {
					return;
				}

				IEditorPart eidtor = workbenchPage.findEditor(input);

				if (null == eidtor) {
					if (forceOpenEditor) {
						try {
							eidtor = workbenchPage.openEditor(input, SiteMultiPageEditor.EditorID);
						} catch (PartInitException e) {
							RefreshSiteJob.logger.error("open editor", e);
						}
					}
				}

				if (eidtor instanceof SiteMultiPageEditor) {
					((SiteMultiPageEditor) eidtor).refresh();
					workbenchPage.activate(eidtor);
				}
			}
		});
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean belongsTo(Object family) {
		if (family instanceof String) {
			String familyName = (String) family;
			return getFamilyName(this.siteName).equalsIgnoreCase(familyName);
		} else {
			return false;
		}
	}

}
