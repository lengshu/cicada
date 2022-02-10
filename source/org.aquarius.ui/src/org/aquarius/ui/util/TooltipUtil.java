/**
 * 
 */
package org.aquarius.ui.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.UiActivator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.slf4j.Logger;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class TooltipUtil {

	private static final Logger logger = LogUtil.getLogger(TooltipUtil.class);

	public static final int LevelInfo = 0;

	// public static final int LevelWarn = 1;

	public static final int LevelError = 2;

	// public static final int LevelFatal = 4;

	public static final int LevelNo = 5;

	private static int level = LevelInfo;

	/**
	 * 
	 */
	private TooltipUtil() {
		super();
	}

	/**
	 * @return the level
	 */
	public static int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public static void setLevel(int level) {

		if (level >= 0) {
			TooltipUtil.level = level;
		}
	}

	/**
	 * Just show info tooltip.<BR>
	 * 
	 * @param title
	 * @param message
	 */
	public static void showInfoTip(String title, String message) {
		showTip(null, title, message, LevelInfo);
	}

	/**
	 * Just show error tooltip.<BR>
	 * 
	 * @param title
	 * @param message
	 */
	public static void showErrorTip(String title, String message) {
		showTip(null, title, message, LevelError);
	}

	/**
	 * Show a tip.<BR>
	 *
	 * @param shell
	 * @param title
	 * @param message
	 */
	private static void showTip(Shell shell, String title, String message, int infoLevel) {

		if (StringUtils.isEmpty(title)) {
			return;
		}

		if (StringUtils.isEmpty(message)) {
			return;
		}

		if (infoLevel < level) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {

				try {
					Shell activeShell = shell;
					if (activeShell == null) {
						activeShell = SwtUtil.findShell();
					}

					if (activeShell == null) {
						return;
					}

					final Tray tray = activeShell.getDisplay().getSystemTray();

					ToolTip tip = null;

					if (level == LevelError) {
						tip = new ToolTip(activeShell, SWT.BALLOON | SWT.ICON_ERROR);
					}

					if (null == tip) {
						tip = new ToolTip(activeShell, SWT.BALLOON | SWT.ICON_INFORMATION);
					}

					tip.setAutoHide(true);
					tip.setMessage(message);
					tip.setText(title);

					tip.addListener(SWT.Selection, event -> {
						MessageDialog.openInformation(SwtUtil.findShell(), title, message);
					});

					TrayItem item = findTrayItem(tray);
					item.setToolTip(tip);
					tip.setVisible(true);
				}

				catch (Exception e) {
					logger.error("show tip", e); //$NON-NLS-1$
				}
			}
		});
	}

	/**
	 * Find a tray item,if not found,a new tray item will be created.<BR>
	 * 
	 * @param tray
	 * @return
	 */
	private static TrayItem findTrayItem(Tray tray) {
		TrayItem[] items = tray.getItems();

		if (ArrayUtils.isEmpty(items)) {
			TrayItem item = new TrayItem(tray, SWT.None);

			ImageDescriptor imageDescriptor = UiActivator.getImageDescriptor("/icons/cicada.png");
			Image image = imageDescriptor.createImage();
			item.setImage(image);
			item.setHighlightImage(image);

			return item;
		} else {
			return items[0];
		}
	}
}
