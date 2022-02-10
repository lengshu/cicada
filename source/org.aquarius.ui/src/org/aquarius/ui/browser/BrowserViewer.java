/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Jacek Pospychala - jacek.pospychala@pl.ibm.com - fix for bug 224887
 *    Ian Pun & Lucas Bullen (Red Hat Inc.) - Bug 508508
 *******************************************************************************/
package org.aquarius.ui.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.aquarius.ui.UiActivator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.VisibilityWindowAdapter;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A Web browser widget. It extends the Eclipse SWT Browser widget by adding an
 * optional toolbar complete with a URL combo box, history, back &amp; forward,
 * and refresh buttons.
 * <p>
 * Use the style bits to choose which toolbars are available within the browser
 * composite. You can access the embedded SWT Browser directly using the
 * getBrowser() method.
 * </p>
 * <p>
 * Additional capabilities are available when used as the internal Web browser,
 * including status text and progress on the Eclipse window's status line, or
 * moving the toolbar capabilities up into the main toolbar.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LOCATION_BAR, BUTTON_BAR</dd>
 * <dt><b>Events:</b></dt>
 * <dd>None</dd>
 * </dl>
 *
 * @since 1.0
 */
public class BrowserViewer extends Composite {
	/**
	 * Style parameter (value 1) indicating that the URL and Go button will be on
	 * the local toolbar.
	 */
	public static final int LOCATION_BAR = 1 << 1;

	/**
	 * Style parameter (value 2) indicating that the toolbar will be available on
	 * the web browser. This style parameter cannot be used without the LOCATION_BAR
	 * style.
	 */
	public static final int BUTTON_BAR = 1 << 2;

	protected static final String PROPERTY_TITLE = "title"; //$NON-NLS-1$

	private static final int MAX_HISTORY = 50;

	public Clipboard clipboard;

	public Combo combo;

	protected boolean showToolbar;

	protected boolean showURLbar;

	protected ToolItem back;

	protected ToolItem forward;

	protected MenuItem autoRefresh;

	protected BusyIndicator busy;

	protected boolean loading;

	protected static java.util.List<String> history;

	protected Browser browser;

	protected int browserStyle;

	protected BrowserText text;

	protected boolean newWindow;

	protected IBrowserViewerContainer container;

	protected String title;

	protected int progressWorked = 0;

	protected WatchService watcher;

	protected List<PropertyChangeListener> propertyListeners;

	/**
	 * Under development - do not use
	 */
	public static interface ILocationListener {
		public void locationChanged(String url);

		public void historyChanged(String[] history2);
	}

	public ILocationListener locationListener;

	/**
	 * Under development - do not use
	 */
	public static interface IBackNextListener {
		public void updateBackNextBusy();
	}

	public IBackNextListener backNextListener;

	private ImageResourceManager imageManager;

	private static final String URL_PREFIX = "$nl$/icons/"; //$NON-NLS-1$
	private static final String URL_ELCL = URL_PREFIX + "elcl16/"; //$NON-NLS-1$
	private static final String URL_CLCL = URL_PREFIX + "clcl16/"; //$NON-NLS-1$
	private static final String URL_DLCL = URL_PREFIX + "dlcl16/"; //$NON-NLS-1$

	/**
	 * Creates a new Web browser given its parent and a style value describing its
	 * behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in the class
	 * header or class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together (that is, using
	 * the <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent       a composite control which will be the parent of the new
	 *                     instance (cannot be null)
	 * @param style        the style of control to construct
	 * @param browserStyle the style of browser.
	 */
	public BrowserViewer(Composite parent, int style, int browserStyle) {
		super(parent, SWT.NONE);

		this.browserStyle = browserStyle;

		if ((style & LOCATION_BAR) != 0)
			this.showURLbar = true;

		if ((style & BUTTON_BAR) != 0)
			this.showToolbar = true;

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 1;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		this.clipboard = new Clipboard(parent.getDisplay());

		if (this.showToolbar || this.showURLbar) {
			Composite toolbarComp = new Composite(this, SWT.NONE);
			this.imageManager = new ImageResourceManager(toolbarComp);
			toolbarComp.setLayout(new ToolbarLayout());
			toolbarComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));

