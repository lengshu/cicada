/**
 *
 */
package org.aquarius.downloader.core.impl.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.model.Segment;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloader;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloaderFactory;
import org.aquarius.downloader.core.util.NetUtil;

/**
 * The http downloader factory.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HttpSegmentDownloaderFactory extends AbstractSegmentDownloaderFactory {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isAcceptable(DownloadTask downloadTask) {
		return StringUtils.startsWithAny(downloadTask.getDownloadUrl(), new String[] { "http://", "https://" });
	}

	/**
	 * A lot of files has multi download source.<BR>
	 * So one url will be choosed to download.
	 *
	 * @param downloadTask
	 * @return
	 */
	private String checkUrl(DownloadTask downloadTask) {

		long totalLength = downloadTask.getRemoteFileLength();

		File folder = new File(downloadTask.getFolder());
		File downloadFile = downloadTask.getFile();

		if (!downloadFile.exists()) {

			if (downloadTask.getRemoteFileLength() > folder.getFreeSpace()) {
				downloadTask.setErrorMessage("DiskSpaceIsLessThanTargetFile");
				return null;
			}
		}

		if (StringUtils.isBlank(downloadTask.getFileName()) || totalLength == 0) {

			String url = downloadTask.getDownloadUrl();

			try {
				NetUtil.fetchHttpInfo(downloadTask, url);

				if (downloadTask.getRemoteFileLength() > 0) {
					return url;
				}

			} catch (IOException e) {
				super.logger.error("fill download task info ", e);
				downloadTask.setException(e);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doCheck(DownloadTask downloadTask) {

		String url = checkUrl(downloadTask);

		if (StringUtils.isEmpty(url)) {
			return false;
		}

		downloadTask.setDownloadUrl(url);

		long totalLength = downloadTask.getRemoteFileLength();

		totalLength = downloadTask.getRemoteFileLength();
		String targetFile = downloadTask.getFile().getAbsolutePath();

		List<Segment> segmentList = new ArrayList<Segment>();

		if (totalLength <= 0) {
			// Just a segment
			Segment segment = createSegment(targetFile, url, 0, 0);
			segmentList.add(segment);

		} else {
			long segmentSize = DownloadManager.getInstance().getConfiguration().getSegmentSize();

			long count = totalLength / segmentSize;
			long reminder = totalLength % segmentSize;

			for (long i = 0; i < count; i++) {
				Segment segment = createSegment(targetFile, url, (i * segmentSize), (i + 1) * segmentSize);
				segmentList.add(segment);
			}

			if (reminder > 0) {
				Segment segment = createSegment(targetFile, url, (count * segmentSize), totalLength);
				segmentList.add(segment);
			}
		}

		downloadTask.setSegmentList(segmentList);

		return true;
	}

	/**
	 *
	 * create a segment for specified start and end.
	 *
	 * @param targetFile
	 * @param url
	 * @param chunkSize
	 * @param i
	 * @return
	 */
	private Segment createSegment(String targetFile, String url, long startPosition, long endPostion) {
		Segment segment = new Segment();

		segment.setStart(startPosition);
		segment.setEnd(endPostion);

		return segment;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected AbstractSegmentDownloader doCreateDownloader(DownloadTask downloadTask) {

		// Try to split the http file into multi segment by specified size.

		return new HttpSegmentDownloader(downloadTask);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getType() {
		return TypeHttp;
	}

}
