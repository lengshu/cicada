/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.editor.FilterSite;
import org.aquarius.cicada.workbench.function.base.AbstractMovieListFunction;
import org.aquarius.cicada.workbench.job.RefreshSiteJob;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.swt.browser.Browser;

/**
 * Refresh site to get newest movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshSiteFunction extends AbstractMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public RefreshSiteFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public RefreshSiteFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(RefreshSiteFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object callFunction(IMovieListService movieListSerivce, Object[] arguments) {

		if (movieListSerivce instanceof FilterSite) {
			FilterSite filterSite = (FilterSite) movieListSerivce;

			Site site = filterSite.getSite();

			if (site.isRefreshing()) {
				TooltipUtil.showErrorTip(Messages.WarnDialogTitle, "RefreshingErrorMessage");
				return false;
			}

			new RefreshSiteJob(Messages.RefreshSiteAction_RefreshSite, site.getSiteName(), isDisposed()).schedule();
			return BrowserUtil.Success;
		} else {
			return "This operation is not supported.";
		}

	}

}