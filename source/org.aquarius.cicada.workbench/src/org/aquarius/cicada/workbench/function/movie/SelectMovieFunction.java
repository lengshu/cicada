/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractMovieListFunction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.browser.Browser;

/**
 * Select movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SelectMovieFunction extends AbstractMovieListFunction {

	private ISelectionProvider selectionProvider;

	/**
	 *
	 * @param browser
	 * @param selectionProvider
	 * @param movieListSerivce
	 * @param name
	 */
	public SelectMovieFunction(Browser browser, ISelectionProvider selectionProvider, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
		this.selectionProvider = selectionProvider;
	}

	/**
	 *
	 * @param browser
	 * @param selectionProvider
	 * @param movieListSerivce
	 */
	public SelectMovieFunction(Browser browser, ISelectionProvider selectionProvider, IMovieListService movieListSerivce) {
		this(browser, selectionProvider, movieListSerivce, BrowserUtil.getShortClassName(SelectMovieFunction.class));
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Object callFunction(IMovieListService movieListSerivce, Object[] arguments) {

		if (ArrayUtils.isEmpty(arguments)) {
			return null;
		}

		if (null == this.selectionProvider) {
			return null;
		}

		if (arguments.length >= 1) {
			String value = ObjectUtils.toString(arguments[0]);
			Set<Integer> idList = MovieUtil.extractMovieIdList(value);

			if (!idList.isEmpty()) {
				List<Movie> selectedMovieList = getMovieListSerivce().findMovies(idList);

				if (CollectionUtils.isNotEmpty(selectedMovieList)) {
					StructuredSelection selection = new StructuredSelection(selectedMovieList);
					this.selectionProvider.setSelection(selection);
					return BrowserUtil.Success;
				}
			}
		}

		if (null != this.selectionProvider) {
			this.selectionProvider.setSelection(StructuredSelection.EMPTY);

		}

		return BrowserUtil.Success;

	}

}