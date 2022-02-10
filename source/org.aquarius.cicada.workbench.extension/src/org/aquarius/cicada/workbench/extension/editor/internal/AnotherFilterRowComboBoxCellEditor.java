/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.StringUtil;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.FilterRowComboBoxCellEditor;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

/**
 * In fact ,this class is used just because FilterRowComboBoxCellEditor has
 * resource leak.<BR>
 * It doesn't release cursor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AnotherFilterRowComboBoxCellEditor extends FilterRowComboBoxCellEditor {

	/**
	 * @param dataProvider
	 */
	public AnotherFilterRowComboBoxCellEditor(IComboBoxDataProvider dataProvider) {
		super(dataProvider);
	}

	/**
	 * @param dataProvider
	 * @param maxVisibleItems
	 */
	public AnotherFilterRowComboBoxCellEditor(IComboBoxDataProvider dataProvider, int maxVisibleItems) {
		super(dataProvider, maxVisibleItems);

		this.setShowDropdownFilter(true);
		this.setMultiselectValueSeparator("|");
		this.setMultiselectTextBracket("", "");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object getCanonicalValue() {
		List<Object> list = (List<Object>) super.getCanonicalValue();
		return StringUtils.join(list, NatTableConstant.TextDelimiter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCanonicalValue(Object canonicalValue) {

		if (canonicalValue instanceof List) {
			super.setCanonicalValue(canonicalValue);
		} else {
			if (canonicalValue instanceof String) {
				String[] arrayValues = StringUtils.split((String) canonicalValue, NatTableConstant.TextDelimiter);

				if (arrayValues.length <= 1) {
					arrayValues = StringUtils.split((String) canonicalValue, StringUtil.ContentSeparator);
				}

				super.setCanonicalValue(Arrays.asList(arrayValues));
			} else {
				super.setCanonicalValue(canonicalValue);
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void close() {

		Control editorControl = getEditorControl();
		disposeCursor(editorControl);
		super.close();
	}

	/**
	 * @param editorControl
	 */
	static void disposeCursor(Control editorControl) {
		if (editorControl != null && !editorControl.isDisposed()) {
			Cursor cursor = editorControl.getCursor();

			editorControl.setCursor(null);
			SwtUtil.disposeResourceQuietly(cursor);
		}
	}

}
