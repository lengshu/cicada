/**
 *
 */
package org.aquarius.ui.binder.impl;

import org.aquarius.ui.binder.IValueAccessor;
import org.aquarius.ui.binder.base.AbstractControBinder;
import org.eclipse.swt.widgets.Text;

/**
 * Binder for text.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class TextBinder extends AbstractControBinder<Text, String> {

	/**
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 */
	public TextBinder(Text control, IValueAccessor valueAccessor, Object object, String propertyName) {
		super(control, valueAccessor, object, propertyName);

		control.addModifyListener(event -> {
			fireValueChangeEvent();
		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad(Text control, String value) {
		if (value == null) {
			control.setText("");
		} else {
			control.setText(value);
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected String getValue(Text control) {
		return control.getText();
	}

}
