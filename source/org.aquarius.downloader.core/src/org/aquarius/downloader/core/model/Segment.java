/**
 *
 */
package org.aquarius.downloader.core.model;

import org.aquarius.util.AssertUtil;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Segment for multi download.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class Segment {

	@JSONField(name = "sun")
	private String shortUrlName;

	@JSONField(name = "s")
	private long start;

	@JSONField(name = "e")
	private long end;

	@JSONField(name = "c")
	private long currentPosition;

	@JSONField(name = "f")
	private String fileName;

	@JSONField(name = "fd")
	private boolean finished = false;

	private transient Object owner;

	private transient int retryCount;

	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return this.finished;
	}

	/**
	 * @param finished the finished to set
	 */
	public synchronized void setFinished(Object onwer, boolean finished) {
		this.finished = finished;
		this.owner = null;
		this.currentPosition = this.end - this.start;
	}

	public long getDownloadedLength() {
		return this.currentPosition;
	}

	public synchronized long addDownloadedLength(Object owner, long downloadedLength) {
		checkOwner(owner);

		this.currentPosition = this.currentPosition + downloadedLength;

		return this.currentPosition;
	}

	/**
	 * @param owner
	 */
	private void checkOwner(Object owner) {
		AssertUtil.assertNotNull(owner, "The owner should not be null.");

		if (owner != this.owner) {
			throw new UnsupportedOperationException("Only the owner who locked the segment can operate the segment.");
		}
	}

	/**
	 * @return the locked
	 */
	public synchronized boolean isLocked() {
		return null != this.owner || this.isFinished();
	}

	/**
	 *
	 * @param owner
	 */
	public void lock(Object owner) {

		AssertUtil.assertNotNull(owner, "The owner should not be null.");

		if (null == this.owner) {
			this.owner = owner;
		} else {
			throw new UnsupportedOperationException("The segment can't be locked twice.");
		}
	}

	/**
	 *
	 * @param owner
	 */
	public void unlock(Object owner) {

		if (null == this.owner) {
			return;
		}

		checkOwner(owner);

		this.owner = null;
	}

	/**
	 * @return the start
	 */
	public long getStart() {
		return this.start;
	}

	/**
	 * @param start the start to set
	 */
	public synchronized void setStart(long start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public long getEnd() {
		return this.end;
	}

	/**
	 * @param end the end to set
	 */
	public synchronized void setEnd(long end) {
		this.end = end;
	}

	public long getRealPosition() {
		return this.start + this.currentPosition;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return this.retryCount;
	}

	/**
	 * @param retryCount the retryCount to set
	 */
	public synchronized void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * @return the shortUrlName
	 */
	public String getShortUrlName() {
		return this.shortUrlName;
	}

	/**
	 * @param shortUrlName the shortUrlName to set
	 */
	public void setShortUrlName(String shortUrlName) {
		this.shortUrlName = shortUrlName;
	}

}
