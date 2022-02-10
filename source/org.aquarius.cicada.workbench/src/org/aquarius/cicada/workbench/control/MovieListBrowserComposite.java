/**
 *
 */
package org.aquarius.cicada.workbench.control;

import java.io.File;

import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.control.base.AbstractBrowserComposite;
import org.aquarius.cicada.workbench.function.movie.SelectMovieFunction;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.base.SelectionProviderAdapter;
import org.aquarius.util.io.FileUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;

/**
 * Use browser to show movie list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieListBrowserComposite extends AbstractBrowserComposite {

	private Logger logger = LogUtil.getLogger(this.getClass());

	private SelectionProviderAdapter selectionProvider = new SelectionProviderAdapter();

	private String fileUrl;

	/**
	 *
	 * @param parent
	 * @param style
	 */
	public MovieListBrowserComposite(Composite parent, int style) {
		super(parent, style);

		String workingFolder = Starter.getInstance().getWorkingFolder();
		String htmlFileUrlPath = workingFolder + FileUtil.getRealFilePath("/config/site/index.html");
		this.fileUrl = new File(htmlFileUrlPath).toURI().toString();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPageUrl() {
		return this.fileUrl;
	}

	/**
	 * @param browser
	 * @param service
	 */
	@Override
	protected void doInjectFunctions(Browser browser, IMovieListService service) {

		super.doInjectFunctions(browser, service);
		new SelectMovieFunction(this.getBrowser(), this.selectionProvider, service);
	}

	/**
	 * @return the selectionProvider
	 */
	public SelectionProviderAdapter getSelectionProvider() {
		return this.selectionProvider;
	}

}
