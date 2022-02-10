/**
 *
 */
package org.aquarius.cicada.workbench.intro;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.dialog.InitDialog;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.browser.BrowserViewer;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.DesktopUtil;
import org.aquarius.util.LocaleUtil;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.part.IntroPart;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

/**
 * User intro to show the rcp functionb.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UserIntroPart extends IntroPart implements IIntroPart {

	private static Logger logger = LogUtil.getLogger(UserIntroPart.class);

	private Browser browser;

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void standbyStateChanged(boolean standby) {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void createPartControl(Composite parent) {
		BrowserViewer browserViewer = new BrowserViewer(parent, BrowserViewer.BUTTON_BAR, SWT.None);
		this.browser = browserViewer.getBrowser();

		openHelp(this.browser);

		init();

	}

	/**
	 * Open the help in the specified browser
	 * 
	 * @param browser
	 * @return
	 */
	public static boolean openHelp(Browser browser) {
		File file = getHelpFile();

		if (null == file) {
			browser.setUrl("https://github.com/aquariusStudio/cicada");
		} else {
			browser.setUrl(file.toURI().toString());
		}

		return true;
	}

	/**
	 * Open the help in the specified browser
	 * 
	 * @param browser
	 * @return
	 */
	public static boolean openHelp() {
		File file = getHelpFile();

		DesktopUtil.openWebpage(file.toURI());

		return true;
	}

	/**
	 * 
	 */
	private void init() {
		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		boolean inited = store.getBoolean(InitDialog.class.getName());

		if (!inited) {
			Display display = SwtUtil.findDisplay();

			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					Shell dialogShell = new Shell(getIntroSite().getWorkbenchWindow().getShell(), SWT.PRIMARY_MODAL);

					// Shell dialogShell = getIntroSite().getWorkbenchWindow().getShell();
					InitDialog dialog = new InitDialog(dialogShell);
					dialog.open();

					SwtUtil.disposeWidgetQuietly(dialogShell);

					store.setValue(InitDialog.class.getName(), true);
				}
			});

		}

	}

	/**
	 * Compute the file.<BR>
	 * 
	 * @return
	 */
	public static File getHelpFile() {

		Bundle bundle = Platform.getBundle(WorkbenchActivator.HELP_PLUGIN_ID);

		Collection<String> candidateNames = LocaleUtil.findDefaultCandaidateLocaleNames();

		for (String lang : candidateNames) {
			URL resourceUrl = bundle.getEntry("/html/" + lang + "/intro.html");

			try {
				URL fileUrl = FileLocator.toFileURL(resourceUrl);

				if (null != fileUrl) {
					File file = new File(fileUrl.getFile());
					if (file.exists()) {
						return file;
					}
				}

			} catch (IOException e) {
				logger.error("Open Help ", e);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		this.browser.setFocus();
	}

}
