/**
 *
 */
package org.aquarius.cicada.core.spi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;

/**
 * the base class for download list generator
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSingleDownloadListGenerator extends AbstractDownloadListGenerator {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String generateDownloadList(List<Movie> movieList, String folderPrefix) {
		List<String> downloadList = new ArrayList<String>();

		for (Movie movie : movieList) {

			List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();

			for (DownloadInfo downloadInfo : downloadInfoList) {

				List<Link> links = downloadInfo.getDownloadLinks();

				for (Link link : links) {

					if (link.isSelected()) {
						String url = this.doGenerate(movie, downloadInfo, link, folderPrefix);
						if (StringUtils.isNotEmpty(url)) {
							downloadList.add(url);
							break;
						}
					}
				}
			}
		}

		return StringUtils.join(downloadList, "\r\n");
	}

	protected abstract String doGenerate(Movie movie, DownloadInfo downloadInfo, Link link, String folderPrefix);

}
