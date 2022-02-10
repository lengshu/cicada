/**
 *
 */
package org.aquarius.cicada.workbench.action;

import java.util.ArrayList;
import java.util.List;

import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.action.base.AbstractDropDownAction;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Drop down action for multi edit site action.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class EditSiteConfigDropDownAction extends AbstractDropDownAction {

	private final IWorkbenchWindow window;

	/**
	 * 
	 * @param window
	 * @param label
	 */
	public EditSiteConfigDropDownAction(IWorkbenchWindow window, String label) {
		super(label);
		this.window = window;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SITE_CONFIG_EDIT);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_SITE_CONFIG_EDIT);

	}

	/**
	 * 
	 * @return
	 */
	@Override
	public List<IContributionItem> createActionItems() {

		List<IContributionItem> list = new ArrayList<>();

		List<SiteConfig> siteConfigList = SiteConfigManager.getInstance().getAllSiteConfigs();
		for (SiteConfig siteConfig : siteConfigList) {
			Action action = new EditSiteConfigAction(EditSiteConfigDropDownAction.this.window, siteConfig.getSiteName(), siteConfig);
			ActionContributionItem item = new ActionContributionItem(action);
			list.add(item);
		}

		return list;
	}

}
