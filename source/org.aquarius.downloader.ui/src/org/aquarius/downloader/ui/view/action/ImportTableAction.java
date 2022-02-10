/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class ImportTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public ImportTableAction(ITaskService taskService) {
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
			DownloadManager downloadManager = DownloadManager.getInstance();
			try (InputStream inputStream = new FileInputStream(fileName)) {
				List<DownloadTask> taskList = downloadManager.getTaskStoreService().parseTasks(null);
				List<String> stringList = new ArrayList<>();

				for (DownloadTask downloadTask : taskList) {
					Optional<DownloadTask> oldTask = downloadManager.findTaskByTagId(downloadTask.getTagId());
					if (oldTask.isPresent()) {
						stringList.add(oldTask.get().getTitle());
					} else {
						downloadManager.addDownloadTask(downloadTask);
					}
				}

				if (!stringList.isEmpty()) {
					TooltipUtil.showInfoTip(Messages.ImportTableAction_WarnTitle, StringUtils.join(stringList, "\r\n")); // $NON-NLS-2$
				}

			} catch (Exception e) {
				SwtUtil.showErrorDialog(getShell(), DownloadActivator.PLUGIN_ID, Messages.ExportTableAction_ErrorTitle, e);
			}

		}

	}

}
