/**
 * 
 */
package org.aquarius.util.update;

import java.io.File;

/**
 * Resource info for update.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public final class Resource {

	private String name;

	private String tooltip;

	private boolean absPath;

	private String targetPath;

	private transient boolean selected = true;

	private transient File realTargetPath;

	private transient File localCachePath;

	private String remoteUrl;

	private boolean needUnpack;

	/**
	 * 
	 */
	public Resource() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return this.tooltip;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @return the absPath
	 */
	public boolean isAbsPath() {
		return this.absPath;
	}

	/**
	 * @param absPath the absPath to set
	 */
	public void setAbsPath(boolean absPath) {
		this.absPath = absPath;
	}

	/**
	 * @return the targetPath
	 */
	public String getTargetPath() {
		return this.targetPath;
	}

	/**
	 * @param targetPath the targetPath to set
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * @return the realTargetPath
	 */
	public File getRealTargetPath() {
		return this.realTargetPath;
	}

	/**
	 * @param realTargetPath the realTargetPath to set
	 */
	public void setRealTargetPath(File realTargetPath) {
		this.realTargetPath = realTargetPath;
	}

	/**
	 * @return the localCachePath
	 */
	public File getLocalCachePath() {
		return this.localCachePath;
	}

	/**
	 * @param localCachePath the localCachePath to set
	 */
	public void setLocalCachePath(File localCachePath) {
		this.localCachePath = localCachePath;
	}

	/**
	 * @return the remoteUrl
	 */
	public String getRemoteUrl() {
		return this.remoteUrl;
	}

	/**
	 * @param remoteUrl the remoteUrl to set
	 */
	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	/**
	 * @return the needUnpack
	 */
	public boolean isNeedUnpack() {
		return this.needUnpack;
	}

	/**
	 * @param needUnpack the needUnpack to set
	 */
	public void setNeedUnpack(boolean needUnpack) {
		this.needUnpack = needUnpack;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
