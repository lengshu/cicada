/**
 *
 */
package org.aquarius.ui.table.label;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Show table index.<BR>
 * It should be used as a index column<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class IndexLabelProvider extends ColumnLabelProvider {

	/**
	 * @param viewer
	 */
	public IndexLabelProvider(Viewer viewer) {
		super();
	}

	@Override
	public String getText(Object element) {
		return "";
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void update(ViewerCell cell) {
		super.update(cell);

		TableItem tableItem = (TableItem) cell.getItem();
		Table table = tableItem.getParent();
		int index = table.indexOf(tableItem) + 1;

		cell.setText(index + "");
	}
}
