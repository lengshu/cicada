/**
 *
 */
package org.aquarius.cicada.core.helper;

import java.util.Arrays;
import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.util.AssertUtil;

/**
 * Helper class to simple download list generator operation.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieDownloadListGeneratorHelper {

	/**
	 *
	 */
	private MovieDownloadListGeneratorHelper() {
		// No instances needed
	}

	/**
	 *
	 * @param name
	 * @param downloadFolder
	 * @param movieList
	 * @return
	 */
	public static String generate(String name, String downloadFolder, List<Movie> movieList) {
		AbstractDownloadListGenerator generator = RuntimeManager.getInstance().findDownloadListGenerator(name);
		AssertUtil.assertNotNull(generator, "The generator can't be found for " + name);

		return generator.generateDownloadList(movieList, downloadFolder);
	}

	/**
	 *
	 * @param name
	 * @param downloadFolder
	 * @param movies
	 * @return
	 */
	public static String generate(String name, String downloadFolder, Movie... movies) {
		return generate(name, downloadFolder, Arrays.asList(movies));
	}

}
