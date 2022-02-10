/**
 * 
 */
package org.aquarius.ui.message;

import org.eclipse.jface.dialogs.TitleAreaDialog;

/**
 * Use TitleAreaDialog to show message.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class DialogMessageShower implements IMessageShower {

	private TitleAreaDialog titleAreaDialog;

	/**
	 * @param titleAreaDialog
	 */
	public DialogMessageShower(TitleAreaDialog titleAreaDialog) {
		super();
		this.titleAreaDialog = titleAreaDialog;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMessage(String message, int type) {
		this.titleAreaDialog.setMessage(message, type);
	}

}
