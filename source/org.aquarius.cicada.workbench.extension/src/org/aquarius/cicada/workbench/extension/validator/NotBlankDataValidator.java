/**
 *
 */
package org.aquarius.cicada.workbench.extension.validator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.DataValidator;
import org.eclipse.nebula.widgets.nattable.data.validate.ValidationFailedException;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * Validate the string should not be blank.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NotBlankDataValidator extends DataValidator {

	private String message;

	/**
	 *
	 */
	public NotBlankDataValidator() {
		this("The input value should not be blank or empty.");
	}

	/**
	 * @param message
	 */
	public NotBlankDataValidator(String message) {
		super();
		this.message = message;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean validate(int columnIndex, int rowIndex, Object newValue) {

		if (null == newValue) {
			throw new ValidationFailedException(this.message);
		}

		if (!(newValue instanceof String)) {
			return false;
		}
		String value = (String) newValue;

		if (StringUtils.isBlank(value)) {
			throw new ValidationFailedException(this.message);
		}

		return true;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean validate(ILayerCell cell, IConfigRegistry configRegistry, Object newValue) {

		if (cell.getRowPosition() < 2) {
			return true;
		} else {
			return super.validate(cell, configRegistry, newValue);

		}

	}

}
