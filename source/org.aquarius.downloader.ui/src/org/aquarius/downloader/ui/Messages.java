/**
 *
 */
package org.aquarius.downloader.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.aquarius.downloader.ui.messages"; //$NON-NLS-1$

	public static String WarnDialogTitle;

	public static String DeleteTableAction_ConfirmDeleteDialogMessage;

	public static String DownloadConfigurationPreferencePage_FfmpegMergeFile;
	public static String DownloadConfigurationPreferencePage_FfmpegMergeCommand;
	public static String DownloadConfigurationPreferencePage_FfmpegDownloadCommand;
	public static String DownloadConfigurationPreferencePage_ClearCommand;

	public static String DownloadConfigurationPreferencePage_AutoStartDownloading;

	public static String DownloadConfigurationPreferencePage_AutoAddSiteConstraint;

	public static String DownloadConfigurationPreferencePage_DoubleClickAction;

	public static String DownloadConfigurationPreferencePage_OpenUrl;
	public static String DownloadConfigurationPreferencePage_OpenFile;
	public static String DownloadConfigurationPreferencePage_OpenFolder;
	public static String DownloadConfigurationPreferencePage_StartOrPause;

	public static String DownloadConfigurationPreferencePage_AutoBackup;
	public static String DownloadConfigurationPreferencePage_AutoRename;
	public static String DownloadConfigurationPreferencePage_CacheSize;
	public static String DownloadConfigurationPreferencePage_ConcurrentDownloadCount;
	public static String DownloadConfigurationPreferencePage_CountPerTask;
	public static String DownloadConfigurationPreferencePage_DefaultDownloadFolder;
	public static String DownloadConfigurationPreferencePage_DeleteMergedHLS;
	public static String DownloadConfigurationPreferencePage_DeleteToTrash;
	public static String DownloadConfigurationPreferencePage_CheckDiskLeftSpace;
	public static String DownloadConfigurationPreferencePage_DiskLeftSpace;
	public static String DownloadConfigurationPreferencePage_DuplicatedStrategy;
	public static String DownloadConfigurationPreferencePage_FfmpegFile;
	public static String DownloadConfigurationPreferencePage_LowerLimitSize;
	public static String DownloadConfigurationPreferencePage_MergeHLS;
	public static String DownloadConfigurationPreferencePage_OverWrite;
	public static String DownloadConfigurationPreferencePage_RetryCount;
	public static String DownloadConfigurationPreferencePage_SegmentSize;
	public static String DownloadConfigurationPreferencePage_ConfirmDelete;
	public static String DownloadConfigurationPreferencePage_SiteConcurrent;

	public static String DownloadView_ActionAdd;
	public static String DownloadView_ActionCopyAll;
	public static String DownloadView_ActionCopyLocation;
	public static String DownloadView_ActionCopyTitle;
	public static String DownloadView_ActionCopyUrl;
	public static String DownloadView_ActionDelete;
	public static String DownloadView_ActionDeleteFiles;
	public static String DownloadView_ActionOpenFile;
	public static String DownloadView_ActionOpenFolder;
	public static String DownloadView_ActionOpenRefer;
	public static String DownloadView_ActionPause;
	public static String DownloadView_ActionRefresh;
	public static String DownloadView_ActionStart;
	public static String DownloadView_ActionUpdate;
	public static String DownloadView_ActionImport;
	public static String DownloadView_ActionExport;

	public static String DownloadView_ConfirmReloadTasks;

	public static String DownloadView_ActionReloadTasks;
	public static String DownloadView_ActionMoveUp;
	public static String DownloadView_ActionMoveDown;
	public static String DownloadView_ActionMoveTop;
	public static String DownloadView_ActionMoveBottom;

	public static String DownloadView_ColumnSourceHost;
	public static String DownloadView_ColumnDownloadHost;
	public static String DownloadView_ColumnFileLength;
	public static String DownloadView_ColumnFinishedLength;
	public static String DownloadView_ColumnRemaingTime;
	public static String DownloadView_ColumnPercent;
	public static String DownloadView_ColumnRetry;
	public static String DownloadView_ColumnSpeed;
	public static String DownloadView_ColumnThreads;
	public static String DownloadView_ColumnTitle;
	public static String DownloadView_ColumnErrorMessage;

	public static String DownloadView_StateAll;
	public static String DownloadView_StateDelete;
	public static String DownloadView_StateError;
	public static String DownloadView_StateFinish;
	public static String DownloadView_StatePause;
	public static String DownloadView_StateRunning;
	public static String DownloadView_StateStart;
	public static String DownloadView_StateInit;

	public static String ExportTableAction_ErrorTitle;
	public static String ImportTableAction_WarnTitle;

	public static String NewDownloadTaskDialog_Browse;
	public static String NewDownloadTaskDialog_ErrorFileNameShouldBeFolder;
	public static String NewDownloadTaskDialog_ErrorFolderMustBeFilled;
	public static String NewDownloadTaskDialog_ErrorResourceIsNotValidFileName;
	public static String NewDownloadTaskDialog_ErrorResourceShouldBeFile;
	public static String NewDownloadTaskDialog_ErrorResourceShouldBeFolder;
	public static String NewDownloadTaskDialog_ErrorUrlMustBeFilled;
	public static String NewDownloadTaskDialog_FileName;
	public static String NewDownloadTaskDialog_Folder;
	public static String NewDownloadTaskDialog_Url;

	public static String SiteConcurrentFieldEditor_AddLabel;
	public static String SiteConcurrentFieldEditor_RemoveLabel;
	public static String SiteConcurrentFieldEditor_InputDialogMessage;
	public static String SiteConcurrentFieldEditor_InputDialogTitle;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
