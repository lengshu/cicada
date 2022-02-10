/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.swt.widgets.FileDialog;

/**
 * Export tasks to external file.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ExportTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public ExportTableAction(ITaskService taskService) {
		super(taskService);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		FileDialog fileDialog = new FileDialog(this.getShell());
		String fileName = fileDialog.open();

		if (StringUtils.isNotBlank(fileName)) {
			List<DownloadTask> taskList = DownloadManager.getInstance().getAllTasks();

			try (OutputStream outputStream = new FileOutputStream(fileName)) {
				DownloadManager.getInstance().getTaskStoreService().exportTasks(taskList, null);
			} catch (Exception e) {
				SwtUtil.showErrorDialog(getShell(), DownloadActivator.PLUGIN_ID, Messages.ExportTableAction_ErrorTitle, e);
			}
		}

	}

}
