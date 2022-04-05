/**
 * #protocol
 */
package org.aquarius.cicada.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieExample;
import org.aquarius.cicada.core.model.MovieMapper;
import org.aquarius.cicada.core.model.Pagination;
import org.aquarius.cicada.core.model.VisitHistory;
import org.aquarius.cicada.core.model.VisitHistoryExample;
import org.aquarius.cicada.core.model.VisitHistoryMapper;
import org.aquarius.cicada.core.service.IMovieStoreService;
import org.aquarius.log.LogUtil;
import org.aquarius.util.NumberUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.collection.CollectionUtil;
import org.aquarius.util.jdbc.Executor;
import org.aquarius.util.jdbc.JDBCUtil;
import org.slf4j.Logger;

/**
 * Use mybatis and h2 database to store movies.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieStoreService implements IMovieStoreService {

	private PooledDataSource dataSource;

	private SqlSession sqlSession;

	private MovieMapper movieMapper;

	private VisitHistoryMapper visitHistoryMapper;

	private Logger logger = LogUtil.getLogger(getClass());

	/**
	 *
	 */
	public MovieStoreService() {
		super();

	}

	@Override
	public void close(boolean compact) {

		try {
			Statement statement = this.dataSource.getConnection().createStatement();
			if (compact) {
				statement.execute(" SHUTDOWN COMPACT ");
			}
			statement.close();
		} catch (Exception e) {
			this.logger.error("close", e);
		}

		this.dataSource.forceCloseAll();
		IOUtils.closeQuietly(this.sqlSession);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> queryMoviesBySite(String site, Pagination pagination) {
		MovieExample movieExample = new MovieExample();
		movieExample.createCriteria().andSiteEqualTo(site);
		movieExample.setOrderByClause(" ID DESC");

		updatePagination(movieExample, pagination);

		return this.movieMapper.selectByExample(movieExample);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> queryMoviesByChannel(String channel, Pagination pagination) {
		MovieExample movieExample = new MovieExample();
		movieExample.createCriteria().andChannelEqualTo(channel);
		movieExample.setOrderByClause(" ID DESC");

		updatePagination(movieExample, pagination);

		return this.movieMapper.selectByExample(movieExample);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void loadDatabase(String databaseFolder) throws SQLException {

		if (null != this.sqlSession) {
			this.close(false);
		}
		initSqlSession(databaseFolder);
	}

	/**
	 * load database and create sql sessions.
	 *
	 * @param databaseFolder
	 * @throws SQLException
	 */
	private void initSqlSession(String databaseFolder) throws SQLException {

		boolean nullDatabase = false;
		File folder = new File(databaseFolder);
		if (!folder.exists()) {
			folder.mkdirs();
			nullDatabase = true;
		}

		File mvDbFile = new File(databaseFolder + File.separator + "movie.db.mv.db");
		if (!mvDbFile.exists()) {
			nullDatabase = true;
		}

		String databaseUrl = null;
		if (RuntimeManager.isDebug()) {
			databaseUrl = MessageFormat.format("jdbc:h2:{0}/movie.db;AUTO_SERVER=true", databaseFolder);
		} else {
			databaseUrl = MessageFormat.format("jdbc:h2:{0}/movie.db", databaseFolder);
		}

		databaseUrl = FilenameUtils.normalize(databaseUrl);

		this.dataSource = new PooledDataSource("org.h2.Driver", databaseUrl, "king", "kingdom");
		this.dataSource.setDefaultAutoCommit(true);

		if (nullDatabase) {
			doInit(this.dataSource);
		}

		Environment environment = new Environment("dev", new JdbcTransactionFactory(), this.dataSource);
		Configuration configuration = new Configuration(environment);

		configuration.addMapper(MovieMapper.class);
		configuration.addMapper(VisitHistoryMapper.class);

		SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
		SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(configuration);

		this.sqlSession = sqlSessionFactory.openSession();

		this.movieMapper = this.sqlSession.getMapper(MovieMapper.class);
		this.visitHistoryMapper = this.sqlSession.getMapper(VisitHistoryMapper.class);

	}

	/**
	 * @param dataSource2
	 * @throws SQLException
	 */
	private void doInit(PooledDataSource currentDataSource) throws SQLException {

		try {
			URL url = MovieStoreService.class.getResource("init.ddl");
			String ddl = IOUtils.toString(url, StringUtil.CODEING_UTF8);
			JDBCUtil.execute(currentDataSource, new Executor<Statement>() {

				@Override
				public void execute(Connection connection, Statement statement) throws SQLException {
					statement.addBatch(ddl);
					statement.executeBatch();
				}
			});
		} catch (IOException e) {
			this.logger.error("init database ", e);
		}

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Movie queryMovieById(Integer id) {
		return this.movieMapper.selectByPrimaryKey(id);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> queryMovieByUniId(String uniId) {
		MovieExample movieExample = new MovieExample();
		movieExample.createCriteria().andUniIdEqualTo(uniId);

		return this.movieMapper.selectByExample(movieExample);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Movie queryMovieByPageUrl(String pageUrl) {
		MovieExample movieExample = new MovieExample();
		movieExample.createCriteria().andPageUrlEqualTo(pageUrl);

		List<Movie> movieList = this.movieMapper.selectByExample(movieExample);
		return CollectionUtil.findFirstElement(movieList);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> queryMovies(MovieExample movieExample, Pagination pagination) {

		updatePagination(movieExample, pagination);

		return this.movieMapper.selectByExample(movieExample);
	}

	/**
	 * @param movieExample
	 * @param pagination
	 */
	private void updatePagination(MovieExample movieExample, Pagination pagination) {
		if (null != pagination) {
			movieExample.setStart(pagination.getStart());
			movieExample.setCount(pagination.getCountPerPage());
		}
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void insertOrUpdateMovie(Movie movie) {

		this.doInsertOrUpdateMovie(movie);
		this.sqlSession.commit();
	}

	/**
	 *
	 * @param movie
	 */
	private void doInsertOrUpdateMovie(Movie movie) {

		if (NumberUtil.getIntValue(movie.getId()) == 0) {
			this.movieMapper.insert(movie);
		} else {
			this.movieMapper.updateByPrimaryKey(movie);
		}
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void insertOrUpdateMovies(Collection<Movie> movies) {

		if (CollectionUtils.isEmpty(movies)) {
			return;
		}

		for (Movie movie : movies) {
			this.doInsertOrUpdateMovie(movie);
		}

		this.sqlSession.commit();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deleteMovies(Collection<Movie> movies) {
		if (CollectionUtils.isEmpty(movies)) {
			return;
		}

		for (Movie movie : movies) {
			this.movieMapper.deleteByPrimaryKey(movie.getId());
		}

		this.sqlSession.commit();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public VisitHistory queryVisitHistory(String siteName, String channel) {
		VisitHistoryExample visitHistoryExample = new VisitHistoryExample();
		visitHistoryExample.createCriteria().andSiteNameEqualTo(siteName).andChannelEqualTo(channel);

		List<VisitHistory> historyList = this.visitHistoryMapper.selectByExample(visitHistoryExample);
		return CollectionUtil.findFirstElement(historyList);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<VisitHistory> queryVisitHistories(String siteName) {

		VisitHistoryExample visitHistoryExample = new VisitHistoryExample();
		visitHistoryExample.createCriteria().andSiteNameEqualTo(siteName);

		return this.visitHistoryMapper.selectByExample(visitHistoryExample);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void insertOrUpdateVisitHistory(VisitHistory visitHistory) {
		this.doInsertOrUpdateVisitHistory(visitHistory);
		this.sqlSession.commit();
	}

	/**
	 *
	 * @param visitHistory
	 */
	private void doInsertOrUpdateVisitHistory(VisitHistory visitHistory) {
		if (NumberUtil.getIntValue(visitHistory.getId()) == 0) {
			this.visitHistoryMapper.insert(visitHistory);
		} else {
			this.visitHistoryMapper.updateByPrimaryKey(visitHistory);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void insertOrUpdateVisitHistories(Collection<VisitHistory> visitHistories) {

		if (CollectionUtils.isEmpty(visitHistories)) {
			return;
		}

		for (VisitHistory visitHistory : visitHistories) {
			this.doInsertOrUpdateVisitHistory(visitHistory);
		}

		this.sqlSession.commit();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deleteVisitHistories(Collection<VisitHistory> visitHistories) {
		if (CollectionUtils.isEmpty(visitHistories)) {
			return;
		}

		for (VisitHistory visitHistory : visitHistories) {
			this.visitHistoryMapper.deleteByPrimaryKey(visitHistory.getId());
		}

		this.sqlSession.commit();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<VisitHistory> queryAllVisitHistories() {
		VisitHistoryExample visitHistoryExample = new VisitHistoryExample();

		return this.visitHistoryMapper.selectByExample(visitHistoryExample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> queryAllActors() {

		List<String> actorList = this.sqlSession.selectList("selectActors");
		List<String> resultList = new ArrayList<>();

		for (String actor : actorList) {
			if (StringUtils.length(actor) >= 2) {

				actor = actor.trim();

				if (!resultList.contains(actor)) {
					resultList.add(actor);
				}
			}
		}

		return resultList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSite(String siteName) {
		{
			MovieExample deleteByExample = new MovieExample();
			deleteByExample.createCriteria().andSiteEqualTo(siteName);

			this.movieMapper.deleteByExample(deleteByExample);
		}

		{
			VisitHistoryExample deleteByExample = new VisitHistoryExample();
			deleteByExample.createCriteria().andSiteNameEqualTo(siteName);

			this.visitHistoryMapper.deleteByExample(deleteByExample);
		}

	}

}
