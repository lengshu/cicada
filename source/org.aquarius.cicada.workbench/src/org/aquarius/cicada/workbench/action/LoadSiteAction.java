package org.aquarius.cicada.workbench.action;

import java.text.MessageFormat;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.SiteEditorInput;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.job.RefreshSiteJob;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * Load the specified site.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LoadSiteAction extends Action {

	private IWorkbenchWindow window;

	private Logger logger = LogUtil.getLogger(this.getClass());

	private String siteName;

	public LoadSiteAction(IWorkbenchWindow window, String siteName) {
		super(siteName);

		this.window = window;
		this.siteName = siteName;

	}

	@Override
	public void run() {

		RuntimeManager.getInstance().getStoreService().queryAllActors();

		try {

			Site site = RuntimeManager.getInstance().loadSite(this.siteName);

			if (!site.isRefreshing()) {
				if (site.getMovieSize() == 0) {
					String message = MessageFormat.format(Messages.LoadSiteAction_ImportDialogMessage, this.siteName);
					if (MessageDialog.openQuestion(this.window.getShell(), Messages.LoadSiteAction_ImportDialogTitle, message)) {
						new RefreshSiteJob(Messages.RefreshSiteAction_RefreshSite, site.getSiteName(), false).schedule();
						return;
					}
				}
			}

			SiteEditorInput input = new SiteEditorInput(site);

			this.window.getActivePage().openEditor(input, SiteMultiPageEditor.EditorID);

		} catch (PartInitException e) {
			this.logger.error("error while opening editor:", e); //$NON-NLS-1$
			String message = MessageFormat.format(Messages.LoadSiteAction_ErrorDialogMessage, this.siteName);
			SwtUtil.showErrorDialog(this.window.getShell(), Messages.LoadSiteAction_ErrorDialogTitle, message, e);
		}
	}

}
