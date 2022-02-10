/**
 *
 */
package org.aquarius.ui.binder.impl;

import org.aquarius.ui.binder.IValueAccessor;
import org.aquarius.ui.binder.base.AbstractControBinder;
import org.eclipse.swt.widgets.Label;

/**
 * Binder for text.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LabelBinder extends AbstractControBinder<Label, String> {

	/**
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 */
	public LabelBinder(Label control, IValueAccessor valueAccessor, Object object, String propertyName) {
		super(control, valueAccessor, object, propertyName);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad(Label control, String value) {
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
	public boolean isDirty() {
		return false;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void save() {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void resetDirtyState() {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String validate() {
		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected String getValue(Label control) {
		return control.getText();
	}

}
