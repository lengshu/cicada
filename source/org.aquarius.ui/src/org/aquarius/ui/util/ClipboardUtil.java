/**
 *
 */
package org.aquarius.ui.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.apache.commons.lang.StringUtils;
import org.aquarius.log.LogUtil;
import org.slf4j.Logger;

/**
 *
 * Copy contents to clipboard.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ClipboardUtil {

	private static Logger logger = LogUtil.getLogger(ClipboardUtil.class);

	public static String getStringFromClipboard() {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipData = clipboard.getContents(clipboard);

		try {
			return (String) (clipData.getTransferData(DataFlavor.stringFlavor));
		} catch (Exception e) {
			logger.error("getStringFromClipboard", e);
			return e.getLocalizedMessage();
		}

	}

	public static void setClipboardString(String text) {

		if (StringUtils.isEmpty(text)) {
			return;
		}

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection data = new StringSelection(text);
		clipboard.setContents(data, data); // setContent

	}

}
