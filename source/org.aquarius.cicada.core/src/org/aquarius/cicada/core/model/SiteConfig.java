/**
 *
 */
package org.aquarius.cicada.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class SiteConfig {

	private List<MovieChannel> movieChannelList = new ArrayList<MovieChannel>();

	private String siteName;

	private boolean supportImport = true;

	private boolean supportPaging = true;

	private boolean supportAlbum;

	private boolean supportMultiDownloadSource;

	/**
	 * the duration unit is second.
	 */
	private int waitTime = 5;

	/**
	 * the duration unit is minute.
	 */
	private int validPeriod = 180;

	private boolean supportJumpToLast = true;

	private String mainPage;

	private transient ScriptDefinition parseListScriptDefinition = new ScriptDefinition();

	private transient ScriptDefinition parseDetailScriptDefinition = new ScriptDefinition();

	private transient ScriptDefinition parseLinkScriptDefinition = new ScriptDefinition();

	private int checkDuplicationCount = 1;

	private transient File folder;

	private String cookie;

	private String parseMovieIdRegex;

	private boolean enable = true;

	/**
	 *
	 */
	public SiteConfig() {
		super();
	}

	/**
	 * @return the enable
	 */
	public boolean isEnable() {
		return this.enable;
	}

	/**
	 * @param enable the enable to set
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
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
	 * @return the folder
	 */
	public File getFolder() {
		return this.folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(File folder) {
		this.folder = folder;
	}

	/**
	 * @return the movieChannelList
	 */
	public List<MovieChannel> getMovieChannelList() {
		return this.movieChannelList;
	}

	/**
	 * @param movieChannelList the movieChannelList to set
	 */
	public void setMovieChannelList(List<MovieChannel> movieChannelList) {
		this.movieChannelList = movieChannelList;
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
	 * @return the supportImport
	 */
	public boolean isSupportImport() {
		return this.supportImport;
	}

	/**
	 * @param supportImport the supportImport to set
	 */
	public void setSupportImport(boolean supportImport) {
		this.supportImport = supportImport;
	}

	/**
	 * @return the waitTime
	 */
	public int getWaitTime() {
		return this.waitTime;
	}

	/**
	 * @param waitTime the waitTime to set
	 */
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 * @return the supportJumpToLast
	 */
	public boolean isSupportJumpToLast() {
		return this.supportJumpToLast;
	}

	/**
	 * @param supportJumpToLast the supportJumpToLast to set
	 */
	public void setSupportJumpToLast(boolean supportJumpToLast) {
		this.supportJumpToLast = supportJumpToLast;
	}

	/**
	 * @return the mainPage
	 */
	public String getMainPage() {
		return this.mainPage;
	}

	/**
	 * @param mainPage the mainPage to set
	 */
	public void setMainPage(String mainPage) {
		this.mainPage = mainPage;
	}

	/**
	 * @return the validPeriod
	 */
	public int getValidPeriod() {
		return this.validPeriod;
	}

	/**
	 * @param validPeriod the validPeriod to set
	 */
	public void setValidPeriod(int validPeriod) {
		this.validPeriod = validPeriod;
	}

	/**
	 * @return the parseListScriptDefinition
	 */
	public ScriptDefinition getParseListScriptDefinition() {
		return this.parseListScriptDefinition;
	}

	/**
	 * @param parseListScriptDefinition the parseListScriptDefinition to set
	 */
	public void setParseListScriptDefinition(ScriptDefinition parseListScriptDefinition) {
		this.parseListScriptDefinition = parseListScriptDefinition;
	}

	/**
	 * @return the parseDetailScriptDefinition
	 */
	public ScriptDefinition getParseDetailScriptDefinition() {
		return this.parseDetailScriptDefinition;
	}

	/**
	 * @param parseDetailScriptDefinition the parseDetailScriptDefinition to set
	 */
	public void setParseDetailScriptDefinition(ScriptDefinition parseDetailScriptDefinition) {
		this.parseDetailScriptDefinition = parseDetailScriptDefinition;
	}

	/**
	 * @return the parseLinkScriptDefinition
	 */
	public ScriptDefinition getParseLinkScriptDefinition() {
		return this.parseLinkScriptDefinition;
	}

	/**
	 * @param parseLinkScriptDefinition the parseLinkScriptDefinition to set
	 */
	public void setParseLinkScriptDefinition(ScriptDefinition parseLinkScriptDefinition) {
		this.parseLinkScriptDefinition = parseLinkScriptDefinition;
	}

	/**
	 * @return the checkDuplicationCount
	 */
	public int getCheckDuplicationCount() {
		return this.checkDuplicationCount;
	}

	/**
	 * @param checkDuplicationCount the checkDuplicationCount to set
	 */
	public void setCheckDuplicationCount(int checkDuplicationCount) {
		this.checkDuplicationCount = checkDuplicationCount;
	}

	/**
	 * @return the supportAlbum
	 */
	public boolean isSupportAlbum() {
		return this.supportAlbum;
	}

	/**
	 * @param supportAlbum the supportAlbum to set
	 */
	public void setSupportAlbum(boolean supportAlbum) {
		this.supportAlbum = supportAlbum;
	}

	/**
	 * @return the supportMultiDownloadSource
	 */
	public boolean isSupportMultiDownloadSource() {
		return this.supportMultiDownloadSource;
	}

	/**
	 * @param supportMultiDownloadSource the supportMultiDownloadSource to set
	 */
	public void setSupportMultiDownloadSource(boolean supportMultiDownloadSource) {
		this.supportMultiDownloadSource = supportMultiDownloadSource;
	}

	/**
	 * @return the parseMovieIdRegex
	 */
	public String getParseMovieIdRegex() {
		return this.parseMovieIdRegex;
	}

	/**
	 * @param parseMovieIdRegex the parseMovieIdRegex to set
	 */
	public void setParseMovieIdRegex(String parseMovieIdRegex) {
		this.parseMovieIdRegex = parseMovieIdRegex;
	}

	/**
	 * @return the cookie
	 */
	public String getCookie() {
		return this.cookie;
	}

	/**
	 * @param cookie the cookie to set
	 */
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	/**
	 * Return the video site has auto refresh channel or not.<BR>
	 * 
	 * @return
	 */
	public boolean hasAutoRefreshChannel() {
		for (MovieChannel movieChannel : this.movieChannelList) {
			if (movieChannel.isAutoRefreshList()) {
				return true;
			}
		}

		return false;
	}

}
