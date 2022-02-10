/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.editor.FilterSite;
import org.aquarius.cicada.workbench.function.base.AbstractMovieListFunction;
import org.aquarius.util.DesktopUtil;
import org.eclipse.swt.browser.Browser;

/**
 * Open the site url in the system browser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenSiteFunction extends AbstractMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public OpenSiteFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public OpenSiteFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(OpenSiteFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object callFunction(IMovieListService movieListSerivce, Object[] arguments) {

		if (movieListSerivce instanceof FilterSite) {
			FilterSite filterSite = (FilterSite) movieListSerivce;

			DesktopUtil.openWebpages(filterSite.getSite().getMovieParser().getSiteUrls());

			return BrowserUtil.Success;
		} else {
			return "This operation is not supported.";
		}

	}

}