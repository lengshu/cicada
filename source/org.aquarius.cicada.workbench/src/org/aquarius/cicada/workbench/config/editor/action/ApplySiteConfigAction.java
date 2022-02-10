/**
 *
 */
package org.aquarius.cicada.workbench.config.editor.action;

import java.io.IOException;

import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.config.editor.SiteConfigEditor;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Apple the current site Config.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ApplySiteConfigAction extends AbstractSiteConfigEditorAction {

	/**
	 * @param text
	 */
	public ApplySiteConfigAction(String text) {
		super(text);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/apply.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doRun(SiteConfigEditor siteConfigEditor) {

		siteConfigEditor.doSave(null);

		if (MessageDialog.openConfirm(siteConfigEditor.getSite().getShell(), Messages.ApplySiteConfigAction_ConfirmDialogTitle,
				Messages.ApplySiteConfigAction_ConfirmDialogMessage)) {
			try {
				SiteConfig siteConfig = siteConfigEditor.getSiteConfig();

				SiteConfigManager.getInstance().applySiteConfig(siteConfig);
			} catch (IOException e) {
				SwtUtil.showErrorDialog(siteConfigEditor.getSite().getShell(), Messages.ApplySiteConfigAction_ErrorDialogTitle,
						Messages.ApplySiteConfigAction_ErrorDialogMessage, e);
			}

		}
	}

}
