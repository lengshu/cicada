package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.eclipse.jface.action.Action;

/**
 * Clear the disk cache.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ClearCacheAction extends Action {

	/**
	 *
	 * @param label
	 */
	public ClearCacheAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_CLEAR_CACHE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_CLEAR_CACHE);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/clearCache.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		RuntimeManager.getInstance().getCacheService().clear();
	}

}
