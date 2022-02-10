/**
 * 
 */
package org.aquarius.ui.message;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Use ControlDecoration to show message.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class ControlDecorationMessageShower implements IMessageShower {

	private ControlDecoration controlDecoration;

	/**
	 * @param controlDecoration
	 */
	public ControlDecorationMessageShower(ControlDecoration controlDecoration) {
		super();
		this.controlDecoration = controlDecoration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMessage(String message, int type) {

		Image newImage = null;
		switch (type) {
		case IMessageProvider.NONE:
			break;
		case IMessageProvider.INFORMATION:
			newImage = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
			break;
		case IMessageProvider.WARNING:
			newImage = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
			break;
		case IMessageProvider.ERROR:
			newImage = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
			break;
		}

		this.controlDecoration.setImage(newImage);
		this.controlDecoration.setDescriptionText(message);
		this.controlDecoration.show();

	}

}
