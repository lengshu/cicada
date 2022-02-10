/**
 *
 */
package org.aquarius.downloader.ui.view.action;

import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.base.AbstractTableAction;
import org.aquarius.downloader.ui.view.dialog.NewDownloadTaskDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * Action to add a task.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AddTableAction extends AbstractTableAction {

	/**
	 * @param taskService
	 */
	public AddTableAction(ITaskService taskService) {
		super(taskService);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		Shell shell = Display.getDefault().getActiveShell();
		NewDownloadTaskDialog dialog = new NewDownloadTaskDialog(shell);

		if (dialog.open() == IDialogConstants.OK_ID) {
			DownloadTask downloadTask = dialog.getDownloadTask();

			DownloadManager.getInstance().clearDownloadTasks();
			DownloadManager.getInstance().addDownloadTask(downloadTask);
		}

	}

}
