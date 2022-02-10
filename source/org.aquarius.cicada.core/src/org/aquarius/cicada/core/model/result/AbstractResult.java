/**
 *
 */
package org.aquarius.cicada.core.model.result;

import org.apache.commons.lang.StringUtils;

/**
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractResult {

	private boolean shouldRetry = true;

	private String redirectUrl;

	private boolean prohibitReloadPage;

	private String errorMessage;

	private long waitTime = 3000;

	private int retryCount = 0;

	/**
	 * @return the shouldRetry
	 */
	public boolean isShouldRetry() {
		return this.shouldRetry;
	}

	/**
	 * @param shouldRetry the shouldRetry to set
	 */
	public void setShouldRetry(boolean shouldRetry) {
		this.shouldRetry = shouldRetry;
	}

	/**
	 * @return the hasError
	 */
	public boolean isHasError() {
		return StringUtils.isNotBlank(this.errorMessage);
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the waitTime
	 */
	public long getWaitTime() {
		return this.waitTime;
	}

	/**
	 * @param waitTime the waitTime to set
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
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
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * @return the redirectUrl
	 */
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	/**
	 * @param redirectUrl the redirectUrl to set
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * @return the prohibitReloadPage
	 */
	public boolean isProhibitReloadPage() {
		return this.prohibitReloadPage;
	}

	/**
	 * @param prohibitReloadPage the prohibitReloadPage to set
	 */
	public void setProhibitReloadPage(boolean prohibitReloadPage) {
		this.prohibitReloadPage = prohibitReloadPage;
	}
}
