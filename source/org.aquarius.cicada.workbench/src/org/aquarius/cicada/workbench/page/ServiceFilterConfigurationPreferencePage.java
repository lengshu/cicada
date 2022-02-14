/**
 * 
 */
package org.aquarius.cicada.workbench.page;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.service.manager.impl.ConfigurationServiceFilter;
import org.aquarius.util.nls.NlsResource;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class ServiceFilterConfigurationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Tree tree;

	private List<ConfigurationServiceFilter> serviceFilters;

	/**
	 * 
	 */
	public ServiceFilterConfigurationPreferencePage() {
		super();
	}

	/**
	 * @param title
	 */
	public ServiceFilterConfigurationPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public ServiceFilterConfigurationPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite parent) {

		this.load();

		this.tree = new Tree(parent, SWT.BORDER | SWT.CHECK);

		this.tree.addListener(SWT.Selection, event -> {
			if (event.detail == SWT.CHECK) {
				TreeItem item = (TreeItem) event.item;
				updateChecked(item);
			}
		});

		NlsResource nlsResource = RuntimeManager.getInstance().getNlsResource();

		for (ConfigurationServiceFilter filter : this.serviceFilters) {

			TreeItem parentItem = new TreeItem(this.tree, SWT.NONE);
			parentItem.setData(filter);

			String nlsText = nlsResource.getValue(filter.getDomain());
			parentItem.setText(nlsText);

			Map<String, Boolean> map = filter.getMap();

			TreeItem lastTreeItem = null;

			for (Entry<String, Boolean> entry : map.entrySet()) {
				TreeItem childItem = new TreeItem(parentItem, SWT.NONE);
				childItem.setText(entry.getKey());
				childItem.setChecked(!entry.getValue());

				lastTreeItem = childItem;
			}

			updateChecked(lastTreeItem);
		}

		this.tree.setLayoutData(new GridData(GridData.FILL_BOTH));

		return this.tree;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {

		TreeItem[] parentItems = this.tree.getItems();
		for (TreeItem parentItem : parentItems) {
			TreeItem[] childrenItems = parentItem.getItems();
			parentItem.setChecked(true);

			for (TreeItem childItem : childrenItems) {
				childItem.setChecked(true);
			}
		}

		super.performDefaults();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {

		TreeItem[] parentItems = this.tree.getItems();
		for (TreeItem parentItem : parentItems) {

			ConfigurationServiceFilter filter = (ConfigurationServiceFilter) parentItem.getData();
			Map<String, Boolean> map = filter.getMap();

			TreeItem[] childrenItems = parentItem.getItems();

			for (TreeItem childItem : childrenItems) {
				map.put(childItem.getText(), !childItem.getChecked());
			}
		}

		RuntimeManager.getInstance().getServiceFilterConfiguration().save();

		return super.performOk();
	}

	/**
	 * @param item
	 */
	private void updateChecked(TreeItem item) {

		if (null != item) {
			boolean checked = item.getChecked();
			checkItems(item, checked);
			checkPath(item.getParentItem(), checked, false);
		}

	}

	/**
	 * 
	 */
	private void load() {
		this.serviceFilters = RuntimeManager.getInstance().getServiceFilterConfiguration().getFilters();
	}

	static void checkPath(TreeItem item, boolean checked, boolean grayed) {
		if (item == null)
			return;
		if (grayed) {
			checked = true;
		} else {
			int index = 0;
			TreeItem[] items = item.getItems();
			while (index < items.length) {
				TreeItem child = items[index];
				if (child.getGrayed() || checked != child.getChecked()) {
					checked = grayed = true;
					break;
				}
				index++;
			}
		}
		item.setChecked(checked);
		item.setGrayed(grayed);
		checkPath(item.getParentItem(), checked, grayed);
	}

	static void checkItems(TreeItem item, boolean checked) {
		item.setGrayed(false);
		item.setChecked(checked);
		for (TreeItem child : item.getItems()) {
			checkItems(child, checked);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do
	}

}
