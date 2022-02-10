/**
 *
 */
package org.aquarius.downloader.core.spi;

import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.log.LogUtil;
import org.aquarius.util.base.AbstractComparable;
import org.slf4j.Logger;

/**
 * Download factory to create downloader.
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSegmentDownloaderFactory extends AbstractComparable<AbstractSegmentDownloaderFactory> {

	public static final String TypeHttp = "http";

	public static final String TypeFtp = "ftp";

	public static final String TypeHls = "hls";

	protected Logger logger = LogUtil.getLogger(this.getClass());

	public abstract boolean isAcceptable(DownloadTask downloadTask);

	/**
	 *
	 * @param downloadTask
	 * @return
	 */
	public AbstractSegmentDownloader createDownloader(DownloadTask downloadTask) {

		downloadTask.setMaxThreadCount(getSuggestedThreadCount());

		return this.doCreateDownloader(downloadTask);
	}

	/**
	 * @return
	 */
	protected int getSuggestedThreadCount() {
		return DownloadManager.getInstance().getConfiguration().getThreadCountPerTask();
	}

	/**
	 * Do create downloader
	 *
	 * @param downloadTask
	 * @return
	 */
	protected abstract AbstractSegmentDownloader doCreateDownloader(DownloadTask downloadTask);

	/**
	 * If the task is split into multi segments.<BR>
	 * Return true.<BR>
	 * Else this task will be split int to multi segments.<BR>
	 *
	 * @param downloadTask
	 * @return
	 */
	public boolean check(DownloadTask downloadTask) {

		downloadTask.setType(this.getType());

		if (downloadTask.getSegmentSize() > 0) {
			return true;
		} else {
			return this.doCheck(downloadTask);
		}
	}

	/**
	 * Check the task and split into multi segments.<BR>
	 *
	 * @param downloadTask
	 * @return
	 */
	protected abstract boolean doCheck(DownloadTask downloadTask);

	/**
	 * Return the downloader type.<BR>
	 *
	 * @return
	 */
	public abstract String getType();
}
