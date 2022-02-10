/**
 *
 */
package org.aquarius.downloader.ui.view.table;

import org.aquarius.downloader.core.DownloadManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * Task table element provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class TaskContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof DownloadManager) {
			DownloadManager downloadManager = (DownloadManager) inputElement;

			return downloadManager.getAllTasks().toArray();
		}

		return null;
	}

}
