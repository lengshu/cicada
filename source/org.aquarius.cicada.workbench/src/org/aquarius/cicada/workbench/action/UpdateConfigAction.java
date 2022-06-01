/**
 * 
 */
package org.aquarius.cicada.workbench.action;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.job.UpdateConfigJob;
import org.eclipse.jface.action.Action;

/**
 * Update the config files.
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class UpdateConfigAction extends Action {

	/**
	 * @param text
	 */
	public UpdateConfigAction(String text) {
		super(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {

		new UpdateConfigJob(Messages.ApplicationActionBarAdvisor_UpdateConfig, true).schedule();
	}

}
