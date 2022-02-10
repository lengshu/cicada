/**
 * #protocol
 */
package org.aquarius.cicada.core.impl.generator;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.spi.AbstractSingleDownloadListGenerator;

/**
 * Just the simplest download generator.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DefaultDownloadListGenerator extends AbstractSingleDownloadListGenerator {

	/**
	 *
	 */
	public static final String ID = "default";

	/**
	 *
	 */
	public DefaultDownloadListGenerator() {
		super();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getTitle() {
		return RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.DownloadListGenerator_TitleDefault);
	}

	/**
	 *
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
	protected String doGenerate(Movie movie, DownloadInfo downloadInfo, Link link, String downloadFolder) {
		return getDownloadUrl(link);
	}

}
