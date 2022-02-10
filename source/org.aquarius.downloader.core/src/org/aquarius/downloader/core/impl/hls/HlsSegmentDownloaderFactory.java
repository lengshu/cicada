/**
 *
 */
package org.aquarius.downloader.core.impl.hls;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.model.Segment;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloader;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloaderFactory;
import org.aquarius.log.LogUtil;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.HexUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.collection.CollectionUtil;
import org.aquarius.util.exception.DecoderException;
import org.aquarius.util.exception.ExceptionUtil;
import org.aquarius.util.net.HttpUtil;
import org.aquarius.util.net.UrlUtil;
import org.aquarius.util.security.SecretKey;
import org.slf4j.Logger;

/**
 * The factory for hls downloader.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HlsSegmentDownloaderFactory extends AbstractSegmentDownloaderFactory {

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 *
	 */
	public HlsSegmentDownloaderFactory() {
		super();

		this.setPriority(Higher);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected int getSuggestedThreadCount() {
		return super.getSuggestedThreadCount();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isAcceptable(DownloadTask downloadTask) {

		String urlString = downloadTask.getDownloadUrl();

		return HttpUtil.isHls(urlString);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean doCheck(DownloadTask downloadTask) {

		File folder = checkFolder(downloadTask);

		HlsModel hlsModel = null;
		try {
			hlsModel = this.parse(downloadTask.getDownloadUrl());
		} catch (IOException e) {
			this.logger.error("doChecl", e);

			return false;
		}

		List<Segment> segmentList = new ArrayList<Segment>();
		List<TsFile> tsFileList = hlsModel.getTsFileList();
		List<String> mergeStringList = new ArrayList<String>();

		for (int i = 0; i < tsFileList.size(); i++) {

			TsFile tsFile = tsFileList.get(i);
			Segment segment = new Segment();

			String fileName = FilenameUtils.getName(tsFile.getFileName());

			segment.setFileName(fileName);
			segment.setShortUrlName(tsFile.getShortUrlName());

			segment.setStart(i);
			segment.setEnd(i + 1);

			segmentList.add(segment);

			String filePath = folder.getAbsolutePath() + File.separator + fileName;
			mergeStringList.add("file '" + filePath + "'");
		}

		File m3u8File = new File(folder.getAbsolutePath() + File.separator + "index.m3u8");
		File mergeFile = new File(folder.getAbsolutePath() + File.separator + "merge.list");

		try {
			FileUtils.write(m3u8File, hlsModel.getContent(), "UTF-8");
			FileUtils.writeLines(mergeFile, StringUtil.CODEING_UTF8, mergeStringList);
		} catch (IOException e) {
			this.logger.error("Writing files ", e);
			downloadTask.setException(e);
			return false;
		}

		downloadTask.setSecretKeyString(hlsModel.getSecretKeyString());

		downloadTask.setSegmentList(segmentList);
		downloadTask.setState(DownloadTask.StateWaiting);

		// For hls download task, make the segment size as the file length.
		downloadTask.setRemoteFileLength(tsFileList.size());

		return true;

	}

	/**
	 * Compute the secrect key.<BR>
	 *
	 * @param downloadTask
	 * @return
	 * @throws DecoderException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private SecretKey computeSecrectKey(DownloadTask downloadTask) throws DecoderException, MalformedURLException, IOException {

		String secretKeyString = downloadTask.getSecretKeyString();
		String baseUrl = downloadTask.getDownloadUrl();

		SecretKey secretKey = computeSecrectKey(secretKeyString, baseUrl);

		return secretKey;
	}

	/**
	 * get security key from HLS file
	 *
	 * @param secretKeyString
	 * @param baseUrl
	 * @return
	 * @throws DecoderException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static SecretKey computeSecrectKey(String secretKeyString, String baseUrl) throws DecoderException, IOException, MalformedURLException {
		String line = StringUtils.substringAfter(secretKeyString, "#EXT-X-KEY:");

		String basePath = UrlUtil.getBasePath(baseUrl);
		SecretKey secretKey = new SecretKey();

		String[] nameValues = StringUtils.split(line, ",");

		for (String nameValue : nameValues) {
			String name = StringUtils.substringBefore(nameValue, "=");
			String value = StringUtils.substringAfter(nameValue, "=");

			if (StringUtils.equalsIgnoreCase("METHOD", name)) {
				secretKey.algorithm = value;
			}

			if (StringUtils.equalsIgnoreCase("IV", name)) {
				if (StringUtils.startsWithIgnoreCase(value, "0x")) {
					value = value.substring(2);
				}
				secretKey.iv = HexUtil.decodeHex(value.toCharArray());
			}

			if (StringUtils.equalsIgnoreCase("URI", name)) {

				if (value.startsWith("\"")) {
					value = value.substring(1);
				}

				if (value.endsWith("\"")) {
					value = value.substring(0, value.length() - 1);
				}

				String uriString = basePath + value;

				secretKey.key = IOUtils.toByteArray(new URL(uriString));
			}
		}

		secretKey.check();
		return secretKey;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected AbstractSegmentDownloader doCreateDownloader(DownloadTask downloadTask) {

		checkFolder(downloadTask);

		Exception exception = null;

		if (StringUtils.isNotBlank(downloadTask.getSecretKeyString())) {
			for (int i = 0; i < 10; i++) {
				try {
					SecretKey secretKey = this.computeSecrectKey(downloadTask);
					downloadTask.setSecretKey(secretKey);
					exception = null;
					break;
				} catch (Exception e) {
					this.logger.error("Writing files ", e);
					exception = e;
					SystemUtil.sleepQuietly(5 * SystemUtil.TimeSecond);
				}
			}

			ExceptionUtil.throwRuntimeException(exception);
		}

		return new HlsSegmentDownloader(downloadTask);
	}

	/**
	 * Check the folder exists or not.<BR>
	 * If not exist,a new folder will be created.<BR>
	 *
	 * @param downloadTask
	 */
	private File checkFolder(DownloadTask downloadTask) {

		String newFileName = downloadTask.getFileName();

		if (newFileName.endsWith(".mp4")) {
			newFileName = FilenameUtils.removeExtension(newFileName);
			downloadTask.setFileName(newFileName);
		}

		File folder = downloadTask.getFile();
		if (!folder.exists()) {
			folder.mkdirs();
		}

		AssertUtil.assertTrue(folder.isDirectory(), "The location should be directory.");

		return folder;
	}

	/**
	 * Parse the m3u8 file and save the model to HlsModel.<BR>
	 *
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	private HlsModel parse(String urlString) throws IOException {

		String content = HttpUtil.doGet(urlString, null);

		if (StringUtils.contains(content, "#EXT-X-STREAM-INF:")) {

			String[] lines = StringUtils.split(content, "\r\n");
			Map<String, String> urls = new HashMap<>();

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (StringUtils.startsWithIgnoreCase(line, "#EXT-X-STREAM-INF:")) {

					Map<String, String> map = CollectionUtil.splitToMap(StringUtils.substringAfter(line, "#EXT-X-STREAM-INF:"));
					for (Map.Entry<String, String> entry : map.entrySet()) {
						if ("RESOLUTION".equalsIgnoreCase(entry.getKey())) {
							String pixel = StringUtils.substringAfter(entry.getValue(), "x");
							urls.put(pixel.toUpperCase(), lines[i++]);
						}
					}
				}
			}

			List<String> pixelList = DownloadManager.getInstance().getConfiguration().getPixels();

			for (String pixel : pixelList) {
				if (urls.containsKey(pixel.toUpperCase())) {
					String url = urls.get(pixel.toUpperCase());
					content = HttpUtil.doGet(urlString, null);

					return doParseModel(url, content);
				}
			}
		}

		return doParseModel(urlString, content);
	}

	/**
	 * Parse the url to HlsModel<BR>
	 *
	 * @param urlString
	 * @param content
	 * @return
	 */
	private HlsModel doParseModel(String urlString, String content) {

		AssertUtil.assertTrue(StringUtils.startsWithIgnoreCase(content, "#EXTM3U"), "The resource should be m3u8");

		String[] lines = StringUtils.split(content, "\r\n");

		List<TsFile> tsFileList = new ArrayList<TsFile>();

		int count = 0;
		int fileCount = 0;

		DecimalFormat format = new DecimalFormat("00000");

		String secretKey = null;

		while (count < lines.length) {
			String line = lines[count];
			count++;

			if (StringUtils.startsWithIgnoreCase(line, "#EXT-X-KEY:")) {
				secretKey = line;
				continue;
			}

			if (StringUtils.startsWithIgnoreCase(line, "##EXT-X-MAP:")) {
				String shortUrlName = StringUtils.substringAfter(line, "URI=");

				if (StringUtils.contains(shortUrlName, ",")) {
					shortUrlName = StringUtils.substringBefore(line, ",");
				}

				shortUrlName = StringUtils.remove(shortUrlName, "\"");

				fileCount++;
				String fileName = format.format(fileCount);

				TsFile tsFile = new TsFile(shortUrlName, fileName);
				tsFileList.add(tsFile);

				continue;
			}

			if (StringUtils.startsWithIgnoreCase(line, "#EXTINF:")) {
				String shortUrlName = lines[count];
				count++;

				fileCount++;
				String fileName = format.format(fileCount) + ".ts";

				TsFile tsFile = new TsFile(shortUrlName, fileName);
				tsFileList.add(tsFile);
			}
		}

		HlsModel hlsModel = new HlsModel(tsFileList, content, secretKey);

		return hlsModel;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getType() {
		return TypeHls;
	}
}
