/**

 *

 */

package org.aquarius.cicada.workbench.control.base;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.control.IMovieListRefreshable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Use browser to show movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 * 
 */

public abstract class AbstractBrowserComposite extends Composite implements IMovieListRefreshable {

	private IMovieListService service;

	private Browser browser;

	private boolean inited = false;

	private String querySelectedScript = "var selectedElements=$('li.selected');\r\n" + "var results=[];\r\n"
			+ "for(var i=0; i<selectedElements.length; i++)\r\n" + "{\r\n" + "	results.push(selectedElements[i].getAttribute('name'));\r\n" + "}\r\n"
			+ "	\r\n" + "return results.join('|');";
	/**
	 * <code>
	 	var selectedElements=$('li.selected');
	
		var results=[];
		for(var i=0;i<selectedElements.length;i++)
		{
			results.push(selectedElements[i].getAttribute('name'));
		}
			
		return results.join('|');
	 * </code>
	 */

	private static final String TestScript = "try {\r\n" + "    if (typeof eval('queryMovieCountFunction') === 'function') { \r\n" + "       return 'true';\r\n"
			+ "    } else { \r\n" + "       return 'false';\r\n" + "    }\r\n" + "} catch (e) { \r\n" + "   return 'false';\r\n" + "}";

	/**
	 * <code>
	 * try {
		if (typeof eval('testFunction') === 'function') { 
	   			return 'true';
			} 
			else { 
	   			return 'false';
			}
		} catch (e) { 
			return 'false';
		}
	</code>
	 */

	/**
	 * 
	 * @param parent
	 * @param style
	 * 
	 */

	public AbstractBrowserComposite(Composite parent, int style) {

		super(parent, style);

		this.setLayout(new FillLayout());

		this.browser = new Browser(this, SWT.NONE);

	}

	/**
	 * 
	 * @return the service
	 * 
	 */

	public IMovieListService getService() {
		return this.service;

	}

	/**
	 * 
	 * @param service the service to set
	 * 
	 */

	public void setService(IMovieListService service) {

		if (this.service == service) {

			return;

		}

		this.service = service;

		refreshContent();

	}

	/**
	 *
	 */
	@Override
	public void refreshContent() {
		if (this.inited) {
			doFresh(this.browser, this.service);
			return;
		}

		boolean injectFunction = this.injectFunctions(this.browser, this.service);

		if (injectFunction) {
			this.inited = true;
			doFresh(this.browser, this.service);
		}

		/**
		 * Use this timer to solve the problem that browser load function fail in linux.
		 */
		boolean executeTimer = (!injectFunction) || (!SystemUtils.IS_OS_WINDOWS);

		if (executeTimer) {
			Timer timer = new Timer();

			TimerTask timerTask = new TimerTask() {

				int retryCount = 5;

				@Override
				public void run() {

					this.retryCount--;

					if (this.retryCount < 0) {
						this.cancel();
						timer.cancel();
					}

					AbstractBrowserComposite.this.browser.getDisplay().syncExec(() -> {

						if (injectFunctions(AbstractBrowserComposite.this.browser, AbstractBrowserComposite.this.service)) {
							try {
								AbstractBrowserComposite.this.browser.refresh();
								this.retryCount = 0;
								this.cancel();
								timer.cancel();
							} catch (Exception e) {
								// Nothing to do
							}
						} else {
							System.err.println("run false   ");
						}
					});
				}
			};

			timer.schedule(timerTask, 800, 300);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateContent() {
		// Nothing to do
	}

	protected abstract String getPageUrl();

	/**
	 * 
	 * @return the browser
	 * 
	 */

	public Browser getBrowser() {
		return this.browser;
	}

	/**
	 * refresh the browser to show new contents.<BR>
	 *
	 * @param browser
	 * @param service
	 * 
	 */
	protected void doFresh(Browser browser, IMovieListService service) {

		String pageUrl = this.getPageUrl();
		String zoom = WorkbenchActivator.getDefault().getConfiguration().getBrowserZoom();

		if (StringUtils.isNotBlank(zoom)) {
			pageUrl = pageUrl + "?zoom=" + zoom;
		}

		String[] header = service.getRequestHeader();

		browser.setUrl(pageUrl, null, header);
	}

	/**
	 * 
	 * @param browser
	 * @param service
	 * @return
	 */
	private boolean injectFunctions(Browser browser, IMovieListService service) {
		if (validateInjectionResult(browser)) {
			return true;
		}

		this.doInjectFunctions(browser, service);
		return validateInjectionResult(browser);
	}

	/**
	 * 
	 * @param browser
	 * @return
	 */
	private boolean validateInjectionResult(Browser browser) {
		Object resultObject = null;
		try {
			resultObject = browser.evaluate("return isFunctionInjected();", true);
		} catch (Exception e) {
			try {
				resultObject = browser.evaluate(TestScript);
			} catch (Exception e2) {
				// Nothint to do
			}

		}

		String resultString = ObjectUtils.toString(resultObject, "false");
		return BooleanUtils.toBoolean(resultString);
	}

	/**
	 * 
	 * @param browser
	 * @param service
	 */
	protected void doInjectFunctions(Browser browser, IMovieListService service) {

		BrowserUtil.registerCommonFunctions(browser);

		BrowserUtil.registerMovieFunctions(browser, service);

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> getSelectedMovieList() {

		Object resultObject = null;

		try {
			resultObject = this.getBrowser().evaluate("return querySelectedMovies(); ", true);
		} catch (Exception e) {
			resultObject = this.getBrowser().evaluate(this.querySelectedScript, true);
		}

		String resultString = ObjectUtils.toString(resultObject);
		Set<Integer> idSet = MovieUtil.extractMovieIdList(resultString);

		return this.getService().findMovies(idSet);

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedMovieList(List<Movie> movieList) {
		if (CollectionUtils.isEmpty(movieList)) {

			this.getBrowser().getDisplay().asyncExec(() -> {
				try {
					getBrowser().evaluate("cancelSelection();");
				} catch (Exception e) {
					// Nothing to do
				}
			});

		}
	}

}
