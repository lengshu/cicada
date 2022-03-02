/**
 *
 */
package org.aquarius.cicada.workbench.page;

import org.apache.commons.lang.SystemUtils;
import org.aquarius.cicada.workbench.RuntimeConstant;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.service.IPropertyStoreService;
import org.aquarius.service.config.AbstractStoreableConfiguration;
import org.aquarius.ui.service.EclipsePropertyStoreService;
import org.aquarius.ui.util.TooltipUtil;

/**
 * Configuration for the workbench.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class WorkbenchConfiguration extends AbstractStoreableConfiguration {

	public static final String Key_UseEdge = WorkbenchConfiguration.class.getName() + ".Key.UseEdge";

	public static final String Key_UseBrowserMenu = WorkbenchConfiguration.class.getName() + ".Key.UseBrowserMenu";

	public static final String Key_ShowInfoDialog = WorkbenchConfiguration.class.getName() + ".Key.ShowInfoDialog";

	public static final String Key_AutoFinish = WorkbenchConfiguration.class.getName() + ".Key.AutoFinish";

	public static final String Key_DoubleClickToOpenUrl = WorkbenchConfiguration.class.getName() + ".Key.DoubleClickToOpenUrl";

	public static final String Key_AutoGc = WorkbenchConfiguration.class.getName() + ".Key.AutoGc";

	public static final String Key_HideImageTooltip = WorkbenchConfiguration.class.getName() + ".Key.HideImageTooltip";

	public static final String Key_ShowTooltip = WorkbenchConfiguration.class.getName() + ".Key.ShowTooltip";

	public static final String Key_HistoryKeywordCount = WorkbenchConfiguration.class.getName() + ".Key.HistoryKeywordCount";

	public static final String Key_FilterType = WorkbenchConfiguration.class.getName() + ".Key.FilterType";

	public static final String Key_UseSimpleFilter = WorkbenchConfiguration.class.getName() + ".Key.UseSimpleFilter";

	public static final String Key_DisableExtensionTable = WorkbenchConfiguration.class.getName() + ".Key.DisableExtensionTable";

	public static final String Key_EnableTableEditable = WorkbenchConfiguration.class.getName() + ".Key.EnableTableEditable";

	public static final String Key_MediaPlayerCommand = WorkbenchConfiguration.class.getName() + "Key.MediaPlayerCommand";

	public static final String Key_ConfirmQuit = WorkbenchConfiguration.class.getName() + "Key.ConfirmQuit";

	public static final String Key_TooltipLevel = WorkbenchConfiguration.class.getName() + "Key.TooltipLevel";

	public static final String Key_PersistNatTableConfig = WorkbenchConfiguration.class.getName() + "Key.PersistNatTableConfig";

	public static final String Key_PersistBrowserConfig = WorkbenchConfiguration.class.getName() + "Key.PersistBrowserConfig";

	public static final String Key_DefaultEditor = WorkbenchConfiguration.class.getName() + "Key.DefaultEditor";

	public static final String Key_TableFullSelection = WorkbenchConfiguration.class.getName() + "Key.TableFullSelection";

	public static final String Key_AutoCleanLog = WorkbenchConfiguration.class.getName() + "Key.AutoCleanLog";

	public static final String Key_AutoActivationKey = WorkbenchConfiguration.class.getName() + "Key.AutoActivationKey";

	public static final String Key_ShowClearSiteAction = WorkbenchConfiguration.class.getName() + "Key.ShowClearSiteAction";

	public static final String Key_ForceCloseAllPopup = WorkbenchConfiguration.class.getName() + "Key.ForceCloseAllPopup";

	public static final String Key_BrowserZoom = WorkbenchConfiguration.class.getName() + "Key.BrowserZoom";

	public static final String Key_BrowserPageSize = WorkbenchConfiguration.class.getName() + "Key.BrowserPageSize";

	public static final int Default_HistoryKeywordCount = 20;

	/**
	 * Return whether table select style is row or cell.<BR>
	 * 
	 * @return
	 */
	public boolean isTableFullSelection() {
		return this.getStoreService().getBoolean(Key_TableFullSelection);
	}

	/**
	 * 
	 * @return
	 */
	public String getBrowserZoom() {
		return this.getStoreService().getString(Key_BrowserZoom);
	}

	/**
	 *
	 */
	public WorkbenchConfiguration() {
		super(new EclipsePropertyStoreService(WorkbenchActivator.getDefault().getPreferenceStore()));
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUseTextForNatTableRowFilter() {
		return true;
	}

	public String getAutoActivationKey() {
		return this.getStoreService().getString(Key_AutoActivationKey);
	}

	public boolean isShowClearSiteAction() {
		return this.getStoreService().getBoolean(Key_ShowClearSiteAction);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoadDefaults(IPropertyStoreService storeService) {

		storeService.setDefault(Key_DoubleClickToOpenUrl, true);
		storeService.setDefault(Key_ShowInfoDialog, true);

		storeService.setDefault(Key_HistoryKeywordCount, Default_HistoryKeywordCount);
		storeService.setDefault(Key_FilterType, RuntimeConstant.FilterTypeSimple);
		storeService.setDefault(Key_TooltipLevel, TooltipUtil.LevelError);

		storeService.setDefault(Key_BrowserZoom, RuntimeConstant.BrowserZoomNormal);

		storeService.setDefault(Key_AutoActivationKey, "Ctrl+.");

		storeService.setDefault(Key_BrowserPageSize, 40);

		if (SystemUtils.IS_OS_WINDOWS) {
			storeService.setDefault(Key_MediaPlayerCommand, "vlc.exe \"{0}\"");
		} else {
			storeService.setDefault(Key_MediaPlayerCommand, "vlc \"{0}\"");
		}
	}

	public int getBrowserPageSize() {
		return this.getStoreService().getInt(Key_BrowserPageSize);
	}

	/**
	 * 
	 * @return
	 */
	public String getDefaultEditor() {
		return this.getStoreService().getString(Key_DefaultEditor);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAutoCleanLog() {
		return this.getStoreService().getBoolean(Key_AutoCleanLog);
	}

	/**
	 * Return whether use chrominum or not.<BR>
	 * 
	 * @return
	 */
	public boolean isUseEdge() {
		return this.getStoreService().getBoolean(Key_UseEdge);
	}

	/**
	 * Return whether persist nattable config or not.<BR>
	 * 
	 * @return
	 */
	public boolean isPersistNatTableConfig() {
		return this.getStoreService().getBoolean(Key_PersistNatTableConfig);
	}

	/**
	 * Return whether persist browser config or not.<BR>
	 * 
	 * @return
	 */
	public boolean isPersistBrowserConfig() {
		return this.getStoreService().getBoolean(Key_PersistBrowserConfig);
	}

	/**
	 * Return whether tool tip level.<BR>
	 * 
	 * @return
	 */
	public int getTooltipLevel() {
		return this.getStoreService().getInt(Key_TooltipLevel);
	}

	/**
	 * Return whether use simple filter or not.<BR>
	 * 
	 * @return
	 */
	public boolean isUseSimpleFilter() {
		return this.getStoreService().getBoolean(Key_UseSimpleFilter);
	}

	/**
	 * Use content outline to filter movie.<BR>
	 *
	 * @return
	 */
	public String getFilterType() {
		return this.getStoreService().getString(Key_FilterType);
	}

	/**
	 * Return whether the table is editable.<BR>
	 *
	 * @return
	 */
	public boolean isEnableTableEditable() {
		return this.getStoreService().getBoolean(Key_EnableTableEditable);

	}

	/**
	 * Use extension table to display movies.<BR>
	 *
	 * @return
	 */
	public boolean isDisableExtensionTable() {
		return this.getStoreService().getBoolean(Key_DisableExtensionTable);
	}

	/**
	 * Return this count for history keywords.<BR>
	 * The default value is 20;
	 *
	 * @return
	 */
	public int getHistoryKeywordCount() {
		return this.getStoreService().getInt(Key_HistoryKeywordCount);
	}

	/**
	 * Return whether auto garbage collection.<BR>
	 *
	 * @return
	 */
	public boolean isAutoGc() {
		return this.getStoreService().getBoolean(Key_AutoGc);
	}

	/**
	 * Return whether auto garbage collection.<BR>
	 *
	 * @return
	 */
	public boolean isShowTooltip() {
		return this.getStoreService().getBoolean(Key_ShowTooltip);
	}

	/**
	 * Return whether use browser system menu.
	 *
	 * @return
	 */
	public boolean isUseBrowserMenu() {
		return this.getStoreService().getBoolean(Key_UseBrowserMenu);
	}

	/**
	 * Return whether show tooltip with image.
	 *
	 * @return
	 */
	public boolean isHideImageTooltip() {
		return this.getStoreService().getBoolean(Key_HideImageTooltip);
	}

	/**
	 * When info available , use dialog to show and save.<BR>
	 *
	 * @return
	 */
	public boolean isShowInfoDialog() {
		return this.getStoreService().getBoolean(Key_ShowInfoDialog);
	}

	/**
	 * When downloading a movie,set it's state to finished.<BR>
	 *
	 * @return
	 */
	public boolean isAutoFinish() {
		return this.getStoreService().getBoolean(Key_AutoFinish);
	}

	/**
	 * When double click a movie url,open it in the system browser.<BR>
	 *
	 * @return
	 */
	public boolean isDoubleClickToOpenUrl() {
		return this.getStoreService().getBoolean(Key_DoubleClickToOpenUrl);
	}

	/**
	 * Return media player.<BR>
	 *
	 * @return
	 */
	public String getMediaPlayerCommand() {
		return this.getStoreService().getString(Key_MediaPlayerCommand);
	}

	/**
	 * Set the media player path.
	 * 
	 * @param playerFile
	 */
	public void setMediaPlayerFile(String playerFile) {
		this.getStoreService().setValue(Key_MediaPlayerCommand, playerFile + " \"{0}\"");
	}

}
