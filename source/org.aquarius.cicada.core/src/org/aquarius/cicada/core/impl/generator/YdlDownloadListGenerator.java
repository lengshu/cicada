/**
 * #protocol
 */
package org.aquarius.cicada.core.impl.generator;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;

/**
 * Use youtube-dl to download generator
 *
 * @author aquarius.github@gmail.com
 *
 */
public class YdlDownloadListGenerator extends AbstractDownloadListGenerator {

	private static final Map<String, String> pixelMapping = new TreeMap<>();

	static {
		pixelMapping.put("360P", "134/243");
		pixelMapping.put("480P", "135/244");
		pixelMapping.put("720P", "136/247");
		pixelMapping.put("1080P", "137/248");
		pixelMapping.put("1440P", "271");
		pixelMapping.put("2160P", "313");
	}

	/**
	 *
	 */
	public static final String ID = "Youtube-dl";

	/**
	 *
	 */
	public YdlDownloadListGenerator() {
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
	 * 
	 * {@inheritDoc}}
	 */
	@Override
	public String generateDownloadList(List<Movie> movieList, String downloadFolder) {

		StringJoiner pixleString = new StringJoiner("/");
		List<String> pixelList = RuntimeManager.getInstance().getConfiguration().getPixelList();

		for (String pixel : pixelList) {
			String newPixel = pixelMapping.get(pixel.toUpperCase());
			if (StringUtils.isNotBlank(newPixel)) {
				pixleString.add(newPixel);
			}
		}

		StringJoiner stringJoiner = new StringJoiner("\r\n");

		for (Movie movie : movieList) {

			String ydlFormat = "youtube-dl -f {0} \"{1}\"";

			String url = MessageFormat.format(ydlFormat, pixleString, movie.getPageUrl());

			stringJoiner.add(url);
		}

		return stringJoiner.toString();
	}

	/**
	 * 
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isUsePrimitiveUrl() {
		return true;
	}
}
