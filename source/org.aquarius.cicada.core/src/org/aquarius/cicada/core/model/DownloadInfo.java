/**
 *
 */
package org.aquarius.cicada.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadInfo {

	private String mp4File;

	private String title;

	private List<Link> downloadLinks = new ArrayList<Link>();

	private String pixel;

	private String source;

	/**
	 * @return the mp4File
	 */
	public String getMp4File() {
		return this.mp4File;
	}

	/**
	 * @param mp4File the mp4File to set
	 */
	public void setMp4File(String mp4File) {
		this.mp4File = mp4File;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public List<Link> getDownloadLinks() {
		return this.downloadLinks;
	}

	/**
	 * @param downloadLinks the downloadLinks to set
	 */
	public void setDownloadLinks(List<Link> downloadLinks) {
		this.downloadLinks = downloadLinks;
	}

//	public void addDownloadLinks(List<Link> links) {
//
//		if (CollectionUtils.isNotEmpty(links)) {
//			for (Link link : links) {
//				if (!this.downloadLinks.contains(link)) {
//					this.downloadLinks.add(link);
//				}
//			}
//		}
//	}
//
//	public void addDownloadLinks(Link... links) {
//
//		if (ArrayUtils.isNotEmpty(links)) {
//			for (Link link : links) {
//				if (!this.downloadLinks.contains(link)) {
//					this.downloadLinks.add(link);
//				}
//			}
//		}
//	}

	/**
	 * @return the pixel
	 */
	public String getPixel() {
		return this.pixel;
	}

	/**
	 * @param pixel the pixel to set
	 */
	public void setPixel(String pixel) {
		this.pixel = pixel;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "DownloadInfo [mp4File=" + this.mp4File + ", title=" + this.title + ", downloadLinks=" + this.downloadLinks + ", pixel=" + this.pixel
				+ ", source=" + this.source + "]";
	}

}
