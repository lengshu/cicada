/**
 *
 */
package org.aquarius.service.config;

import org.aquarius.service.IPropertyStoreService;

/**
 * Use IPropertyStoreService to store values.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractStoreableConfiguration {

	private static final String InitMarker = "._Key_._Init_";

	private IPropertyStoreService storeService;

	/**
	 * @param storeService
	 */
	public AbstractStoreableConfiguration(IPropertyStoreService storeService) {
		super();
		this.storeService = storeService;

		this.loadDefaults();
	}

	/**
	 * @return the storeService
	 */
	public IPropertyStoreService getStoreService() {
		return this.storeService;
	}

	/**
	 * load defaults
	 */
	private void loadDefaults() {
		this.doLoadDefaults(storeService);
	}

	/**
	 * Load defaults value.<BR>
	 * 
	 * @param storeService TODO
	 */
	protected abstract void doLoadDefaults(IPropertyStoreService storeService);

}
