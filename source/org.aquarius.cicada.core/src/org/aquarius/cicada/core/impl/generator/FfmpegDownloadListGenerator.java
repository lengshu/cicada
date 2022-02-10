/**
 * #protocol
 */
package org.aquarius.cicada.core.impl.generator;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractSingleDownloadListGenerator;
import org.aquarius.downloader.core.DownloadManager;

/**
 * Use youtube-dl to download generator
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FfmpegDownloadListGenerator extends AbstractSingleDownloadListGenerator {

	/**
	 *
	 */
	public static final String ID = "ffmpeg";

	/**
	 *
	 */
	public FfmpegDownloadListGenerator() {
		super();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return ID;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected String doGenerate(Movie movie, DownloadInfo downloadInfo, Link link, String folderPrefix) {

		String ffmpegCommand = DownloadManager.getInstance().getConfiguration().getFfmpegDownloadCommand();
		String fileName = downloadInfo.getMp4File();

		if (StringUtils.isNotBlank(folderPrefix)) {
			fileName = folderPrefix + File.separator + fileName;
		}

		String realCmd = MessageFormat.format(ffmpegCommand, getDownloadUrl(link), fileName);
		return realCmd;
	}

}
