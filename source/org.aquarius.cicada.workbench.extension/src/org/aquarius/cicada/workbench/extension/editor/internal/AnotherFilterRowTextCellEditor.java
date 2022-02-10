/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowTextCellEditor;
import org.eclipse.swt.widgets.Control;

/**
 * In fact ,this class is used just because FilterRowTextCellEditor has resource
 * leak.<BR>
 * It doesn't release cursor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AnotherFilterRowTextCellEditor extends FilterRowTextCellEditor {

	/**
	 *
	 */
	public AnotherFilterRowTextCellEditor() {
		// default
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
