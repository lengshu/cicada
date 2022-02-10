/**
 *
 */
package org.aquarius.util.validator.impl;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.validator.base.AbstractStringValidator;

/**
 * To validate a string to make sure it should not be blank.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NotBlankValidator extends AbstractStringValidator {

	/**
	 *
	 */
	public NotBlankValidator() {
		this("error");
	}

	/**
	 * @param message
	 */
	public NotBlankValidator(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean doValidate(String value) {
		return StringUtils.isNotBlank(value);
	}

}
