/**
 * 
 */
package org.aquarius.cicada.workbench.browser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.WordUtils;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.config.editor.DebugBrowserScriptControlFactory;
import org.aquarius.cicada.workbench.function.common.CloseSiteFunction;
import org.aquarius.cicada.workbench.function.common.CloseWindowFunction;
import org.aquarius.cicada.workbench.function.common.IsAcceptablePixelFunction;
import org.aquarius.cicada.workbench.function.common.JoinStringFunction;
import org.aquarius.cicada.workbench.function.common.QueryGeneratorNameListFunction;
import org.aquarius.cicada.workbench.function.common.QueryPixelsFunction;
import org.aquarius.cicada.workbench.function.movie.DownloadMovieListFunction;
import org.aquarius.cicada.workbench.function.movie.GenerateMovieDownloadUrlsFunction;
import org.aquarius.cicada.workbench.function.movie.QueryMovieCountFunction;
import org.aquarius.cicada.workbench.function.movie.QueryMovieListFunction;
import org.aquarius.cicada.workbench.function.net.HttpFunction;
import org.aquarius.cicada.workbench.function.net.WrapFvsUrlFunction;
import org.aquarius.cicada.workbench.function.net.WrapUrlFunction;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.SystemUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Register functions to common use.
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class BrowserUtil {

	public static final String Success = "success";

	public static final String Error = "error";

	// private static Timer daemonTimer = new Timer("close popup window ");

	private static boolean forceCloseAllPopup = true;

	private static List<Browser> popupBrowserList = Collections.synchronizedList(new ArrayList<>());

	static {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				if (CollectionUtils.isEmpty(BrowserUtil.popupBrowserList)) {
					return;
				}

				Display.getDefault().asyncExec(() -> {
					List<Browser> disposedBrowserList = new ArrayList<>();

					for (Browser browser : BrowserUtil.popupBrowserList) {
						try {

							if (SwtUtil.isValid(browser)) {
								Shell shell = browser.getShell();

								if (SwtUtil.isValid(shell)) {
									shell.dispose();
								}
							}

							disposedBrowserList.add(browser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					BrowserUtil.popupBrowserList.removeAll(disposedBrowserList);
				});

			}
		};

		long time = 2 * SystemUtil.TimeSecond;
		// daemonTimer.schedule(task, time, time);
	}

	/**
	 * 
	 */
	private BrowserUtil() {
		// No instances needed.
	}

	public static void registerCommonFunctions(Browser browser) {
		new JoinStringFunction(browser);
		new QueryPixelsFunction(browser);
		new IsAcceptablePixelFunction(browser);

		new CloseSiteFunction(browser);
		new CloseWindowFunction(browser);

		new QueryGeneratorNameListFunction(browser);

		new HttpFunction(browser);
		new WrapFvsUrlFunction(browser);
		new WrapUrlFunction(browser);
	}

	public static void registerMovieFunctions(Browser browser, IMovieListService service) {

		new QueryMovieCountFunction(browser, service);
		new QueryMovieListFunction(browser, service);
		new DownloadMovieListFunction(browser, service, true);
		new GenerateMovieDownloadUrlsFunction(browser, service);

	}

	/**
	 * Return the short class name.<BR>
	 *
	 * @param clazz
	 * @return
	 */
	public static String getShortClassName(Class<?> clazz) {
		String name = ClassUtils.getShortClassName(clazz);
		return WordUtils.uncapitalize(name);
	}

	/* register WindowEvent listeners */
	public static void initialize(Browser browser) {
		browser.addOpenWindowListener(event -> {
			Shell shell = new Shell(browser.getDisplay());
			shell.setText("New Window");
			shell.setLayout(new FillLayout());
			Browser popupBrowser = new Browser(shell, SWT.None);

			initialize(popupBrowser);

			event.browser = popupBrowser;

			// BrowserUtil.popupBrowserList.add(popupBrowser);

		});

		browser.addVisibilityWindowListener(new VisibilityWindowListener() {
			@Override
			public void hide(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				Shell shell = browser.getShell();
				shell.setVisible(false);
			}

			@Override
			public void show(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				final Shell shell = browser.getShell();
				/* popup blocker - ignore windows with no style */

				if (forceCloseAllPopup) {
					event.display.asyncExec(() -> shell.close());
					return;
				}

//				boolean isOSX = SWT.getPlatform().equals("cocoa");
//				if (!event.addressBar && !event.statusBar && !event.toolBar && (!event.menuBar || isOSX)) {
//					event.display.asyncExec(() -> shell.close());
//					return;
//				}

				if (event.location != null)
					shell.setLocation(event.location);
				if (event.size != null) {
					Point size = event.size;
					shell.setSize(shell.computeSize(size.x, size.y));
				}
				shell.open();
			}
		});
		browser.addCloseWindowListener(event -> {
			Browser browser1 = (Browser) event.widget;
			Shell shell = browser1.getShell();
			shell.close();
		});
	}

	/**
	 * 
	 */
	public static void openDebugPage(String url) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());

		DebugBrowserScriptControlFactory debugScriptControlFactory = new DebugBrowserScriptControlFactory(url);
		debugScriptControlFactory.createControl(shell);

		shell.setText("Debug Script");
		shell.setSize(1280, 800);
		shell.open();
	}

}