			if (this.showToolbar)
				createToolbar(toolbarComp);

			if (this.showURLbar)
				createLocationBar(toolbarComp);

			if (this.showToolbar || this.showURLbar) {
				this.busy = new BusyIndicator(toolbarComp, SWT.NONE);
				this.busy.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				this.busy.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						setURL("http://www.eclipse.org"); //$NON-NLS-1$
					}
				});
			}

		}

		// create a new SWT Web browser widget, checking once again to make sure
		// we can use it in this environment
		// if (WebBrowserUtil.canUseInternalWebBrowser())
		try {
			this.browser = new Browser(this, browserStyle);
//			this.browser.addLocationListener(LocationListener.changingAdapter(event -> {
//				URI uri = URI.create(event.location);
//				if (!(uri.getScheme().equals("http") || uri.getScheme().equals("https") //$NON-NLS-1$ //$NON-NLS-2$
//						|| uri.getScheme().equals("file"))) //$NON-NLS-1$
//					try {
//						if (IUriSchemeProcessor.INSTANCE.canHandle(uri)) {
//							IUriSchemeProcessor.INSTANCE.handleUri(uri);
//							event.doit = false;
//						}
//					} catch (CoreException e) {
//						UiActivator.logError(e.getMessage(), e);
//					}
//
//			}));
		} catch (SWTError e) {
			if (e.code != SWT.ERROR_NO_HANDLES) {
				WebBrowserUtil.openError(Messages.errorCouldNotLaunchInternalWebBrowser);
				return;
			}
			this.text = new BrowserText(this, this, e);
		}

		if (this.showURLbar)
			updateHistory();
		if (this.showToolbar)
			updateBackNextBusy();

		if (this.browser != null) {
			this.browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		} else
			this.text.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		addBrowserListeners();
		addDisposeListener(this::dispose);
	}

	/**
	 * Returns the underlying SWT browser widget.
	 *
	 * @return the underlying browser
	 */
	public Browser getBrowser() {
		return this.browser;
	}

	/**
	 * Navigate to the home URL.
	 */
	public void home() {
		this.browser.setText(""); //$NON-NLS-1$
	}

	/**
	 * Loads a URL.
	 *
	 * @param url the URL to be loaded
	 * @return true if the operation was successful and false otherwise.
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the url is
	 *                                     null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS when
	 *                                     called from the wrong thread</li>
	 *                                     <li>ERROR_WIDGET_DISPOSED when the widget
	 *                                     has been disposed</li>
	 *                                     </ul>
	 * @see #getURL()
	 */
	public void setURL(String url) {
		setURL(url, true);
	}

	protected void updateBackNextBusy() {
		if (!this.back.isDisposed()) {
			this.back.setEnabled(isBackEnabled());
		}
		if (!this.forward.isDisposed()) {
			this.forward.setEnabled(isForwardEnabled());
		}
		if (!this.busy.isDisposed()) {
			this.busy.setBusy(this.loading);
		}

		if (this.backNextListener != null)
			this.backNextListener.updateBackNextBusy();
	}

	protected void updateLocation() {
		if (this.locationListener != null)
			this.locationListener.historyChanged(null);

		if (this.locationListener != null)
			this.locationListener.locationChanged(null);
	}

	/**
	 *
	 */
	private void addBrowserListeners() {
		if (this.browser == null)
			return;
		// respond to ExternalBrowserInstance StatusTextEvents events by
		// updating the status line
		this.browser.addStatusTextListener(event -> {
			// System.out.println("status: " + event.text); //$NON-NLS-1$
			if (this.container != null) {
				IStatusLineManager status = this.container.getActionBars().getStatusLineManager();
				status.setMessage(event.text);
			}
		});

		// Add listener for new window creation so that we can instead of
		// opening a separate
		// new window in which the session is lost, we can instead open a new
		// window in a new
		// shell within the browser area thereby maintaining the session.
		this.browser.addOpenWindowListener(event -> {
			Shell shell2 = new Shell(getShell(), SWT.SHELL_TRIM);
			shell2.setLayout(new FillLayout());
			shell2.setText(Messages.viewWebBrowserTitle);
			shell2.setImage(getShell().getImage());
			if (event.location != null)
				shell2.setLocation(event.location);
			if (event.size != null)
				shell2.setSize(event.size);
			int style = 0;
			if (this.showURLbar)
				style += LOCATION_BAR;
			if (this.showToolbar)
				style += BUTTON_BAR;
			BrowserViewer browser2 = new BrowserViewer(shell2, style, this.browserStyle);
			browser2.newWindow = true;
			event.browser = browser2.browser;
		});

		this.browser.addVisibilityWindowListener(new VisibilityWindowAdapter() {
			@Override
			public void show(WindowEvent e) {
				Browser browser2 = (Browser) e.widget;
				if (browser2.getParent().getParent() instanceof Shell) {
					Shell shell = (Shell) browser2.getParent().getParent();
					if (e.location != null)
						shell.setLocation(e.location);
					if (e.size != null)
						shell.setSize(shell.computeSize(e.size.x, e.size.y));
					shell.open();
				}
			}
		});

		this.browser.addCloseWindowListener(event -> {
			// if shell is not null, it must be a secondary popup window,
			// else its an editor window
			if (this.newWindow)
				getShell().dispose();
			else
				this.container.close();
		});

		this.browser.addProgressListener(new ProgressListener() {
			@Override
			public void changed(ProgressEvent event) {
				// System.out.println("progress: " + event.current + ", " + event.total);
				// //$NON-NLS-1$ //$NON-NLS-2$
				if (event.total == 0)
					return;

				boolean done = (event.current == event.total);

				int percentProgress = event.current * 100 / event.total;
				if (BrowserViewer.this.container != null) {
					IProgressMonitor monitor = BrowserViewer.this.container.getActionBars().getStatusLineManager().getProgressMonitor();
					if (done) {
						monitor.done();
						BrowserViewer.this.progressWorked = 0;
					} else if (BrowserViewer.this.progressWorked == 0) {
						monitor.beginTask("", event.total); //$NON-NLS-1$
						BrowserViewer.this.progressWorked = percentProgress;
					} else {
						monitor.worked(event.current - BrowserViewer.this.progressWorked);
						BrowserViewer.this.progressWorked = event.current;
					}
				}

				if (BrowserViewer.this.showToolbar) {
					if (!BrowserViewer.this.busy.isBusy() && !done)
						BrowserViewer.this.loading = true;
					else if (BrowserViewer.this.busy.isBusy() && done) // once the progress hits
						// 100 percent, done, set
						// busy to false
						BrowserViewer.this.loading = false;

					// System.out.println("loading: " + loading); //$NON-NLS-1$
					updateBackNextBusy();
					updateHistory();
				}
			}

			@Override
			public void completed(ProgressEvent event) {
				if (BrowserViewer.this.container != null) {
					IProgressMonitor monitor = BrowserViewer.this.container.getActionBars().getStatusLineManager().getProgressMonitor();
					monitor.done();
				}
				if (BrowserViewer.this.showToolbar) {
					BrowserViewer.this.loading = false;
					updateBackNextBusy();
					updateHistory();
				}
			}
		});

		if (this.showURLbar) {
			this.browser.addLocationListener(new LocationAdapter() {
				@Override
				public void changed(LocationEvent event) {
					if (!event.top)
						return;
					if (BrowserViewer.this.combo != null) {
						if (!"about:blank".equals(event.location)) { //$NON-NLS-1$
							BrowserViewer.this.combo.setText(event.location);
							addToHistory(event.location);
							updateHistory();
						} // else
							// combo.setText(""); //$NON-NLS-1$
					}
					if (BrowserViewer.this.showToolbar) {
						// enable auto-refresh button if URL is a file
						File temp = getFile(BrowserViewer.this.browser.getUrl());
						if (temp != null && temp.exists()) {
							BrowserViewer.this.autoRefresh.setEnabled(true);
							if (BrowserViewer.this.autoRefresh.getSelection()) {
								fileChangedWatchService(temp);
							}
						} else {
							BrowserViewer.this.autoRefresh.setSelection(false);
							toggleAutoRefresh();
							BrowserViewer.this.autoRefresh.setEnabled(false);
						}
					}
				}
			});
		}

		this.browser.addTitleListener(event -> {
			String oldTitle = this.title;
			this.title = event.title;
			firePropertyChangeEvent(PROPERTY_TITLE, oldTitle, this.title);
		});
	}

	/**
	 * Add a property change listener to this instance.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.propertyListeners == null)
			this.propertyListeners = new ArrayList<>();
		this.propertyListeners.add(listener);
	}

	/**
	 * Remove a property change listener from this instance.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (this.propertyListeners != null)
			this.propertyListeners.remove(listener);
	}

	/**
	 * Fire a property change event.
	 */
	protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		if (this.propertyListeners == null)
			return;

		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		// Trace.trace("Firing: " + event + " " + oldValue);
		try {
			int size = this.propertyListeners.size();
			PropertyChangeListener[] pcl = new PropertyChangeListener[size];
			this.propertyListeners.toArray(pcl);

			for (int i = 0; i < size; i++)
				try {
					pcl[i].propertyChange(event);
				} catch (Exception e) {
					// ignore
				}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Navigate to the next session history item. Convenience method that calls the
	 * underlying SWT browser.
	 *
	 * @return <code>true</code> if the operation was successful and
	 *         <code>false</code> otherwise
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS when called from the
	 *                         wrong thread</li>
	 *                         <li>ERROR_WIDGET_DISPOSED when the widget has been
	 *                         disposed</li>
	 *                         </ul>
	 * @see #back
	 */
	public boolean forward() {
		if (this.browser == null)
			return false;
		return this.browser.forward();
	}

	/**
	 * Navigate to the previous session history item. Convenience method that calls
	 * the underlying SWT browser.
	 *
	 * @return <code>true</code> if the operation was successful and
	 *         <code>false</code> otherwise
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS when called from the
	 *                         wrong thread</li>
	 *                         <li>ERROR_WIDGET_DISPOSED when the widget has been
	 *                         disposed</li>
	 *                         </ul>
	 * @see #forward
	 */
	public boolean back() {
		if (this.browser == null)
			return false;
		return this.browser.back();
	}

	/**
	 * Returns <code>true</code> if the receiver can navigate to the previous
	 * session history item, and <code>false</code> otherwise. Convenience method
	 * that calls the underlying SWT browser.
	 *
	 * @return the receiver's back command enabled state
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 * @see #back
	 */
	public boolean isBackEnabled() {
		if (this.browser == null)
			return false;
		return this.browser.isBackEnabled();
	}

	/**
	 * Returns <code>true</code> if the receiver can navigate to the next session
	 * history item, and <code>false</code> otherwise. Convenience method that calls
	 * the underlying SWT browser.
	 *
	 * @return the receiver's forward command enabled state
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 * @see #forward
	 */
	public boolean isForwardEnabled() {
		if (this.browser == null)
			return false;
		return this.browser.isForwardEnabled();
	}

	/**
	 * Stop any loading and rendering activity. Convenience method that calls the
	 * underlying SWT browser.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS when called from the
	 *                         wrong thread</li>
	 *                         <li>ERROR_WIDGET_DISPOSED when the widget has been
	 *                         disposed</li>
	 *                         </ul>
	 */
	public void stop() {
		if (this.browser != null)
			this.browser.stop();
	}

	/**
	 *
	 */
	private boolean navigate(String url) {
		if (url != null && url.equals(getURL())) {
			refresh();
			return true;
		}
		if (this.browser != null)
			return this.browser.setUrl(url, null, new String[] { "Cache-Control: no-cache" }); //$NON-NLS-1$
		return this.text.setUrl(url);
	}

	/**
	 * Refresh the current page. Convenience method that calls the underlying SWT
	 * browser.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS when called from the
	 *                         wrong thread</li>
	 *                         <li>ERROR_WIDGET_DISPOSED when the widget has been
	 *                         disposed</li>
	 *                         </ul>
	 */
	public void refresh() {
		if (this.browser != null)
			this.browser.refresh();
		else
			this.text.refresh();
		try {
			Thread.sleep(50);
		} catch (Exception e) {
			// ignore
		}
	}

	private void toggleAutoRefresh() {
		File temp = getFile(this.browser.getUrl());
		if (temp != null && temp.exists() && this.autoRefresh.getSelection()) {
			refresh();
			fileChangedWatchService(temp);
		} else if (this.watcher != null) {
			try {
				this.watcher.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setURL(String url, boolean browse) {
		if (url == null) {
			home();
			return;
		}

		if ("eclipse".equalsIgnoreCase(url)) //$NON-NLS-1$
			url = "http://www.eclipse.org"; //$NON-NLS-1$
		else if ("wtp".equalsIgnoreCase(url)) //$NON-NLS-1$
			url = "http://www.eclipse.org/webtools/"; //$NON-NLS-1$

		if (browse) {
			navigate(url);
		}

		addToHistory(url);
		updateHistory();
	}

	protected void addToHistory(String url) {
		if (history == null)
			history = WebBrowserPreference.getInternalWebBrowserHistory();
		int found = -1;
		int size = history.size();
		for (int i = 0; i < size; i++) {
			String s = history.get(i);
			if (s.equals(url)) {
				found = i;
				break;
			}
		}

		if (found == -1) {
			if (size >= MAX_HISTORY)
				history.remove(size - 1);
			history.add(0, url);
			WebBrowserPreference.setInternalWebBrowserHistory(history);
		} else if (found != 0) {
			history.remove(found);
			history.add(0, url);
			WebBrowserPreference.setInternalWebBrowserHistory(history);
		}
	}

	/**
	 *
	 */
	private void dispose(DisposeEvent event) {
		this.showToolbar = false;

		if (this.busy != null)
			this.busy.dispose();
		this.busy = null;

		this.browser = null;
		this.text = null;
		if (this.clipboard != null)
			this.clipboard.dispose();
		this.clipboard = null;

		if (this.watcher != null) {
			try {
				this.watcher.close();
			} catch (IOException e) {
				UiActivator.logError(e.getMessage(), e);
			}
		}
		this.watcher = null;

		removeSynchronizationListener();
	}

	private ToolBar createLocationBar(Composite parent) {
		this.combo = new Combo(parent, SWT.DROP_DOWN);

		updateHistory();

		this.combo.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
			try {
				if (this.combo.getSelectionIndex() != -1 && !this.combo.getListVisible()) {
					setURL(this.combo.getItem(this.combo.getSelectionIndex()));
				}
			} catch (Exception e1) {
				// ignore
			}
		}));
		this.combo.addListener(SWT.DefaultSelection, e -> setURL(this.combo.getText()));

		ToolBar toolbar = new ToolBar(parent, SWT.FLAT);

		ToolItem go = new ToolItem(toolbar, SWT.NONE);
		go.setImage(getImage(URL_ELCL, "nav_go.png")); //$NON-NLS-1$
		go.setHotImage(getImage(URL_CLCL, "nav_go.png")); //$NON-NLS-1$
		go.setDisabledImage(getImage(URL_DLCL, "nav_go.png")); //$NON-NLS-1$
		go.setToolTipText(Messages.actionWebBrowserGo);
		go.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> setURL(this.combo.getText())));

		return toolbar;
	}

	private ToolBar createToolbar(Composite parent) {
		ToolBar toolbar = new ToolBar(parent, SWT.FLAT);

		// create back and forward actions
		this.back = new ToolItem(toolbar, SWT.NONE);
		this.back.setImage(getImage(URL_ELCL, "nav_backward.png")); //$NON-NLS-1$
		this.back.setHotImage(getImage(URL_CLCL, "nav_backward.png")); //$NON-NLS-1$
		this.back.setDisabledImage(getImage(URL_DLCL, "nav_backward.png")); //$NON-NLS-1$
		this.back.setToolTipText(Messages.actionWebBrowserBack);
		this.back.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> back()));

		this.forward = new ToolItem(toolbar, SWT.NONE);
		this.forward.setImage(getImage(URL_ELCL, "nav_forward.png")); //$NON-NLS-1$
		this.forward.setHotImage(getImage(URL_CLCL, "nav_forward.png")); //$NON-NLS-1$
		this.forward.setDisabledImage(getImage(URL_DLCL, "nav_forward.png")); //$NON-NLS-1$
		this.forward.setToolTipText(Messages.actionWebBrowserForward);
		this.forward.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> forward()));

		// create refresh, stop, and print actions
		ToolItem stop = new ToolItem(toolbar, SWT.NONE);
		stop.setImage(getImage(URL_ELCL, "nav_stop.png")); //$NON-NLS-1$
		stop.setHotImage(getImage(URL_CLCL, "nav_stop.png")); //$NON-NLS-1$
		stop.setDisabledImage(getImage(URL_DLCL, "nav_stop.png")); //$NON-NLS-1$
		stop.setToolTipText(Messages.actionWebBrowserStop);
		stop.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> stop()));

		ToolItem refresh = new ToolItem(toolbar, SWT.DROP_DOWN);
		refresh.setImage(getImage(URL_ELCL, "nav_refresh.png")); //$NON-NLS-1$
		refresh.setHotImage(getImage(URL_CLCL, "nav_refresh.png")); //$NON-NLS-1$
		refresh.setDisabledImage(getImage(URL_DLCL, "nav_refresh.png")); //$NON-NLS-1$
		refresh.setToolTipText(Messages.actionWebBrowserRefresh);

		// create auto-refresh action
		Menu refreshMenu = new Menu(getShell(), SWT.POP_UP);
		this.autoRefresh = new MenuItem(refreshMenu, SWT.CHECK);
		this.autoRefresh.setText(Messages.actionWebBrowserAutoRefresh);
		this.autoRefresh.setEnabled(false);
		refresh.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
			if (e.detail == SWT.ARROW) {
				refreshMenu.setVisible(true);
			} else {
				refresh();
			}
		}));
		this.autoRefresh.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> toggleAutoRefresh()));

		return toolbar;
	}

	private Image getImage(String folder, String name) {
		ImageDescriptor id = ImageResourceManager.getImageDescriptor(folder + name);
		return this.imageManager.getImage(id);
	}

	/**
	 * Returns the current URL. Convenience method that calls the underlying SWT
	 * browser.
	 *
	 * @return the current URL or an empty <code>String</code> if there is no
	 *         current URL
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS when called from the
	 *                         wrong thread</li>
	 *                         <li>ERROR_WIDGET_DISPOSED when the widget has been
	 *                         disposed</li>
	 *                         </ul>
	 * @see #setURL(String)
	 */
	public String getURL() {
		if (this.browser != null)
			return this.browser.getUrl();
		return this.text.getUrl();
	}

	@Override
	public boolean setFocus() {
		if (this.browser != null) {
			this.browser.setFocus();
			updateHistory();
			return true;
		}
		return super.setFocus();
	}

	/**
	 * Update the history list to the global/shared copy.
	 */
	protected void updateHistory() {
		if (this.combo == null || this.combo.isDisposed())
			return;

		String temp = this.combo.getText();
		if (history == null)
			history = WebBrowserPreference.getInternalWebBrowserHistory();

		String[] historyList = new String[history.size()];
		history.toArray(historyList);
		this.combo.setItems(historyList);

		this.combo.setText(temp);
	}

	public IBrowserViewerContainer getContainer() {
		return this.container;
	}

	public void setContainer(IBrowserViewerContainer container) {
		if (container == null && this.container != null) {
			IStatusLineManager manager = this.container.getActionBars().getStatusLineManager();
			if (manager != null)
				manager.getProgressMonitor().done();
		}
		this.container = container;
	}

	protected File file;
	protected long timestamp;
	protected Thread fileListenerThread;
	protected LocationListener locationListener2;
	protected Object syncObject = new Object();

	protected void addSynchronizationListener() {
		if (this.fileListenerThread != null)
			return;

		this.fileListenerThread = new Thread("Browser file synchronization") { //$NON-NLS-1$
			@Override
			public void run() {
				while (BrowserViewer.this.fileListenerThread != null) {
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
						// ignore
					}
					synchronized (BrowserViewer.this.syncObject) {
						if (BrowserViewer.this.file != null && BrowserViewer.this.file.lastModified() != BrowserViewer.this.timestamp) {
							BrowserViewer.this.timestamp = BrowserViewer.this.file.lastModified();
							Display.getDefault().syncExec(() -> refresh());
						}
					}
				}
			}
		};
		this.fileListenerThread.setDaemon(true);
		this.fileListenerThread.setPriority(Thread.MIN_PRIORITY);

		this.locationListener2 = new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				File temp = getFile(event.location);
				if (temp != null && temp.exists()) {
					synchronized (BrowserViewer.this.syncObject) {
						BrowserViewer.this.file = temp;
						BrowserViewer.this.timestamp = BrowserViewer.this.file.lastModified();
					}
				} else
					BrowserViewer.this.file = null;
			}
		};
		this.browser.addLocationListener(this.locationListener2);

		File temp = getFile(this.browser.getUrl());
		if (temp != null && temp.exists()) {
			this.file = temp;
			this.timestamp = this.file.lastModified();
		}
		this.fileListenerThread.start();
	}

	protected static File getFile(String location) {
		if (location == null)
			return null;
		if (location.startsWith("file:/")) //$NON-NLS-1$
			location = location.substring(6);

		return new File(location);
	}

	protected void removeSynchronizationListener() {
		if (this.fileListenerThread == null)
			return;

		this.fileListenerThread = null;
		this.browser.removeLocationListener(this.locationListener2);
		this.locationListener2 = null;
	}

	/*
	 * Start the WatchService so that it monitors the local file system for any
	 * changes. This is used by the auto-refresh action as it will monitor the file
	 * being displayed and refresh the browser when there are changes.
	 */
	private void fileChangedWatchService(File file) {
		while (file.isFile()) {
			// get the directory as that is the requirement of WatchService
			file = file.getParentFile();
		}
		try {
			if (this.watcher != null) {
				this.watcher.close();
			}
			this.watcher = FileSystems.getDefault().newWatchService();
			final Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			path.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
			new Thread(() -> {
				try {
					WatchKey key = this.watcher.take();
					while (key != null) {
						for (WatchEvent<?> event : key.pollEvents()) {
							final Path changedPath = (Path) event.context();
							Display.getDefault().asyncExec(() -> {
								if (this.browser.getUrl().endsWith(changedPath.toString())) {
									this.browser.refresh();
								}
							});
						}
						key.reset();
						key = this.watcher.take();
					}
				} catch (InterruptedException | ClosedWatchServiceException e) {
					// catch and continue to abort the thread
				}
			}).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
