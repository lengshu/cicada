/**
 *
 */
package org.aquarius.cicada.core.spi;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.result.MovieListResult;
import org.aquarius.log.LogUtil;
import org.aquarius.service.INameService;
import org.aquarius.util.base.AbstractComparable;
import org.aquarius.util.mark.IUrlAcceptable;
import org.slf4j.Logger;

/**
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractMovieParser extends AbstractComparable<AbstractMovieParser> implements IUrlAcceptable, INameService<AbstractMovieParser> {

	protected Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * The name of the site.<BR>
	 *
	 * @return
	 */
	@Override
	public abstract String getName();

	/**
	 * Return the site urls.<BR>
	 *
	 * @return
	 */
	public abstract String[] getSiteUrls();

	/**
	 * To judge whether the urlPattern can be parsed by this parser.
	 *
	 * @param urlPattern
	 * @return
	 */
	public boolean isAcceptable(String urlString) {
		return StringUtils.containsIgnoreCase(urlString, this.getName());
	}

	/**
	 * Return whether whether the site can jump to the last page or not.<BR>
	 *
	 * @return
	 */
	public abstract boolean supportJumpToLast();

	/**
	 * Return whether the current site support paging or not.<BR>
	 * 
	 * @return
	 */
	public boolean supportPaging() {
		return true;

	}

	/**
	 *
	 * @param pageUrlString
	 * @param lastVisitedMovie TODO
	 * @param processMonitor
	 * @return
	 */
	public abstract MovieListResult parseMovieListPage(String pageUrlString, Movie lastVisitedMovie, IProcessMonitor processMonitor);

	/**
	 * Parse the download info for the specified movies.
	 * 
	 * @param movie
	 * @param processMonitor
	 * @param includeDownloadInfo
	 */
	public abstract boolean parseMovieDetailPage(Movie movie, IProcessMonitor processMonitor, boolean includeDownloadInfo);

	/**
	 * Return the movie channels to import
	 *
	 * @return
	 */
	public abstract List<MovieChannel> getChannels();

	/**
	 * Return whether the site can be imported or not.<BR>
	 *
	 * @return Even the result is <CODE>true</CODE>, movie channels should be over
	 *         1.<BR>
	 */
	public boolean isSupportImport() {
		return true;
	}

	/**
	 * Some site need to check duplicate count more than once to make sure refresh
	 * done.
	 *
	 * @return
	 */
	public int getCheckDuplicationCount() {
		return 1;
	}

	/**
	 * Some site support album.<BR>
	 *
	 * @return
	 */
	public boolean isSupportAlbum() {
		return true;
	}

	/**
	 * 转换用来比较。<BR>
	 *
	 * @param movie
	 * @return
	 */
	public String parseMovieId(Movie movie) {
		return movie.getPageUrl();
	}

	/**
	 * Return whether the site support multi source to download.<BR>
	 *
	 * @return
	 */
	public boolean isSupportMultiDownloadSource() {
		return false;
	}

	/**
	 * Return the video site has auto refresh channel or not.<BR>
	 * 
	 * @return
	 */
	public boolean hasAutoRefreshChannel() {
		return false;
	}
}
