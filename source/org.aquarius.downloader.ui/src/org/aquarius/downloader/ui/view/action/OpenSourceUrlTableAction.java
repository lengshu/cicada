/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.util.List;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.util.DesktopUtil;

/**
 * Open the refer url of the specified tasks.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenSourceUrlTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public OpenSourceUrlTableAction(ITaskService taskService) {
		super(taskService);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		List<DownloadTask> downloadTaskList = this.getTaskService().getSelectedTasks();

		for (DownloadTask downloadTask : downloadTaskList) {
			DesktopUtil.openWebpages(downloadTask.getPageUrl());
		}

	}

}
