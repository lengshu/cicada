package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.core.spi.web.IWebAccessorVisible;
import org.aquarius.cicada.core.web.WebAccessorManager;
import org.eclipse.jface.action.Action;

/**
 * Show the default browser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ShowBrowserAction extends Action {

	/**
	 *
	 * @param label
	 */
	public ShowBrowserAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SHOW_BROWSER);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_SHOW_BROWSER);

		IWebAccessorVisible visibleState = WebAccessorManager.getInstance().getDefaultWebAccessorService().getVisibleState();

		if (visibleState == IWebAccessorVisible.none) {
			this.setEnabled(false);
		}

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		WebAccessorManager.getInstance().getDefaultWebAccessorService().setVisible(true);
	}
}
