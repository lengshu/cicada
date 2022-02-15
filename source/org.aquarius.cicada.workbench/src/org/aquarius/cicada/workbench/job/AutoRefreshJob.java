/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.helper.filter.AutoRefreshMovieChannelFilter;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.model.VisitHistory;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.RuntimeConstant;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.SiteEditorInput;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.collection.CollectionUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 *
 * Auto refresh site and movies.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AutoRefreshJob extends AbstractCancelableJob {

	private MovieConfiguration movieConfiguration = RuntimeManager.getInstance().getConfiguration();

	private static final int MaxCount = 200;

	private int count = 0;

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * @param name
	 */
	public AutoRefreshJob(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		if (this.movieConfiguration.isAutoRefreshSite()) {

			try {
				refreshSites(monitor);
			} catch (Exception e) {
				this.logger.error("auto refresh sites ", e);
			}

		}

		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}

		if (this.movieConfiguration.isAutoRefreshMovie()) {
			return doRefreshMovie(monitor);
		} else {
			return Status.OK_STATUS;
		}
	}

	/**
	 * Refresh the sites.<BR>
	 * If update time is less than 1 day,the operation is not run.<BR>
	 *
	 * @param monitor
	 */
	private void refreshSites(IProgressMonitor monitor) {
		List<VisitHistory> visitHistories = RuntimeManager.getInstance().getStoreService().queryAllVisitHistories();
		Map<String, SiteConfig> siteConfigMap = new TreeMap<>();

		long currentTime = System.currentTimeMillis();

		for (VisitHistory visitHistory : visitHistories) {

			if ((visitHistory.getLastUpdate().getTime() + SystemUtil.TimeDay) < currentTime) {

				String siteName = visitHistory.getSiteName();
				SiteConfig siteConfig = siteConfigMap.get(siteName);

				if (null == siteConfig) {
					siteConfig = SiteConfigManager.getInstance().findSiteConfig(siteName);
					if (null == siteConfig) {
						continue;
					}

					if (!siteConfig.isEnable()) {
						continue;
					}
				}

				if (isChannelAutoRefresh(visitHistory, siteConfig)) {
					siteConfigMap.putIfAbsent(siteName, siteConfig);
				}
			}
		}

		for (SiteConfig siteConfig : siteConfigMap.values()) {

			if (monitor.isCanceled()) {
				return;
			}

			Site site = RuntimeManager.getInstance().loadSite(siteConfig.getSiteName());
			if (site.isRefreshing()) {
				break;
			}

			try {
				site.setRefreshing(this, true);
				doRefreshSite(site, monitor);
			} finally {
				site.setRefreshing(this, false);
			}

		}
	}

	private boolean isChannelAutoRefresh(VisitHistory visitHistory, SiteConfig siteConfig) {
		MovieChannel movieChannel = MovieUtil.findMovieChannel(siteConfig, visitHistory.getChannel());

		if (null == movieChannel) {
			return false;
		}

		return movieChannel.isAutoRefreshList();
	}

	/**
	 * execute refresh site operation.
	 *
	 * @param site
	 */
	private void doRefreshSite(Site site, IProgressMonitor monitor) {

		AbstractMovieParser movieParser = site.getMovieParser();
		if (!movieParser.hasAutoRefreshChannel()) {
			return;
		}

		int oldSize = site.getMovieSize();

		IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);

		MovieParserHelper.parseSite(site, processMonitor, AutoRefreshMovieChannelFilter.getInstance());

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				SiteEditorInput input = new SiteEditorInput(site);

				IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart eidtor = workbenchPage.findEditor(input);

				if (eidtor instanceof SiteMultiPageEditor) {
					((SiteMultiPageEditor) eidtor).refreshContent();
					workbenchPage.activate(eidtor);
				}

				int sizeInterval = site.getMovieSize() - oldSize;

				String title = Messages.AutoRefreshJob_RefreshSiteTitle;
				String message = Messages.AutoRefreshJob_RefreshSiteMessage;

				title = MessageFormat.format(title, site.getSiteName());
				message = MessageFormat.format(message, sizeInterval);

				TooltipUtil.showInfoTip(title, message);
			}
		});
	}

	/**
	 * execute refresh movie operations.
	 *
	 * @param monitor
	 * @return
	 */
	private IStatus doRefreshMovie(IProgressMonitor monitor) {

		try {

			IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);
			RuntimeManager runtimeManager = RuntimeManager.getInstance();

			monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

			Collection<String> siteNames = runtimeManager.getAllSiteNames();

			for (String siteName : siteNames) {

				SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfig(siteName);

				if ((null != siteConfig) && (!siteConfig.isEnable())) {
					continue;
				}

				Set<String> filteredChannelSet = new TreeSet<>();
				List<MovieChannel> movieChannelList = siteConfig.getMovieChannelList();

				for (MovieChannel movieChannel : movieChannelList) {
					if (!movieChannel.isAutoRefreshDetail()) {
						filteredChannelSet.add(movieChannel.getName());
					}
				}

				if (filteredChannelSet.size() == movieChannelList.size()) {
					continue;
				}

				Site site = RuntimeManager.getInstance().loadSite(siteName);

				List<Movie> sourceMovieList = site.getMovieList();
				if (sourceMovieList.isEmpty()) {
					continue;
				}

				List<Movie> unparsedMovieList = MovieUtil.findUnparsedMovies(sourceMovieList);

				while (!unparsedMovieList.isEmpty()) {

					this.count++;

					if (this.count > MaxCount) {
						this.count = 0;

						if (WorkbenchActivator.getDefault().getConfiguration().isAutoGc()) {
							System.gc();
						}
					}

					List<Movie> todoMovieList = CollectionUtil.removeList(unparsedMovieList, 0, RuntimeConstant.RefreshBatchCount);
					List<Movie> filteredMovieList = new ArrayList<Movie>();

					if (filteredChannelSet.isEmpty()) {
						filteredMovieList.addAll(todoMovieList);
					} else {
						for (Movie movie : todoMovieList) {
							if (!filteredChannelSet.contains(movie.getChannel())) {
								filteredMovieList.add(movie);
							}
						}
					}

					String errorMessage = MovieParserHelper.parseMovieDetailInfo(false, filteredMovieList, processMonitor);

					if (processMonitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					if (StringUtils.isNotBlank(errorMessage)) {
						TooltipUtil.showInfoTip(Messages.ErrorDialogTitle, errorMessage);
						return new Status(Status.ERROR, WorkbenchActivator.PLUGIN_ID, 1, errorMessage, null); // $NON-NLS-1$
					}

					RefreshMovieJob.doUpdateDetailedMovieList(todoMovieList);
				}
			}

		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}
}
