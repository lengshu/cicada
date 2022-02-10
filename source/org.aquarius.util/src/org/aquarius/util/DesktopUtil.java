/**
 * 
 */
package org.aquarius.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.aquarius.log.LogUtil;
import org.slf4j.Logger;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public final class DesktopUtil {

	public static final Logger logger = LogUtil.getLogger(DesktopUtil.class);

	/**
	 * 
	 */
	private DesktopUtil() {
		super();
	}

	/**
	 * 
	 * @param file
	 */
	public static void openFile(String file) {
		openFile(new File(file));
	}

	/**
	 * Open system explorer to open multi urls.<BR>
	 *
	 * @param urlStrings
	 */
	public static void openWebpages(String... urlStrings) {

		for (String urlString : urlStrings) {
			openWebpage(urlString);
		}
	}

	/**
	 * Open system explorer to open multi urls.<BR>
	 *
	 * @param urlStrings
	 */
	public static void openWebpages(List<String> urlStrings) {

		for (String urlString : urlStrings) {
			openWebpage(urlString);
		}
	}

	/**
	 * Use system browser to open a web page.<BR>
	 *
	 * @param uriString
	 * @return
	 */
	public static boolean openWebpage(String uriString) {

		try {
			return openWebpage(new URI(uriString));
		} catch (URISyntaxException e) {
			logger.error("openWebpage", e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Use system browser to open a web page.<BR>
	 *
	 * @param uri
	 * @return
	 */
	public static boolean openWebpage(URI uri) {

		if (null == uri) {
			return false;
		}

		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				logger.error("openWebpage", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * Use the system setting open a file or a folder.<BR>
	 *
	 * @param file
	 */
	public static void openFile(File file) {
		try {
			if (file.exists()) {

				java.awt.Desktop.getDesktop().open(file);
			}

		} catch (IOException e) {
			logger.error("open file ", e); //$NON-NLS-1$
		}
	}

	/**
	 * Use the system explorer open a folder.<BR>
	 *
	 * @param folder
	 */
	public static void openFolder(File folder) {
		try {
			if (folder.exists()) {

				if (folder.isFile()) {
					folder = folder.getParentFile();
				}

				java.awt.Desktop.getDesktop().open(folder);
			}
		} catch (IOException e) {
			logger.error("open folder ", e); //$NON-NLS-1$
		}
	}

}
