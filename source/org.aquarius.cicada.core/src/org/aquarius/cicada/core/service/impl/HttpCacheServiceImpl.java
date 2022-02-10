/**
 *
 */
package org.aquarius.cicada.core.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aquarius.log.LogUtil;
import org.aquarius.service.IHttpCacheService;
import org.aquarius.util.net.HttpUtil;
import org.h2.util.DoneFuture;
import org.slf4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

/**
 * http Cache Service implements with ehcache.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class HttpCacheServiceImpl implements IHttpCacheService {

	/**
	 *
	 */
	private static final int CacheCount = 10;

	private static final int DefaultMaxThreadCount = 40;

	private CacheManager cacheManager;

	private Cache cache;

	private ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, DefaultMaxThreadCount, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	private Logger logger = LogUtil.getLogger(getClass());

	private int totalCount = 0;

	/**
	 *
	 */
	public HttpCacheServiceImpl() {
		URL url = HttpCacheServiceImpl.class.getResource("/ehcache.xml");

		if (null == url) {
			this.cacheManager = CacheManager.create(url);
		} else {
			Configuration config = new Configuration();

			DiskStoreConfiguration diskConfiguration = new DiskStoreConfiguration();
			diskConfiguration.setPath("user.dir/workspace/cache");
			config.addDiskStore(diskConfiguration);

			CacheConfiguration cacheConfiguration = new CacheConfiguration();
			cacheConfiguration.setEternal(false);
			cacheConfiguration.setMaxElementsInMemory(200);
			cacheConfiguration.setDiskPersistent(true);
			cacheConfiguration.setTimeToIdleSeconds(10368000);
			cacheConfiguration.setTimeToLiveSeconds(10368000);
			cacheConfiguration.setDiskExpiryThreadIntervalSeconds(120);
			cacheConfiguration.setDiskSpoolBufferSizeMB(200);
			cacheConfiguration.setOverflowToDisk(true);
			cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");

			config.addDefaultCache(cacheConfiguration);

			this.cacheManager = new CacheManager(config);
		}

		this.cache = this.cacheManager.getCache("main");
		if (null == this.cache) {
			this.cacheManager.addCache("main");
		}

		this.cache = this.cacheManager.getCache("main");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Future<Serializable> getElement(String urlString, Map<String, String> headers) {
		Element element = this.cache.get(urlString);
		if (null != element) {
			return new DoneFuture<Serializable>(element.getValue());
		}

		return this.executorService.submit(() -> doFetchInfo(urlString, headers, null));

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Serializable getElement(String urlString, Map<String, String> headers, boolean background) {

		Element element = this.cache.get(urlString);
		if (null != element) {
			return element.getValue();
		}

		Future<Serializable> future = this.executorService.submit(() -> doFetchInfo(urlString, headers, null));

		if (!background) {
			try {
				return future.get();
			} catch (Exception e) {
				this.logger.error("getElement", e);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Serializable getElement(String urlString, Map<String, String> headers, LoadFinishListener<Serializable> listener) {
		Element element = this.cache.get(urlString);
		if (null != element) {
			listener.loadFinished(urlString, element.getValue());
			return element.getValue();
		}

		this.executorService.submit(() -> doFetchInfo(urlString, headers, listener));
		return null;
	}

	/**
	 * 
	 * @param urlString
	 * @param headers
	 * @param listener
	 * @return
	 * @throws IOException
	 */
	private Serializable doFetchInfo(String urlString, Map<String, String> headers, LoadFinishListener<Serializable> listener) throws IOException {
		byte[] bytes = HttpUtil.doGetByteArray(urlString, headers);
		this.cache.put(new Element(urlString, bytes));

		this.totalCount++;

		if (this.totalCount > CacheCount) {
			try {
				this.cache.flush();
			} catch (Exception e) {
				this.logger.error("doFetchInfo", e);
			}
		}

		if (null != listener) {
			listener.loadFinished(urlString, bytes);
		}

		return bytes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() {

		this.cache.flush();
		this.cacheManager.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.cache.removeAll();
		this.cache.flush();
	}

}
