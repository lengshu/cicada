/**
 *
 */
package org.aquarius.ui.binder.impl;

import org.apache.commons.lang.BooleanUtils;
import org.aquarius.ui.binder.IValueAccessor;
import org.aquarius.ui.binder.base.AbstractControBinder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

/**
 * Binder for check box.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CheckboxBinder extends AbstractControBinder<Button, Boolean> {

	/**
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 */
	public CheckboxBinder(Button control, IValueAccessor valueAccessor, Object object, String propertyName) {
		super(control, valueAccessor, object, propertyName);

		control.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				fireValueChangeEvent();
			}
		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Boolean getValue(Button control) {
		return control.getSelection();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad(Button control, Boolean value) {
		control.setSelection(BooleanUtils.isTrue(value));
	}

}
