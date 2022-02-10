/**
 *
 */
package org.aquarius.ui.binder.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.ui.binder.IValueAccessor;
import org.aquarius.ui.binder.base.AbstractControBinder;
import org.aquarius.util.NumberUtil;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Spinner;

/**
 * Binder for spinner.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SpinnerBinder extends AbstractControBinder<Spinner, Integer> {

	/**
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 */
	public SpinnerBinder(Spinner control, IValueAccessor valueAccessor, Object object, String propertyName) {
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
	protected Integer getValue(Spinner control) {
		return NumberUtils.toInt(control.getText(), 0);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad(Spinner control, Integer value) {

		control.setSelection(NumberUtil.getIntValue(value));

	}

}
