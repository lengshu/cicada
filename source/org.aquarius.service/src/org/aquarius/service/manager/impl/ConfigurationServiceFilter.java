/**
 * 
 */
package org.aquarius.service.manager.impl;

import java.util.HashMap;
import java.util.Map;

import org.aquarius.service.INameService;
import org.aquarius.service.manager.spi.IServiceFilter;
import org.aquarius.util.AssertUtil;

/**
 * Use configuration to enable or disable a serivce.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public final class ConfigurationServiceFilter<T> implements IServiceFilter<T> {

	private boolean defautEnableState;

	private String domain;

	private Map<String, Boolean> map;

	/**
	 * 
	 * @param domain
	 * @param map
	 * @param defautEnableState
	 */
	public ConfigurationServiceFilter(String domain, Map<String, Boolean> map, boolean defautEnableState) {
		super();

		AssertUtil.assertNotNull(domain);

		this.defautEnableState = defautEnableState;
		this.domain = domain;

		if (null == map) {
			this.map = new HashMap<String, Boolean>();
		} else {
			this.map = map;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean isFiltered(T object) {
		AssertUtil.assertNotNull(object);

		String key = object.getClass().getName();

		if (object instanceof INameService) {
			key = ((INameService) object).getName();
		}

		Boolean value = this.map.get(key);
		if (null == value) {
			return this.defautEnableState;
		} else {
			return value.booleanValue();
		}
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return this.domain;
	}

	/**
	 * @return the map
	 */
	public Map<String, Boolean> getMap() {
		return this.map;
	}

}
