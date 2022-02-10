package org.aquarius.cicada.workbench.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * Reset the perspective to the default layout.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ResetPerspectiveAction extends Action {

	public ResetPerspectiveAction(String label) {
		super(label);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
	}

}
