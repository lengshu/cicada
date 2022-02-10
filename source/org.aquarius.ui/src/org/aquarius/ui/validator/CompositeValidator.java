/**
 *
 */
package org.aquarius.ui.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class CompositeValidator implements IInputValidator {

	private List<IInputValidator> validators = new ArrayList<>();

	/**
	 *
	 */
	public CompositeValidator(IInputValidator... validators) {
		super();

		CollectionUtils.addAll(this.validators, validators);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String isValid(String newText) {

		for (IInputValidator validator : this.validators) {
			String message = validator.isValid(newText);
			if (StringUtils.isNotBlank(message)) {
				return message;
			}
		}

		return null;
	}

}
