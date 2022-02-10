/**
 *
 */
package org.aquarius.downloader.ui.page.internal;

import java.util.Collection;
import java.util.List;

import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.action.ReflectionAction;
import org.aquarius.ui.table.SpinnerCellEditor;
import org.aquarius.ui.table.impl.IntegerEntryTableViewerFactory;
import org.aquarius.ui.table.support.IntegerEditingSupport;
import org.aquarius.ui.validator.CompositeValidator;
import org.aquarius.ui.validator.DuplicatedValidator;
import org.aquarius.ui.validator.LengthValidator;
import org.aquarius.util.collection.Entry;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class SiteConcurrentFieldEditor extends FieldEditor {

	private Logger logger = LogUtil.getLogger(getClass());

	private TableViewer tableViewer;

	private IntegerEntryTableViewerFactory<String> entryTableViewerFactory;

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public SiteConcurrentFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		this.createTableEditors();

		Group group = new Group(parent, SWT.NONE);
		group.setText(this.getLabelText());

		group.setLayout(new GridLayout());

		ToolBarManager toolbarManager = new ToolBarManager();
		ReflectionAction addAction = new ReflectionAction(this, "add"); //$NON-NLS-1$
		addAction.setText(Messages.SiteConcurrentFieldEditor_AddLabel);
		addAction.setImageDescriptor(DownloadActivator.getImageDescriptor("icons/add.png")); //$NON-NLS-1$

		ReflectionAction removeAction = new ReflectionAction(this, "remove"); //$NON-NLS-1$
		removeAction.setText(Messages.SiteConcurrentFieldEditor_RemoveLabel);
		removeAction.setImageDescriptor(DownloadActivator.getImageDescriptor("icons/delete.png")); //$NON-NLS-1$

		toolbarManager.add(addAction);
		toolbarManager.add(removeAction);

		ToolBar toolbar = toolbarManager.createControl(group);
		toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.entryTableViewerFactory.createComposite(group);
		this.tableViewer = this.entryTableViewerFactory.getTableViewer();
		this.tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.tableViewer.getTable().setHeaderVisible(true);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = numColumns + 2;

		group.setLayoutData(gd);

	}

	/**
	 *
	 */
	private void createTableEditors() {
		this.entryTableViewerFactory = new IntegerEntryTableViewerFactory<String>() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			protected void updateEditingSupport(EditingSupport editingSupport) {
				IntegerEditingSupport<String> integerEditingSupport = (IntegerEditingSupport<String>) editingSupport;
				SpinnerCellEditor cellEditor = integerEditingSupport.getEditor();
				cellEditor.setRange(1, 20);
				super.updateEditingSupport(editingSupport);
			}

		};
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad() {
		String value = this.getPreferenceStore().getString(this.getPreferenceName());
		this.doLoad(value);
	}

	/**
	 * Load property from specified value.<BR>
	 *
	 * @param value
	 */
	private void doLoad(String value) {
		try {

			List<Entry> list = JSON.parseArray(value, Entry.class);

			if (null != list) {
				for (Entry<String, Integer> entry : list) {
					this.entryTableViewerFactory.addEntry(entry.getKey(), entry.getValue());
				}
			}

		} catch (Exception e) {
			this.logger.error("parse map", e); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoadDefault() {
		String value = this.getPreferenceStore().getDefaultString(this.getPreferenceName());
		this.doLoad(value);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doStore() {
		String value = JSON.toJSONString(this.entryTableViewerFactory.getEntryList());
		this.getPreferenceStore().setValue(this.getPreferenceName(), value);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * Add a new site concurrent config.<BR>
	 */
	public void add() {

		Collection<String> names = this.entryTableViewerFactory.getKeySet();
		CompositeValidator validator = new CompositeValidator(new DuplicatedValidator(names), new LengthValidator(64));

		InputDialog dialog = new InputDialog(this.tableViewer.getTable().getShell(), Messages.SiteConcurrentFieldEditor_InputDialogTitle,
				Messages.SiteConcurrentFieldEditor_InputDialogMessage, "", // $NON-NLS-3$
				validator);

		if (Dialog.OK == dialog.open()) {
			String propertyName = dialog.getValue();
			this.entryTableViewerFactory.addEntry(propertyName, 2);

		}
	}

	/***
	 * Remove the selection element.<BR>
	 */
	public void remove() {

		IStructuredSelection selection = this.tableViewer.getStructuredSelection();
		if (selection.isEmpty()) {
			return;
		}

		Object object = selection.getFirstElement();
		Entry<String, Integer> entry = (Entry<String, Integer>) object;

		this.entryTableViewerFactory.removeEntry(entry.getKey());

	}
}
