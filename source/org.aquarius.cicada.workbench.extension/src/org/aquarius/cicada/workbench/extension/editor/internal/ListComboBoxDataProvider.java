/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;

/**
 * A simple implementation for IComboBoxDataProvider using list.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ListComboBoxDataProvider implements IComboBoxDataProvider {

	private List<?> values;

	/**
	 * @param values
	 */
	public ListComboBoxDataProvider(List<?> values) {
		super();
		this.values = values;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<?> getValues(int columnIndex, int rowIndex) {
		return this.values;
	}

}
