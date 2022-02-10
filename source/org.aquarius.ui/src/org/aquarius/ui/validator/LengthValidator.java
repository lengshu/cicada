/**
 *
 */
package org.aquarius.ui.validator;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.aquarius.ui.Messages;
import org.aquarius.util.AssertUtil;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class LengthValidator implements IInputValidator {

	private int length;

	/**
	 * @param length
	 */
	public LengthValidator(int length) {
		super();

		AssertUtil.assertTrue(length > 0, "The length should >0"); //$NON-NLS-1$

		this.length = length;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String isValid(String newText) {

		if (StringUtils.isBlank(newText)) {
			return Messages.LengthValidator_NotBlankMessage;
		}

		if (newText.length() < this.length) {
			return null;
		} else {
			String message = MessageFormat.format(Messages.LengthValidator_LengthLessThan, this.length);
			return message;
		}
	}

}
