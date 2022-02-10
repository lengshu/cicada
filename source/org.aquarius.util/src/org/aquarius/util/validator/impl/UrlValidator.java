/**
 *
 */
package org.aquarius.util.validator.impl;

import org.aquarius.util.validator.base.AbstractStringValidator;

/**
 * To validate a string is a url or not.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UrlValidator extends AbstractStringValidator {

	/**
	 *
	 */
	public UrlValidator() {
		this("error");
	}

	/**
	 * @param message
	 */
	public UrlValidator(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean doValidate(String value) {
		return org.apache.commons.validator.routines.UrlValidator.getInstance().isValid(value);
	}

}
