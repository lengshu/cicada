/**
 *
 */
package org.aquarius.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.aquarius.ui.Messages;
import org.aquarius.ui.UiActivator;
import org.aquarius.ui.action.ReflectionAction;
import org.aquarius.ui.table.label.BeanPropertyColumnLableProvider;
import org.aquarius.util.AssertUtil;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

/**
 *
 * use checked table to filter or sort elements.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CheckedTableControlFactory<T> implements ITableService {

	private List<T> elementList = new ArrayList<T>();

	private Map<String, String> propertyNameMapping = new HashMap<>();

	private TableViewer tableViewer;

	private ToolBar toolbar;

	/**
	 *
	 */
	public CheckedTableControlFactory(Collection<T> elementList, Map<String, String> propertyNameMapping) {
		super();

		if (null != propertyNameMapping) {
			this.propertyNameMapping.putAll(propertyNameMapping);
		}

		if (null != elementList) {
			this.elementList.addAll(elementList);
		}
	}

	/**
	 *
	 * @param parent
	 * @param tableStyle
	 * @param allowAdjustPosition
	 * @return
	 */
	public Composite createControl(Composite parent, int tableStyle, boolean allowAdjustPosition, boolean allowRemove) {

		AssertUtil.assertNull(this.tableViewer);

		Composite root = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout(1, true);
		root.setLayout(gridLayout);

		ToolBarManager toolBarManager = new ToolBarManager();

		if (allowAdjustPosition) {
			ImageDescriptor moveUpImageDescriptor = UiActivator.getImageDescriptor("icons/moveUp.png");
			ImageDescriptor moveDownImageDescriptor = UiActivator.getImageDescriptor("icons/moveDown.png");

			toolBarManager.add(new ReflectionAction(this, "moveUp", Messages.CheckedTableControlFactory_MoveUp, moveUpImageDescriptor));
			toolBarManager.add(new ReflectionAction(this, "moveDown", Messages.CheckedTableControlFactory_MoveDown, moveDownImageDescriptor));
		}

		if (allowRemove) {
			ImageDescriptor removeImageDescriptor = UiActivator.getImageDescriptor("icons/delete.png");
			toolBarManager.add(new ReflectionAction(this, "remove", Messages.CheckedTableControlFactory_Remove, removeImageDescriptor));
		}

		if (toolBarManager.getSize() > 0) {
			this.toolbar = toolBarManager.createControl(root);
			this.toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		this.tableViewer = new TableViewer(root, tableStyle | SWT.SINGLE);
		this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		// this.tableViewer.getTable().setLinesVisible(true);

		if (MapUtils.isEmpty(this.propertyNameMapping)) {
			final TableViewerColumn tableColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			tableColumn.setLabelProvider(new ColumnLabelProvider());

			tableColumn.getColumn().setWidth(160);
		} else {

			for (Entry<String, String> entry : this.propertyNameMapping.entrySet()) {
				final TableViewerColumn tableColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
				tableColumn.setLabelProvider(new BeanPropertyColumnLableProvider(entry.getKey()));
				tableColumn.getColumn().setText(entry.getValue());
				tableColumn.getColumn().setWidth(160);
			}
		}

		this.tableViewer.setInput(this.elementList);

		Table table = this.tableViewer.getTable();

		for (TableColumn tableColumn : table.getColumns()) {
			tableColumn.pack();
		}

		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		return root;
	}

	/**
	 * @return the tableViewer
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}

	/**
	 * @return the toolbar
	 */
	public ToolBar getToolbar() {
		return this.toolbar;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void moveUp() {
		IStructuredSelection selection = this.tableViewer.getStructuredSelection();
		Object selectedObject = selection.getFirstElement();
		if (null != selectedObject) {
			int index = this.elementList.indexOf(selectedObject);

			if (index != 0) {
				doSwap(index, index - 1);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveTop() {
		IStructuredSelection selection = this.tableViewer.getStructuredSelection();
		Object selectedObject = selection.getFirstElement();
		if (null != selectedObject) {
			int index = this.elementList.indexOf(selectedObject);
			doSwap(index, 0);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveBottom() {
		IStructuredSelection selection = this.tableViewer.getStructuredSelection();
		Object selectedObject = selection.getFirstElement();
		if (null != selectedObject) {
			int index = this.elementList.indexOf(selectedObject);
			doSwap(index, (this.elementList.size() - 1));
		}
	}

	/**
	 * Swap items.<BR>
	 *
	 * @param sourceIndex
	 * @param targetIndex
	 */
	private void doSwap(int sourceIndex, int targetIndex) {

		TableItem sourceTableItem = this.tableViewer.getTable().getItem(sourceIndex);
		TableItem targetTableItem = this.tableViewer.getTable().getItem(targetIndex);

		boolean sourceChecked = sourceTableItem.getChecked();
		boolean targetChecked = targetTableItem.getChecked();

		Collections.swap(this.elementList, sourceIndex, targetIndex);
		this.tableViewer.refresh();

		sourceTableItem = this.tableViewer.getTable().getItem(sourceIndex);
		targetTableItem = this.tableViewer.getTable().getItem(targetIndex);

		targetTableItem.setChecked(sourceChecked);
		sourceTableItem.setChecked(targetChecked);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void moveDown() {
		IStructuredSelection selection = this.tableViewer.getStructuredSelection();
		Object selectedObject = selection.getFirstElement();
		if (null != selectedObject) {
			int index = this.elementList.indexOf(selectedObject);

			if (index != (this.elementList.size() - 1)) {
				doSwap(index, index + 1);
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void remove() {
		IStructuredSelection selection = this.tableViewer.getStructuredSelection();
		Object[] selectedObjects = selection.toArray();
		if (ArrayUtils.isNotEmpty(selectedObjects)) {
			this.tableViewer.remove(selectedObjects);
			this.elementList.removeAll(selection.toList());
		}
	}

	/**
	 * Return the checked elements.<BR>
	 *
	 * @return
	 */
	public List<T> getCheckedElements() {
		List<T> result = new ArrayList<>();

		Table table = this.tableViewer.getTable();

		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem tableItem = table.getItem(i);

			if (tableItem.getChecked()) {
				result.add(this.elementList.get(i));
			}
		}

		return result;
	}

	/**
	 * 
	 */
	public void checkAll() {
		Table table = this.tableViewer.getTable();

		for (TableItem tableItem : table.getItems()) {
			tableItem.setChecked(true);
		}
	}

	/**
	 * Set the checked elements.<BR>
	 *
	 * @param checkedElementList
	 */
	public void setCheckedElements(List<T> checkedElementList) {
		Table table = this.tableViewer.getTable();

		for (T element : checkedElementList) {
			int index = this.elementList.indexOf(element);
			if (index >= 0) {
				TableItem tableItem = table.getItem(index);
				tableItem.setChecked(true);
			}
		}
	}

}
