/**
 * #protocol
 */
package org.aquarius.cicada.core.impl.generator;

import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractSingleDownloadListGenerator;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class N_M3U8DownloadListGenerator extends AbstractSingleDownloadListGenerator {

	/**
	 *
	 */
	public static final String ID = "N_m3u8DL";

	public static final String Pattern = "N_m3u8DL-CLI.exe \"{0}\" --saveName \"{1}\" --enableDelAfterDone ";

	/*
	 * (non-Javadoc)
	 *
	 * @see org.aquarius.porn.core.spi.AbstractDownloadListGenerator#getName()
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

		String realCmd = MessageFormat.format(Pattern, getDownloadUrl(link), FilenameUtils.removeExtension(downloadInfo.getMp4File()));

		if (StringUtils.isNotBlank(folderPrefix)) {
			realCmd = realCmd + MessageFormat.format("  --workDir \"{0}\"  ", folderPrefix);
		}

		return realCmd;
	}

}
