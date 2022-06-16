/**
 * 
 */
package org.aquarius.ui.util;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * Widget function provider.
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public final class WidgetUtil {

	/**
	 * 
	 */
	private WidgetUtil() {
		// No instances will be created.
	}

	/**
	 * 
	 * @param button
	 * @param controls
	 */
	public static void bindState(Button button, Control... controls) {

		if ((button.getStyle() & SWT.CHECK) == 0) {
			return;
		}

		if (ArrayUtils.isEmpty(controls)) {
			return;
		}

		button.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {

				for (Control control : controls) {
					control.setEnabled(button.getSelection());
				}
			}

		});

		for (Control control : controls) {
			control.setEnabled(button.getSelection());
		}

	}

}
