/**
 *
 */
package org.aquarius.ui.binder;

import org.eclipse.swt.widgets.Control;

/**
 * Monitor the value change of a source object.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IValueChangeListener {

	/**
	 * when user change the value of a control.<BR>
	 * It will be called.<BR>
	 *
	 * @param control
	 */
	public void valueChanged(Control control);

}
