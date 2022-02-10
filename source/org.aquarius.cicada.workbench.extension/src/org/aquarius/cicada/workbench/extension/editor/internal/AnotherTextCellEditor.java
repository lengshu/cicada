/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.swt.widgets.Control;

/**
 * In fact ,this class is used just because TextCellEditor has resource
 * leak.<BR>
 * It doesn't release cursor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AnotherTextCellEditor extends TextCellEditor {

	/**
	 *
	 */
	public AnotherTextCellEditor() {
		// default
	}

	/**
	 * @param commitOnUpDown
	 */
	public AnotherTextCellEditor(boolean commitOnUpDown) {
		super(commitOnUpDown);
	}

	/**
	 * @param commitOnUpDown
	 * @param moveSelectionOnEnter
	 */
	public AnotherTextCellEditor(boolean commitOnUpDown, boolean moveSelectionOnEnter) {
		super(commitOnUpDown, moveSelectionOnEnter);
	}

	/**
	 * @param commitOnUpDown
	 * @param moveSelectionOnEnter
	 * @param commitOnLeftRight
	 */
	public AnotherTextCellEditor(boolean commitOnUpDown, boolean moveSelectionOnEnter, boolean commitOnLeftRight) {
		super(commitOnUpDown, moveSelectionOnEnter, commitOnLeftRight);
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

}
