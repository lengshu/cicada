/**
 * 
 */
package org.aquarius.util.update;

/**
 * The root config for update.
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public final class UpdateConfig {

	private String remoteUrl;

	private String currentVersion;

	private String localPath;

	private boolean overwrite;

	/**
	 * 
	 */
	public UpdateConfig() {
		super();
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
	 * @return the currentVersion
	 */
	public String getCurrentVersion() {
		return this.currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	/**
	 * @return the localPath
	 */
	public String getLocalPath() {
		return this.localPath;
	}

	/**
	 * @param localPath the localPath to set
	 */
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	/**
	 * @return the overwrite
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

}
