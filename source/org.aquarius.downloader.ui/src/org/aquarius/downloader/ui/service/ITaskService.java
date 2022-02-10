/**
 *
 */
package org.aquarius.downloader.ui.service;

import java.util.List;

import org.aquarius.downloader.core.DownloadTask;

/**
 * Task service for beging managed by ui.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface ITaskService {

	/**
	 * Return selected tasks.<BR>
	 *
	 * @return
	 */
	public List<DownloadTask> getSelectedTasks();

	/**
	 * Return all tasks.<BR>
	 *
	 * @return
	 */
	public List<DownloadTask> getAllTasks();

	/**
	 * Refresh tasks <BR>
	 */
	public void refreshTasks();

	/**
	 * Update tasks.<BR>
	 */
	public void updateTasks();

	/**
	 * resume tasks.<BR>
	 *
	 * @param downloadTaskList
	 */
	public void resumeTasks(List<DownloadTask> downloadTaskList);

	/**
	 * pause tasks.<BR>
	 *
	 * @param downloadTaskList
	 */
	public void pauseTasks(List<DownloadTask> downloadTaskList);

	/**
	 * delete tasks.<BR>
	 *
	 * @param downloadTaskList
	 * @param alsoDeleteFiles
	 */
	public void deleteTasks(List<DownloadTask> downloadTaskList, boolean alsoDeleteFiles);

	/**
	 * Move the selected tasks up.<BR>
	 */
	public void moveUp();

	/**
	 * Move the selected tasks down.<BR>
	 */
	public void moveDown();

	/**
	 * Move the selected tasks to top.<BR>
	 */
	public void moveTop();

	/**
	 * Move the selected tasks to bottom.<BR>
	 */
	public void moveBottom();

}
