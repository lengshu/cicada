/**
 *
 */
package org.aquarius.ui.binder;

import org.aquarius.util.validator.IValueValidator;
import org.eclipse.swt.widgets.Control;

/**
 *
 * Control binder to support validation and other functions.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IControlBinder<C extends Control, T> {

	/**
	 * Load data from source to control.<BR>
	 */
	public void load();

	/**
	 * Save the control value to source.<BR>
	 */
	public void save();

	/**
	 * Set a validator to validate control value.<BR>
	 * If you want to validate the value in many ways,<BR>
	 * Please use composite validator to support it.<BR>
	 *
	 * @see org.aquarius.util.validator.impl.CompositeValueValidator<T>
	 * @param valueValidators
	 */
	public void setValidator(IValueValidator<T>... valueValidators);

	/**
	 * Validate the control value.<BR>
	 * If not validator set,the result must be null.<BR>
	 *
	 * @return
	 */
	public String validate();

	/**
	 * Return whether user change the control value.<BR>
	 *
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Reset the dirty state.<BR>
	 * It means all changes are accepted and saved.<BR>
	 */
	public void resetDirtyState();

	/**
	 * Set a value change listener.<BR>
	 *
	 * @param valueChangeListener
	 */
	public void setValueChangeListener(IValueChangeListener valueChangeListener);

}
