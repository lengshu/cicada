/**
 *
 */
package org.aquarius.cicada.workbench.function.base;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.core.util.MovieUtil;
import org.eclipse.swt.browser.Browser;

/**
 * The function to operate multi movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractBatchMovieListFunction extends AbstractMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public AbstractBatchMovieListFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	@Override
	public Object callFunction(IMovieListService movieListSerivce, Object[] arguments) {

		if (ArrayUtils.isEmpty(arguments)) {
			return null;
		}

		if (arguments.length >= 1) {
			String value = ObjectUtils.toString(arguments[0]);
			Set<Integer> idList = MovieUtil.extractMovieIdList(value);

			if (!idList.isEmpty()) {
				List<Movie> selectedMovieList = getMovieListSerivce().findMovies(idList);

				if (CollectionUtils.isNotEmpty(selectedMovieList)) {
					doCallFunction(selectedMovieList, arguments);
				}
			}
		}

		return null;

	}

	/**
	 *
	 * @param selectedMovieList
	 * @param arguments
	 * @return
	 */
	protected abstract Object doCallFunction(List<Movie> selectedMovieList, Object[] arguments);

}