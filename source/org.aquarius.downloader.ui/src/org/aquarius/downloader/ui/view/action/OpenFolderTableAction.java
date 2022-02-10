/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.io.File;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.downloader.core.DownloadManager;
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
public class OpenFolderTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public OpenFolderTableAction(ITaskService taskService) {
		super(taskService);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		List<DownloadTask> downloadTaskList = this.getTaskService().getSelectedTasks();

		if (CollectionUtils.isEmpty(downloadTaskList)) {
			File folder = new File(DownloadManager.getInstance().getConfiguration().getDefaultDownloadFolder());
			DesktopUtil.openFolder(folder);
			return;
		}

		for (DownloadTask downloadTask : downloadTaskList) {
			String folderString = downloadTask.getFolder();
			File folder = new File(folderString);

			DesktopUtil.openFolder(folder);
		}

	}

}
