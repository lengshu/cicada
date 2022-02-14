/**
 * 
 */
package org.aquarius.cicada.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.aquarius.service.IPropertyStoreService;
import org.aquarius.service.config.AbstractStoreableConfiguration;
import org.aquarius.service.manager.impl.ConfigurationServiceFilter;

import com.alibaba.fastjson.JSON;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class ServiceFilterConfiguration extends AbstractStoreableConfiguration {

	private Map<String, ConfigurationServiceFilter> filters = new TreeMap<>();

	/**
	 * @param storeService
	 */
	public ServiceFilterConfiguration(IPropertyStoreService storeService) {
		super(storeService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoadDefaults(IPropertyStoreService storeService) {
		// Nothing to do
	}

	/**
	 * 
	 * @param domain
	 * @param defautEnableState
	 * @return
	 */
	public synchronized ConfigurationServiceFilter createConfigurationServiceFilter(String domain, boolean defautEnableState) {

		ConfigurationServiceFilter filter = this.filters.get(domain);
		if (null == filter) {
			String jsonContent = this.getStoreService().getString(domain);
			Map<String, Boolean> map = null;
			if (StringUtils.isNotBlank(jsonContent)) {

				try {
					map = JSON.parseObject(jsonContent, Map.class);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (null == map) {
				map = new HashMap<String, Boolean>();
			}

			filter = new ConfigurationServiceFilter<>(domain, map, defautEnableState);
			this.filters.put(domain, filter);
		}

		return filter;
	}

	/**
	 * @return the filters
	 */
	public List<ConfigurationServiceFilter> getFilters() {
		return new ArrayList<>(this.filters.values());
	}

	/**
	 * 
	 */
	public void save() {

		for (Entry<String, ConfigurationServiceFilter> entry : this.filters.entrySet()) {

			try {
				ConfigurationServiceFilter filter = entry.getValue();
				String jsonContent = JSON.toJSONString(filter.getMap());

				this.getStoreService().setValue(filter.getDomain(), jsonContent);

			} catch (Exception e) {
				// Nothing to do
			}

		}
	}

}
