/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * The editor input for a site parser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteConfigEditorInput implements IEditorInput {

	private SiteConfig siteConfig;

	private boolean isNew;

	/**
	 * @param name
	 */
	public SiteConfigEditorInput() {
		this("unnamed");

	}

	/**
	 * @param name
	 */
	public SiteConfigEditorInput(String name) {
		super();

		this.siteConfig = new SiteConfig();
		this.siteConfig.setSiteName(name);
		this.isNew = true;
	}

	/**
	 * @param siteConfig
	 */
	public SiteConfigEditorInput(SiteConfig siteConfig) {
		super();
		this.siteConfig = siteConfig;

		this.isNew = false;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if (SiteConfig.class == adapter) {
			return (T) this.siteConfig;
		}

		return null;
	}

	/**
	 * @return the siteConfig
	 */
	public SiteConfig getSiteConfig() {
		return this.siteConfig;
	}

	/**
	 * @return the isNew
	 */
	public boolean isNew() {
		return this.isNew;
	}

	/**
	 * @param isNew the isNew to set
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return WorkbenchActivator.getImageDescriptor("/icons/parser.png");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.siteConfig.getSiteName();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getToolTipText() {
		return this.siteConfig.getSiteName();
	}

}
