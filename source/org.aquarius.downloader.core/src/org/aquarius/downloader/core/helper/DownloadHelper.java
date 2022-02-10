/**
 *
 */
package org.aquarius.downloader.core.helper;

import java.io.File;

import org.aquarius.downloader.core.DownloadManager;

/**
 * Helper function.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadHelper {

	/**
	 *
	 */
	private DownloadHelper() {
		// No instance needed.
	}

	/**
	 * Check whether the partition has enough space.<BR>
	 *
	 * @return
	 */
	public static boolean checkFreeSpace(File targetFile) {

		if (null == targetFile) {
			return false;
		}

		if (!DownloadManager.getInstance().getConfiguration().isCheckDiskLeftSpace()) {
			return true;
		}

		long freeSpace = targetFile.getFreeSpace();

		return freeSpace > DownloadManager.getInstance().getConfiguration().getDiskLeftSpace();
	}

}
