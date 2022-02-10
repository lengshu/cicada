/**
 *
 */
package org.aquarius.ui.binder.validator;

import org.apache.commons.validator.routines.RegexValidator;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.validator.IValueValidator;

/**
 * Use regex to validate string.<BR>
 *
 * @author aquarius.github@gmail.com
 * @param <T>
 *
 */
public class RegexValueValidator implements IValueValidator<String> {

	private RegexValidator validator;

	/**
	 *
	 * @param pattern
	 */
	public RegexValueValidator(String pattern) {

		AssertUtil.assertNotNull(pattern);
		this.validator = new RegexValidator(pattern);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String validate(String newText) {
		if (this.validator.isValid(newText)) {
			return null;
		} else {
			return "Please input a correct name like mysite123.";
		}
	}

}
