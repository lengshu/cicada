/**
 *
 */
package org.aquarius.cicada.core.model;

import java.util.HashMap;
import java.util.Map;

import org.aquarius.util.base.AbstractComparable;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class Link extends AbstractComparable<Link> {

	private String sourceUrl;

	private String refererUrl;

	private String downloadUrl;

	private boolean selected = true;

	private boolean converted;

	private boolean hls = false;

	private boolean valid = true;

	private String errorMessage;

	private boolean ignoreParseLink = false;

	private Map<String, String> requestHeaders = new HashMap<>();

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
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
	 * @return the selected
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the converted
	 */
	public boolean isConverted() {
		return this.converted;
	}

	/**
	 * @param converted the converted to set
	 */
	public void setConverted(boolean converted) {
		this.converted = converted;
	}

	/**
	 * @return the hls
	 */
	public boolean isHls() {
		return this.hls;
	}

	/**
	 * @param isHls the isHls to set
	 */
	public void setHls(boolean hls) {
		this.hls = hls;
	}

	/**
	 * @return the referUrl
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
	 * @param requestHeaders the requestHeaders to set
	 */
	public void addRequestHeaders(Map<String, String> requestHeaders) {
		if (null != requestHeaders) {
			this.requestHeaders.putAll(requestHeaders);
		}
	}

	/**
	 * Merge the new contents to this.<BR>
	 * 
	 * @param requestHeaders
	 */
	public void mergeRequestHeaders(Map<String, String> requestHeaders) {
		if (null != requestHeaders) {
			this.requestHeaders.putAll(requestHeaders);
		}
	}

	/**
	 * @return the ignoreParseLink
	 */
	public boolean isIgnoreParseLink() {
		return this.ignoreParseLink;
	}

	/**
	 * @param ignoreParseLink the ignoreParseLink to set
	 */
	public void setIgnoreParseLink(boolean ignoreParseLink) {
		this.ignoreParseLink = ignoreParseLink;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Link [sourceUrl=" + this.sourceUrl + ", refererUrl=" + this.refererUrl + ", downloadUrl=" + this.downloadUrl + ", selected=" + this.selected
				+ ", converted=" + this.converted + ", hls=" + this.hls + ", valid=" + this.valid + ", errorMessage=" + this.errorMessage + ", ignoreParseLink="
				+ this.ignoreParseLink + ", requestHeaders=" + this.requestHeaders + "]";
	}

}
