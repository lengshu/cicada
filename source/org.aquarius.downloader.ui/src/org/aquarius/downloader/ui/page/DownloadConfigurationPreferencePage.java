/**
 *
 */
package org.aquarius.downloader.ui.page;

import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.downloader.ui.page.internal.SiteConcurrentFieldEditor;
import org.aquarius.util.SystemUtil;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for download configuration.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadConfigurationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor deleteMergedHlsTrashFieldEditor;

	private IntegerFieldEditor diskLeftSpaceFieldEditor;

	private StringFieldEditor ffmpegDownloadCommandFieldEditor;

	private StringFieldEditor ffmpegMergeCommandFieldEditor;

	/**
	 *
	 */
	public DownloadConfigurationPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		IPreferenceStore store = DownloadActivator.getDefault().getPreferenceStore();
		this.setPreferenceStore(store);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to to
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createFieldEditors() {

		Composite parent = this.getFieldEditorParent();

		BooleanFieldEditor autoStartDownloadingFieldEditor = new BooleanFieldEditor(DownloadConfiguration.Key_AutoStartDownloading,
				Messages.DownloadConfigurationPreferencePage_AutoStartDownloading, parent);
		this.addField(autoStartDownloadingFieldEditor);

		DirectoryFieldEditor defaultDownloadFolderFieldEditor = new DirectoryFieldEditor(DownloadConfiguration.Key_DefaultDownloadFolder,
				Messages.DownloadConfigurationPreferencePage_DefaultDownloadFolder, parent);
		defaultDownloadFolderFieldEditor.setEmptyStringAllowed(false);
		this.addField(defaultDownloadFolderFieldEditor);

		IntegerFieldEditor threadCountPerTaskFieldEditor = new IntegerFieldEditor(DownloadConfiguration.Key_ThreadCountPerTask,
				Messages.DownloadConfigurationPreferencePage_CountPerTask, parent);
		threadCountPerTaskFieldEditor.setValidRange(1, DownloadConfiguration.MaxThreadCountPerTask);
		this.addField(threadCountPerTaskFieldEditor);

		IntegerFieldEditor concurrentDownloadCountFieldEditor = new IntegerFieldEditor(DownloadConfiguration.Key_ConcurrentDownloadCount,
				Messages.DownloadConfigurationPreferencePage_ConcurrentDownloadCount, parent);
		concurrentDownloadCountFieldEditor.setValidRange(1, DownloadConfiguration.MaxDownloadCount);
		this.addField(concurrentDownloadCountFieldEditor);

		BooleanFieldEditor checkDiskLeftSpaceFieldEditor = new BooleanFieldEditor(DownloadConfiguration.Key_CheckDiskLeftSpace,
				Messages.DownloadConfigurationPreferencePage_CheckDiskLeftSpace, parent);
		this.addField(checkDiskLeftSpaceFieldEditor);

		this.diskLeftSpaceFieldEditor = new IntegerFieldEditor(DownloadConfiguration.Key_DiskLeftSpace,
				Messages.DownloadConfigurationPreferencePage_DiskLeftSpace, parent);
		this.diskLeftSpaceFieldEditor.setValidRange(0, 10);
		this.addField(this.diskLeftSpaceFieldEditor);

		IntegerFieldEditor cacheSizeFieldEditor = new IntegerFieldEditor(DownloadConfiguration.Key_CacheSize,
				Messages.DownloadConfigurationPreferencePage_CacheSize, parent);
		cacheSizeFieldEditor.setValidRange(1, DownloadConfiguration.MaxCacheSize);
		this.addField(cacheSizeFieldEditor);

		IntegerFieldEditor segmentSizeFieldEditor = new IntegerFieldEditor(DownloadConfiguration.Key_SegmentSize,
				Messages.DownloadConfigurationPreferencePage_SegmentSize, parent);
		segmentSizeFieldEditor.setValidRange(1, DownloadConfiguration.MaxSegmentSize);
		this.addField(segmentSizeFieldEditor);

		IntegerFieldEditor lowerSizeLimitFieldEditor = new IntegerFieldEditor(DownloadConfiguration.Key_LowerSizeLimit,
				Messages.DownloadConfigurationPreferencePage_LowerLimitSize, parent);
		lowerSizeLimitFieldEditor.setValidRange(0, DownloadConfiguration.MaxLowerSizeLimit);
		this.addField(lowerSizeLimitFieldEditor);

		{
			String[][] entryNamesAndValues = new String[][] {
					{ Messages.DownloadConfigurationPreferencePage_OverWrite, DownloadConfiguration.DuplicatedOverWriteExistFile + "" }, // $NON-NLS-2$ //$NON-NLS-1$
					{ Messages.DownloadConfigurationPreferencePage_AutoRename, DownloadConfiguration.DuplicatedAutoRenameFile + "" }, // $NON-NLS-2$ //$NON-NLS-1$
					{ Messages.DownloadConfigurationPreferencePage_AutoBackup, DownloadConfiguration.DuplicatedAutoBackupFile + "" } }; // $NON-NLS-2$ //$NON-NLS-1$

			ComboFieldEditor duplicatedStrategyFieldEditor = new ComboFieldEditor(DownloadConfiguration.Key_DuplicatedStrategy,
					Messages.DownloadConfigurationPreferencePage_DuplicatedStrategy, entryNamesAndValues, parent);
			this.addField(duplicatedStrategyFieldEditor);
		}

		{
			String[][] entryNamesAndValues = new String[][] {
					{ Messages.DownloadConfigurationPreferencePage_StartOrPause, DownloadConfiguration.DoubleClickAction_StartOrPause + "" },
					{ Messages.DownloadConfigurationPreferencePage_OpenUrl, DownloadConfiguration.DoubleClickAction_OpenUrl + "" }, // $NON-NLS-2$ //$NON-NLS-1$
					{ Messages.DownloadConfigurationPreferencePage_OpenFile, DownloadConfiguration.DoubleClickAction_OpenFile + "" }, // $NON-NLS-2$ //$NON-NLS-1$
					{ Messages.DownloadConfigurationPreferencePage_OpenFolder, DownloadConfiguration.DoubleClickAction_OpenFolder + "" }

			}; // $NON-NLS-2$ //$NON-NLS-1$

			ComboFieldEditor doubleClickActionFieldEditor = new ComboFieldEditor(DownloadConfiguration.Key_DoubleClickAction,
					Messages.DownloadConfigurationPreferencePage_DoubleClickAction, entryNamesAndValues, parent);
			this.addField(doubleClickActionFieldEditor);
		}

		BooleanFieldEditor deletePromotionFieldEditor = new BooleanFieldEditor(DownloadConfiguration.Key_ConfirmDelete,
				Messages.DownloadConfigurationPreferencePage_ConfirmDelete, parent);
		this.addField(deletePromotionFieldEditor);

		if (SystemUtil.isSupportDeleteToTrash()) {
			BooleanFieldEditor deleteToTrashFieldEditor = new BooleanFieldEditor(DownloadConfiguration.Key_DeleteToTrash,
					Messages.DownloadConfigurationPreferencePage_DeleteToTrash, parent);
			this.addField(deleteToTrashFieldEditor);
		}

		BooleanFieldEditor mergeHlsFieldEditor = new BooleanFieldEditor(DownloadConfiguration.Key_MergeHls,
				Messages.DownloadConfigurationPreferencePage_MergeHLS, parent);
		this.addField(mergeHlsFieldEditor);

		this.deleteMergedHlsTrashFieldEditor = new BooleanFieldEditor(DownloadConfiguration.Key_DeleteMergedHls,
				Messages.DownloadConfigurationPreferencePage_DeleteMergedHLS, parent);
		this.addField(this.deleteMergedHlsTrashFieldEditor);

		FileFieldEditor ffmpegFileFieldEditor = new FileFieldEditor(DownloadConfiguration.Key_FFmpegFile,
				Messages.DownloadConfigurationPreferencePage_FfmpegMergeFile, parent);
		this.addField(ffmpegFileFieldEditor);

		this.ffmpegMergeCommandFieldEditor = new StringFieldEditor(DownloadConfiguration.Key_FfmpegMergeCommand,
				Messages.DownloadConfigurationPreferencePage_FfmpegMergeCommand, parent);
		this.addField(this.ffmpegMergeCommandFieldEditor);

		StringFieldEditor clearCommandFieldEditor = new StringFieldEditor(DownloadConfiguration.Key_ClearCommand,
				Messages.DownloadConfigurationPreferencePage_ClearCommand, parent);
		this.addField(clearCommandFieldEditor);

		this.ffmpegDownloadCommandFieldEditor = new StringFieldEditor(DownloadConfiguration.Key_FfmpegDownloadCommand,
				Messages.DownloadConfigurationPreferencePage_FfmpegDownloadCommand, parent);
		this.addField(this.ffmpegDownloadCommandFieldEditor);

		BooleanFieldEditor autoAddSiteConstraintEditor = new BooleanFieldEditor(DownloadConfiguration.Key_AutoAddSiteConstraint,
				Messages.DownloadConfigurationPreferencePage_AutoAddSiteConstraint, parent);
		this.addField(autoAddSiteConstraintEditor);

		SiteConcurrentFieldEditor siteConcurrentFieldEditor = new SiteConcurrentFieldEditor(DownloadConfiguration.Key_SiteConcurrent,
				Messages.DownloadConfigurationPreferencePage_SiteConcurrent, parent);
		this.addField(siteConcurrentFieldEditor);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);

		if (event.getSource() instanceof FieldEditor) {
			FieldEditor fieldEditor = (FieldEditor) event.getSource();

			if (DownloadConfiguration.Key_MergeHls.equals(fieldEditor.getPreferenceName())) {
				boolean newValue = (boolean) event.getNewValue();
				this.deleteMergedHlsTrashFieldEditor.setEnabled(newValue, getFieldEditorParent());
			}

			if (DownloadConfiguration.Key_CheckDiskLeftSpace.equals(fieldEditor.getPreferenceName())) {
				boolean newValue = (boolean) event.getNewValue();
				this.diskLeftSpaceFieldEditor.setEnabled(newValue, getFieldEditorParent());
			}

			if (DownloadConfiguration.Key_FFmpegFile.equals(fieldEditor.getPreferenceName())) {
				String fileName = (String) event.getNewValue();
				if (StringUtils.isNotBlank(fileName)) {
					this.ffmpegMergeCommandFieldEditor.setStringValue(DownloadConfiguration.generateFfmpegMergeCommand(fileName));
					this.ffmpegDownloadCommandFieldEditor.setStringValue(DownloadConfiguration.generateFfmpegDownloadCommand(fileName));
				}
			}
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();

		DownloadManager.getInstance().getConfiguration().rebuildSiteConcurrent();

		return result;
	}

}
