/**
 *
 */
package org.aquarius.downloader.ui.view.action.base;

import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * The base action class for task table.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractTableAction extends Action implements IShellProvider {

	private ITaskService taskService;

	protected Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * @param taskService
	 */
	public AbstractTableAction(ITaskService taskService) {
		super();
		this.taskService = taskService;
	}

	/**
	 * @return the taskService
	 */
	protected ITaskService getTaskService() {
		return this.taskService;
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Shell getShell() {
		return SwtUtil.findShell();
	}

}
