/**
 *
 */
package org.aquarius.cicada.workbench.control;

import java.io.File;

import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.control.base.AbstractBrowserComposite;
import org.aquarius.cicada.workbench.function.common.CloseWindowFunction;
import org.aquarius.cicada.workbench.function.movie.SelectMovieFunction;
import org.aquarius.util.io.FileUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite to download movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FixedMovieDownloadComposite extends AbstractBrowserComposite {

	private String fileUrl;

	/**
	 * @param parent
	 * @param style
	 */
	public FixedMovieDownloadComposite(Composite parent, int style) {
		super(parent, style);

		String workingFolder = Starter.getInstance().getWorkingFolder();
		String fileUrlPath = workingFolder + FileUtil.getRealFilePath("/config/site/download.html");
		this.fileUrl = new File(fileUrlPath).toURI().toString();
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

		new SelectMovieFunction(this.getBrowser(), null, service);

		new CloseWindowFunction(browser);
	}

}
