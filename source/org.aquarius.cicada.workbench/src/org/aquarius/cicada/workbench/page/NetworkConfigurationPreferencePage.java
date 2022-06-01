/**
 *
 */
package org.aquarius.cicada.workbench.page;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

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

	/**
	 * 
	 */
	private void doLoad() {

		Properties refererMapping = this.configuration.getRefererMapping();

		StringBuilder stringBuilder = new StringBuilder();

		for (Map.Entry<Object, Object> e : refererMapping.entrySet()) {
			String key = (String) e.getKey();
			String val = (String) e.getValue();

			stringBuilder.append(key + "=" + val);
			stringBuilder.append(System.lineSeparator());
		}

		this.styledText.setText(stringBuilder.toString());
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

		StringReader reader = new StringReader(referString);
		Properties refererMapping = new Properties();

		try {
			refererMapping.load(reader);
			WorkbenchActivator.getDefault().getNetworkConfiguration().setRefererMapping(refererMapping);
		} catch (IOException e) {
			// Nothing to do
		}

		return super.performOk();
	}

}
