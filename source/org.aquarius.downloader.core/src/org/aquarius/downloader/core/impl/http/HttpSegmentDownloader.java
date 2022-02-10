/**
 *
 */
package org.aquarius.downloader.core.impl.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.SiteConcurrent;
import org.aquarius.downloader.core.model.Segment;
import org.aquarius.downloader.core.nls.DownloadNlsMessageConstant;
import org.aquarius.downloader.core.spi.AbstractProgressListener;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloader;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.net.HttpUtil;
import org.aquarius.util.net.UrlUtil;

/**
 * Http downloader which support multi segments.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HttpSegmentDownloader extends AbstractSegmentDownloader {

	private static final int ChunkSize = 64 * SystemUtil.DiskSizeInK;

	/**
	 * @param downloadTask
	 */
	public HttpSegmentDownloader(DownloadTask downloadTask) {
		super(downloadTask);
	}

	/**
	 * {@inheritDoc}}
	 *
	 * @throws IOException
	 */
	@Override
	protected void doStartDownload(DownloadTask downloadTask, Segment segment, AbstractProgressListener listener) throws IOException {

		HttpURLConnection httpConnection = null;
		InputStream input = null;
		RandomAccessFile accessFile = null;

		long absolutePosition = segment.getRealPosition();

		String downloadUrl = downloadTask.getDownloadUrl();

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			prepareHttpConnection(downloadTask, absolutePosition, segment.getEnd(), httpConnection);

			synchronized (downloadTask) {
				accessFile = prepareFile(downloadTask, listener);
			}

			input = httpConnection.getInputStream();

			if (internalDownload(downloadTask, segment, listener, httpConnection, input, accessFile)) {
				segment.setFinished(this, true);
			}

		} catch (IOException e) {
			if (StringUtils.contains(e.getLocalizedMessage(), "403")) {

				downloadTask.increaseRetryCount();

				if (downloadTask.getRejectErrorCount() > 10) {
					downloadTask.setMaxThreadCount(1);

					if (DownloadManager.getInstance().getConfiguration().isAutoAddSiteConstraint()) {

						SiteConcurrent siteConcurrent = DownloadManager.getInstance().getConfiguration().findSiteConcurrent(downloadUrl);
						if (null == siteConcurrent) {
							try {
								String domain = UrlUtil.getDomain(downloadUrl);
								siteConcurrent = new SiteConcurrent(domain, 1);
								DownloadManager.getInstance().getConfiguration().addSiteConcurrent(siteConcurrent);
							} catch (Exception otherException) {
								// Nothing to do
							}
						}

					}
				}

			}
			throw e;
		} finally {
			IOUtils.closeQuietly(accessFile);
			IOUtils.closeQuietly(input);
			IOUtils.close(httpConnection);
		}

	}

	/**
	 * do download operations.<BR>
	 *
	 * @param downloadTask
	 * @param segment
	 * @param listener
	 * @param httpConnection
	 * @param input
	 * @param accessFile
	 * @return
	 * @throws IOException
	 */
	private boolean internalDownload(DownloadTask downloadTask, Segment segment, AbstractProgressListener listener, HttpURLConnection httpConnection,
			InputStream input, RandomAccessFile accessFile) throws IOException {

		long currentPosition = 0;
		long absolutePosition = segment.getRealPosition();
		long contentLength = httpConnection.getContentLengthLong();

		String contentLengthString = httpConnection.getHeaderField("Content-Range");

		String fileLengthString = StringUtils.substringAfterLast(contentLengthString, "/");
		long fileLength = NumberUtils.toLong(fileLengthString, -1);

		if ((fileLength != -1) && (downloadTask.getRemoteFileLength() != fileLength)) {
			String errorMessage = DownloadManager.getInstance().getNlsResource()
					.getValue(DownloadNlsMessageConstant.HttpSegmentDownloader_FileLengthInconsistentError);
			errorMessage = MessageFormat.format(errorMessage, downloadTask.getTitle());

			downloadTask.setState(DownloadTask.StateError);
			downloadTask.setErrorMessage(errorMessage);

			this.logger.error(errorMessage);
			return false;
		}

		int bufferLength = -1;

		byte[] buffer = new byte[ChunkSize];
		ByteArrayOutputStream cache = new ByteArrayOutputStream();

		while (((bufferLength = input.read(buffer)) != -1)) {

			currentPosition = currentPosition + bufferLength;
			absolutePosition = absolutePosition + bufferLength;

			cache.write(buffer, 0, bufferLength);
			downloadTask.addDownloadedLength(bufferLength, bufferLength);

			listener.onUpdate(downloadTask);

			if (isPaused()) {
				doWriteContents(downloadTask, segment, listener, accessFile, cache, true);
				return false;
			} else {

				boolean finished = (absolutePosition >= segment.getEnd());
				finished = finished || (currentPosition >= contentLength);

				doWriteContents(downloadTask, segment, listener, accessFile, cache, finished);

				if (finished) {
					return true;
				}
			}
		}

		return true;
	}

	/**
	 * @param segment
	 * @param accessFile
	 * @param cache
	 * @param flushCache
	 * @throws IOException
	 */
	private void doWriteContents(DownloadTask downloadTask, Segment segment, AbstractProgressListener listener, RandomAccessFile accessFile,
			ByteArrayOutputStream cache, boolean flushCache) throws IOException {
		int cacheSize = cache.size();

		if (cacheSize >= DownloadManager.getInstance().getConfiguration().getCacheSize() || flushCache) {

			accessFile.seek(segment.getRealPosition());
			accessFile.write(cache.toByteArray());

			segment.addDownloadedLength(this, cacheSize);

			cache.reset();
		}
	}

	/**
	 * Prepare file.<BR>
	 * Use random access file for write contents to specified locations.<BR>
	 *
	 * @param downloadTask
	 * @param listener
	 * @return
	 * @throws IOException
	 */
	private synchronized RandomAccessFile prepareFile(DownloadTask downloadTask, AbstractProgressListener listener) throws IOException {
		RandomAccessFile accessFile;

		File file = downloadTask.getFile();

		if (file.exists()) {
			AssertUtil.assertTrue(file.isFile(), "The target '" + file.getAbsolutePath() + "' should be file.");
		} else {
			file.createNewFile();
		}

		accessFile = new RandomAccessFile(file.getAbsolutePath(), "rwd");

		long remoteFileLength = downloadTask.getRemoteFileLength();

		if (remoteFileLength == 0) {
			this.logger.error("FileLength == 0");
		}

		accessFile.setLength(remoteFileLength);

		return accessFile;
	}

	/**
	 * Prepare http connection like browser.<BR>
	 *
	 * @param startPos
	 * @param httpConnection
	 * @throws ProtocolException
	 */
	private void prepareHttpConnection(DownloadTask downloadTask, long startPos, long endPos, HttpURLConnection httpConnection) throws ProtocolException {
		httpConnection.setRequestMethod("GET");
		httpConnection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 Edge/16.16299");

		// httpConnection.setRequestProperty("Accept-Encoding", "identity");
		httpConnection.setConnectTimeout(5000);
		httpConnection.setReadTimeout(3000);
		httpConnection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);// startPosition=0
		httpConnection.setRequestProperty("Referer", downloadTask.getRefererUrl());

		HttpUtil.updateHttpConnectionProperty(httpConnection, downloadTask.getRequestHeaders());

		// Keep-Alive:timeout=5
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public AbstractSegmentDownloader fork() {
		return new HttpSegmentDownloader(this.getDownloadTask());
	}

}
