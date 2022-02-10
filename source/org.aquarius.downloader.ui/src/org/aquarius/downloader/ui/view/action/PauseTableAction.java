/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.util.List;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;

/**
 * Pause tasks.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class PauseTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public PauseTableAction(ITaskService taskService) {
		super(taskService);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		List<DownloadTask> downloadTaskList = this.getTaskService().getSelectedTasks();
		this.getTaskService().pauseTasks(downloadTaskList);
	}

}
