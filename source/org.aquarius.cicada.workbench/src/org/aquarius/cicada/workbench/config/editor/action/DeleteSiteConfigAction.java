/**
 *
 */
package org.aquarius.cicada.workbench.config.editor.action;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.config.editor.SiteConfigEditor;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Delete the current site Config.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DeleteSiteConfigAction extends AbstractSiteConfigEditorAction {

	/**
	 * @param text
	 */
	public DeleteSiteConfigAction(String text) {
		super(text);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/delete.png"));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doRun(SiteConfigEditor siteConfigEditor) {

		if (MessageDialog.openConfirm(siteConfigEditor.getSite().getShell(), Messages.DeleteSiteConfigAction_ConfirmDialogTitle,
				Messages.DeleteSiteConfigAction_ConfirmDialogMessage)) {
			SiteConfigManager.getInstance().removeSiteConfig(siteConfigEditor.getSiteConfig());
			SwtUtil.closeEditor(siteConfigEditor, false);
		}
	}
}
