/**
 *
 */
package org.aquarius.ui.table.support;

import org.aquarius.ui.table.SpinnerCellEditor;
import org.aquarius.util.collection.Entry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

/**
 * @author aquarius.github@gmail.com
 * @param <K>
 *
 */
public class IntegerEditingSupport<K> extends EditingSupport {

	private SpinnerCellEditor editor;

	/**
	 * @param viewer
	 */
	public IntegerEditingSupport(ColumnViewer viewer) {
		super(viewer);

		this.editor = new SpinnerCellEditor((Composite) viewer.getControl());
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public CellEditor getCellEditor(Object element) {
		return this.editor;
	}

	/**
	 * @return the editor
	 */
	public SpinnerCellEditor getEditor() {
		return this.editor;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object getValue(Object element) {
		Entry<K, Integer> entry = (Entry<K, Integer>) element;
		Integer value = entry.getValue();

		if (null == value) {
			return 0;
		} else {
			return value;
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		Entry<K, Integer> entry = (Entry<K, Integer>) element;

		if (!(value instanceof Integer)) {
			value = 0;
		}

		entry.setValue((Integer) value);

		this.getViewer().update(element, null);

	}

}
