/**
 *
 */
package org.aquarius.ui.validator;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Use regex to validate a string.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RegexValidator implements IInputValidator {

	private org.apache.commons.validator.routines.RegexValidator validator;

	private String errorMessage;

	/**
	 *
	 */
	public RegexValidator(String pattern, String errorMessage) {

		this.validator = new org.apache.commons.validator.routines.RegexValidator(pattern);

		this.errorMessage = errorMessage;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String isValid(String newText) {
		if (this.validator.isValid(newText)) {
			return null;
		} else {
			return this.errorMessage;
		}
	}

}
