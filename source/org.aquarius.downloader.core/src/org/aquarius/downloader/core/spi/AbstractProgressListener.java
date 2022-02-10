/**
 *
 */
package org.aquarius.downloader.core.spi;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.util.base.AbstractComparable;

/**
 *
 * Progress listener.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractProgressListener extends AbstractComparable<AbstractProgressListener> {

	/**
	 * This method will be invoked before download task start.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onBefore(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked after download task finished.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onFinish(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked when tasks update like download more bytes.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onUpdate(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked when tasks is added to the queue.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onAdd(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked when tasks is removed from the queue.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onDelete(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked after tasks is paused.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onPause(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked after tasks is resumed for download.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onResume(DownloadTask... downloadTasks) {

	}

	/**
	 * This method will be invoked after tasks is stopped for error.<BR>
	 *
	 * @param downloadTasks
	 */
	public void onError(DownloadTask... downloadTasks) {

	}
}
