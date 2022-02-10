/**
 * 
 */
package org.aquarius.cicada.workbench.page;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.aquarius.service.IPropertyStoreService;
import org.aquarius.service.config.AbstractStoreableConfiguration;

import com.alibaba.fastjson.JSON;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class NetworkConfiguration extends AbstractStoreableConfiguration {

	public static final String Key_Mapping = WorkbenchConfiguration.class.getName() + ".Key.Mapping";

	private Map<String, String> refererMapping;

	/**
	 * @param storeService
	 */
	public NetworkConfiguration(IPropertyStoreService storeService) {
		super(storeService);

		this.load();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoadDefaults(IPropertyStoreService storeService) {
		Map<String, String> mapping = new HashMap<>();
		mapping.put("pornimg.xyz", "https://hpjav.tv");

		storeService.setDefault(Key_Mapping, JSON.toJSONString(mapping));
	}

	public void load() {
		String mappingString = this.getStoreService().getString(Key_Mapping);

		try {
			this.refererMapping = JSON.parseObject(mappingString, HashMap.class);
		} catch (Exception e) {
			// Nothing to do
		}

		if (null == this.refererMapping) {
			this.refererMapping = new HashMap<>();
			this.refererMapping.put(Key_Mapping, JSON.toJSONString(this.refererMapping));
		}
	}

	/**
	 * @return the refererMapping
	 */
	public Map<String, String> getRefererMapping() {
		return this.refererMapping;
	}

	/**
	 * 
	 * @param url
	 * @param defaultRefererUrl
	 * @return
	 */
	public String getReferer(String url, String defaultRefererUrl) {
		if (null == url) {
			return defaultRefererUrl;
		}

		url = url.toLowerCase();

		for (Entry<String, String> entry : this.refererMapping.entrySet()) {
			if (StringUtils.contains(url, entry.getKey())) {
				return entry.getValue();
			}
		}

		return defaultRefererUrl;
	}

}
