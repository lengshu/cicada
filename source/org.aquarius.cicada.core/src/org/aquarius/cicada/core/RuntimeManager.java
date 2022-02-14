/**
 *
 */
package org.aquarius.cicada.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.config.ServiceFilterConfiguration;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.service.IMovieStoreService;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.cicada.core.spi.AbstractDownloadUrlAnalyser;
import org.aquarius.cicada.core.spi.AbstractMovieInfoProcssor;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.core.template.TemplateStore;
import org.aquarius.service.IHttpCacheService;
import org.aquarius.service.INameService;
import org.aquarius.service.manager.ServiceManager;
import org.aquarius.service.manager.impl.ConfigurationServiceFilter;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.nls.InternalNlsResource;
import org.aquarius.util.nls.NlsResource;

/**
 * The entry class to support all service.
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class RuntimeManager {

	public static final String ID = "org.aquarius.cicada.core";

	private static final RuntimeManager instance = new RuntimeManager();

	private ServiceManager<AbstractMovieParser> movieParserManager = new ServiceManager<>();

	private ServiceManager<AbstractDownloadUrlAnalyser> downloadUrlAnalyserManagers = new ServiceManager<>();

	private ServiceManager<AbstractDownloadListGenerator> downloadListGeneratorManager = new ServiceManager<>();

	private ServiceManager<AbstractUrlRedirector> urlRedirectorManager = new ServiceManager<>();

	private ServiceManager<AbstractMovieInfoProcssor> movieInfoProcessorManager = new ServiceManager<>();

	private IMovieStoreService storeService;

	private TemplateStore templateStore;

	private Map<String, Site> siteCache = new HashMap<String, Site>();

	private MovieConfiguration configuration;

	private ServiceFilterConfiguration serviceFilterConfiguration;

	private NlsResource nlsResource;

	private IHttpCacheService cacheService;

	/**
	 *
	 */
	private RuntimeManager() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("org.aquarius.cicada.core.nls/lang");
		this.nlsResource = new InternalNlsResource(resourceBundle);
	}

	public void init() {
		AssertUtil.assertNotNull(this.configuration, "Please set movie configuration before init.");
		AssertUtil.assertNotNull(this.serviceFilterConfiguration, "Please set service filter configuration before init.");

		// this.movieParserManager = new
		// ServiceManager<>(this.serviceFilterConfiguration.createConfigurationServiceFilter(AbstractMovieParser.class.getName()));

		this.downloadUrlAnalyserManagers = new ServiceManager<>(
				this.serviceFilterConfiguration.createConfigurationServiceFilter(AbstractDownloadUrlAnalyser.class.getName(), false));

		this.downloadListGeneratorManager = new ServiceManager<>(
				this.serviceFilterConfiguration.createConfigurationServiceFilter(AbstractDownloadListGenerator.class.getName(), false));

		this.urlRedirectorManager = new ServiceManager<>(
				this.serviceFilterConfiguration.createConfigurationServiceFilter(AbstractUrlRedirector.class.getName(), false));

		this.movieInfoProcessorManager = new ServiceManager<>(
				this.serviceFilterConfiguration.createConfigurationServiceFilter(AbstractMovieInfoProcssor.class.getName(), true));

	}

	/**
	 * 
	 * @param urlString
	 * @return
	 */
	public boolean isFilteredAnalyserSite(String urlString) {
		List<String> analyserSiteFilters = this.configuration.getAnalyserSiteFilters();
		if (CollectionUtils.isEmpty(analyserSiteFilters)) {
			return false;
		}

		for (String analyserSiteFilter : analyserSiteFilters) {

			Pattern pattern = Pattern.compile(analyserSiteFilter, Pattern.CASE_INSENSITIVE);

			Matcher matcher = pattern.matcher(urlString);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return the instance
	 */
	public static RuntimeManager getInstance() {
		return instance;
	}

	/**
	 * @return the resourceBundle
	 */
	public NlsResource getNlsResource() {
		return this.nlsResource;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isDebug() {
		String value = System.getProperty("aquarius.debug");

		return BooleanUtils.toBoolean(value);
	}

	/**
	 * Load site with data.
	 *
	 * @param siteName
	 * @return
	 */
	public synchronized Site loadSite(String siteName) {

		Site site = this.siteCache.get(siteName);

		if (null == site) {
			AbstractMovieParser movieParser = findMovieParserBySite(siteName);
			AssertUtil.assertNotNull(movieParser);

			List<Movie> movieList = this.storeService.queryMoviesBySite(movieParser.getName(), null);

			site = new Site(movieParser, movieList);

			this.siteCache.put(siteName, site);
		}

		return site;

	}

	/**
	 * Rebuild all site.<BR>
	 * The actors/tags/categories will be rebuild.<BR>
	 */
	public synchronized void rebuildAllSite() {
		for (Entry<String, Site> entry : this.siteCache.entrySet()) {
			entry.getValue().build();
		}
	}

	/**
	 * Rebuild the specified site.<BR>
	 * The actors/tags/categories will be rebuild.<BR>
	 *
	 * @param siteName
	 */
	public synchronized void rebuildSite(String siteName) {
		Site site = this.siteCache.get(siteName);
		if (null != site) {
			site.build();
		}
	}

	/**
	 * @return the storeService
	 */
	public IMovieStoreService getStoreService() {
		return this.storeService;
	}

	/**
	 * Set the store service.
	 *
	 * @param movieService
	 */
	public void setMovieStoreService(IMovieStoreService movieService) {

		AssertUtil.assertNotNull(movieService);

		this.storeService = movieService;
	}

	/**
	 * 
	 * @param serviceManager
	 * @param defautEnableState
	 * @param services
	 */
	private void checkFilter(ServiceManager<?> serviceManager, boolean defautEnableState, INameService... services) {

		ConfigurationServiceFilter<INameService<?>> filter = (ConfigurationServiceFilter<INameService<?>>) serviceManager.getServiceFilter();
		Map<String, Boolean> map = filter.getMap();

		for (INameService service : services) {
			if (!map.containsKey(service.getName())) {
				map.put(service.getName(), defautEnableState);
			}
		}

	}

	/**
	 * Register a new movie parser to parse urlPattern.
	 *
	 * @param movieParser
	 */
	public void registerMovieParsers(AbstractMovieParser... movieParsers) {
		this.movieParserManager.registerServices(movieParsers);

		// this.checkFilter(this.movieParserManager, movieParsers);
	}

	/**
	 * Unregister a new movie parser to parse urlPattern.
	 *
	 * @param movieParser
	 */
	public void unregisterMovieParsers(AbstractMovieParser... movieParsers) {
		this.movieParserManager.unregisterServices(movieParsers);
	}

	/**
	 * Register a new url redirector to parse url.
	 *
	 * @param movieParser
	 */
	public void registerUrlRedirectors(AbstractUrlRedirector... urlRedirectors) {
		this.urlRedirectorManager.registerServices(urlRedirectors);

		this.checkFilter(this.urlRedirectorManager, false, urlRedirectors);
	}

	/**
	 * Register a new movie tag processor to give a new name to a movie.
	 *
	 * @param movieParser
	 */
	public void registerMovieProcessors(AbstractMovieInfoProcssor... movieTagProcessors) {
		this.movieInfoProcessorManager.registerServices(movieTagProcessors);

		this.checkFilter(this.movieInfoProcessorManager, true, movieTagProcessors);
	}

	/**
	 * Get all site names.<BR>
	 *
	 * @return
	 */
	public Collection<String> getAllSiteNames() {
		Set<String> names = new TreeSet<String>();

		for (AbstractMovieParser movieParser : this.movieParserManager.getAllServices()) {
			names.add(movieParser.getName());
		}

		return new TreeSet<String>(names);
	}

	/**
	 * Find a movie parser to parse the specified urlPattern.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public AbstractMovieParser findMovieParserByUrl(String urlString) {

		for (AbstractMovieParser movieParser : this.movieParserManager.getAllServices()) {
			if (movieParser.isAcceptable(urlString)) {
				return movieParser;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param urlString
	 * @return
	 */
	public boolean isSupport(String urlString) {
		AbstractMovieParser movieParser = this.findMovieParserByUrl(urlString);
		return null != movieParser;
	}

	/**
	 * Find a movie parser to parse the specified urlPattern.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public AbstractMovieParser findMovieParserBySite(String siteName) {
		return this.movieParserManager.findService(siteName);
	}

	/**
	 * Get the sites which allow to import movies.
	 *
	 * @return
	 */
	public Set<String> getSupportImportSiteNames() {
		Set<String> names = new TreeSet<String>();

		for (AbstractMovieParser movieParser : this.movieParserManager.getAllServices()) {
			if (movieParser.isSupportImport() && (CollectionUtils.isNotEmpty(movieParser.getChannels()))) {
				names.add(movieParser.getName());
			}
		}

		return names;
	}

	/**
	 * Register a new movie parser to parse urlPattern.
	 *
	 * @param downloadUrlAnalyserManagers
	 */
	public void registerDownloadUrlAnalyser(AbstractDownloadUrlAnalyser... downloadUrlAnalysers) {
		this.downloadUrlAnalyserManagers.registerServices(downloadUrlAnalysers);

		this.checkFilter(this.downloadUrlAnalyserManagers, false, downloadUrlAnalysers);
	}

	/**
	 * Find a movie parser to parse the specified urlPattern.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public AbstractDownloadUrlAnalyser findDownloadUrlAnalyserr(String urlString) {
		for (AbstractDownloadUrlAnalyser downloadUrlAnalyser : this.downloadUrlAnalyserManagers.getAllServices()) {
			if (downloadUrlAnalyser.isAcceptable(urlString)) {
				return downloadUrlAnalyser;
			}
		}

		return null;
	}

	/**
	 * Find a specified download list generator.
	 *
	 * @param name
	 * @return
	 */
	public AbstractDownloadListGenerator findDownloadListGenerator(String name) {
		return this.downloadListGeneratorManager.findService(name);
	}

	/**
	 * Find a specified movie tag processor by a url.
	 *
	 * @param name
	 * @return
	 */
	public AbstractMovieInfoProcssor findMovieTagProcessorByUrl(String urlString) {
		for (AbstractMovieInfoProcssor movieInfoProcssor : this.movieInfoProcessorManager.getAllServices()) {
			if (movieInfoProcssor.isAcceptable(urlString)) {
				return movieInfoProcssor;
			}
		}

		return null;
	}

	/**
	 * Register a new download list generator .
	 *
	 * @param downloadListGeneratorManager
	 */
	public void registerDownloadListGenerator(AbstractDownloadListGenerator... generators) {
		this.downloadListGeneratorManager.registerServices(generators);

		this.checkFilter(this.downloadListGeneratorManager, false, generators);
	}

	/**
	 * Return all the download list generator names.
	 *
	 * @return
	 */
	public List<String> getAllDownloadListGeneratorNames() {
		return this.downloadListGeneratorManager.getAllNames();
	}

	/**
	 * Return all the download downloadListGeneratorManager.<BR>
	 *
	 * @return
	 */
	public List<AbstractDownloadListGenerator> getAllDownloadListGenerators() {
		return this.downloadListGeneratorManager.getAllServices();
	}

	/**
	 * Return all movie parsers.
	 *
	 * @return
	 */
	public List<AbstractMovieParser> getAllMovieParsers() {
		return this.movieParserManager.getAllServices();
	}

	/**
	 * Return all url redirectors.
	 *
	 * @return
	 */
	public List<AbstractUrlRedirector> getAllUrlRedirectors() {
		return this.urlRedirectorManager.getAllServices();
	}

	/**
	 * @return the configuration
	 */
	public MovieConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(MovieConfiguration configuration) {
		if (null != configuration) {
			this.configuration = configuration;
		}
	}

	/**
	 * @return the serviceFilterConfiguration
	 */
	public ServiceFilterConfiguration getServiceFilterConfiguration() {
		return this.serviceFilterConfiguration;
	}

	/**
	 * @param serviceFilterConfiguration the serviceFilterConfiguration to set
	 */
	public void setServiceFilterConfiguration(ServiceFilterConfiguration serviceFilterConfiguration) {
		if (null != serviceFilterConfiguration) {
			this.serviceFilterConfiguration = serviceFilterConfiguration;
		}
	}

	/**
	 * @return the templateStore
	 */
	public TemplateStore getTemplateStore() {
		return this.templateStore;
	}

	/**
	 * @param templateStore the templateStore to set
	 */
	public void setTemplateStore(TemplateStore templateStore) {
		AssertUtil.assertNotNull(templateStore);
		this.templateStore = templateStore;
	}

	/**
	 * @return the cacheService
	 */
	public IHttpCacheService getCacheService() {
		return this.cacheService;
	}

	/**
	 * @param cacheService the cacheService to set
	 */
	public void setCacheService(IHttpCacheService cacheService) {
		this.cacheService = cacheService;
	}

}
