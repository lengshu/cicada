/**
 *
 */
package org.aquarius.cicada.workbench.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.service.Filter;
import org.aquarius.cicada.core.service.IMovieListService;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.util.collection.CollectionUtil;

/**
 *
 * This model is designed to support filter a site movie list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FilterSite implements IMovieListService {

	private Filter filter = new Filter();

	private List<Movie> filterMovieList = new ArrayList<>();

	private Site site;

	/**
	 *
	 * @param site
	 */
	public FilterSite(Site site) {
		this.site = site;

		this.filterMovieList.addAll(site.getMovieList());
	}

	/**
	 *
	 * @param movieList
	 */
	public void removeMovies(List<Movie> movieList) {
		this.filterMovieList.removeAll(movieList);
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return this.site;
	}

	@Override
	public void setFilter(Filter filter) {

		if (this.filter != filter) {
			this.filter = filter;
		}

		doFilter();
	}

	/**
	 * @return the filter
	 */
	public Filter getFilter() {
		return this.filter;
	}

	/**
	 * Execute filter by condition.<BR>
	 *
	 */
	public synchronized void doFilter() {
		if (null == this.filter || (!this.filter.isValid())) {
			this.filterMovieList.clear();
			this.filterMovieList.addAll(this.site.getMovieList());
			return;
		} else {
			this.filterMovieList = this.site.getMovieList().stream().filter(movie -> (!this.filter.isFiltered(movie))).collect(Collectors.toList());

		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> subList(int start, int end) {
		return CollectionUtil.subList(this.filterMovieList, start, end);
	}

	/**
	 * @return the filterMovieList
	 */
	public List<Movie> getFilterMovieList() {
		return this.filterMovieList;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getMovieSize() {
		return this.filterMovieList.size();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> findMovies(Set<Integer> idList) {
		return MovieUtil.findMovies(this.filterMovieList, idList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getRequestHeader() {

		SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfig(this.site.getSiteName());

		if (null == siteConfig) {
			return null;
		}

		return new String[] { "Referer: " + siteConfig.getMainPage() };
	}
}
