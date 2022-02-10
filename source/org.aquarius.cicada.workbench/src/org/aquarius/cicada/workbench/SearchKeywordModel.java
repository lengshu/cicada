/**
 * 
 */
package org.aquarius.cicada.workbench;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class SearchKeywordModel {

	private boolean justSearchInTitle = false;

	private boolean useRegex;

	private String keyword;

	private List<String> sites = new ArrayList<>();

	/**
	 * 
	 */
	public SearchKeywordModel() {
		super();
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return this.keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the sites
	 */
	public List<String> getSites() {
		return Collections.unmodifiableList(this.sites);
	}

	/**
	 * @return the justSearchInTitle
	 */
	public boolean isJustSearchInTitle() {
		return this.justSearchInTitle;
	}

	/**
	 * @return the useRegex
	 */
	public boolean isUseRegex() {
		return this.useRegex;
	}

	/**
	 * @param useRegex the useRegex to set
	 */
	public void setUseRegex(boolean useRegex) {
		this.useRegex = useRegex;
	}

	/**
	 * @param justSearchInTitle the justSearchInTitle to set
	 */
	public void setJustSearchInTitle(boolean justSearchInTitle) {
		this.justSearchInTitle = justSearchInTitle;
	}

	/**
	 * 
	 * @param sites
	 */
	public void setSites(List<String> sites) {
		if (CollectionUtils.isNotEmpty(sites)) {
			this.sites.clear();
			this.sites.addAll(sites);
		}
	}
}
