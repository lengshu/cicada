/**
 *
 */
package org.aquarius.cicada.workbench.editor;

import java.util.Objects;

import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * The editor input for a site.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteEditorInput implements IEditorInput {

	private Site site;

	private String name;

	private boolean supportFilter = true;

	private boolean virtual = false;

	/**
	 * @param site
	 */
	public SiteEditorInput(Site site) {
		this(site, false, true);
	}

	/**
	 * @param site
	 */
	public SiteEditorInput(Site site, boolean virtual, boolean supportFilter) {
		super();

		this.site = site;
		this.supportFilter = supportFilter;
		this.virtual = virtual;

		this.name = site.getSiteName();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (Site.class == adapter) {
			return (T) this.site;
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean exists() {
		return false;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return WorkbenchActivator.getImageDescriptor("/icons/movie.png");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.name;
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
		return this.name;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.site);
	}

	/**
	 * @return the supportFilter
	 */
	public boolean isSupportFilter() {
		return this.supportFilter;
	}

	/**
	 * @return the virtual
	 */
	public boolean isVirtual() {
		return this.virtual;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		SiteEditorInput other = (SiteEditorInput) obj;
		return Objects.equals(this.site, other.site);
	}

}
