/**
 *
 */
package org.aquarius.cicada.workbench.action;

import org.apache.commons.validator.routines.RegexValidator;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.config.editor.SiteConfigEditor;
import org.aquarius.cicada.workbench.config.editor.SiteConfigEditorInput;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.io.FileUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * Create a new site.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CreateSiteConfigAction extends Action {

	private static RegexValidator regexValidator = new RegexValidator(FileUtil.ValidFileRegex);

	private final IWorkbenchWindow window;

	public CreateSiteConfigAction(IWorkbenchWindow window, String label) {
		this.window = window;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SITE_CONFIG_CREATE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_SITE_CONFIG_CREATE);

	}

	@Override
	public void run() {

		IInputValidator validator = new IInputValidator() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String isValid(String newText) {

				if (!regexValidator.isValid(newText)) {
					return Messages.CreateSiteConfigAction_FileNamePatternErrorMessage;
				}

				SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfig(newText);

				if (null != siteConfig) {
					return Messages.CreateSiteConfigAction_DuplicatedSiteErrorMessage;
				}

				return null;
			}

		};

		InputDialog dialog = new InputDialog(this.window.getShell(), Messages.CreateSiteConfigAction_CreateDialogTitle,
				Messages.CreateSiteConfigAction_CreateDialogMessage, "unnamed", validator); //$NON-NLS-1$

		if (Dialog.OK == dialog.open()) {
			SiteConfigEditorInput editorInput = new SiteConfigEditorInput(dialog.getValue());
			try {
				this.window.getActivePage().openEditor(editorInput, SiteConfigEditor.EditorID);
			} catch (PartInitException e) {

				SwtUtil.showErrorDialog(this.window.getShell(), WorkbenchActivator.PLUGIN_ID, Messages.CreateSiteConfigAction_ErrorDialogTitle, e);
			}
		}
	}
}
