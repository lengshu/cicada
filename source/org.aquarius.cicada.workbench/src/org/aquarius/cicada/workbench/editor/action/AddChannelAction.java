package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;

/**
 * Add a channel to the current site.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AddChannelAction extends AbstractSiteEditorAction {

	/**
	 *
	 * @param label
	 */
	public AddChannelAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_ADD_CHANNEL);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_ADD_CHANNEL);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/add.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doRun(SiteMultiPageEditor siteEditor) {

		return false;
	}

}
