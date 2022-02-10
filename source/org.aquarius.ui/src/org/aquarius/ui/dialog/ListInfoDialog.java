/**
 *
 */
package org.aquarius.ui.dialog;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aquarius.ui.Messages;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.util.StringUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * display string by lines.<BR>
 * Use can copy or save.<BR>
 * when saving to file.the encoding is "UTF-8"<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ListInfoDialog extends Dialog {

	private String content;

	private String title;

	private Text text;

	private static final int SaveAsId = 880;

	private static final int CopyToClipboardId = 888;

	/**
	 * @param parentShell
	 */
	public ListInfoDialog(Shell parentShell, String title, String content) {
		super(parentShell);

		this.title = title;

		this.content = content;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (this.title != null) {
			shell.setText(this.title);
		}

		shell.setSize(1200, 720);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);

		this.text = new Text(composite, SWT.MULTI | SWT.BORDER);
		this.text.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.text.setText(this.content);

		applyDialogFont(composite);
		return composite;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {

		if (SaveAsId == buttonId) {
			FileDialog fileDialog = new FileDialog(getShell());
			String fileName = fileDialog.open();

			if (StringUtils.isNotEmpty(fileName)) {
				try {
					FileUtils.write(new File(fileName), this.content, StringUtil.CODEING_UTF8);
				} catch (IOException e1) {
					MessageDialog.openError(getShell(), "", //$NON-NLS-1$
							ExceptionUtils.getFullStackTrace(e1));
				}
			}
			this.close();
			return;
		}

		if (CopyToClipboardId == buttonId) {
			ClipboardUtil.setClipboardString(this.content);

			this.close();
			return;
		}

		if (IDialogConstants.CLOSE_ID == buttonId) {

			this.close();
			return;
		}

		super.buttonPressed(buttonId);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		createButton(parent, IDialogConstants.CLOSE_ID, Messages.ListInfoDialog_Close, true);
		createButton(parent, SaveAsId, Messages.ListInfoDialog_SaveAs, false);
		createButton(parent, CopyToClipboardId, Messages.ListInfoDialog_CopyToClipboard, false);

	}

}