package org.aquarius.cicada.workbench;

import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.util.PrefUtil;

/**
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return BrowserPerspective.ID;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(false);

		this.disableReopenEditors();
		this.showHeapStatus();
	}

	/**
	 * No editors will be opened and restored.<BR>
	 */
	private void disableReopenEditors() {
		String pref = org.eclipse.ui.IWorkbenchPreferenceConstants.CLOSE_EDITORS_ON_EXIT;
		PlatformUI.getPreferenceStore().setValue(pref, true);
	}

	/**
	 * 
	 */
	private void showHeapStatus() {
		PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
	}

}
