/**
 *
 */
package org.aquarius.util.validator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.validator.IValueValidator;

/**
 * Desinged by composite pattern.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CompositeValueValidator<T> implements IValueValidator<T> {

	private boolean traverseAll = false;

	private List<IValueValidator<T>> validators = new ArrayList<>();

	/**
	 * @param traverseAll if the value is set true.<BR>
	 *                    All validators will be used.<BR>
	 */
	public CompositeValueValidator(boolean traverseAll) {
		super();
		this.traverseAll = traverseAll;
	}

	/**
	 *
	 * @param traverseAll     if the value is set true.<BR>
	 *                        All validators will be used.<BR>
	 * @param valueValidators
	 */
	public CompositeValueValidator(boolean traverseAll, IValueValidator<T>... valueValidators) {
		super();
		this.traverseAll = traverseAll;

		CollectionUtils.addAll(this.validators, valueValidators);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String validate(T value) {
		if (this.traverseAll) {
			StringJoiner stringJoiner = new StringJoiner("\r\n");

			for (IValueValidator<T> validator : this.validators) {
				String message = validator.validate(value);

				if (StringUtils.isNotBlank(message)) {
					stringJoiner.add(message);
				}
			}

			return stringJoiner.toString();
		} else {
			for (IValueValidator<T> validator : this.validators) {
				String message = validator.validate(value);

				if (StringUtils.isNotBlank(message)) {
					return message;
				}
			}

			return null;
		}
	}

	/**
	 * Add a validator
	 *
	 * @param validator
	 */
	public void addValidator(IValueValidator<T> validator) {
		AssertUtil.assertNotNull(validator);
		this.validators.add(validator);
	}

	/**
	 * Remove a validator.
	 *
	 * @param validator
	 */
	public void removeValidator(IValueValidator<T> validator) {
		this.validators.remove(validator);
	}

}
