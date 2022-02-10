/**
 *
 */
package org.aquarius.cicada.workbench.dialog;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.dialog.MultiInputDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Create a download task .<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadExternalUrlDialog extends MultiInputDialog {

	private boolean directDownload = false;

	private Button directDownloadButton;

	private static final String AutoDownload = DownloadExternalUrlDialog.class.getName() + ".AutoDownload";

	/**
	 * @param parentShell
	 * @param dialogTitle
	 * @param dialogMessage
	 * @param initialValue
	 * @param validator
	 */
	public DownloadExternalUrlDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		((GridLayout) parent.getLayout()).numColumns++;

		this.directDownloadButton = new Button(parent, SWT.CHECK);
		this.directDownloadButton.setText(Messages.DownloadExternalUrlDialog_DirectDownloadLabel);
		setButtonLayoutData(this.directDownloadButton);

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		this.directDownloadButton.setSelection(store.getBoolean(AutoDownload));

		super.createButtonsForButtonBar(parent);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void okPressed() {

		this.directDownload = this.directDownloadButton.getSelection();
		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		store.setValue(AutoDownload, this.directDownload);

		super.okPressed();
	}

	/**
	 * @return the directDownload
	 */
	public boolean isDirectDownload() {
		return this.directDownload;
	}

}
