/**
 *
 */
package org.aquarius.ui.table.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ObjectUtils;
import org.aquarius.ui.Messages;
import org.aquarius.ui.table.label.BeanPropertyColumnLableProvider;
import org.aquarius.util.collection.Entry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 *
 * Like a property editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractEntryTableViewerFactory<K, V> {

	private List<Entry<K, V>> entryList = new ArrayList<>();

	private TableViewer tableViewer;

	/**
	 *
	 */
	public AbstractEntryTableViewerFactory() {

	}

	/**
	 * Create a table to edit data.<BR>
	 *
	 * @param parent
	 * @return
	 */
	public Table createComposite(Composite parent) {

		this.tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);

		TableViewerColumn keyColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		keyColumn.getColumn().setText(Messages.AbstractEntryTableViewerFactory_KeyColumnTitle);
		keyColumn.setLabelProvider(new BeanPropertyColumnLableProvider("key")); //$NON-NLS-1$

		TableViewerColumn valueColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		valueColumn.getColumn().setText(Messages.AbstractEntryTableViewerFactory_ValueColumnTitle);
		valueColumn.setLabelProvider(new BeanPropertyColumnLableProvider("value")); //$NON-NLS-1$

		EditingSupport editingSupport = this.createEditingSupport(this.tableViewer);
		this.updateEditingSupport(editingSupport);
		valueColumn.setEditingSupport(editingSupport);

		this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		this.tableViewer.setInput(this.entryList);

		TableLayout tableLayout = new TableLayout();
		this.tableViewer.getTable().setLayout(tableLayout);

		this.tableViewer.getTable().setLinesVisible(true);

		tableLayout.addColumnData(new ColumnWeightData(50));
		tableLayout.addColumnData(new ColumnWeightData(50));

		return this.tableViewer.getTable();

	}

	/**
	 * Update the editing support.<BR>
	 * For example ,the spinner need to be set the range.<BR>
	 *
	 * @param editingSupport
	 */
	protected void updateEditingSupport(EditingSupport editingSupport) {

	}

	/**
	 * Create a editing support.<BR>
	 *
	 * @param tableViewer
	 * @return
	 */
	public abstract EditingSupport createEditingSupport(TableViewer tableViewer);

	/**
	 * @return the tableViewer
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}

	/**
	 * Add a map.<BR>
	 *
	 * @param map
	 */
	public void addMap(Map<K, V> map) {

		if (null != map) {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				this.addEntry(entry.getKey(), entry.getValue());
			}
		}

	}

	/**
	 * Add a entry.<BR>
	 *
	 * @param key
	 * @param value
	 */
	public void addEntry(K key, V value) {

		for (Entry<K, V> entry : this.entryList) {

			if (ObjectUtils.equals(entry.getKey(), key)) {
				entry.setValue(value);

				if (null != this.tableViewer) {
					this.tableViewer.update(entry, null);
				}

				return;
			}
		}

		Entry<K, V> entry = new Entry<K, V>(key, value);
		this.entryList.add(entry);
		this.tableViewer.add(entry);

	}

	/**
	 * Remove a entry.<BR>
	 *
	 * @param key
	 */
	public void removeEntry(K key) {

		for (int i = 0; i < this.entryList.size(); i++) {
			Entry<K, V> entry = this.entryList.get(i);

			if (ObjectUtils.equals(entry.getKey(), key)) {
				this.entryList.remove(i);

				if (null != this.tableViewer) {
					this.tableViewer.remove(entry);
				}

				return;
			}
		}
	}

	/**
	 * Return the keys.<BR>
	 *
	 * @return
	 */
	public Set<K> getKeySet() {
		Set<K> keys = new TreeSet<>();

		for (Entry<K, V> entry : this.entryList) {
			keys.add(entry.getKey());
		}

		return keys;
	}

	/**
	 * @return the entryList
	 */
	public List<Entry<K, V>> getEntryList() {
		return new ArrayList<>(this.entryList);
	}

}
