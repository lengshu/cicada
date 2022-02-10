/**
 *
 */
package org.aquarius.downloader.core;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.mark.IUrlAcceptable;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class SiteConcurrent implements IUrlAcceptable {

	private String name;

	private int currentCount;

	private int maxConcurrentCount;

	/**
	 * @param name
	 * @param maxConcurrentCount
	 */
	public SiteConcurrent(String name, int maxConcurrentCount) {
		super();
		this.name = name;
		this.maxConcurrentCount = maxConcurrentCount;
	}

	/**
	 *
	 */
	public synchronized void increaseConcurrentCount() {
		if (this.currentCount < 0) {
			this.currentCount = 0;
		}

		this.currentCount++;
	}

	/**
	 *
	 */
	public synchronized void decreaseConcurrentCount() {
		this.currentCount--;

		if (this.currentCount < 0) {
			this.currentCount = 0;
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isFull() {
		return this.currentCount >= this.maxConcurrentCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the maxConcurrentCount
	 */
	public int getMaxConcurrentCount() {
		return this.maxConcurrentCount;
	}

	/**
	 * @param maxConcurrentCount the maxConcurrentCount to set
	 */
	public void setMaxConcurrentCount(int maxConcurrentCount) {
		this.maxConcurrentCount = maxConcurrentCount;
	}

	/**
	 *
	 * @param urlString
	 * @return
	 */
	@Override
	public boolean isAcceptable(String urlString) {
		return StringUtils.containsIgnoreCase(urlString, this.name);
	}

}
