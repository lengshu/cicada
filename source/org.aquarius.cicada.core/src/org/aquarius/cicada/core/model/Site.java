/**
 *
 */
package org.aquarius.cicada.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.LockObject;
import org.aquarius.util.sort.EntryComparator;

/**
 *
 * Video site model.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class Site extends LockObject {

	private String siteName;

	private AbstractMovieParser movieParser;

	private List<Movie> sourceMovieList = new ArrayList<>();

	private List<String> channelNameList = new ArrayList<>();

	private List<String> tagList = new ArrayList<>();

	private List<String> categoryList = new ArrayList<>();

	private List<String> actorList = new ArrayList<>();

	private List<String> producerList = new ArrayList<>();

	private Set<String> allMarkList = new ListOrderedSet<String>();

	public static final int DefaultFilterFrequencyCount = 10;

	private int filterFrequencyCount = DefaultFilterFrequencyCount;

	private boolean refreshing;

	/**
	 * 
	 * @param movieParser
	 * @param movieList
	 * @param filterFrequencyCount
	 */
	public Site(AbstractMovieParser movieParser, List<Movie> movieList, int filterFrequencyCount) {
		super();

		AssertUtil.assertNotNull(movieParser);
		AssertUtil.assertNotNull(movieList);

		this.filterFrequencyCount = filterFrequencyCount;

		this.movieParser = movieParser;
		this.sourceMovieList.addAll(movieList);

		build();

	}

	public void clearAllData() {
		this.sourceMovieList.clear();
		this.actorList.clear();
		this.tagList.clear();
		this.categoryList.clear();
		this.producerList.clear();
		this.allMarkList.clear();
	}

	/**
	 *
	 * @param movieParser
	 * @param movieList
	 */
	public Site(AbstractMovieParser movieParser, List<Movie> movieList) {
		this(movieParser, movieList, DefaultFilterFrequencyCount);

	}

	/**
	 * 
	 * @param siteName
	 * @param movieList
	 * @param filterFrequencyCount
	 */
	public Site(String siteName, List<Movie> movieList, int filterFrequencyCount) {
		super();

		AssertUtil.assertNotNull(siteName);

		this.filterFrequencyCount = filterFrequencyCount;
		this.siteName = siteName;
		this.sourceMovieList = movieList;

		build();

	}

	/**
	 * 
	 * @param siteName
	 * @param movieList
	 */
	public Site(String siteName, List<Movie> movieList) {
		this(siteName, movieList, DefaultFilterFrequencyCount);

	}

	/**
	 * @param contentSet
	 * @param content
	 */
	private void addElements(MultiSet<String> contentSet, String content) {
		String[] names = MovieUtil.split(content);
		if (ArrayUtils.isNotEmpty(names)) {

			for (String name : names) {
				if (name.length() > 8) {
					name = name.substring(0, 8);
				}

				contentSet.add(name);
			}
		}
	}

	/**
	 *
	 */
	public void build() {

		MultiSet<String> actorSet = new HashMultiSet<>();
		MultiSet<String> categorySet = new HashMultiSet<>();
		MultiSet<String> tagSet = new HashMultiSet<>();
		MultiSet<String> producerSet = new HashMultiSet<>();
		MultiSet<String> titleSet = new HashMultiSet<>();

		Set<String> channelSet = new HashSet<>();

		for (Movie movie : this.sourceMovieList) {

			channelSet.add(movie.getChannel());

			{
				String category = movie.getCategory();
				addElements(categorySet, category);
			}

			{
				String tag = movie.getTag();
				addElements(tagSet, tag);

			}

			{
				String actor = movie.getActor();
				addElements(actorSet, actor);
			}

			{
				String producer = movie.getProducer();
				String[] producers = MovieUtil.split(producer);
				if (ArrayUtils.isNotEmpty(producers)) {
					CollectionUtils.addAll(producerSet, producers);
				}
			}

			titleSet.add(movie.getTitle());
			if (StringUtils.isNotBlank(movie.getName())) {
				titleSet.add(movie.getName());
			}
		}

		this.allMarkList.addAll(actorSet);
		this.allMarkList.addAll(categorySet);
		this.allMarkList.addAll(tagSet);
		this.allMarkList.addAll(producerSet);
		this.allMarkList.addAll(titleSet);

		if (RuntimeManager.getInstance().getConfiguration().isKeywordFrequencySort()) {
			this.actorList = this.sort(actorSet);
			this.categoryList = this.sort(categorySet);
			this.tagList = this.sort(tagSet);
			this.producerList = this.sort(producerSet);
		} else {
			this.actorList.addAll(actorSet.uniqueSet());
			Collections.sort(this.actorList);

			this.categoryList.addAll(categorySet.uniqueSet());
			Collections.sort(this.categoryList);

			this.tagList.addAll(tagSet.uniqueSet());
			Collections.sort(this.tagList);

			this.producerList.addAll(producerSet.uniqueSet());
			Collections.sort(this.producerList);
		}

		if (null != this.movieParser) {
			List<String> channelNameList = MovieUtil.getChannelNames(this.movieParser.getChannels());
			channelSet.addAll(channelNameList);
		}

		this.channelNameList.addAll(channelSet);

	}

	/**
	 *
	 * @param todoSet
	 * @return
	 */
	private List<String> sort(MultiSet<String> todoSet) {
		Set<Entry<String>> set = new TreeSet<Entry<String>>(new EntryComparator<>());
		set.addAll(todoSet.entrySet());

		List<String> targetList = new ArrayList<>();
		for (Entry<String> entry : set) {

			if (entry.getCount() > this.filterFrequencyCount) {
				targetList.add(entry.getElement());
			}
		}

		return targetList;
	}

	/**
	 * @return the movieParser
	 */
	public AbstractMovieParser getMovieParser() {
		return this.movieParser;
	}

	/**
	 *
	 * @return
	 */
	public List<Movie> getMovieList() {
		return this.sourceMovieList;
	}

	/**
	 * @return the actorList
	 */
	public List<String> getActors() {
		return this.actorList;
	}

	/**
	 * @return the tagList
	 */
	public List<String> getTags() {
		return this.tagList;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getChannelNames() {
		return this.channelNameList;
	}

	/**
	 * @return the categoryList
	 */
	public List<String> getCategories() {
		return this.categoryList;
	}

	/**
	 * Insert movies to the header location.<BR>
	 *
	 * @param movieList
	 */
	public void addMoviesToHeader(List<Movie> movieList) {
		this.sourceMovieList.addAll(0, movieList);
	}

	/**
	 * Remove specified movies.<BR>
	 *
	 * @param movieList
	 */
	public void removeMovies(List<Movie> movieList) {
		this.sourceMovieList.removeAll(movieList);
	}

	/**
	 * Return the site name.<BR>
	 *
	 * @return
	 */
	public String getSiteName() {

		if (null == this.movieParser) {
			return this.siteName;
		} else {
			return this.movieParser.getName();
		}

	}

	/**
	 * Return all keywords.
	 *
	 * @return
	 */
	public Collection<String> getAllMarks() {
		return this.allMarkList;
	}

	/**
	 * @return the producerList
	 */
	public List<String> getProducerList() {
		return this.producerList;
	}

	/**
	 * Get the movie size.<BR>
	 *
	 * @return
	 */
	public int getMovieSize() {
		return this.sourceMovieList.size();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRefreshing() {
		return this.refreshing;
	}

	/**
	 * 
	 * @param owner
	 * @param refreshing
	 */
	public void setRefreshing(Object owner, boolean refreshing) {
		if (this.unlock(owner)) {
			this.refreshing = refreshing;
		}
	}

}
