package org.aquarius.cicada.workbench.action;

import java.util.ArrayList;
import java.util.List;

import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.action.base.AbstractDropDownAction;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;

/**
 * For drop down menu for multi sites.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenSiteDropDownAction extends AbstractDropDownAction {

	public OpenSiteDropDownAction(String label) {
		super(label);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public List<IContributionItem> createActionItems() {

		List<IContributionItem> list = new ArrayList<>();

		{
			Action action = new OpenAllSiteAction(Messages.OpenSiteDropDownAction_OpenAllSites);
			ActionContributionItem item = new ActionContributionItem(action);
			list.add(item);

			list.add(new Separator());
		}

		List<SiteConfig> siteConfigList = SiteConfigManager.getInstance().getEnableSiteConfigs();
		for (SiteConfig siteConfig : siteConfigList) {
			Action action = new OpenSiteAction(siteConfig.getSiteName(), siteConfig);
			ActionContributionItem item = new ActionContributionItem(action);
			list.add(item);
		}

		return list;
	}

}
