/**
 *
 */
package org.aquarius.downloader.core.impl.hls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.core.model.Segment;
import org.aquarius.downloader.core.spi.AbstractProgressListener;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloader;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.net.HttpUtil;
import org.aquarius.util.net.UrlUtil;
import org.aquarius.util.security.SecretKey;
import org.aquarius.util.security.SecurityUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class HlsSegmentDownloader extends AbstractSegmentDownloader {

	private static final int ThreadCountPerTask = 3;

	private static final int MaxRetryCount = 200;

	/**
	 * @param downloadTask
	 */
	public HlsSegmentDownloader(DownloadTask downloadTask) {
		super(downloadTask);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void finishMovie(DownloadTask downloadTask) {

		super.finishMovie(downloadTask);

		DownloadConfiguration configuration = DownloadManager.getInstance().getConfiguration();

		if (!configuration.isMergeHls()) {
			return;
		}

		try {

			String ffmpeg = configuration.getFfmepgFile();
			String targetFile = downloadTask.getFile() + ".mp4";

			String mergeFile = downloadTask.getFile() + File.separator + "merge.list";

			if (StringUtils.isBlank(ffmpeg)) {
				return;
			}

			String FfmpegCommand = DownloadManager.getInstance().getConfiguration().getFfmpegMergeCommand();
			String ffmpegCommand = MessageFormat.format(FfmpegCommand, mergeFile, targetFile);

			String exitCommand = "exit";

			String clearCommandTemplate = DownloadManager.getInstance().getConfiguration().getClearCommand();
			String clearCommand = MessageFormat.format(clearCommandTemplate, downloadTask.getFile());

			File ffmpegCommandFile;
			File clearCommandFile;
			if (SystemUtils.IS_OS_WINDOWS) {
				ffmpegCommandFile = new File(downloadTask.getFile() + File.separator + "ffmpeg.bat");
				clearCommandFile = new File(downloadTask.getFile() + File.separator + "clear.bat");
			} else {
				ffmpegCommandFile = new File(downloadTask.getFile() + File.separator + "ffmpeg.sh");
				clearCommandFile = new File(downloadTask.getFile() + File.separator + "clear.sh");
			}

			try {
				FileUtils.write(clearCommandFile, clearCommand, StringUtil.CODEING_UTF8);
				FileUtils.write(ffmpegCommandFile, ffmpegCommand, StringUtil.CODEING_UTF8);
			} catch (Exception e) {
				// Nothing to do
			}

			if (configuration.isDeleteMergedHls()) {
				SystemUtil.executeCommandByFile(ffmpegCommand, clearCommand, exitCommand);
			} else {
				SystemUtil.executeCommandByFile(ffmpegCommand, exitCommand);
			}

		} catch (Exception e) {
			super.logger.error("merge file ", e);
		}
	}

	/**
	 * {@inheritDoc}}
	 *
	 * @throws IOException
	 */
	@Override
	protected void doStartDownload(DownloadTask downloadTask, Segment segment, AbstractProgressListener listener) throws IOException {

		String fileName = downloadTask.getFile().getAbsolutePath() + File.separator + segment.getFileName();
		File segmentFile = new File(fileName);

		String shortUrlName = segment.getShortUrlName();
		String urlString;
		if (StringUtils.startsWith(shortUrlName, "http")) {
			urlString = shortUrlName;
		} else {
			String basePath = UrlUtil.getBasePath(downloadTask.getDownloadUrl());
			urlString = basePath + shortUrlName;
		}

		URL url = new URL(urlString);
		URLConnection urlConnection = null;
		InputStream inputStream = null;
		OutputStream fileOutputStream = null;
		long fileLength = 0;

		try {

			urlConnection = url.openConnection();

			urlConnection.setConnectTimeout(DefaultTimeOut);

			urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			// urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
			urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			urlConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

			if (StringUtils.isNotBlank(downloadTask.getRefererUrl())) {
				urlConnection.setRequestProperty(HttpUtil.Referer, downloadTask.getRefererUrl());
			}

			inputStream = urlConnection.getInputStream();
			fileOutputStream = new FileOutputStream(segmentFile);

			if (StringUtils.isBlank(downloadTask.getSecretKeyString())) {
				IOUtils.copy(inputStream, fileOutputStream);
			} else {

				try {
					SecretKey secretKey = downloadTask.getSecretKey();

					byte[] bytes = IOUtils.toByteArray(inputStream);
					byte[] newBytes = SecurityUtil.decryptByAES(bytes, secretKey.key, secretKey.iv);
					IOUtils.write(newBytes, fileOutputStream);

					fileLength = bytes.length;

				} catch (Exception e) {
					this.logger.error("decrypt data ", e);
					throw new RuntimeException(e);
				}
			}
		} finally {
			IOUtils.closeQuietly(fileOutputStream);
			IOUtils.closeQuietly(inputStream);
			IOUtils.close(urlConnection);

		}

		// For hls ,the segment count is marked as the length of the file.
		downloadTask.addDownloadedLength(fileLength, 1);
		segment.addDownloadedLength(this, 1);

		segment.setFinished(this, true);

		listener.onUpdate(downloadTask);

	}

	@Override
	public int getSuggestThreadCountPerTask() {
		return ThreadCountPerTask;
	}

	/**
	 * @return
	 */
	@Override
	protected long getWaitingTime() {
		return 500;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getSuggestMaxRetryCount() {
		return MaxRetryCount;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public AbstractSegmentDownloader fork() {
		return new HlsSegmentDownloader(getDownloadTask());
	}
}
