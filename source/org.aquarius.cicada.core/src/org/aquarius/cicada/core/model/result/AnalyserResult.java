/**
 *
 */
package org.aquarius.cicada.core.model.result;

import java.util.HashMap;
import java.util.Map;

import org.aquarius.util.AssertUtil;

/**
 *
 * For download analyser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AnalyserResult extends AbstractResult {

	private String url;

	private Map<String, String> requestHeaders = new HashMap<>();

	private boolean hls = false;

	public static AnalyserResult createErrorResult(String errorMessage) {
		AssertUtil.assertNotNull(errorMessage);
		AnalyserResult result = new AnalyserResult();
		result.setErrorMessage(errorMessage);
		return result;
	}

	/**
	 *
	 */
	private AnalyserResult() {
		super();
	}

	/**
	 * @param url
	 */
	public AnalyserResult(String url, Map<String, String> headers) {
		super();
		this.url = url;

		this.setRequestHeaders(headers);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getRequestHeaders() {
		return this.requestHeaders;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setRequestHeaders(Map<String, String> headers) {
		if (null != headers) {
			this.requestHeaders.clear();
			this.requestHeaders.putAll(headers);
		}
	}

	/**
	 * @param headers the headers to set
	 */
	public void mergeRequestHeaders(Map<String, String> headers) {
		if (null != headers) {
			this.requestHeaders.putAll(headers);
		}
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
