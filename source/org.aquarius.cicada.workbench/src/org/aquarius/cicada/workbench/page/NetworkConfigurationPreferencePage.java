/**
 *
 */
package org.aquarius.cicada.workbench.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for network configuration.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NetworkConfigurationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private StyledText styledText;

	private NetworkConfiguration configuration;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
		this.configuration = WorkbenchActivator.getDefault().getNetworkConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite parent) {

		Composite rootPane = new Composite(parent, SWT.None);
		rootPane.setLayout(new FillLayout());

		Group group = new Group(rootPane, SWT.FLAT);
		group.setLayout(new FillLayout());

		this.styledText = new StyledText(group, SWT.None);

		this.styledText.setToolTipText(Messages.NetworkConfigurationPreferencePage_NetworkRefererToolTip);

		this.doLoad();

		return rootPane;
	}

	private void doLoad() {

		List<String> list = new ArrayList<>();

		Map<String, String> refererMapping = this.configuration.getRefererMapping();

		for (Entry<String, String> entry : refererMapping.entrySet()) {
			list.add(entry.getKey());
			list.add(entry.getValue());
		}

		String referString = StringUtils.join(list, "\r\n"); //$NON-NLS-1$

		this.styledText.setText(referString);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {

		this.configuration.resetDefaults();
		this.doLoad();

		super.performDefaults();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {

		String referString = this.styledText.getText();

		String[] results = StringUtils.split(referString);

		int lineNumber = 0;
		Map<String, String> refererMapping = new HashMap<>();

		while ((lineNumber + 1) < results.length) {
			String key = results[lineNumber];
			lineNumber++;
			String value = results[lineNumber];
			lineNumber++;

			refererMapping.put(key, value);
		}

		WorkbenchActivator.getDefault().getNetworkConfiguration().setRefererMapping(refererMapping);

		return super.performOk();
	}

}
