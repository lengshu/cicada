/**
 * 
 */
package org.aquarius.ui.message;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Use Label to show message.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class LabelMessageShower implements IMessageShower {

	private CLabel label;

	/**
	 * @param button
	 */
	public LabelMessageShower(CLabel label) {
		super();
		this.label = label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMessage(String message, int type) {
		Image newImage = null;

		Color newColor = Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND);

		if (StringUtils.isNotEmpty(message)) {
			if (message != null) {
				switch (type) {
				case IMessageProvider.NONE:
					newImage = null;
					break;
				case IMessageProvider.INFORMATION:
					newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
					break;
				case IMessageProvider.WARNING:
					newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
					break;
				case IMessageProvider.ERROR:
					newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
					newColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
					break;
				}
			}
		}

		this.label.setForeground(newColor);
		this.label.setText(message);
		this.label.setImage(newImage);
	}

}
