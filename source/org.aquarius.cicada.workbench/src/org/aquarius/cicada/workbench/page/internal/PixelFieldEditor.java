/**
 *
 */
package org.aquarius.cicada.workbench.page.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.ui.table.CheckedTableControlFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class PixelFieldEditor extends FieldEditor {

	private List<String> predefinedPixels;

	private CheckedTableControlFactory<String> checkedTableControlFactory;

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public PixelFieldEditor(String name, String labelText, Composite parent) {
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

		this.predefinedPixels = new ArrayList<>();
		this.predefinedPixels.addAll(Arrays.asList(MovieUtil.split(MovieConfiguration.AllPixels)));
		this.checkedTableControlFactory = new CheckedTableControlFactory<String>(this.predefinedPixels, null);

		Group group = new Group(parent, SWT.BORDER);
		group.setLayout(new FillLayout());
		group.setText(this.getLabelText());

		this.checkedTableControlFactory.createControl(group, SWT.CHECK, true, false);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns + 1;

		group.setLayoutData(gd);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoad() {
		String value = getPreferenceStore().getString(getPreferenceName());
		doLoad(value);

	}

	/**
	 * @param value
	 */
	private void doLoad(String value) {
		String[] pixels = MovieUtil.split(value);
		this.checkedTableControlFactory.setCheckedElements(Arrays.asList(pixels));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoadDefault() {
		String defaultPixels = getPreferenceStore().getDefaultString(getPreferenceName());
		doLoad(defaultPixels);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doStore() {
		List<String> checkedList = this.checkedTableControlFactory.getCheckedElements();
		if (CollectionUtils.isNotEmpty(checkedList)) {
			getPreferenceStore().setValue(getPreferenceName(), StringUtils.join(checkedList, ","));
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getNumberOfControls() {
		return 1;
	}

}
