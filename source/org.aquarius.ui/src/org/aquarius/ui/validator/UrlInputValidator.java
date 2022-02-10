/**
 *
 */
package org.aquarius.ui.validator;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.aquarius.ui.Messages;
import org.aquarius.util.StringUtil;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 *
 * validate a string is a url or not.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UrlInputValidator implements IInputValidator {

	private UrlValidator validator = UrlValidator.getInstance();

	/**
	 *
	 */
	public UrlInputValidator() {
		super();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String isValid(String text) {

		if (StringUtils.isEmpty(text)) {
			return Messages.UrlInputValidator_PleaseInputUrls;
		}

		String[] lines = StringUtil.toLines(text);

		StringBuilder stringBuilder = new StringBuilder();

		String message = Messages.UrlInputValidator_InputUrlsNotValid;

		for (String line : lines) {
			if (!this.validator.isValid(line)) {
				stringBuilder.append(MessageFormat.format(message, line));
			}
		}

		if (stringBuilder.length() > 0) {
			return stringBuilder.toString();
		}

		return null;
	}

}
