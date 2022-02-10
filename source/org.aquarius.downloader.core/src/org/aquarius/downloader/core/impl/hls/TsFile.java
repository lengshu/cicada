/**
 *
 */
package org.aquarius.downloader.core.impl.hls;

/**
 *
 * Hls file.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class TsFile {

	private String fileName;

	private String shortUrlName;

	/**
	 *
	 * @param url
	 * @param fileName
	 */
	public TsFile(String shortUrlName, String fileName) {
		super();
		this.fileName = fileName;
		this.shortUrlName = shortUrlName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @return the shortUrlName
	 */
	public String getShortUrlName() {
		return this.shortUrlName;
	}

}
