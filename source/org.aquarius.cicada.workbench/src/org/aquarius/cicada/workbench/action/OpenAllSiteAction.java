package org.aquarius.cicada.workbench.action;

import java.util.List;

import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.eclipse.jface.action.Action;

/**
 * Open browser for all site.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenAllSiteAction extends Action {

	public OpenAllSiteAction(String label) {
		super(label);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		List<SiteConfig> siteConfigList = SiteConfigManager.getInstance().getEnableSiteConfigs();
		for (SiteConfig siteConfig : siteConfigList) {
			try {
				WebAccessorHelper.openBrowserForSetting(siteConfig.getMainPage());
			} catch (Exception e) {
				// Nothing to do
			}
		}

	}

}
