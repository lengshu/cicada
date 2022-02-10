/**
 *
 */
package org.aquarius.ui.dialog;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Allow user to input multi line text.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MultiInputDialog extends InputDialog {

	/**
	 * @param parentShell
	 * @param dialogTitle
	 * @param dialogMessage
	 * @param initialValue
	 * @param validator
	 */
	public MultiInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected int getInputTextStyle() {
		return SWT.MULTI | SWT.BORDER;
	}

}
