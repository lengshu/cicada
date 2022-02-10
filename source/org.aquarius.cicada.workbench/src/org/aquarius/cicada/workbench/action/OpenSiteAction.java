package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.aquarius.cicada.core.model.SiteConfig;
import org.eclipse.jface.action.Action;

/**
 * Users can open the browser to update the web site .<BR>
 * For example,users can set the lang or login in.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenSiteAction extends Action {

	private SiteConfig siteConfig;

	public OpenSiteAction(String label, SiteConfig siteConfig) {
		super(label);

		this.siteConfig = siteConfig;
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		WebAccessorHelper.openBrowserForSetting(this.siteConfig.getMainPage());
	}

}
