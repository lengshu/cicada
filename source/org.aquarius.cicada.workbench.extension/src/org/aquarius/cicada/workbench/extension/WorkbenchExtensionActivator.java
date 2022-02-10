/**
 *
 */
package org.aquarius.cicada.workbench.extension;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class WorkbenchExtensionActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.aquarius.cicada.workbench.extension"; //$NON-NLS-1$

	private static WorkbenchExtensionActivator instance;

	/**
	 *
	 */
	public WorkbenchExtensionActivator() {
		instance = this;
	}

	/**
	 * @return the instance
	 */
	public static WorkbenchExtensionActivator getInstance() {
		return instance;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);

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

}
