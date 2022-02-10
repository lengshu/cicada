/**
 *
 */
package org.aquarius.ui.table.impl;

import org.aquarius.ui.table.base.AbstractEntryTableViewerFactory;
import org.aquarius.ui.table.support.IntegerEditingSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

/**
 * Use to edit integer.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class IntegerEntryTableViewerFactory<K> extends AbstractEntryTableViewerFactory<K, Integer> {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public EditingSupport createEditingSupport(TableViewer tableViewer) {
		IntegerEditingSupport<K> editingSupport = new IntegerEditingSupport<K>(tableViewer);

		return editingSupport;
	}

}
