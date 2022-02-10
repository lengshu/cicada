/**
 *
 */
package org.aquarius.downloader.core.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.aquarius.downloader.core.DownloadTask;

/**
 *
 * A service to store tasks.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractTaskStoreService {

	private boolean dirty = false;

	/**
	 * Save the tasks for persistence.<BR>
	 * It will check dirty or not.<BR>
	 *
	 * @param downloadTaskList
	 * @param forceSave
	 */
	public void saveTasks(List<DownloadTask> downloadTaskList, boolean forceSave) {
		if (this.dirty || forceSave) {
			this.doSaveTasks(downloadTaskList);
		}
	}

	/**
	 * This method will be invoked when it is dirty.<BR>
	 *
	 * @param downloadTaskList
	 */
	protected abstract void doSaveTasks(List<DownloadTask> downloadTaskList);

	/**
	 * Load tasks from persistence.<BR>
	 *
	 * @return
	 */
	public abstract List<DownloadTask> loadTasks();

	/**
	 * Load tasks from specified source.<BR>
	 *
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public abstract List<DownloadTask> parseTasks(InputStream inputStream) throws IOException;

	/**
	 * Export the tasks to specified target.<BR>
	 *
	 * @param downloadTaskList
	 * @param outputStream
	 * @throws IOException
	 */
	public abstract void exportTasks(List<DownloadTask> downloadTaskList, OutputStream outputStream) throws IOException;

	/**
	 * Mark dirty for save.
	 */
	public void markDirty() {
		this.dirty = true;
	}

}
