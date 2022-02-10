/**
 *
 */
package org.aquarius.downloader.core.impl.hls;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for hls model.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HlsModel {

	private String secretKeyString;

	private String content;

	private List<TsFile> tsFileList = new ArrayList<TsFile>();

	/**
	 * Build the model after the hls file parsed.
	 *
	 * @param fileList
	 * @param content
	 * @param secretKeyString
	 */
	public HlsModel(List<TsFile> fileList, String content, String secretKeyString) {
		super();
		this.tsFileList = fileList;

		this.content = content;
		this.secretKeyString = secretKeyString;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @return the tsFileList
	 */
	public List<TsFile> getTsFileList() {
		return this.tsFileList;
	}

	/**
	 * @return the secretKeyString
	 */
	public String getSecretKeyString() {
		return this.secretKeyString;
	}
}
