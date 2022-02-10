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
public class DownloadUrlResult extends AbstractResult {

	private Map<String, String> urlMap = new HashMap<>();

	private Map<String, String> requestHeaders = new HashMap<>();

	private boolean hls = false;

	/**
	 * 
	 */
	public DownloadUrlResult() {
		super();
	}

	/**
	 * @return the urlMap
	 */
	public Map<String, String> getUrlMap() {
		return this.urlMap;
	}

	/**
	 * @param urlMap the urlMap to set
	 */
	public void setUrlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
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

	/**
	 * @return the hls
	 */
	public boolean isHls() {
		return this.hls;
	}

	/**
	 * @param hls the hls to set
	 */
	public void setHls(boolean hls) {
		this.hls = hls;
	}

}
