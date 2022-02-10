/**
 *
 */
package org.aquarius.cicada.core.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieExample;
import org.aquarius.cicada.core.model.Pagination;
import org.aquarius.cicada.core.model.VisitHistory;

/**
 * To store movie and other related objects.
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IMovieStoreService {

	/**
	 * close the database.<BR>
	 *
	 * @param compact if the value is <CODE>true</CODE>,the database will be
	 *                compacted to save space.<BR>
	 */
	public void close(boolean compact);

	/**
	 * Load database.<BR>
	 *
	 * @param databaseFolder
	 * @throws SQLException
	 */
	public void loadDatabase(String databaseFolder) throws SQLException;

	/**
	 * Query a movie by the id.<BR>
	 *
	 * @param id
	 * @return
	 */
	public Movie queryMovieById(Integer id);

	/**
	 * Query a movie by the url.<BR>
	 *
	 * @param pageUrl
	 * @return
	 */
	public Movie queryMovieByPageUrl(String pageUrl);

	/**
	 * Query movie list by the uni id.<BR>
	 *
	 * @param uniId
	 * @return
	 */
	public List<Movie> queryMovieByUniId(String uniId);

	/**
	 * Query movie list by the site and pagination.<BR>
	 *
	 * @param site
	 * @param pagination
	 * @return
	 */
	public List<Movie> queryMoviesBySite(String site, Pagination pagination);

	/**
	 * Query movie list by the channel and pagination.<BR>
	 *
	 * @param channel
	 * @param pagination
	 * @return
	 */
	public List<Movie> queryMoviesByChannel(String channel, Pagination pagination);

	/**
	 * Query movie list by the condition and pagination.<BR>
	 *
	 * @param movieExample
	 * @param pagination
	 * @return
	 */
	public List<Movie> queryMovies(MovieExample movieExample, Pagination pagination);

	/**
	 * Delete specified movies .<BR>
	 *
	 * @param movies
	 */
	public void deleteMovies(Collection<Movie> movies);

	/**
	 * Insert a movie if the movie is new.<BR>
	 * Or update a movie.<BR>
	 *
	 * @param movie
	 */
	public void insertOrUpdateMovie(Movie movie);

	/**
	 * Insert a movie if a movie is new.<BR>
	 * Or update a movie.<BR>
	 *
	 * @param movies
	 */
	public void insertOrUpdateMovies(Collection<Movie> movies);

	/**
	 * Query a visit history for the specified site and channel.<BR>
	 *
	 * @param siteName
	 * @param channel
	 * @return
	 */
	public VisitHistory queryVisitHistory(String siteName, String channel);

	/**
	 * Query all visit histories
	 *
	 * @return
	 */
	public List<VisitHistory> queryAllVisitHistories();

	/**
	 * Query all visit histories for the specified site.<BR>
	 *
	 * @param siteName
	 * @return
	 */
	public List<VisitHistory> queryVisitHistories(String siteName);

	/**
	 * Insert a visit history if it is new.<BR>
	 * Or update a visit history.<BR>
	 *
	 * @param visitHistory
	 */
	public void insertOrUpdateVisitHistory(VisitHistory visitHistory);

	/**
	 * Insert a visit history if it is new.<BR>
	 * Or update a visit history.<BR>
	 *
	 * @param visitHistories
	 */
	public void insertOrUpdateVisitHistories(Collection<VisitHistory> visitHistories);

	/**
	 * Delete specified visit histories.<BR>
	 *
	 * @param visitHistories
	 */
	public void deleteVisitHistories(Collection<VisitHistory> visitHistories);

	/**
	 * 
	 * @return
	 */
	public List<String> queryAllActors();

	/**
	 * Remove all relevant movies and visit histories.<BR>
	 * 
	 * @param siteName
	 */
	public void clearSite(String siteName);

}
