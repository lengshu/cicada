/**
 *
 */
package org.aquarius.ui.binder.base;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.ui.binder.IControlBinder;
import org.aquarius.ui.binder.IValueAccessor;
import org.aquarius.ui.binder.IValueChangeListener;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.validator.IValueValidator;
import org.aquarius.util.validator.impl.CompositeValueValidator;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * Base class for a control binder.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractControBinder<C extends Control, T> implements IControlBinder<C, T> {

	private C control;

	private IValueAccessor valueAccessor;

	private Object object;

	private T oldValue;

	private String propertyName;

	private IValueValidator<T> valueValidator;

	private ControlDecoration errorDecoration;

	private IValueChangeListener valueChangeListener;

	/**
	 *
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 */
	public AbstractControBinder(C control, IValueAccessor valueAccessor, Object object, String propertyName) {

		this.control = control;
		this.valueAccessor = valueAccessor;

		this.object = object;
		this.propertyName = propertyName;

		this.errorDecoration = this.createErrorDecoration(control);
		createFocusListener();

		resetDirtyState();

	}

	/**
	 * when user change the value ,it should be invoked.<BR>
	 */
	protected void fireValueChangeEvent() {
		if (null != this.valueChangeListener) {
			this.valueChangeListener.valueChanged(this.control);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDirty() {
		Object currentValue = getValue(this.control);
		return !ObjectUtils.equals(currentValue, this.oldValue);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void resetDirtyState() {
		this.oldValue = (T) this.valueAccessor.getValue(this.object, this.propertyName);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValueChangeListener(IValueChangeListener valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}

	/**
	 * Create a error decoration on the left-top corner.<BR>
	 *
	 * @param control
	 */
	private ControlDecoration createErrorDecoration(C control) {
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
		ControlDecoration errorDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
		errorDecoration.setImage(image);
		errorDecoration.hide();

		return errorDecoration;

	}

	/**
	 * Return the current control<BR>
	 *
	 * @return
	 */
	protected C getControl() {
		return this.control;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void save() {
		T value = this.getValue(this.control);
		this.valueAccessor.setValue(this.object, this.propertyName, value);
		resetDirtyState();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void load() {
		this.doLoad(this.control, this.oldValue);
	}

	/**
	 * Update the value to the control.<BR>
	 *
	 * @param control
	 * @param value
	 */
	protected abstract void doLoad(C control, T value);

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String validate() {
		String message = doValidate();
		this.errorDecoration.setDescriptionText(message);

		if (StringUtils.isNotEmpty(message)) {
			this.errorDecoration.show();
		} else {
			this.errorDecoration.hide();
		}

		return message;
	}

	/**
	 * Use the validator to valid input.<BR>
	 *
	 * @return
	 */
	private String doValidate() {
		if (null != this.valueValidator) {
			T value = this.getValue(this.control);
			return this.valueValidator.validate(value);
		}
		return null;
	}

	/**
	 * Return the value of the control.<BR>
	 *
	 * @param control
	 * @return
	 */
	protected abstract T getValue(C control);

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValidator(IValueValidator<T>... valueValidators) {
		AssertUtil.assertNotNull(valueValidators);
		AssertUtil.assertTrue(ArrayUtils.isNotEmpty(valueValidators), "please check the parameters,it can't be zero length array.");

		if (valueValidators.length == 1) {
			this.valueValidator = valueValidators[0];
		} else {
			this.valueValidator = new CompositeValueValidator<>(true, valueValidators);
		}
	}

	/**
	 * create a focus listener.<BR>
	 * When focus lost, do validation and show error decoration.<BR>
	 */
	private void createFocusListener() {
		this.control.addFocusListener(new FocusAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void focusLost(FocusEvent e) {
				String message = validate();
				AbstractControBinder.this.errorDecoration.setDescriptionText(message);

				if (StringUtils.isNotBlank(message)) {
					AbstractControBinder.this.errorDecoration.show();
				} else {
					AbstractControBinder.this.errorDecoration.hide();
				}
			}

		});
	}
}
