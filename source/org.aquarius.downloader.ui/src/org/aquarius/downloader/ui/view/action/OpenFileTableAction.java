/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.io.File;
import java.util.List;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.util.DesktopUtil;

/**
 * Open the file or folder of the specified tasks.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenFileTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public OpenFileTableAction(ITaskService taskService) {
		super(taskService);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		List<DownloadTask> downloadTaskList = this.getTaskService().getSelectedTasks();

		for (DownloadTask downloadTask : downloadTaskList) {
			File file = downloadTask.getFile();

			DesktopUtil.openFile(file);
		}

	}

}
