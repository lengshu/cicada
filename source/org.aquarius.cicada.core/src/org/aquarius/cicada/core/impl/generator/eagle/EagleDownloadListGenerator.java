/**
 *
 */
package org.aquarius.cicada.core.impl.generator.eagle;

import java.util.List;

import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;

import com.alibaba.fastjson.JSON;

/**
 * Eagle download list generator.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class EagleDownloadListGenerator extends AbstractDownloadListGenerator {

	/**
	 *
	 */
	public static final String ID = "Eagle";

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
	public String generateDownloadList(List<Movie> movieList, String folderPrefix) {

		EagleModel eagleModel = new EagleModel();

		for (Movie movie : movieList) {
			List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();
			EagleTask task = new EagleTask();

			for (DownloadInfo downloadInfo : downloadInfoList) {

				List<Link> links = downloadInfo.getDownloadLinks();

				for (Link link : links) {

					if (link.isSelected()) {

						task.setReferer(link.getRefererUrl());
						task.setFname(downloadInfo.getMp4File());
						task.setUrl(getDownloadUrl(link));

						eagleModel.getTasks().add(task);

						break;
					}
				}
			}
		}

		return JSON.toJSONString(eagleModel);
	}

}
