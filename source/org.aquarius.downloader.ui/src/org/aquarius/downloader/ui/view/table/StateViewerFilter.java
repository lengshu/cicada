/**
 * 
 */
package org.aquarius.downloader.ui.view.table;

import org.aquarius.downloader.core.DownloadTask;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class StateViewerFilter extends ViewerFilter {

	private int state;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (this.state < 0) {
			return true;
		}

		if (element instanceof DownloadTask) {
			DownloadTask downloadTask = (DownloadTask) element;
			return downloadTask.getState() == this.state;
		}

		return false;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

}
