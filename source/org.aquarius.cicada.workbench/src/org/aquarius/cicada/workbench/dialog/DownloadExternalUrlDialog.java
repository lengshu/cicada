/**
 *
 */
package org.aquarius.cicada.workbench.dialog;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.dialog.MultiInputDialog;
import org.aquarius.ui.util.WidgetUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Create a download task .<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadExternalUrlDialog extends MultiInputDialog {

	private boolean autoDownload = false;

	private AbstractUrlRedirector urlRedirector;

	private Button directDownloadButton;

	private Button openExternalSiteButton;

	private Combo externalSiteCombo;

	private List<AbstractUrlRedirector> urlRedirectors;

	private static final String AutoDownload = DownloadExternalUrlDialog.class.getName() + ".AutoDownload"; //$NON-NLS-1$

	private static final String OpenExternalSite = DownloadExternalUrlDialog.class.getName() + ".OpenExternalSite"; //$NON-NLS-1$

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
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite oldParent = (Composite) super.createDialogArea(parent);

		this.directDownloadButton = new Button(oldParent, SWT.CHECK);
		this.directDownloadButton.setText(Messages.DownloadExternalUrlDialog_DirectDownloadLabel);
		setWidgetLayoutData(this.directDownloadButton);

		this.openExternalSiteButton = new Button(oldParent, SWT.CHECK);
		this.openExternalSiteButton.setText(Messages.DownloadExternalUrlDialog_OpenExternalSite);
		setWidgetLayoutData(this.openExternalSiteButton);

		this.externalSiteCombo = new Combo(oldParent, SWT.BORDER | SWT.READ_ONLY);
		setWidgetLayoutData(this.externalSiteCombo);

		this.urlRedirectors = RuntimeManager.getInstance().getAllUrlRedirectors();
		for (AbstractUrlRedirector redirector : this.urlRedirectors) {
			this.externalSiteCombo.add(redirector.getName());
		}

		if (this.urlRedirectors.isEmpty()) {
			this.openExternalSiteButton.setVisible(false);
			this.externalSiteCombo.setVisible(false);

		} else {
			this.externalSiteCombo.select(0);
		}

		Text text = this.getText();
		GridData textGridData = (GridData) text.getLayoutData();
		textGridData.heightHint = 320;

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		this.directDownloadButton.setSelection(store.getBoolean(AutoDownload));

		this.openExternalSiteButton.setSelection(store.getBoolean(OpenExternalSite));

		WidgetUtil.bindState(this.openExternalSiteButton, this.externalSiteCombo);

		return oldParent;
	}

	/**
	 * Set the layout data of the button to a GridData with appropriate heights and
	 * widths.
	 *
	 * @param control The control which layout data is to be set.
	 */
	private void setWidgetLayoutData(Control control) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		control.setLayoutData(data);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void okPressed() {

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

		this.autoDownload = this.directDownloadButton.getSelection();
		store.setValue(AutoDownload, this.autoDownload);

		store.setValue(OpenExternalSite, this.openExternalSiteButton.getSelection());

		if ((!this.urlRedirectors.isEmpty()) && (this.openExternalSiteButton.getSelection())) {
			this.urlRedirector = this.urlRedirectors.get(this.externalSiteCombo.getSelectionIndex());
		}

		super.okPressed();
	}

	/**
	 * @return the autoDownload
	 */
	public boolean isAutoDownload() {
		return this.autoDownload;
	}

	/**
	 * @return the urlRedirector
	 */
	public AbstractUrlRedirector getUrlRedirector() {
		return this.urlRedirector;
	}

}
