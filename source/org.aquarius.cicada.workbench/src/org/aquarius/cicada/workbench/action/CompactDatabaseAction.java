package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.jface.action.Action;

/**
 *
 * Compact database.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CompactDatabaseAction extends Action {

	/**
	 *
	 * @param label
	 */
	public CompactDatabaseAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_COMPACT_DATABASE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_COMPACT_DATABASE);

	}

	@Override
	public void run() {
		WorkbenchActivator.getDefault().setCompactDatabase();
		TooltipUtil.showInfoTip(Messages.InfoDialogTitle, Messages.CompactDatabaseAction_Message);
	}
}
