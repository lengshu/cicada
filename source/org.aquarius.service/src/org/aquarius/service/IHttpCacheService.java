/**
 *
 */
package org.aquarius.service;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * load http resources and cache them.
 * 
 * @author aquarius.github@gmail.com
 *
 */
public interface IHttpCacheService {

	@FunctionalInterface
	public interface LoadFinishListener<T> {
		public void loadFinished(String urlString, T value);
	}

	/**
	 * Close the cache.<BR>
	 */
	void shutdown();

	/**
	 * Clear cache and release resources.
	 */
	void clear();

	/**
	 * 
	 * @param urlString
	 * @param headers
	 * @return
	 */
	Future<Serializable> getElement(String urlString, Map<String, String> headers);

	/**
	 * 
	 * @param urlString
	 * @param headers
	 * @param background
	 * @return
	 */
	Serializable getElement(String urlString, Map<String, String> headers, boolean background);

	/**
	 * 
	 * @param urlString
	 * @param headers
	 * @param listener
	 * @return
	 */
	Serializable getElement(String urlString, Map<String, String> headers, LoadFinishListener<Serializable> listener);

}
