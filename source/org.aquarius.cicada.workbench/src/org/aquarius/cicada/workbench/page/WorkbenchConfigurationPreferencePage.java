/**
 *
 */
package org.aquarius.cicada.workbench.page;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.RuntimeConstant;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.ui.message.preference.KeyStrokeFieldEditor;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference for WorkbenchConfiguration
 *
 * @author aquarius.github@gmail.com
 *
 */
public class WorkbenchConfigurationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public WorkbenchConfigurationPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		this.setPreferenceStore(store);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = this.getFieldEditorParent();

		if (SwtUtil.isSupportEdge()) {

			Composite edgeParent = new Composite(parent, SWT.NONE);

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			edgeParent.setLayoutData(gd);

			FieldEditor useEdgeFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_UseEdge, "", edgeParent); //$NON-NLS-1$
			this.addField(useEdgeFieldEditor);

			GridLayout gridLayout = new GridLayout(4, false);
			gridLayout.marginWidth = 0;
			edgeParent.setLayout(gridLayout);

			Link link = SwtUtil.createLink(edgeParent, Messages.WorkbenchConfigurationPreferencePage_UseEdge);

			link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		}

		FieldEditor confirmQuitMenuFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_ConfirmQuit,
				Messages.WorkbenchConfigurationPreferencePage_ConfirmQuit, parent);

		String[][] entryNamesAndValues = new String[][] { { Messages.WorkbenchConfigurationPreferencePage_TooltipShowAllInfo, TooltipUtil.LevelInfo + "" }, // $NON-NLS-2$
				{ Messages.WorkbenchConfigurationPreferencePage_TooltipShowJustError, TooltipUtil.LevelError + "" }, // $NON-NLS-2$
				{ Messages.WorkbenchConfigurationPreferencePage_TooltipShowNothing, TooltipUtil.LevelNo + "" } }; // $NON-NLS-2$

		FieldEditor isPersistNatTableConfigFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_PersistNatTableConfig,
				Messages.WorkbenchConfigurationPreferencePage_PersistNatTableConfig, parent);
		FieldEditor isPersistBrowserConfigFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_PersistBrowserConfig,
				Messages.WorkbenchConfigurationPreferencePage_PersistBrowserConfig, parent);

		FieldEditor userBrowserMenuFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_UseBrowserMenu,
				Messages.WorkbenchConfigurationPreferencePage_UseBrowserMenu, parent);
		FieldEditor showInfoDialogFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_ShowInfoDialog,
				Messages.WorkbenchConfigurationPreferencePage_ShowInfoDialog, parent);
		FieldEditor autoFinishDialogFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_AutoFinish,
				Messages.WorkbenchConfigurationPreferencePage_AutoFinish, parent);

		FieldEditor disableExtensionTableFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_DisableExtensionTable,
				Messages.WorkbenchConfigurationPreferencePage_DisableExtensionTable, parent);

		FieldEditor doubleClickToOpenUrlDialogFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_DoubleClickToOpenUrl,
				Messages.WorkbenchConfigurationPreferencePage_DoubleClickToOpenUrl, parent);

		FieldEditor showTooltipDialogFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_ShowTooltip,
				Messages.WorkbenchConfigurationPreferencePage_ShowTooltip, parent);
		FieldEditor hideImageTooltipDialogFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_HideImageTooltip,
				Messages.WorkbenchConfigurationPreferencePage_HideImageTooltip, parent);

		FieldEditor autoGcFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_AutoGc, Messages.WorkbenchConfigurationPreferencePage_AutoGc, parent);

		FieldEditor autoCleanLogFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_AutoCleanLog,
				Messages.WorkbenchConfigurationPreferencePage_AutoCleanLog, parent);

		FieldEditor showClearSiteActionFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_ShowClearSiteAction,
				Messages.WorkbenchConfigurationPreferencePage_ShowClearSiteAction, parent);

		this.addField(confirmQuitMenuFieldEditor);

		this.addField(isPersistNatTableConfigFieldEditor);
		this.addField(isPersistBrowserConfigFieldEditor);
		this.addField(userBrowserMenuFieldEditor);
		this.addField(showInfoDialogFieldEditor);
		this.addField(autoFinishDialogFieldEditor);
		this.addField(disableExtensionTableFieldEditor);
		this.addField(doubleClickToOpenUrlDialogFieldEditor);

		this.addField(showTooltipDialogFieldEditor);
		this.addField(hideImageTooltipDialogFieldEditor);
		this.addField(autoGcFieldEditor);
		this.addField(autoCleanLogFieldEditor);
		this.addField(showClearSiteActionFieldEditor);

		FieldEditor tableFullSelectionStyleFieldEditor = new BooleanFieldEditor(WorkbenchConfiguration.Key_TableFullSelection,
				Messages.WorkbenchConfigurationPreferencePage_TableFullSelection, parent);

		this.addField(tableFullSelectionStyleFieldEditor);

		KeyStrokeFieldEditor autoActivationKeyFieldEditor = new KeyStrokeFieldEditor(WorkbenchConfiguration.Key_AutoActivationKey,
				Messages.WorkbenchConfigurationPreferencePage_AutoActivationKey, parent);

		this.addField(autoActivationKeyFieldEditor);

		FieldEditor tooltipLevelFieldEditor = new ComboFieldEditor(WorkbenchConfiguration.Key_TooltipLevel,
				Messages.WorkbenchConfigurationPreferencePage_TooltipLevel, entryNamesAndValues, parent);
		this.addField(tooltipLevelFieldEditor);

		entryNamesAndValues = new String[][] { { Messages.WorkbenchConfigurationPreferencePage_UseInnerFilter, RuntimeConstant.FilterTypeInner + "" }, // $NON-NLS-2$ //$NON-NLS-1$
				{ Messages.WorkbenchConfigurationPreferencePage_UseSimpleFilter, RuntimeConstant.FilterTypeSimple + "" }, // $NON-NLS-2$ //$NON-NLS-1$
				{ Messages.WorkbenchConfigurationPreferencePage_UseOutlineFilter, RuntimeConstant.FilterTypeOutline + "" } }; // $NON-NLS-2$ //$NON-NLS-1$

		FieldEditor filterTypeFieldEditor = new ComboFieldEditor(WorkbenchConfiguration.Key_FilterType,
				Messages.WorkbenchConfigurationPreferencePage_FilterType, entryNamesAndValues, parent);
		this.addField(filterTypeFieldEditor);

		entryNamesAndValues = WorkbenchUtil.createEntryNamesAndValues(SiteMultiPageEditor.getEditorNames());

		FieldEditor defaultEditorFieldEditor = new ComboFieldEditor(WorkbenchConfiguration.Key_DefaultEditor,
				Messages.WorkbenchConfigurationPreferencePage_DefaultEditor, entryNamesAndValues, parent);
		this.addField(defaultEditorFieldEditor);

		IntegerFieldEditor historyKeywordCountFieldEditor = new IntegerFieldEditor(WorkbenchConfiguration.Key_HistoryKeywordCount,
				Messages.WorkbenchConfigurationPreferencePage_HistoryKeywordCount, parent);
		historyKeywordCountFieldEditor.setValidRange(5, 100);
		this.addField(historyKeywordCountFieldEditor);

		FieldEditor mediaPlayerFieldEditor = new StringFieldEditor(WorkbenchConfiguration.Key_MediaPlayerCommand,
				Messages.WorkbenchConfigurationPreferencePage_MediaPlayerCommand, parent);
		this.addField(mediaPlayerFieldEditor);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();

		TooltipUtil.setLevel(this.getPreferenceStore().getInt(WorkbenchConfiguration.Key_TooltipLevel));

		return result;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do
	}

}
