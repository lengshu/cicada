package org.aquarius.cicada.workbench;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.config.ServiceFilterConfiguration;
import org.aquarius.cicada.core.impl.generator.Aria2DownloadListGenerator;
import org.aquarius.cicada.core.impl.generator.DefaultDownloadListGenerator;
import org.aquarius.cicada.core.impl.generator.FdmDownloadListGenerator;
import org.aquarius.cicada.core.impl.generator.FfmpegDownloadListGenerator;
import org.aquarius.cicada.core.impl.generator.N_M3U8DownloadListGenerator;
import org.aquarius.cicada.core.impl.generator.eagle.EagleDownloadListGenerator;
import org.aquarius.cicada.core.service.impl.HttpCacheServiceImpl;
import org.aquarius.cicada.core.spi.web.IWebAccessorService;
import org.aquarius.cicada.core.web.WebAccessorManager;
import org.aquarius.cicada.workbench.internal.BrowserFunctionInitializer;
import org.aquarius.cicada.workbench.job.AutoRefreshJob;
import org.aquarius.cicada.workbench.job.InitCookieJob;
import org.aquarius.cicada.workbench.job.UpdateConfigJob;
import org.aquarius.cicada.workbench.page.NetworkConfiguration;
import org.aquarius.cicada.workbench.page.WorkbenchConfiguration;
import org.aquarius.cicada.workbench.spi.ExtensionLoader;
import org.aquarius.cicada.workbench.web.SwtWebAccessorService;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.service.EclipsePropertyStoreService;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.io.FileUtil;
import org.aquarius.util.io.SkipExistFileFilter;
import org.aquarius.util.net.HttpsUrlValidator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * The activator class controls the plug-in life cycle
 */
