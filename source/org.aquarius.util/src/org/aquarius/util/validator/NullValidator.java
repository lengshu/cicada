/**
 *
 */
package org.aquarius.util.validator;

import org.aquarius.util.validator.base.AbstractValueValidator;

/**
 *
 * Null implementation.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class NullValidator extends AbstractValueValidator<Object> {

	private static final NullValidator Instance = new NullValidator();

	/**
	 * @return the instance
	 */
	public static NullValidator getInstance() {
		return Instance;
	}

	/**
	 *
	 */
	private NullValidator() {
		super();
	}

	/**
	 * @param message
	 * @param checkType
	 */
	public NullValidator(String message, Class<Object> checkType) {
		super(message, checkType);
	}

	/**
	 * @param message
	 */
	public NullValidator(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean doValidate(Object value) {
		return true;
	}

}
