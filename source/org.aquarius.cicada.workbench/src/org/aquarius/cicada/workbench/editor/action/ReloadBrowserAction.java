package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteBrowserEditor;
import org.eclipse.jface.action.Action;

/**
 * Refresh the current browser to refresh.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ReloadBrowserAction extends Action {

	private SiteBrowserEditor browserEditor;

	/**
	 *
	 * @param text
	 */
	public ReloadBrowserAction(SiteBrowserEditor browserEditor, String text) {
		super(text);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_RELOAD_BROWSER);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_RELOAD_BROWSER);
		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/reloadBrowser.png")); //$NON-NLS-1$

		this.browserEditor = browserEditor;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		browserEditor.reload();

	}

}