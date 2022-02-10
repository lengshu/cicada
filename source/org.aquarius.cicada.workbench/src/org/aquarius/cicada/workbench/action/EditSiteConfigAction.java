package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.config.editor.SiteConfigEditor;
import org.aquarius.cicada.workbench.config.editor.SiteConfigEditorInput;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * Open a exist site config to edit.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class EditSiteConfigAction extends Action {

	private IWorkbenchWindow window;

	private Logger logger = LogUtil.getLogger(this.getClass());

	private SiteConfig siteConfig;

	/**
	 *
	 * @param window
	 * @param siteName
	 * @param siteConfig
	 */
	public EditSiteConfigAction(IWorkbenchWindow window, String siteName, SiteConfig siteConfig) {
		super(siteName);

		this.window = window;
		this.siteConfig = siteConfig;

	}

	@Override
	public void run() {

		SiteConfigEditorInput editorInput = new SiteConfigEditorInput(this.siteConfig);
		try {
			this.window.getActivePage().openEditor(editorInput, SiteConfigEditor.EditorID);
		} catch (PartInitException e) {
			this.logger.error("open site config editor:", e); //$NON-NLS-1$
			SwtUtil.showErrorDialog(this.window.getShell(), WorkbenchActivator.PLUGIN_ID, Messages.EditSiteConfigAction_ErrorDialogTitle, e);
		}
	}

}
