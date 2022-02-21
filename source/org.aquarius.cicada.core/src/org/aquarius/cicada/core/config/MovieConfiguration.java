/**
 *
 */
package org.aquarius.cicada.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.service.IPropertyStoreService;
import org.aquarius.service.config.AbstractStoreableConfiguration;
import org.aquarius.util.StringUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class MovieConfiguration extends AbstractStoreableConfiguration {

	public static final int DefaultOutTimeInMinutes = 150;

	public static final String AllPixels = "720P,1080P,1440P,2160P,480P,360P";

	public static final String Default_Pixels = "720P,1080P,480P";

	public static final String Key_UseBrowserDownload = MovieConfiguration.class.getName() + "Key.UseBrowserDownload";

	public static final String Key_BrowserVisible = MovieConfiguration.class.getName() + "Key.BrowserVisible";

	public static final String Key_AutoRefreshSite = MovieConfiguration.class.getName() + "Key.AutoRefreshSite";

	public static final String Key_AutoRefreshMovie = MovieConfiguration.class.getName() + "Key.AutoRefreshMovie";

	public static final String Key_AutoFillActor = MovieConfiguration.class.getName() + "Key.AutoFillActor";

	public static final String Key_CleanPeriod = MovieConfiguration.class.getName() + "Key.CleanPeriod";

	public static final String Key_Pixels = MovieConfiguration.class.getName() + "Key.Pixels";

	public static final String Key_PasteContentToClipboard = MovieConfiguration.class.getName() + "Key.PasteContentToClipboard";

	public static final String Key_IgnoreError = MovieConfiguration.class.getName() + "Key.IgnoreError";

	public static final String Key_KeywordFrequencySort = MovieConfiguration.class.getName() + "Key.KeywordFrequencySort";

	public static final String Key_ConfirmDeleteMovie = MovieConfiguration.class.getName() + "Key.ConfirmDeleteMovie";

	public static final String Key_ConfirmDownloadMovie = MovieConfiguration.class.getName() + "Key.ConfirmDownloadMovie";

	public static final String Key_UseDownloadInfoCache = MovieConfiguration.class.getName() + "Key.UseDownloadInfoCache";

	public static final String Key_MaxErrorCount = MovieConfiguration.class.getName() + "Key.MaxErrorCount";

	public static final String Key_UseTitleAsFileName = MovieConfiguration.class.getName() + "Key.UseTitleAsFileName";

	public static final String Key_StrictCheckStrategy = MovieConfiguration.class.getName() + "Key.StrictCheckStrategy";

	private List<String> analyserSiteFilters = new ArrayList<>();

	/**
	 * @param storeService
	 */
	public MovieConfiguration(IPropertyStoreService storeService) {
		super(storeService);
	}

	/**
	 * @return the analyserSiteFilters
	 */
	public List<String> getAnalyserSiteFilters() {
		return this.analyserSiteFilters;
	}

	/**
	 * @param analyserSiteFilters the analyserSiteFilters to set
	 */
	public void setAnalyserSiteFilters(List<String> analyserSiteFilters) {
		this.analyserSiteFilters = analyserSiteFilters;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoadDefaults(IPropertyStoreService storeService) {

		storeService.setDefault(Key_UseBrowserDownload, false);
		storeService.setDefault(Key_BrowserVisible, false);
		storeService.setDefault(Key_AutoRefreshSite, false);
		storeService.setDefault(Key_AutoRefreshMovie, false);
		storeService.setDefault(Key_PasteContentToClipboard, false);
		storeService.setDefault(Key_IgnoreError, false);
		storeService.setDefault(Key_KeywordFrequencySort, true);

		storeService.setDefault(Key_Pixels, Default_Pixels);
		storeService.setDefault(Key_CleanPeriod, 14);
		storeService.setDefault(Key_MaxErrorCount, 5);

		storeService.setDefault(Key_UseDownloadInfoCache, false);
		storeService.setDefault(Key_StrictCheckStrategy, true);

		storeService.setDefault(Key_UseTitleAsFileName, true);
	}

	/**
	 * When updating movie site, use loose check strategy or not .<BR>
	 * 
	 * @return
	 */
	public boolean isStrictCheckStrategy() {
		return this.getStoreService().getBoolean(Key_StrictCheckStrategy);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUseTitleAsFileName() {
		return this.getStoreService().getBoolean(Key_UseTitleAsFileName);
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxErrorCount() {
		return Integer.max(5, this.getStoreService().getInt(Key_MaxErrorCount));
	}

	/**
	 * Return whether use download info cache or not.<BR>
	 *
	 * @return
	 */
	public boolean isUseDownloadInfoCache() {
		return this.getStoreService().getBoolean(Key_UseDownloadInfoCache);
	}

	/**
	 * Return whether after searching keyword , use frequency to sort result
	 * list.<BR>
	 *
	 * @return
	 */
	public boolean isKeywordFrequencySort() {
		return this.getStoreService().getBoolean(Key_KeywordFrequencySort);
	}

	/**
	 *
	 * @param keywordFrequencySort
	 */
	public void isKeywordFrequencySort(boolean keywordFrequencySort) {
		this.getStoreService().setValue(Key_KeywordFrequencySort, keywordFrequencySort);
	}

	/**
	 * @return the useBrowserDownload
	 */
	public boolean isUseBrowserDownload() {
		return this.getStoreService().getBoolean(Key_UseBrowserDownload);
	}

	/**
	 * @param useBrowserDownload the useBrowserDownload to set
	 */
	public void setUseBrowserDownload(boolean useBrowserDownload) {
		this.getStoreService().setValue(Key_UseBrowserDownload, useBrowserDownload);
	}

	/**
	 * @return the browserVisible
	 */
	public boolean isBrowserVisible() {
		return this.getStoreService().getBoolean(Key_BrowserVisible);
	}

	/**
	 * @param browserVisible the browserVisible to set
	 */
	public void setBrowserVisible(boolean browserVisible) {
		this.getStoreService().setValue(Key_BrowserVisible, browserVisible);
	}

	/**
	 * @return the autoRefreshSite
	 */
	public boolean isAutoRefreshSite() {
		return this.getStoreService().getBoolean(Key_AutoRefreshSite);
	}

	/**
	 * Return whether fill actor info while refreshing movie.
	 * 
	 * @return
	 */
	public boolean isAutoFillActor() {
		return this.getStoreService().getBoolean(Key_AutoFillActor);
	}

	/**
	 * @param autoRefreshSite the autoRefreshSite to set
	 */
	public void setAutoRefreshSite(boolean autoRefreshSite) {
		this.getStoreService().setValue(Key_AutoRefreshSite, autoRefreshSite);
	}

	/**
	 * @return the autoRefreshMovie
	 */
	public boolean isAutoRefreshMovie() {
		return this.getStoreService().getBoolean(Key_AutoRefreshMovie);
	}

	/**
	 * @param autoRefreshMovie the autoRefreshMovie to set
	 */
	public void setAutoRefreshMovie(boolean autoRefreshMovie) {
		this.getStoreService().setValue(Key_AutoRefreshMovie, autoRefreshMovie);
	}

	/**
	 * @return the cleanPeriod
	 */
	public int getCleanPeriod() {
		return this.getStoreService().getInt(Key_CleanPeriod);
	}

	/**
	 * @param cleanPeriod the cleanPeriod to set
	 */
	public void setCleanPeriod(int cleanPeriod) {
		this.getStoreService().setValue(Key_CleanPeriod, cleanPeriod);
	}

	/**
	 * @return the ignoreError
	 */
	public boolean isIgnoreError() {
		return this.getStoreService().getBoolean(Key_IgnoreError);
	}

	/**
	 * @param ignoreError the ignoreError to set
	 */
	public void setIgnoreError(boolean ignoreError) {
		this.getStoreService().setValue(Key_IgnoreError, ignoreError);
	}

	/**
	 * @return the pixels
	 */
	public String getPixels() {
		return this.getStoreService().getString(Key_Pixels);
	}

	/**
	 * @param pixels the pixels to set
	 */
	public void setPixels(String pixels) {

		if (StringUtils.isEmpty(pixels)) {
			pixels = AllPixels;
		}

		this.getStoreService().setValue(Key_Pixels, pixels);
	}

	/**
	 *
	 * @return
	 */
	public List<String> getPixelList() {
		return Arrays.asList(MovieUtil.split(this.getPixels()));
	}

	/**
	 *
	 * @return
	 */
	public static List<String> getAllPixelList() {
		return Arrays.asList(MovieUtil.split(AllPixels));
	}

	/**
	 *
	 * @param pixelList
	 */
	public void setPixelList(List<String> pixelList) {
		this.setPixels(StringUtils.join(pixelList, StringUtil.ContentSeparator));
	}

	/**
	 * @return the pasteContentToClipboard
	 */
	public boolean isPasteContentToClipboard() {
		return this.getStoreService().getBoolean(Key_PasteContentToClipboard);
	}

	/**
	 * @param pasteContentToClipboard the pasteContentToClipboard to set
	 */
	public void setPasteContentToClipboard(boolean pasteContentToClipboard) {
		this.getStoreService().setValue(Key_PasteContentToClipboard, pasteContentToClipboard);
	}

}
