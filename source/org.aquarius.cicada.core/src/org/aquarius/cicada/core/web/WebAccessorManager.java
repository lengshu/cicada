/**
 *
 */
package org.aquarius.cicada.core.web;

import org.aquarius.cicada.core.spi.web.IWebAccessorService;
import org.aquarius.log.LogUtil;
import org.aquarius.service.manager.ServiceManager;
import org.slf4j.Logger;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class WebAccessorManager extends ServiceManager<IWebAccessorService> {

	private Logger logger = LogUtil.getLogger(this.getClass());

	private static final WebAccessorManager instance = new WebAccessorManager();

	private IWebAccessorService defaultWebAccessorService = null;

	/**
	 * @return the instance
	 */
	public static WebAccessorManager getInstance() {
		return instance;
	}

	/**
	 *
	 */
	private WebAccessorManager() {
		//
	}

	/**
	 * @return the defaultWebAccessorService
	 */
	public IWebAccessorService getDefaultWebAccessorService() {
		return this.defaultWebAccessorService;
	}

	/**
	 * @param defaultWebAccessorService the defaultWebAccessorService to set
	 */
	public void setDefaultWebAccessorService(IWebAccessorService defaultWebAccessorService) {
		this.defaultWebAccessorService = defaultWebAccessorService;
		this.registerService(defaultWebAccessorService);
	}

	/**
	 * Dispose resources.<BR>
	 */
	public void dispose() {

		for (IWebAccessorService webAccessorService : this.getAllServices()) {
			try {
				webAccessorService.close();
			} catch (Exception e) {
				this.logger.error("dispose", e);
			}
		}
	}

}
