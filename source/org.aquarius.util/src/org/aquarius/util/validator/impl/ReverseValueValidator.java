/**
 *
 */
package org.aquarius.util.validator.impl;

import org.aquarius.util.validator.base.AbstractValueValidator;

/**
 * Return the reverse result with the specified validator.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ReverseValueValidator<T> extends AbstractValueValidator<T> {

	private AbstractValueValidator<T> validator;

	/**
	 * @param message
	 * @param validator
	 */
	public ReverseValueValidator(String message, AbstractValueValidator<T> validator) {
		super(message);
		this.validator = validator;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean doValidate(T value) {
		return !this.validator.doValidate(value);
	}

}
