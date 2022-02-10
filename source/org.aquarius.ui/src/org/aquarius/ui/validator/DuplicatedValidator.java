/**
 *
 */
package org.aquarius.ui.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.ui.Messages;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class DuplicatedValidator implements IInputValidator {

	private List<String> sourceList = new ArrayList<String>();

	private boolean ignoreCase = true;

	/**
	 *
	 * @param sources
	 * @param ignoreCase
	 */
	public DuplicatedValidator(Collection<String> sources, boolean ignoreCase) {
		super();

		this.sourceList.addAll(sources);
		this.ignoreCase = ignoreCase;

	}

	/**
	 *
	 * @param sources
	 */
	public DuplicatedValidator(Collection<String> sources) {
		this(sources, true);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String isValid(String newText) {

		for (String value : this.sourceList) {

			if (this.ignoreCase) {
				if (StringUtils.equalsIgnoreCase(value, newText)) {
					return Messages.DuplicatedValidator_DuplicatedErrorMessage;
				}
			} else {
				if (StringUtils.equals(value, newText)) {
					return Messages.DuplicatedValidator_DuplicatedErrorMessage;
				}
			}
		}

		return null;

	}

}
