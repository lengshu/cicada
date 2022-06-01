/**
 *
 */
package org.aquarius.cicada.workbench.page;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.job.UpdateConfigJob;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for update configuration.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateConfigurationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public UpdateConfigurationPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		this.setPreferenceStore(store);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = this.getFieldEditorParent();

		TitledSeparator configTitledSeparator = new TitledSeparator(parent, SWT.None);
		configTitledSeparator.setText(Messages.UpdateConfigurationPreferencePage_UpdateConfigTitleSeparator);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		configTitledSeparator.setLayoutData(gridData);

		StringFieldEditor updateUrlFieldEditor = new StringFieldEditor(UpdateConfigJob.KeyUpdateUrl, Messages.UpdateConfigurationPreferencePage_UpdateUrl,
				parent);
		this.addField(updateUrlFieldEditor);

		IntegerFieldEditor updateIntervalFieldEditor = new IntegerFieldEditor(UpdateConfigJob.KeyUpdateInterval,
				Messages.UpdateConfigurationPreferencePage_UpdateInterval, parent);
		updateIntervalFieldEditor.setValidRange(1, 30);
		this.addField(updateIntervalFieldEditor);

		String[][] entryNamesAndValues = new String[][] { { Messages.UpdateConfigurationPreferencePage_IntervalDay0, "0" },
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay1, "1" }, // $NON-NLS-2$ //$NON-NLS-2$
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay3, "3" }, // $NON-NLS-2$ //$NON-NLS-2$
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay7, "7" }, { Messages.UpdateConfigurationPreferencePage_IntervalDay15, "15" }, //$NON-NLS-2$
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay30, "30" } }; // $NON-NLS-2$ //$NON-NLS-4$ //$NON-NLS-6$

		FieldEditor filterTypeFieldEditor = new ComboFieldEditor(WorkbenchConfiguration.Key_FilterType,
				Messages.WorkbenchConfigurationPreferencePage_FilterType, entryNamesAndValues, parent);
		this.addField(filterTypeFieldEditor);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do

	}

}
