/**
 *
 */
package org.aquarius.downloader.core.spi;

import java.io.IOException;

import org.apache.commons.lang.math.RandomUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.model.Segment;
import org.aquarius.log.LogUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.slf4j.Logger;

/**
 *
 * The abstract class for segment downloader.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSegmentDownloader implements Runnable {

	protected static final int DefaultTimeOut = 10 * SystemUtil.TimeSecond;

	private static final int MaxRetryCount = 20;

	private static final int MinBufferSize = 2 * SystemUtil.DiskSizeInM;

	private static final int DefaultWaitingTime = 3 * SystemUtil.TimeSecond;

	// 8M is set
	private int bufferSize = 8 * SystemUtil.DiskSizeInM;

	private DownloadTask downloadTask;

	private Segment segment;

	protected final Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * @param downloadTask the parent task
	 */
	public AbstractSegmentDownloader(DownloadTask downloadTask) {
		super();
		this.downloadTask = downloadTask;

	}

	/**
	 * return the parent task
	 *
	 * @return the downloadTask
	 */
	public DownloadTask getDownloadTask() {
		return this.downloadTask;
	}

	/**
	 * Whether the download task is paused.
	 *
	 * @return
	 */
	protected boolean isPaused() {
		return this.downloadTask.getState() != DownloadTask.StateRunning;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		Thread.currentThread().setName("Downloading  " + this.downloadTask.getTitle());

		try {
			this.downloadTask.increaseCurrentThreadCount();
			// Increase download thread count

			doRun();
		} catch (Exception e) {
			// Nothing to do
		} finally {
			Thread.currentThread().setName("Waiting");
			DownloadManager.getInstance().decreaseCount(this.downloadTask.getDownloadUrl());

			this.downloadTask.decreaseCurrentThreadCount();
			// Anyway decrease the thread count.

		}
	}

	/**
	 * 
	 */
	private void doRun() {
		synchronized (this.downloadTask) {
			if (this.downloadTask.isAllowApply()) {
				this.downloadTask.setState(DownloadTask.StateRunning);
			} else {
				return;
			}
		}
		// CHeck whether the download task can run.

		while (this.downloadTask.shouldRetry()) {

			if (isPaused()) {
				return;
			}
			// The state changed.

			SystemUtil.sleepQuietly(getWaitingTime());

			// Apply a segment to start download
			// The progree will exit whie not more segments available
			this.segment = this.downloadTask.applySegment(this);

			if (null == this.segment) {
				if (this.downloadTask.getState() == DownloadTask.StateFinish) {
					finishMovie(this.downloadTask);
				}
				return;
			}

			try {
				this.doStartDownload(this.downloadTask, this.segment, DownloadManager.getInstance());

			} catch (IOException e) {

				// When error happened, retry count will be increased and check where the task
				// can be continued or not/

				if (!this.downloadTask.shouldRetry()) {
					this.downloadTask.setState(DownloadTask.StateError);
					DownloadManager.getInstance().onError(this.downloadTask);
				}

				if (!StringUtil.containsAnyIgnoreCase(e.getLocalizedMessage(), "403", "429")) {
					this.downloadTask.increaseRetryCount();
					this.logger.error("download the url " + this.downloadTask.getDownloadUrl(), e);
				} else {
					if (RandomUtils.nextInt(10) == 1) {
						this.downloadTask.increaseRetryCount();
						this.logger.error("download the url " + this.downloadTask.getDownloadUrl(), e);
					}
					// 1/10 possibility to log and increase retry count.
				}
				// 403 error is not needed to log.

				for (int i = 0; i < 10; i++) {
					if (this.downloadTask.getState() == DownloadTask.StateRunning) {
						SystemUtil.sleepQuietly(500);
					}
				}

			} finally {

				this.segment.unlock(this);

				// SystemUtil.sleepQuietly(DefaultWaitingTime);
			}
		}
	}

	/**
	 * @return
	 */
	protected long getWaitingTime() {
		return 100;
	}

	/**
	 * Finish the movie.
	 */
	protected void finishMovie(DownloadTask downloadTask) {
		downloadTask.clearSegments();
		DownloadManager.getInstance().onFinish(this.downloadTask);
	}

	/**
	 * Start the segment.
	 *
	 * @param downloadTask
	 * @param segment
	 * @param listener
	 * @throws IOException
	 */
	protected abstract void doStartDownload(DownloadTask downloadTask, Segment segment, AbstractProgressListener listener) throws IOException;

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return this.bufferSize;
	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = Integer.max(bufferSize, MinBufferSize);
	}

	/**
	 * Return suggest thread count for per task.
	 *
	 * @return
	 */
	public int getSuggestThreadCountPerTask() {
		return DownloadManager.getInstance().getConfiguration().getThreadCountPerTask();
	}

	/**
	 * Return suggest max retry count for per task.<BR>
	 * HLS need more retry.
	 *
	 * @return
	 */
	public int getSuggestMaxRetryCount() {
		return MaxRetryCount;
	}

	/**
	 * Fork a new downloader for multi download
	 *
	 * @return
	 */
	public abstract AbstractSegmentDownloader fork();
}
