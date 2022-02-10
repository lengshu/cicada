/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.editor.FilterSite;
import org.aquarius.cicada.workbench.function.base.AbstractBatchMovieListFunction;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.browser.Browser;

/**
 * Delete the specified movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DeleteMovieFunction extends AbstractBatchMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public DeleteMovieFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public DeleteMovieFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(DeleteMovieFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object doCallFunction(List<Movie> selectedMovieList, Object[] arguments) {

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		if (!SwtUtil.openConfirm(SwtUtil.findShell(), Messages.DeleteAction_ConfirmDialogTitle, Messages.DeleteAction_ConfirmDialogMessage, store,
				MovieConfiguration.Key_ConfirmDeleteMovie)) {
			return false;
		}

		if (this.getMovieListSerivce() instanceof FilterSite) {
			FilterSite filterSite = (FilterSite) this.getMovieListSerivce();

			RuntimeManager.getInstance().getStoreService().deleteMovies(selectedMovieList);
			filterSite.getSite().removeMovies(selectedMovieList);
			filterSite.removeMovies(selectedMovieList);

			return BrowserUtil.Success;
		} else {
			return "This operation is not supported.";
		}

	}

}