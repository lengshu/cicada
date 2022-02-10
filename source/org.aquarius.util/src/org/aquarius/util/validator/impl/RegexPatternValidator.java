/**
 *
 */
package org.aquarius.util.validator.impl;

import java.util.regex.Pattern;

import org.aquarius.util.validator.base.AbstractStringValidator;

/**
 * Regex validator for string.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RegexPatternValidator extends AbstractStringValidator {

	private String regex;

	/**
	 *
	 */
	public RegexPatternValidator() {
		this("error");

	}

	/**
	 * @param message
	 */
	public RegexPatternValidator(String message) {
		super(message);
	}

	/**
	 * Set the regex string.<BR>
	 *
	 * @param regexString
	 */
	public void setRegex(String regexString) {
		this.regex = regexString;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean doValidate(String value) {
		return Pattern.matches(this.regex, value);

	}

}
