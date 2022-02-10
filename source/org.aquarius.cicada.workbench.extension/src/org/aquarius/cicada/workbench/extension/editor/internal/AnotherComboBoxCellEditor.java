/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.swt.widgets.Control;

/**
 * n fact ,this class is used just because ComboBoxCellEditor has resource
 * leak.<BR>
 * It doesn't release cursor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AnotherComboBoxCellEditor extends ComboBoxCellEditor {

	/**
	 * @param canonicalValues
	 */
	public AnotherComboBoxCellEditor(List<?> canonicalValues) {
		super(canonicalValues);
	}

	/**
	 * @param canonicalValues
	 */
	public AnotherComboBoxCellEditor(Object... canonicalValues) {
		super(canonicalValues);
	}

	/**
	 * @param dataProvider
	 */
	public AnotherComboBoxCellEditor(IComboBoxDataProvider dataProvider) {
		super(dataProvider);
	}

	/**
	 * @param canonicalValues
	 * @param maxVisibleItems
	 */
	public AnotherComboBoxCellEditor(List<?> canonicalValues, int maxVisibleItems) {
		super(canonicalValues, maxVisibleItems);
	}

	/**
	 * @param dataProvider
	 * @param maxVisibleItems
	 */
	public AnotherComboBoxCellEditor(IComboBoxDataProvider dataProvider, int maxVisibleItems) {
		super(dataProvider, maxVisibleItems);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void close() {

		Control editorControl = getEditorControl();
		AnotherFilterRowComboBoxCellEditor.disposeCursor(editorControl);
		super.close();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object getCanonicalValue() {
		return super.getCanonicalValue();
	}
}
