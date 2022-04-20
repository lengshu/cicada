package org.aquarius.cicada.workbench.action;

import java.io.IOException;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Reset resource.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ResetResourceAction extends Action {

	/**
	 *
	 * @param label
	 */
	public ResetResourceAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_RESET_RESOURCE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_RESET_RESOURCE);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/resetResource.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {

		Shell shell = SwtUtil.findShell();

		if (MessageDialog.openConfirm(shell, Messages.ResetResourceAction_ConfirmTitle, Messages.ResetResourceAction_ConfirmMessage)) {
			String workingFolder = Starter.getInstance().getWorkingFolder();
			try {
				WorkbenchActivator.getDefault().deployResources(workingFolder, true);
			} catch (IOException e) {
				SwtUtil.showErrorDialog(shell, WorkbenchActivator.PLUGIN_ID, Messages.ResetResourceAction_ErrorTitle, e);
			}
		}

	}

}
