/**
 * 
 */
package org.aquarius.cicada.core.model.result;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class LinkResult extends AbstractResult {

	private String sourceUrl;

	private String refererUrl;

	private String downloadUrl;

	private Map<String, String> requestHeaders = new HashMap<>();

	/**
	 * 
	 */
	public LinkResult() {
		super();
	}

	/**
	 * @return the sourceUrl
	 */
	public String getSourceUrl() {
		return this.sourceUrl;
	}

	/**
	 * @param sourceUrl the sourceUrl to set
	 */
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	/**
	 * @return the refererUrl
	 */
	public String getRefererUrl() {
		return this.refererUrl;
	}

	/**
	 * @param refererUrl the refererUrl to set
	 */
	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	/**
	 * @return the downloadUrl
	 */
	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	/**
	 * @param downloadUrl the downloadUrl to set
	 */
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	/**
	 * @return the requestHeaders
	 */
	public Map<String, String> getRequestHeaders() {
		return this.requestHeaders;
	}

	/**
	 * @param requestHeaders the requestHeaders to set
	 */
	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

}
