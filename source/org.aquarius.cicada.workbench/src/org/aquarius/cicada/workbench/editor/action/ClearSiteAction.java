package org.aquarius.cicada.workbench.editor.action;

import java.lang.reflect.InvocationTargetException;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Clear the disk cache.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ClearSiteAction extends AbstractSiteEditorAction {

	private Logger log = LogUtil.getLogger(getClass());

	/**
	 *
	 * @param label
	 */
	public ClearSiteAction(String label) {
		super(label);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/clearSite.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doRun(SiteMultiPageEditor siteEditor) {

		Shell shell = siteEditor.getSite().getShell();

		if (!MessageDialog.openConfirm(shell, Messages.ConfirmDialogTitle, Messages.ClearSiteAction_ConfirmMessage)) {
			return false;
		}

		if (!MessageDialog.openConfirm(shell, Messages.ConfirmDialogTitle, Messages.ClearSiteAction_ConfirmAgainMessage)) {
			return false;
		}

		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
		SwtUtil.closeEditor(siteEditor, false);

		try {
			progressDialog.run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					Site site = siteEditor.getMovieSite();

					site.clearAllData();

					RuntimeManager.getInstance().getStoreService().clearSite(site.getSiteName());

				}
			});
		} catch (Exception e) {
			this.log.error("clear site", e);
			TooltipUtil.showErrorTip(Messages.ErrorDialogTitle, Messages.ClearSiteAction_ErrorMessage);
		}

		return false;
	}

}
