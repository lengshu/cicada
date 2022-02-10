/**
 *
 */
package org.aquarius.cicada.workbench.listener;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.helper.MovieDownloadAnalyserHelper;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.core.spi.AbstractProgressListener;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloaderFactory;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.slf4j.Logger;

/**
 * Some movies use dynamic urls for downloading.<BR>
 * This listener is used for monitor whether the dynamic urls is out of
 * time.<BR>
 * If true, new dynamic urls needed to gained again.<BR>
 *
 * After downloading finished,it will update the state to finished.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DynamicUrlParserProgressListener extends AbstractProgressListener {

	private Logger logger = LogUtil.getLogger(getClass());

	/**
	 *
	 */
	public DynamicUrlParserProgressListener() {
		// Nothing todo
		this.setPriority(Highest);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onFinish(DownloadTask... downloadTasks) {
		for (DownloadTask downloadTask : downloadTasks) {

			if (!StringUtils.equalsIgnoreCase(RuntimeManager.ID, downloadTask.getSource())) {
				continue;
			}

			Movie movie = findMovie(downloadTask);

			if (null != movie) {
				movie.setState(Movie.StateFinished);
				RuntimeManager.getInstance().getStoreService().insertOrUpdateMovie(movie);
			}
		}
	}

	/**
	 * 
	 * @param downloadTask
	 * @return
	 */
	private Movie findMovie(DownloadTask downloadTask) {
		String movieIdValue = MovieUtil.extractIdFromDownloadTag(downloadTask.getTagId());
		int movieId = NumberUtils.toInt(movieIdValue, 0);
		Movie movie = RuntimeManager.getInstance().getStoreService().queryMovieById(movieId);

		if (null != movie) {
			String siteName = movie.getSite();
			Site site = RuntimeManager.getInstance().loadSite(siteName);

			return MovieUtil.findMovie(site.getMovieList(), movieId);
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onBefore(DownloadTask... downloadTasks) {

		for (DownloadTask downloadTask : downloadTasks) {

			if (!StringUtils.equalsIgnoreCase(RuntimeManager.ID, downloadTask.getSource())) {
				continue;
			}

			if (!downloadTask.isDynamic()) {
				continue;
			}

			if (downloadTask.isOutOfValidTime()) {

				Movie movie = this.findMovie(downloadTask);

				MovieParserHelper.parseMovieDetailInfo(true, movie, DefaultProcessMonitor.createInstance());

				List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();

				MovieDownloadAnalyserHelper.analyseDownloadUrls(downloadInfoList, false, DefaultProcessMonitor.createInstance());

				for (int i = 0; i < downloadInfoList.size(); i++) {

					DownloadInfo downloadInfo = downloadInfoList.get(i);
					String tagId = MovieUtil.getDownloadTag(movie, downloadInfo, i);

					if (StringUtils.equalsIgnoreCase(tagId, downloadTask.getTagId())) {

						update(movie, downloadTask, downloadInfo, i);
					}
				}
			}
		}

		super.onBefore(downloadTasks);
	}

	/**
	 * @param movie
	 * @param downloadTask
	 * @param downloadInfo
	 */
	public static DownloadTask update(Movie movie, DownloadTask downloadTask, DownloadInfo downloadInfo, int index) {

		Link link = MovieUtil.find(downloadInfo, false);

		if (null == link) {
			String title = MessageFormat.format(Messages.DownloadMovieJob_MessageError, movie.getTitle());
			String message = MovieUtil.getLinkErrorMessages(downloadInfo.getDownloadLinks());

			TooltipUtil.showErrorTip(title, message);
			movie.setState(Movie.StateError);
			return null;
		}

		DownloadConfiguration downloadConfiguration = DownloadManager.getInstance().getConfiguration();

		if (null == downloadTask) {
			downloadTask = new DownloadTask();

			String folder = downloadConfiguration.getDefaultDownloadFolder();
			String fileName = downloadInfo.getMp4File();

			String tagId = MovieUtil.getDownloadTag(movie, downloadInfo, index);

			downloadTask.setCategory(DownloadTask.CategoryVideo);
			downloadTask.setFileName(fileName);
			downloadTask.setFolder(folder);
			downloadTask.setPageUrl(movie.getPageUrl());
			downloadTask.setTagId(tagId);

			downloadTask.setSource(RuntimeManager.ID);
			downloadTask.setDynamic(true);

			if (link.isHls()) {
				downloadTask.setType(AbstractSegmentDownloaderFactory.TypeHls);
			}

			if (StringUtils.isEmpty(downloadInfo.getTitle())) {
				downloadTask.setTitle(fileName);
			} else {
				downloadTask.setTitle(downloadInfo.getTitle());
			}
		}

		downloadTask.setDownloadUrl(MovieUtil.getDownloadUrl(link));
		downloadTask.setRefererUrl(link.getRefererUrl());
		downloadTask.mergeRequestHeaders(link.getRequestHeaders());

		downloadTask.setLastValidTime(DateUtils.addMinutes(new Date(), MovieConfiguration.DefaultOutTimeInMinutes));

		return downloadTask;
	}

}
