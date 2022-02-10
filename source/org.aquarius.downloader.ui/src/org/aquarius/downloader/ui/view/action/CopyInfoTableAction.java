/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.ui.util.ClipboardUtil;

/**
 * Copy task info to clipboard.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CopyInfoTableAction extends AbstractTableAction {

	private String propertyName;

	/**
	 * @param taskService
	 */
	public CopyInfoTableAction(ITaskService taskService, String propertyName) {
		super(taskService);

		this.propertyName = propertyName;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		List<DownloadTask> downloadTaskList = this.getTaskService().getSelectedTasks();
		List<String> stringList = new ArrayList<String>();

		for (DownloadTask downloadTask : downloadTaskList) {

			if (StringUtils.isBlank(this.propertyName)) {
				stringList.add(downloadTask.toString());
			} else {
				try {
					String string = BeanUtils.getProperty(downloadTask, this.propertyName);
					stringList.add(string);

				} catch (Exception e) {
					super.logger.error("copyinfo ", e);
				}
			}
		}

		ClipboardUtil.setClipboardString(StringUtils.join(stringList, "\r\n"));
	}

}
