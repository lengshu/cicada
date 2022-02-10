/**
 *
 */
package org.aquarius.cicada.core.spi;

import java.text.MessageFormat;
import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.service.INameService;
import org.aquarius.util.base.AbstractComparable;

/**
 * Base class to generate download list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractDownloadListGenerator extends AbstractComparable<AbstractDownloadListGenerator>
		implements INameService<AbstractDownloadListGenerator> {

	/**
	 *
	 * @param movieList
	 * @param downloadFolder
	 * @return
	 */
	public abstract String generateDownloadList(List<Movie> movieList, String downloadFolder);

	/**
	 * Return the title to display.<BR>
	 *
	 * @return
	 */
	public String getTitle() {
		String message = RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.DownloadListGenerator_Title);
		return MessageFormat.format(message, this.getName());
	}

	/**
	 *
	 * @param link
	 * @return
	 */
	protected String getDownloadUrl(Link link) {
		return MovieUtil.getDownloadUrl(link);
	}

	/**
	 * Need parse the download Url
	 *
	 * @return
	 */
	public boolean isUsePrimitiveUrl() {
		return false;
	}
}
