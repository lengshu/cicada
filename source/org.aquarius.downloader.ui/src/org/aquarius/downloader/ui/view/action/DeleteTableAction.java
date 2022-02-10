/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Delete task<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DeleteTableAction extends AbstractTableAction {

	private boolean alsoDeleteFiles;

	/**
	 * @param taskService
	 */
	public DeleteTableAction(ITaskService taskService, boolean alsoDeleteFiles) {
		super(taskService);
		this.alsoDeleteFiles = alsoDeleteFiles;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		List<DownloadTask> downloadTaskList = this.getTaskService().getSelectedTasks();

		if (CollectionUtils.isNotEmpty(downloadTaskList)) {

			IPreferenceStore store = DownloadActivator.getDefault().getPreferenceStore();

			if (SwtUtil.openConfirm(getShell(), Messages.DeleteTableAction_ConfirmDeleteDialogTitle, Messages.DeleteTableAction_ConfirmDeleteDialogMessage,
					store, DownloadConfiguration.Key_ConfirmDelete)) {
				this.getTaskService().deleteTasks(downloadTaskList, this.alsoDeleteFiles);
			}

		}
	}

}
