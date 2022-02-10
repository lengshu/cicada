/**
 *
 */
package org.aquarius.cicada.core.model;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.LocaleUtil;
import org.aquarius.util.StringUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class MovieChannel {

	private String urlPattern;

	private String name;

	private transient String displayName;

	private transient String siteName;

	private String nlsDisplayName;

	private boolean autoRefreshList;

	private boolean autoRefreshDetail;

	private boolean supportPaging = true;

	public static final String PropertyAutoRefreshList = "autoRefreshList";

	public static final String PropertyAutoRefreshDetail = "autoRefreshDetail";

	/**
	 *
	 */
	public MovieChannel() {
		super();
	}

	/**
	 *
	 * @param name
	 * @param urlPattern
	 */
	public MovieChannel(String name, String urlPattern) {
		super();
		this.urlPattern = urlPattern;
		this.name = name;
	}

	/**
	 * @return the urlPattern
	 */
	public String getUrlPattern() {
		return this.urlPattern;
	}

	/**
	 * @param urlPattern the urlPattern to set
	 */
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the supportPaging
	 */
	public boolean isSupportPaging() {
		return this.supportPaging;
	}

	/**
	 * @param supportPaging the supportPaging to set
	 */
	public void setSupportPaging(boolean supportPaging) {
		this.supportPaging = supportPaging;
	}

	/**
	 * @return the autoRefresh
	 */
	public boolean isAutoRefreshList() {
		return this.autoRefreshList;
	}

	/**
	 * @param autoRefreshList the autoRefresh to set
	 */
	public void setAutoRefreshList(boolean autoRefreshList) {
		this.autoRefreshList = autoRefreshList;
	}

	/**
	 * @return the autoRefreshDetail
	 */
	public boolean isAutoRefreshDetail() {
		return this.autoRefreshDetail;
	}

	/**
	 * @param autoRefreshDetail the autoRefreshDetail to set
	 */
	public void setAutoRefreshDetail(boolean autoRefreshDetail) {
		this.autoRefreshDetail = autoRefreshDetail;
	}

	public String getDisplayName() {
		if (null == this.displayName) {
			return this.name;
		}

		else {
			return this.displayName;
		}
	}

	/**
	 * @return the displayName for nls
	 */
	public String getNlsDisplayName() {
		if (StringUtils.isEmpty(this.nlsDisplayName)) {
			return this.name;
		} else {
			return this.nlsDisplayName;
		}
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setNlsDisplayName(String nlsDisplayName) {
		this.nlsDisplayName = nlsDisplayName;

		Map<String, String> map = StringUtil.convertToMap(nlsDisplayName);
		Collection<String> localeNames = LocaleUtil.findDefaultCandaidateLocaleNames();
		localeNames.add("");

		for (String localeName : localeNames) {
			String displayValue = map.get(localeName);

			if (StringUtils.isNotBlank(displayValue)) {
				this.displayName = displayValue;
				return;
			}
		}
	}

}
