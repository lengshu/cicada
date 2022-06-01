/**
 * 
 */
package org.aquarius.cicada.workbench.page;

import java.util.Properties;

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

	private Properties refererMapping;

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
		this.createDefaultValues(storeService);
	}

	private Properties createDefaultValues(IPropertyStoreService storeService) {
		Properties mapping = new Properties();
		mapping.put("pornimg.xyz", "https://hpjav.tv");

		storeService.setDefault(Key_Mapping, JSON.toJSONString(mapping));

		return mapping;
	}

	/**
	 * 
	 */
	public void resetDefaults() {
		this.getStoreService().setValue(Key_Mapping, "");

		this.load();
	}

	/**
	 * 
	 */
	public void load() {
		String mappingString = this.getStoreService().getString(Key_Mapping);

		try {
			this.refererMapping = JSON.parseObject(mappingString, Properties.class);
		} catch (Exception e) {
			// Nothing to do
		}

		if (null == this.refererMapping) {
			this.refererMapping = this.createDefaultValues(getStoreService());
		}
	}

	/**
	 * @return the refererMapping
	 */
	public Properties getRefererMapping() {
		return this.refererMapping;
	}

	/**
	 * @param refererMapping the refererMapping to set
	 */
	public void setRefererMapping(Properties refererMapping) {
		this.refererMapping = refererMapping;

		this.getStoreService().setValue(Key_Mapping, JSON.toJSONString(this.refererMapping));
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

		for (String key : this.refererMapping.stringPropertyNames()) {
			if (StringUtils.contains(url, key)) {
				return this.refererMapping.getProperty(key);
			}
		}

		return defaultRefererUrl;
	}

}