public class WorkbenchActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.aquarius.cicada.workbench"; //$NON-NLS-1$

	public static final String HELP_PLUGIN_ID = "org.aquarius.cicada.help"; //$NON-NLS-1$

	private static final String CORE_PLUGIN_ID = "org.aquarius.cicada.core"; //$NON-NLS-1$

	// private static final String LOG_PLUGIN_ID = "org.eclipse.ui.views.log";
	// //$NON-NLS-1$

	private static final String RESOURCE_VERSION = "Resource.Version"; //$NON-NLS-1$

	// private static final String DeployedMarker = "Deployed.Marker"; //$NON-NLS-1$

	private static final String LoaderExtensionId = "org.aquarius.cicada.loader"; //$NON-NLS-1$

	private static final String CleanPeriod = "CleanPeriod"; //$NON-NLS-1$

	// The shared instance
	private static WorkbenchActivator plugin;

	private WorkbenchConfiguration configuration;

	private NetworkConfiguration networkConfiguration;

	private Logger logger = LogUtil.getLogger(getClass());

	private boolean compactDatabase = false;

	/**
	 * Check the resource version.<BR>
	 * 
	 * @return if the return value is <CODE>false</CODE>, app will check remote
	 *         update info.<BR>
	 */
	private boolean checkResourceVersion() {
		IPreferenceStore store = this.getPreferenceStore();

		String oldVersionString = store.getString(RESOURCE_VERSION);

		if (StringUtils.isEmpty(oldVersionString)) {
			return false;
		}

		Version currentVersion = Version.parseVersion(ResourceVersionConstant.CurrentVersion);
		Version oldVersion = Version.parseVersion(oldVersionString);

		return (currentVersion.compareTo(oldVersion) < 0);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		loadWorkbenchConfiguration();
		loadNetworkConfiguration();

		File workingFile = Platform.getLocation().toFile().getParentFile();
		String workingFolderPath = workingFile.getAbsolutePath() + File.separator + "workspace"; //$NON-NLS-1$

		File workingFolder = new File(workingFolderPath);
		if (!workingFolder.exists()) {
			workingFolder.mkdirs();
		} else {
			if (workingFolder.isFile()) {
				workingFolder.delete();
				workingFolder.mkdirs();
			}
		}

		/**
		 * When the application first run.<BR>
		 * Init the application.<BR>
		 */
		deployResources(workingFolderPath, false);

		initSecurity();

		HttpCacheServiceImpl cacheService = new HttpCacheServiceImpl();
		RuntimeManager.getInstance().setCacheService(cacheService);

		loadRuntimeConfiguration(workingFolderPath);
		loadDownloadConfiguration(workingFolderPath);
		loadUpdateConfiguration();

		List<String> pixelList = RuntimeManager.getInstance().getConfiguration().getPixelList();
		DownloadManager.getInstance().getConfiguration().setPixels(pixelList);

		// Load extension from plugin extension

		initDownloadGenerators();

		initExtensionLoaders();

		Starter.getInstance().start(workingFolderPath);

		updateWebAccessorService();

		startJobs();

	}

	/**
	 * 
	 */
	private void updateWebAccessorService() {

		IWebAccessorService defaultWebAccessorService = WebAccessorManager.getInstance().getDefaultWebAccessorService();

		if (null == defaultWebAccessorService) {

			IWebAccessorService swtWebAccessorService = new SwtWebAccessorService();
			swtWebAccessorService.start(new BrowserFunctionInitializer());

			WebAccessorManager.getInstance().setDefaultWebAccessorService(swtWebAccessorService);
		}
	}

	/**
	 * 
	 */
	private void loadUpdateConfiguration() {

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		store.setDefault(UpdateConfigJob.KeyUpdateInterval, 7);

	}

	/**
	 * 
	 */
	private void loadWorkbenchConfiguration() {

		this.configuration = new WorkbenchConfiguration();
		TooltipUtil.setLevel(this.configuration.getTooltipLevel());

	}

	/**
	 * @param store
	 */
	private void loadNetworkConfiguration() {
		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		this.networkConfiguration = new NetworkConfiguration(new EclipsePropertyStoreService(store));
	}

	/**
	 * 
	 * @param workingFolderPath
	 * @param overwrite
	 * @throws IOException
	 */
	public void deployResources(String workingFolderPath, boolean overwrite) throws IOException {
		IPreferenceStore store = this.getPreferenceStore();

		File destDir = new File(workingFolderPath);
		File configDir = new File(workingFolderPath + File.separator + "config");

		if (configDir.exists() && overwrite) {

			try {
				configDir.delete();
			} catch (Exception e) {
				this.logger.error("deployResources", e);
			}
		}

		if (this.checkResourceVersion() && configDir.exists()) {
			return;
		}

		{
			Bundle bundle = Platform.getBundle(CORE_PLUGIN_ID);
			{
				URL resourceUrl = bundle.getEntry("resources");
				URL fileUrl = FileLocator.toFileURL(resourceUrl);

				File sourceFolder = new File(fileUrl.getFile());
				if (sourceFolder.exists() && sourceFolder.isDirectory()) {
					FileUtils.copyDirectory(sourceFolder, destDir, new SkipExistFileFilter(sourceFolder, destDir));
				}
			}

			{
				URL resourceUrl = bundle.getEntry("settings");
				URL fileUrl = FileLocator.toFileURL(resourceUrl);

				IPath location = Platform.getLocation();
				File sourceFolder = new File(fileUrl.getFile());
				if (sourceFolder.exists() && sourceFolder.isDirectory()) {
					File settingsFolder = location.toFile();
					FileUtils.copyDirectory(sourceFolder, settingsFolder, new SkipExistFileFilter(sourceFolder, settingsFolder));
				}
			}

			store.setValue(RESOURCE_VERSION, ResourceVersionConstant.CurrentVersion);
		}
	}

	/**
	 *
	 */
	private void initExtensionLoaders() {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(LoaderExtensionId);

		for (IConfigurationElement configurationElement : configurationElements) {

			try {
				Object object = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
				if (object instanceof ExtensionLoader) {
					ExtensionLoader extensionLoader = (ExtensionLoader) object;
					extensionLoader.load();
				}
			} catch (Exception e) {
				this.logger.error("initExtensionLoaders", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 *
	 */
	private void initSecurity() {
		try {
			HttpsUrlValidator.trustAllHttpsCertificates();
		} catch (Exception e) {
			this.logger.error("initSecurity", e);
		}
	}

	/**
	 *
	 */
	private void startJobs() {
		MovieConfiguration movieConfiguration = RuntimeManager.getInstance().getConfiguration();
		if (movieConfiguration.isAutoRefreshMovie() || movieConfiguration.isAutoRefreshSite()) {

			String value = System.getProperty("aquarius.disableRefresh");
			boolean disableRefresh = BooleanUtils.toBoolean(value);

			if (!disableRefresh) {
				new AutoRefreshJob(Messages.WorkbenchActivator_AutoRefresh).schedule(5000);
			}
		}

		new InitCookieJob(Messages.WorkbenchActivator_InitCookie).schedule(5000);

		new UpdateConfigJob(Messages.ApplicationActionBarAdvisor_UpdateConfig).schedule(5000);
	}

	/**
	 * Load configuration from eclipse preference.
	 *
	 * @param workingFolderPath
	 */
	private void loadRuntimeConfiguration(String workingFolderPath) {

		IPreferenceStore store = this.getPreferenceStore();
		EclipsePropertyStoreService storeService = new EclipsePropertyStoreService(store);

		ServiceFilterConfiguration serviceFilterConfiguration = new ServiceFilterConfiguration(storeService);
		RuntimeManager.getInstance().setServiceFilterConfiguration(serviceFilterConfiguration);

		MovieConfiguration movieConfiguration = new MovieConfiguration(storeService);
		RuntimeManager.getInstance().setConfiguration(movieConfiguration);

		try {
			File filteredAnalyserJsonFile = new File(Starter.getInstance().getAnalyserPath() + "filter.json");
			if (filteredAnalyserJsonFile.exists()) {

				String jsonContent = FileUtils.readFileToString(filteredAnalyserJsonFile, StringUtil.CODEING_UTF8);

				List<String> filterSites = JSON.parseArray(jsonContent, String.class);

				if (null != filterSites) {
					movieConfiguration.setAnalyserSiteFilters(filterSites);
				}
			}
		} catch (Exception e) {
			// Nothing to do
		}

		RuntimeManager.getInstance().init();

	}

	/**
	 * Load configuration from eclipse preference.
	 *
	 * @param workingFolderPath
	 */
	private void loadDownloadConfiguration(String workingFolderPath) {

		IPreferenceStore preferenceStore = DownloadActivator.getDefault().getPreferenceStore();
		DownloadConfiguration downloadConfiguration = new DownloadConfiguration(new EclipsePropertyStoreService(preferenceStore));

		String downloadFoldr = workingFolderPath + File.separator + "download"; //$NON-NLS-1$
		downloadConfiguration.setDefaultDownloadFolder(downloadFoldr);
		preferenceStore.setDefault(DownloadConfiguration.Key_DefaultDownloadFolder, downloadFoldr);

		downloadConfiguration.setPixels(RuntimeManager.getInstance().getConfiguration().getPixelList());
		downloadConfiguration.rebuildSiteConcurrent();
		DownloadManager.getInstance().setDownloadConfiguration(downloadConfiguration);

	}

	/**
	 * @throws CoreException
	 *
	 */
	private void initDownloadGenerators() throws CoreException {

		RuntimeManager.getInstance().registerDownloadListGenerator(new FdmDownloadListGenerator());
		RuntimeManager.getInstance().registerDownloadListGenerator(new Aria2DownloadListGenerator());
		RuntimeManager.getInstance().registerDownloadListGenerator(new DefaultDownloadListGenerator());
		RuntimeManager.getInstance().registerDownloadListGenerator(new N_M3U8DownloadListGenerator());
		RuntimeManager.getInstance().registerDownloadListGenerator(new EagleDownloadListGenerator());
		RuntimeManager.getInstance().registerDownloadListGenerator(new FfmpegDownloadListGenerator());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;

		IPreferenceStore preferenceStore = getPreferenceStore();

		boolean compact = this.compactDatabase;

		if (!compact) {
			long lastCleanPeriod = preferenceStore.getLong(CleanPeriod);
			if (0 == lastCleanPeriod) {
				preferenceStore.setValue(CleanPeriod, System.currentTimeMillis());
			} else {
				int cleanPeriod = RuntimeManager.getInstance().getConfiguration().getCleanPeriod();
				if ((System.currentTimeMillis() - lastCleanPeriod) > (SystemUtil.TimeDay * cleanPeriod)) {
					compact = true;
					preferenceStore.setValue(CleanPeriod, System.currentTimeMillis());
				}
			}
		}

		RuntimeManager.getInstance().getStoreService().close(compact);

		File logFile = Platform.getLogFileLocation().toFile();
		if (FileUtil.isValid(logFile) && (logFile.length() > (400 * SystemUtil.DiskSizeInK))) {
			FileUtil.forceDeleteFileQuietly(logFile);
		}

		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static WorkbenchActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * @return the configuration
	 */
	public WorkbenchConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * @return the networkConfiguration
	 */
	public NetworkConfiguration getNetworkConfiguration() {
		return this.networkConfiguration;
	}

	/**
	 * Compact database when application end.<BR>
	 */
	public void setCompactDatabase() {
		this.compactDatabase = true;
	}

	/**
	 * The application use chromium or not.<BR>
	 * 
	 * @return
	 */
	public boolean isUseEdge() {
		try {
			return SwtUtil.isSupportEdge() && this.configuration.isUseEdge();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getBrowserStyle() {
		if (this.isUseEdge()) {
			return SwtUtil.EDGE;
		} else {
			return SWT.NONE;
		}
	}
}
