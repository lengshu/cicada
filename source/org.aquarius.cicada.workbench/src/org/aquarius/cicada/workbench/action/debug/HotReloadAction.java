package org.aquarius.cicada.workbench.action.debug;

import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.service.manager.ReloadManager;
import org.aquarius.util.enu.RefreshType;

/**
 * Hot-Reload script.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HotReloadAction extends AbstractSiteEditorAction {

	/**
	 *
	 * @param label
	 */
	public HotReloadAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_HOT_RELOAD);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_HOT_RELOAD);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/hotReload.png"));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType doRun(SiteMultiPageEditor siteEditor) {

		ReloadManager.getInstance().reloadAll();

		return RefreshType.None;
	}

}
