/**
 *
 */
package org.aquarius.cicada.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.util.MovieUtil;

/**
 * Use keyword and state to filter movie
 *
 * @author aquarius.github@gmail.com
 *
 */
public class Filter {

	private List<String> channelNameList = new ArrayList<>();

	private List<String> tagList = new ArrayList<>();

	private List<String> categoryList = new ArrayList<>();

	private List<String> actorList = new ArrayList<>();

	private String keyword;

	private Set<Integer> stateSet = new TreeSet<>();

	/**
	 *
	 */
	public Filter() {
		super();
	}

	public void reset() {
		this.keyword = "";
		this.channelNameList.clear();
		this.tagList.clear();
		this.categoryList.clear();
		this.actorList.clear();
		this.stateSet.clear();
	}

	/**
	 * @return the channelNameList
	 */
	public List<String> getChannelNameList() {
		return this.channelNameList;
	}

	/**
	 * @param channelNameList the channelNameList to set
	 */
	public void setChannelNameList(List<String> channelNameList) {
		this.channelNameList = channelNameList;
	}

	/**
	 * @return the categoryList
	 */
	public List<String> getCategoryList() {
		return this.categoryList;
	}

	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(List<String> categoryList) {
		this.categoryList = categoryList;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return this.keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the stateSet
	 */
	public Set<Integer> getStateSet() {
		return this.stateSet;
	}

	/**
	 * @param stateSet the stateSet to set
	 */
	public void setStateSet(Set<Integer> stateSet) {
		this.stateSet = stateSet;
	}

	/**
	 *
	 * @return
	 */
	public boolean isValid() {
		return StringUtils.isNotBlank(this.keyword) || CollectionUtils.isNotEmpty(this.stateSet) || CollectionUtils.isNotEmpty(this.channelNameList)
				|| CollectionUtils.isNotEmpty(this.actorList) || CollectionUtils.isNotEmpty(this.tagList) || CollectionUtils.isNotEmpty(this.categoryList);
	}

	/**
	 * @return the tagList
	 */
	public List<String> getTagList() {
		return this.tagList;
	}

	/**
	 * @param tagList the tagList to set
	 */
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	/**
	 * @return the actorList
	 */
	public List<String> getActorList() {
		return this.actorList;
	}

	/**
	 * @param actorList the actorList to set
	 */
	public void setActorList(List<String> actorList) {
		this.actorList = actorList;
	}

	/**
	 * Return whether a movie is filtered or not.<BR>
	 *
	 * @param movie
	 * @return
	 */
	public boolean isFiltered(Movie movie) {

		if (CollectionUtils.isNotEmpty(this.stateSet)) {
			if (!this.stateSet.contains(movie.getState())) {
				return true;
			}
		}

		if (CollectionUtils.isNotEmpty(this.channelNameList)) {
			if (!this.channelNameList.contains(movie.getChannel())) {
				return true;
			}
		}

		if (CollectionUtils.isNotEmpty(this.actorList)) {
			boolean found = found(movie.getActor(), this.actorList);

			if (!found) {
				return true;
			}
		}

		if (CollectionUtils.isNotEmpty(this.tagList)) {
			boolean found = found(movie.getTag(), this.tagList);

			if (!found) {
				return true;
			}
		}

		if (CollectionUtils.isNotEmpty(this.categoryList)) {
			boolean found = found(movie.getCategory(), this.categoryList);

			if (!found) {
				return true;
			}
		}

		if (StringUtils.isNotEmpty(this.keyword)) {
			return !MovieUtil.findKeyword(movie, this.keyword);
		}
		// Try to find keyword in title/actors/categoreis/producer

		return false;

	}

	/**
	 * @param movie
	 * @return
	 */
	private boolean found(String content, List<String> contentList) {
		boolean found = false;
		for (String actor : contentList) {
			if (StringUtils.contains(content, actor)) {
				found = true;
				break;
			}
		}
		return found;
	}

}
