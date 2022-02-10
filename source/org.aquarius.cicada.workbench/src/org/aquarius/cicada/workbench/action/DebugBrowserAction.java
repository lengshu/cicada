package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.eclipse.jface.action.Action;

/**
 * Popup a browser to debug script.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DebugBrowserAction extends Action {

	public DebugBrowserAction(String label) {
		super(label);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		this.doRun();

	}

	/**
	 * @param
	 */
	private void doRun() {

		BrowserUtil.openDebugPage(null);

	}

}
