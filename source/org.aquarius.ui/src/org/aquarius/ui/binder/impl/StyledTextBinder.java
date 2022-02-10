/**
 *
 */
package org.aquarius.ui.binder.impl;

import org.aquarius.ui.binder.IValueAccessor;
import org.aquarius.ui.binder.base.AbstractControBinder;
import org.eclipse.swt.custom.StyledText;

/**
 * Binder for styledtext.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class StyledTextBinder extends AbstractControBinder<StyledText, String> {

	/**
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 */
	public StyledTextBinder(StyledText control, IValueAccessor valueAccessor, Object object, String propertyName) {
		super(control, valueAccessor, object, propertyName);
		control.addModifyListener(event -> {
			fireValueChangeEvent();
		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad(StyledText control, String value) {
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
	protected String getValue(StyledText control) {
		return control.getText();
	}

}
