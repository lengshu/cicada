/**
 * 
 */
package org.aquarius.cicada.workbench.extension;

import org.eclipse.osgi.util.NLS;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.aquarius.cicada.workbench.extension.messages"; //$NON-NLS-1$
	public static String SiteNatTableEditor_FilterBySelectedActor;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
