package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.eclipse.jface.action.Action;

/**
 * Users can open the browser to update sth .<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NewBrowserAction extends Action {

	public NewBrowserAction(String label) {
		super(label);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		WebAccessorHelper.openBrowserForSetting();
	}

}
