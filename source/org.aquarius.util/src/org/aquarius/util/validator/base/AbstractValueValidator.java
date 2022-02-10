/**
 *
 */
package org.aquarius.util.validator.base;

import java.text.MessageFormat;

import org.aquarius.util.validator.IValueValidator;

/**
 * a base class.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractValueValidator<T> implements IValueValidator<T> {

	private String message;

	private Class<T> checkType;

	/**
	 *
	 */
	public AbstractValueValidator() {
		this("error");
	}

	/**
	 * @param message
	 */
	public AbstractValueValidator(String message) {
		super();
		this.message = message;
		this.checkType = initCheckType();
	}

	/**
	 * @return
	 */
	protected Class<T> initCheckType() {
		return null;
	}

	/**
	 * @param message
	 * @param checkType
	 */
	public AbstractValueValidator(String message, Class<T> checkType) {
		super();
		this.message = message;
		this.checkType = checkType;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param checkType the checkType to set
	 */
	public void setCheckType(Class<T> checkType) {
		this.checkType = checkType;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String validate(T value) {

		if ((null != value) && (null != this.checkType)) {
			if (value.getClass() != this.checkType) {
				return returnErrorMessage(value);
			}
		}

		if (this.doValidate(value)) {
			return null;
		}

		return returnErrorMessage(value);
	}

	/**
	 * @param value
	 * @return
	 */
	private String returnErrorMessage(T value) {
		if (null != this.message) {
			return MessageFormat.format(this.message, value);
		}

		return "error";
	}

	public abstract boolean doValidate(T value);

}
