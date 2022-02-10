package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.workbench.intro.UserIntroPart;
import org.eclipse.jface.action.Action;

/**
 * Help action.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HelpAction extends Action {

	/**
	 * 
	 * @param label
	 */
	public HelpAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_HELP);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_HELP);

	}

	@Override
	public void run() {

		UserIntroPart.openHelp();
	}

}
