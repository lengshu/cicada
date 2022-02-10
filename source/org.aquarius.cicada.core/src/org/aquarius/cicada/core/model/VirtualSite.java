/**
 * 
 */
package org.aquarius.cicada.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class VirtualSite extends Site {

	private Map<String, String> channelNameMapping = new HashMap<>();

	/**
	 * @param siteName
	 * @param movieList
	 * @param filterFrequencyCount
	 */
	public VirtualSite(String siteName, List<Movie> movieList, int filterFrequencyCount, Map<String, String> channelNameMapping) {
		super(siteName, movieList, filterFrequencyCount);

		if (null != channelNameMapping) {
			this.channelNameMapping.putAll(channelNameMapping);
		}
	}

	/**
	 * @return the channelNameMapping
	 */
	public Map<String, String> getChannelNameMapping() {
		return this.channelNameMapping;
	}

}
