/**
 * #protocol
 */
package org.aquarius.cicada.core.impl.generator;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractSingleDownloadListGenerator;
import org.aquarius.util.net.HttpUtil;

/**
 * Generate download list for aria2.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class Aria2DownloadListGenerator extends AbstractSingleDownloadListGenerator {

	/**
	 *
	 */
	public static final String ID = "Aria2";

	/**
	 *
	 */
	public Aria2DownloadListGenerator() {
		super();
	}

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
	protected String doGenerate(Movie movie, DownloadInfo downloadInfo, Link link, String downloadFolder) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getDownloadUrl(link));

		if (StringUtils.isEmpty(downloadFolder)) {
			stringBuilder.append(" out=" + downloadInfo.getMp4File());
		} else {
			stringBuilder.append(" out=" + downloadFolder + File.separator + downloadInfo.getMp4File());
		}

		String refererUrl = link.getRefererUrl();
		if (null != link.getRequestHeaders()) {
			String newRefererUrl = link.getRequestHeaders().get(HttpUtil.Referer);
			if (StringUtils.isNotEmpty(newRefererUrl)) {
				refererUrl = newRefererUrl;
			}
		}

		if (StringUtils.isNotEmpty(refererUrl)) {
			stringBuilder.append(" --referer=" + refererUrl);
		}

		return stringBuilder.toString();
	}

}
