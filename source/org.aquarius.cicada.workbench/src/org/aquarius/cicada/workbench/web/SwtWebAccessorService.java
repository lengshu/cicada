/**
 *
 */
package org.aquarius.cicada.workbench.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.spi.web.IWebAccessorService;
import org.aquarius.cicada.core.spi.web.IWebAccessorVisible;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.browser.BrowserViewer;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.util.ObjectHolder;
import org.aquarius.util.base.AbstractComparable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * Swt web service.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class SwtWebAccessorService extends AbstractComparable<IWebAccessorService> implements IWebAccessorService {

	public static final String Name = "swt";

	private Display display;

	private Shell shell;

	private TabFolder parentTab;

	private ObjectPool<SwtWebAccessor> webAccessorPool;

	private boolean closed = false;

	private Logger logger = LogUtil.getLogger(this.getClass());

	private Initializer<Browser> initializer;

	/**
	 *
	 */
	public SwtWebAccessorService() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void start(Initializer<T> initializer) {
		this.initializer = (Initializer<Browser>) initializer;

		// this.display = new Display();

		if (SystemUtils.IS_OS_WINDOWS) {
			this.display = new Display();
		} else {
			this.display = Display.getDefault();
		}

		this.shell = new Shell(this.display);

		this.shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.doit = false;
				SwtWebAccessorService.this.shell.setVisible(false);
				// The service can't be closed,just make it invisible.
			}
		});

		this.shell.setLayout(new GridLayout());

		this.parentTab = new TabFolder(this.shell, SWT.FLAT);
		this.parentTab.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.createObjectPool();

		this.shell.open();
		this.shell.setVisible(RuntimeManager.getInstance().getConfiguration().isBrowserVisible());

	}

	/**
	 * Use pool to manager swt browser resource.
	 */
	private void createObjectPool() {

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();

		config.setMinEvictableIdleTimeMillis(-1);
		config.setMaxTotal(20);

		PooledObjectFactory<SwtWebAccessor> pooledObjectFactory = new BasePooledObjectFactory<SwtWebAccessor>() {

			@Override
			public SwtWebAccessor create() throws Exception {

				ObjectHolder<SwtWebAccessor> objectHolder = new ObjectHolder<>();

				SwtWebAccessorService.this.display.syncExec(new Runnable() {

					@Override
					public void run() {
						Browser browser = createBrowserTabItem();

						BrowserUtil.initialize(browser);

						SwtWebAccessor webAccessor = new SwtWebAccessor(browser, SwtWebAccessorService.this);
						objectHolder.setValue(webAccessor);
					}

					/**
					 * @return
					 */
					private Browser createBrowserTabItem() {
						TabItem item = new TabItem(SwtWebAccessorService.this.parentTab, SWT.NONE);

						Composite parent = new Composite(SwtWebAccessorService.this.parentTab, SWT.None);
						GridLayout gridLayout = new GridLayout(2, false);
						parent.setLayout(gridLayout);

						Text urlText = new Text(parent, SWT.FLAT);
						urlText.setEditable(false);
						urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

						Button copyButton = new Button(parent, SWT.PUSH);
						copyButton.setLayoutData(new GridData());
						copyButton.setText(Messages.ActionCopy);

						copyButton.addSelectionListener(new SelectionAdapter() {
							/**
							 * {@inheritDoc}
							 */
							@Override
							public void widgetSelected(SelectionEvent e) {
								ClipboardUtil.setClipboardString(urlText.getText());
							}
						});

						Browser browser = new Browser(parent, WorkbenchActivator.getDefault().getBrowserStyle());

						browser.addLocationListener(new LocationAdapter() {

							@Override
							public void changing(LocationEvent event) {
								urlText.setText(browser.getUrl());
							}
						});

						GridData gridData = new GridData(GridData.FILL_BOTH);
						gridData.horizontalSpan = 2;
						browser.setLayoutData(gridData);

						item.setText(" " + SwtWebAccessorService.this.parentTab.getItemCount() + " ");
						item.setControl(parent);

						return browser;
					}
				});

				return objectHolder.getValue();
			}

			@Override
			public PooledObject<SwtWebAccessor> wrap(SwtWebAccessor webAccessor) {
				return new DefaultPooledObject<SwtWebAccessor>(webAccessor);
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void passivateObject(PooledObject<SwtWebAccessor> pooledObject) throws Exception {
				pooledObject.getObject().get(AboutBlank, DefaultProcessMonitor.createInstance());
			}
		};

		this.webAccessorPool = new GenericObjectPool<>(pooledObjectFactory, config);
	}

	/**
	 *
	 * @param borrower
	 * @param visitor
	 */
	@Override
	public <T> T visit(Visitor<T> visitor) {

		SwtWebAccessor webAccessor = null;

		try {
			webAccessor = this.webAccessorPool.borrowObject();

			if (null != this.initializer) {
				try {
					this.initializer.init(webAccessor.getBrowser());
				} catch (Exception e) {
					this.logger.error("init context ", e);
				}
			}

			return visitor.visit(webAccessor);

		} catch (Exception e) {
			this.logger.error("brorow web driver ", e);
			return null;
		} finally {
			try {
				this.webAccessorPool.returnObject(webAccessor);
			} catch (Exception e) {
				this.logger.error("return web driver ", e);
			}
		}
	}

	@Override
	public void close() {
		this.closed = true;
		// webAccessorPool.close();
		this.shell.dispose();
	}

	/**
	 * @return the closed
	 */
	boolean isClosed() {
		return this.closed;
	}

	@Override
	public void openBrowserForSetting() {
		this.openBrowserForSetting(null);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void openBrowserForSetting(String urlString) {

		Shell dialogShell = new Shell(this.display, SWT.SHELL_TRIM);

		dialogShell.setLayout(new FillLayout());

		BrowserViewer viewer = new BrowserViewer(dialogShell, SWT.BORDER | BrowserViewer.BUTTON_BAR | BrowserViewer.LOCATION_BAR,
				WorkbenchActivator.getDefault().getBrowserStyle());

		if (StringUtils.isNotBlank(urlString)) {
			viewer.getBrowser().setUrl(urlString);
		}

		dialogShell.open();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return Name;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setVisible(boolean visible) {
		this.shell.setVisible(visible);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public IWebAccessorVisible getVisibleState() {

		if (this.shell.isVisible()) {
			return IWebAccessorVisible.show;
		} else {
			return IWebAccessorVisible.hide;
		}
	}
}
