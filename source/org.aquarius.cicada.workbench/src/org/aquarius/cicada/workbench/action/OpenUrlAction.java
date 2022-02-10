package org.aquarius.cicada.workbench.action;

import org.aquarius.util.DesktopUtil;
import org.eclipse.jface.action.Action;

/**
 * Open Url action.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenUrlAction extends Action {

	private String url;

	/**
	 * 
	 * @param label
	 */
	public OpenUrlAction(String label, String url) {
		super(label);

		this.url = url;

	}

	@Override
	public void run() {
		DesktopUtil.openWebpages(this.url);

	}

}
