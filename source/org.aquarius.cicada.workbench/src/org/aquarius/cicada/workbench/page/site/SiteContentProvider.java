/**
 * 
 */
package org.aquarius.cicada.workbench.page.site;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.SiteConfig;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class SiteContentProvider implements ITreeContentProvider {

	/**
	 * 
	 */
	public SiteContentProvider() {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return this.getChildren(inputElement);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof Collection) {
			Collection<?> col = (Collection<?>) parentElement;
			return col.toArray();
		}

		if (parentElement instanceof SiteConfig) {
			SiteConfig siteConfig = (SiteConfig) parentElement;
			List<MovieChannel> movieChannelList = siteConfig.getMovieChannelList();

			return movieChannelList.toArray();
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean hasChildren(Object element) {
		Object[] children = this.getChildren(element);
		return ArrayUtils.isNotEmpty(children);
	}

}
