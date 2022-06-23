/**
 * 
 */
package org.aquarius.cicada.workbench.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class ExternalAnalyserJob extends AbstractCancelableJob {

	private List<Movie> selectedMovieList;

	private AbstractUrlRedirector urlRedirector;

	/**
	 * @param name
	 * @param selectedMovieList
	 * @param template
	 */
	public ExternalAnalyserJob(String name, List<Movie> selectedMovieList, AbstractUrlRedirector urlRedirector) {
		super(name);
		this.selectedMovieList = selectedMovieList;
		this.urlRedirector = urlRedirector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);

		if (this.urlRedirector.isUsePrimitiveUrl()) {

			for (Movie movie : this.selectedMovieList) {
				this.urlRedirector.redirect(movie.getPageUrl());
			}

			return Status.OK_STATUS;

		} else {
			return openAnalysedUrls(monitor, processMonitor);
		}

	}

	/**
	 * @param monitor
	 * @param processMonitor
	 */
	private IStatus openAnalysedUrls(IProgressMonitor monitor, IProcessMonitor processMonitor) {
		for (Movie movie : this.selectedMovieList) {

			String pageUrl = movie.getPageUrl();
			SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfigByUrl(pageUrl);

			if ((null != siteConfig) && (siteConfig.isUseSourceUrl())) {
				this.urlRedirector.redirect(pageUrl);
				continue;
			}

			MovieParserHelper.parseMovieDetailInfo(true, movie, processMonitor);

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();
			List<String> urlList = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(downloadInfoList)) {
				for (DownloadInfo downloadInfo : downloadInfoList) {

					List<Link> linkList = downloadInfo.getDownloadLinks();

					if (CollectionUtils.isNotEmpty(linkList)) {
						for (Link link : linkList) {
							urlList.add(link.getSourceUrl());
						}
					}
				}
			}

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			if (CollectionUtils.isNotEmpty(urlList)) {

				String[] urlStrings = new String[urlList.size()];
				urlList.toArray(urlStrings);

				this.urlRedirector.redirect(urlStrings);
			}
		}

		return Status.OK_STATUS;
	}

}
