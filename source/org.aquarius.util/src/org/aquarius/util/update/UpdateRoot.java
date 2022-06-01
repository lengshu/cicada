/**
 * 
 */
package org.aquarius.util.update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * The root info for update.
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public final class UpdateRoot {

	private List<Resource> resources = new ArrayList<>();

	private String version;

	private Date updateDate;

	private String author;

	private String changeLog;

	private boolean necessary;

	private String nextUrl;

	/**
	 * 
	 */
	public UpdateRoot() {
		super();
	}

	/**
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return this.resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return this.updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the changeLog
	 */
	public String getChangeLog() {
		return this.changeLog;
	}

	/**
	 * @param changeLog the changeLog to set
	 */
	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	/**
	 * @return the necessary
	 */
	public boolean isNecessary() {
		return this.necessary;
	}

	/**
	 * @param necessary the necessary to set
	 */
	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	/**
	 * @return the nextUrl
	 */
	public String getNextUrl() {
		return this.nextUrl;
	}

	/**
	 * @param nextUrl the nextUrl to set
	 */
	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}

	/**
	 * Return whether the update info is valid or not.<BR>
	 * 
	 * @return
	 */
	public boolean checkValid() {
		if (StringUtils.isEmpty(this.version)) {
			return false;
		}

		if (CollectionUtils.isEmpty(this.resources)) {
			return false;
		}

		return true;
	}

}
