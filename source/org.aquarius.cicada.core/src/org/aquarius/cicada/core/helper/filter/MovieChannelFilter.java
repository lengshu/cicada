/**
 * 
 */
package org.aquarius.cicada.core.helper.filter;

import org.aquarius.cicada.core.model.MovieChannel;

/**
 * @author aquarius.github@gmail.com
 *
 */
public interface MovieChannelFilter {

	/**
	 * If the return value is <code>ture</code>,it will not be processed.<BR>
	 * 
	 * @param movieChannel
	 * @return
	 */
	public boolean isFilter(MovieChannel movieChannel);

}
