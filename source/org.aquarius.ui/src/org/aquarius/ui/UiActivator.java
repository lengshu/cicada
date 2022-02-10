/**
 *
 */
package org.aquarius.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UiActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.aquarius.ui"; //$NON-NLS-1$

	private static UiActivator plugin;

	/**
	 *
	 */
	public UiActivator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UiActivator getInstance() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Logs an Error message with an exception. Note that the message should already
	 * be localized to proper locale. ie: Resources.getString() should already have
	 * been called
	 */
	public static synchronized void logError(String message, Throwable ex) {
		if (message == null)
			message = ""; //$NON-NLS-1$
		UiActivator.getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
	}
}
