/**
 *
 */
package org.aquarius.cicada.workbench.function.movie;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.function.base.AbstractMovieListFunction;
import org.eclipse.swt.browser.Browser;

import com.alibaba.fastjson.JSON;

/**
 * Query movie list with specified page.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class QueryMovieListFunction extends AbstractMovieListFunction {

	/**
	 * @param browser
	 * @param movieListSerivce
	 * @param name
	 */
	public QueryMovieListFunction(Browser browser, IMovieListService movieListSerivce, String name) {
		super(browser, movieListSerivce, name);
	}

	/**
	 * @param browser
	 * @param movieListSerivce
	 */
	public QueryMovieListFunction(Browser browser, IMovieListService movieListSerivce) {
		this(browser, movieListSerivce, BrowserUtil.getShortClassName(QueryMovieListFunction.class));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Object callFunction(IMovieListService movieListService, Object[] arguments) {
		int currentPage = 1;
		int pageSize = 40;

		if (arguments.length > 1) {
			String currentPageString = ObjectUtils.toString(arguments[0], "1");
			currentPage = NumberUtils.toInt(currentPageString, 1);

			String pageSizeString = ObjectUtils.toString(arguments[1], "40");
			pageSize = NumberUtils.toInt(pageSizeString, 40);
		}

		int start = (currentPage - 1) * pageSize;
		int end = currentPage * pageSize;

		List<Movie> movieList = movieListService.subList(start, end);
		return JSON.toJSONString(movieList);
	}
}