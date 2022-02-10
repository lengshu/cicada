/**
 *
 */
package org.aquarius.util.validator.base;

/**
 *
 * A base class to validate string.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractStringValidator extends AbstractValueValidator<String> {

	/**
	 *
	 */
	public AbstractStringValidator() {
		this("error");

	}

	/**
	 * @param message
	 */
	public AbstractStringValidator(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Class<String> initCheckType() {
		return String.class;
	}

}
