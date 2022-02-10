package org.aquarius.cicada.workbench;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Just keep it.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class BrowserPerspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "org.aquarius.cicada.workbench.browserPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);

	}
}
