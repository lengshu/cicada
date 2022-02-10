/**
 *
 */
package org.aquarius.cicada.workbench;

import java.io.File;
import java.io.FileFilter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.impl.analyser.DefaultFvsDownloadUrlAnalyser;
import org.aquarius.cicada.core.impl.analyser.LocalJavaScriptDownloadUrlAnalyser;
import org.aquarius.cicada.core.impl.generator.LocalJavaScriptDownloadListGenerator;
import org.aquarius.cicada.core.impl.parser.ConfigBrowserMovieParser;
import org.aquarius.cicada.core.impl.processor.LocalJavaScriptMovieInfoProcssor;
import org.aquarius.cicada.core.impl.redirector.BrowserPostUrlRedirector;
import org.aquarius.cicada.core.impl.redirector.ConfigGetUrlRedirector;
import org.aquarius.cicada.core.service.impl.MovieStoreService;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.core.template.TemplateStore;
import org.aquarius.cicada.workbench.analyser.BrowserJavaScriptDownloadUrlAnalyser;
import org.aquarius.cicada.workbench.listener.DynamicUrlParserProgressListener;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.core.impl.JsonTaskStoreService;
import org.aquarius.downloader.core.spi.impl.DefaultDownloadTypeChecker;
import org.aquarius.log.LogUtil;
import org.aquarius.service.manager.ReloadManager;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.base.AbstractComparable;
import org.aquarius.util.io.FileUtil;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * Use starter instanceof workbench activator to help test case.
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class Starter {

	private static final Starter instance = new Starter();

	private final Logger log = LogUtil.getLogger(Starter.class);

	/**
	 * @return the instance
	 */
	public static Starter getInstance() {
		return instance;
	}

	private String workingFolder;

	/**
	 * @return the workingFolder
	 */
	public String getWorkingFolder() {
		return this.workingFolder;
	}

	/**
	 * @return the workingFolder
	 */
	public String getPersistFolder() {
		return this.workingFolder + File.separator + "persist" + File.separator;
	}

	/**
	 * use the working folder to load configuration and database.
	 *
	 * @param workingFolder the workingFolder to set
	 */
	public void start(String workingFolder) {
		AssertUtil.assertNotNull(workingFolder, "The file should not be null.");

		if (StringUtils.isNotBlank(this.workingFolder)) {
			throw new UnsupportedOperationException("already started ");
		}

		this.workingFolder = workingFolder;

		File file = new File(this.workingFolder);

		if (!file.exists()) {
			file.mkdirs();
		}

		try {
			initDatabase();
		} catch (SQLException e) {
			this.log.error("initDatabase", e);
		}

		loadDownloader();

		loadMovieParsers();

		loadBrowserScriptFolder();
		loadFvsSciptFolder();
		loadLocalSciptFolder();

		loadDownloadGenerators();
		loadMovieTagProcessors();

		loadUrlRedirectors();

		initTemplateStore();
	}

	/**
	 * 
	 */
	private void loadUrlRedirectors() {
		String configPath = this.getRedirectorPath() + File.separator + "config";
		this.loadUrlRedirectors(configPath);

		String browserPath = this.getRedirectorPath() + File.separator + "browser";
		this.loadUrlRedirectors(browserPath);
	}

	/**
	 * 
	 */
	private void loadUrlRedirectors(String folderPath) {

		{
			File folder = new File(folderPath);

			if (folder.exists()) {

				Collection<File> files = FileUtils.listFiles(folder, new String[] { "json" }, false);
				if (CollectionUtils.isNotEmpty(files)) {
					for (File jsonFile : files) {

						try {
							String json = FileUtils.readFileToString(jsonFile, StringUtil.CODEING_UTF8);
							Map<String, String> map = JSON.parseObject(json, ListOrderedMap.class);

							boolean usePrimitiveUrl = AbstractUrlRedirector.isUsePrimitive(jsonFile.getName());

							for (Entry<String, String> entry : map.entrySet()) {
								ConfigGetUrlRedirector urlRedirector = new ConfigGetUrlRedirector(entry.getKey(), entry.getValue(), usePrimitiveUrl);

								RuntimeManager.getInstance().registerUrlRedirectors(urlRedirector);
							}

						} catch (Exception e) {
							this.log.error("load url redirectors " + jsonFile.getAbsolutePath(), e);
						}
					}
				}
			}

		}

		{
			File folder = new File(folderPath);

			if (folder.exists()) {

				Collection<File> files = FileUtils.listFiles(folder, new String[] { "html" }, false);
				if (CollectionUtils.isNotEmpty(files)) {
					for (File htmlFile : files) {

						try {
							boolean usePrimitiveUrl = AbstractUrlRedirector.isUsePrimitive(htmlFile.getName());

							BrowserPostUrlRedirector urlRedirector = new BrowserPostUrlRedirector(htmlFile, usePrimitiveUrl);

							RuntimeManager.getInstance().registerUrlRedirectors(urlRedirector);
						} catch (Exception e) {
							this.log.error("load url redirectors " + htmlFile.getAbsolutePath(), e);
						}

					}
				}
			}

		}

	}

	/**
	 *
	 */
	private void loadDownloadGenerators() {
		String folderString = this.getDownloadGeneratorPath();
		File folder = new File(folderString);

		if (!folder.exists()) {
			return;
		}

		Collection<File> files = FileUtils.listFiles(folder, new String[] { "js" }, false);

		for (File file : files) {
			try {
				LocalJavaScriptDownloadListGenerator downloadListGenerator = new LocalJavaScriptDownloadListGenerator(file);
				RuntimeManager.getInstance().registerDownloadListGenerator(downloadListGenerator);
			} catch (Exception e) {
				this.log.error("load download generator with js file " + file.getAbsolutePath(), e);
			}
		}

	}

	/**
	 *
	 */
	private void loadMovieTagProcessors() {
		String folderString = this.getMovieProcessorPath();
		File folder = new File(folderString);

		if (!folder.exists()) {
			return;
		}

		Collection<File> files = FileUtils.listFiles(folder, new String[] { "js" }, false);

		for (File file : files) {
			try {
				LocalJavaScriptMovieInfoProcssor movieTagProcessor = new LocalJavaScriptMovieInfoProcssor(file);
				RuntimeManager.getInstance().registerMovieProcessors(movieTagProcessor);
			} catch (Exception e) {
				this.log.error("load movie tag processor with js file " + file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * create a template store and save it to runtime manager.
	 */
	private void initTemplateStore() {
		String templateFilePath = this.getWorkingFolder() + File.separator + "template.json";

		TemplateStore templateStore = new TemplateStore(templateFilePath);

		RuntimeManager.getInstance().setTemplateStore(templateStore);
	}

	/***
	 * init database
	 *
	 * @throws SQLException
	 */
	private void initDatabase() throws SQLException {

		String databasePath = this.getWorkingFolder() + File.separator + "database";
		File folder = new File(databasePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		MovieStoreService storeService = new MovieStoreService();
		storeService.loadDatabase(databasePath);

		RuntimeManager.getInstance().setMovieStoreService(storeService);
	}

	/**
	 * Init the download folder and task store file.
	 */
	private void loadDownloader() {

		DownloadConfiguration downloadConfiguration = DownloadManager.getInstance().getConfiguration();

		String defaultDownloadPath = this.workingFolder + File.separator + "download";
		String downloadPath = downloadConfiguration.getDefaultDownloadFolder();
		if (StringUtils.isEmpty(downloadPath)) {
			downloadPath = defaultDownloadPath;
		}

		File folder = new File(downloadPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		downloadConfiguration.setDefaultDownloadFolder(downloadPath);

		String storeFileName = this.getPersistFolder() + ("/download/store.json");
		DownloadManager.getInstance().setTaskStoreService(new JsonTaskStoreService(new File(storeFileName)));

		String typeMappingFileName = this.getPersistFolder() + ("/download/typeMapping.json");
		DefaultDownloadTypeChecker typeChecker = new DefaultDownloadTypeChecker(new File(typeMappingFileName));
		DownloadManager.getInstance().setDownloadTypeChecker(typeChecker);

		DownloadManager.getInstance().addListener(new DynamicUrlParserProgressListener());
		DownloadManager.getInstance().start(SystemUtil.TimeSecond * 30);

	}

	/**
	 * Load movie parsers by folder.
	 */
	private void loadMovieParsers() {

		List<ConfigBrowserMovieParser> parsers = ConfigBrowserMovieParser.loadFolders(getParserPath());

		for (ConfigBrowserMovieParser parser : parsers) {
			RuntimeManager.getInstance().registerMovieParsers(parser);
		}
	}

	/**
	 * Return the parser path.
	 *
	 * @return
	 */
	public String getDownloadGeneratorPath() {
		return getWorkingFolder() + FileUtil.getRealFilePath("/config/generator");
	}

	/**
	 * Return the parser path.
	 *
	 * @return
	 */
	public String getMovieProcessorPath() {
		return getWorkingFolder() + FileUtil.getRealFilePath("/config/processor");
	}

	/**
	 * Return the parser path.
	 *
	 * @return
	 */
	public String getParserPath() {
		return getWorkingFolder() + FileUtil.getRealFilePath("/config/parser");
	}

	/**
	 * Return the analyser path.
	 *
	 * @return
	 */
	public String getAnalyserPath() {
		return getWorkingFolder() + FileUtil.getRealFilePath("/config/analyser");
	}

	/**
	 * Return the redirector path.
	 *
	 * @return
	 */
	public String getRedirectorPath() {
		return getWorkingFolder() + FileUtil.getRealFilePath("/config/redirector");
	}

	/**
	 * Init script analysers which will be execute by browser.<BR>
	 */
	private void loadBrowserScriptFolder() {
		String browserScriptFolderString = getAnalyserPath() + File.separator + "browser";
		File browserScriptFolder = new File(browserScriptFolderString);

		if (!browserScriptFolder.exists()) {
			return;
		}

		FileFilter directoryFilter = DirectoryFileFilter.INSTANCE;
		File[] folders = browserScriptFolder.listFiles(directoryFilter);

		if (ArrayUtils.isNotEmpty(folders)) {
			for (File folder : folders) {
				loadBrowserScriptAnalyser(folder);
			}
		}
	}

	/**
	 * 
	 * @param scriptFolder
	 */
	private void loadBrowserScriptAnalyser(File scriptFolder) {
		int priority = AbstractComparable.getPriority(scriptFolder.getName());

		Collection<File> files = FileUtils.listFiles(scriptFolder, new String[] { "js" }, false);

		for (File file : files) {
			try {
				BrowserJavaScriptDownloadUrlAnalyser analyser = new BrowserJavaScriptDownloadUrlAnalyser(file);
				analyser.setPriority(priority);
				RuntimeManager.getInstance().registerDownloadUrlAnalyser(analyser);

				ReloadManager.getInstance().register(analyser);
			} catch (Exception e) {
				this.log.error("load browser analyzer width config file " + file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * 
	 */
	private void loadFvsSciptFolder() {
		String fvsScriptFolderString = getAnalyserPath() + File.separator + "fvs";
		File fvsScriptFolder = new File(fvsScriptFolderString);

		if (!fvsScriptFolder.exists()) {
			return;
		}

		FileFilter directoryFilter = DirectoryFileFilter.INSTANCE;
		File[] folders = fvsScriptFolder.listFiles(directoryFilter);

		if (ArrayUtils.isNotEmpty(folders)) {
			for (File folder : folders) {
				loadFvsSciptAnalysers(folder);
			}
		}
	}

	/**
	 * @param fvsFolder
	 * @param priority
	 */
	private void loadFvsSciptAnalysers(File fvsFolder) {

		int priority = AbstractComparable.getPriority(fvsFolder.getName());

		Collection<File> files = FileUtils.listFiles(fvsFolder, new String[] { "js" }, false);

		for (File file : files) {
			try {
				String name = FilenameUtils.getBaseName(file.getName());

				DefaultFvsDownloadUrlAnalyser analyser = new DefaultFvsDownloadUrlAnalyser(name);
				analyser.setPriority(priority);
				RuntimeManager.getInstance().registerDownloadUrlAnalyser(analyser);
			} catch (Exception e) {
				this.log.error("load fvs analyzer width config file " + file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * Load script analysers which will be execute by java script engine.<BR>
	 */
	private void loadLocalSciptFolder() {
		String localScriptFolderString = getAnalyserPath() + File.separator + "local";
		File localScriptFolder = new File(localScriptFolderString);

		if (!localScriptFolder.exists()) {
			return;
		}

		FileFilter directoryFilter = DirectoryFileFilter.INSTANCE;
		File[] folders = localScriptFolder.listFiles(directoryFilter);

		if (ArrayUtils.isNotEmpty(folders)) {
			for (File folder : folders) {
				loadLocalSciptAnalysers(folder);
			}
		}
	}

	/**
	 * @param scriptFolder
	 */
	private void loadLocalSciptAnalysers(File scriptFolder) {
		int priority = AbstractComparable.getPriority(scriptFolder.getName());

		Collection<File> files = FileUtils.listFiles(scriptFolder, new String[] { "js" }, false);

		for (File file : files) {
			try {
				LocalJavaScriptDownloadUrlAnalyser analyser = new LocalJavaScriptDownloadUrlAnalyser(file);
				analyser.setPriority(priority);
				RuntimeManager.getInstance().registerDownloadUrlAnalyser(analyser);

			} catch (Exception e) {
				this.log.error("load local script analyzer width config file " + file.getAbsolutePath(), e);
			}
		}
	}

}
