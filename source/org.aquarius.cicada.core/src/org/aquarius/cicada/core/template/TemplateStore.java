/**
 *
 */
package org.aquarius.cicada.core.template;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.log.LogUtil;
import org.aquarius.util.StringUtil;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class TemplateStore {

	private Logger logger = LogUtil.getLogger(getClass());

	private Map<String, String> properties;

	public TemplateStore(String templatePropertyFile) {

		try {
			File file = new File(templatePropertyFile);
			if (file.exists()) {
				String json = FileUtils.readFileToString(file, StringUtil.CODEING_UTF8);
				this.properties = JSON.parseObject(json, HashMap.class);
			}

		} catch (Exception e) {
			this.logger.error("load templates:", e);
			this.properties = new HashMap<>();
		}
	}

	public String getValue(String key, String defaultValue) {
		String value = this.properties.get(key);
		if (StringUtils.isNotEmpty(value)) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public boolean isEmpty() {
		return this.properties.isEmpty();
	}

	public Set<String> getKeys() {
		return this.properties.keySet();
	}
}
