/**
 *
 */
package org.aquarius.cicada.workbench.page;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.job.UpdateConfigJob;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Version;

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
		store.setDefault(UpdateConfigJob.KeyUpdateInterval, "7");
		store.setDefault(UpdateConfigJob.KeyLastVersion, "");

		this.setPreferenceStore(store);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {

		IPreferenceStore store = this.getPreferenceStore();
		store.setToDefault(UpdateConfigJob.KeyLastVersion);

		super.performDefaults();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = this.getFieldEditorParent();

		TitledSeparator configTitledSeparator = new TitledSeparator(parent, SWT.None);

		String title = computeTitle();
		configTitledSeparator.setText(title);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		configTitledSeparator.setLayoutData(gridData);

		StringFieldEditor updateUrlFieldEditor = new StringFieldEditor(UpdateConfigJob.KeyUpdateUrl, Messages.UpdateConfigurationPreferencePage_UpdateUrl,
				parent);
		this.addField(updateUrlFieldEditor);

		String[][] entryNamesAndValues = new String[][] { { Messages.UpdateConfigurationPreferencePage_IntervalDay0, "0" },
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay1, "1" }, // $NON-NLS-2$ //$NON-NLS-2$
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay3, "3" }, // $NON-NLS-2$ //$NON-NLS-2$
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay7, "7" }, { Messages.UpdateConfigurationPreferencePage_IntervalDay15, "15" }, //$NON-NLS-2$
				{ Messages.UpdateConfigurationPreferencePage_IntervalDay30, "30" } }; // $NON-NLS-2$ //$NON-NLS-4$ //$NON-NLS-6$

		FieldEditor updateIntervalFieldEditor = new ComboFieldEditor(UpdateConfigJob.KeyUpdateInterval,
				Messages.UpdateConfigurationPreferencePage_UpdateInterval, entryNamesAndValues, parent);
		this.addField(updateIntervalFieldEditor);

	}

	/**
	 * @return
	 */
	private String computeTitle() {
		String title = null;

		IPreferenceStore store = this.getPreferenceStore();
		String versionString = store.getString(UpdateConfigJob.KeyLastVersion);

		if (StringUtils.isEmpty(versionString)) {
			Version bundleVersion = WorkbenchActivator.getDefault().getBundle().getVersion();
			versionString = bundleVersion.toString();
		}

		title = MessageFormat.format(Messages.UpdateConfigurationPreferencePage_UpdateConfigTitleSeparator, versionString);
		return title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do

	}

}
