/**
 * 
 */
package org.aquarius.cicada.workbench.function.net;

import java.util.Map;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class SimpleHttpRequest {

	private String url;

	private String method;

	private Map<String, String> headers;

	/**
	 * 
	 */
	public SimpleHttpRequest() {
		super();
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
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}
