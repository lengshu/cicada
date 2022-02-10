/**
 * 
 */
package org.aquarius.ui.action;

import org.eclipse.jface.action.IContributionManager;

/**
 * It can contribute actions to menu or toolbar.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public interface IActionBarContributor {

	/**
	 * Fill menu or toolbar.<BR>
	 *
	 * @param contributionManager
	 */
	public void contribute(IContributionManager contributionManager);

}
