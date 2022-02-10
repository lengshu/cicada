/**
 * 
 */
package org.aquarius.downloader.core.spi.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.aquarius.downloader.core.spi.AbstractDownloadTypeChecker;
import org.aquarius.service.IReloadable;
import org.aquarius.util.StringUtil;

import com.alibaba.fastjson.JSON;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class DefaultDownloadTypeChecker extends AbstractDownloadTypeChecker implements IReloadable {

	private Map<String, String> typeMapping;

	private File file;

	/**
	 * @param typeMapping
	 */
	public DefaultDownloadTypeChecker(File file) {
		super();

		this.file = file;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDownloadType(String urlString) {

		if (null == this.typeMapping) {
			return null;
		}

		for (Entry<String, String> entry : this.typeMapping.entrySet()) {
			Pattern pattern = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);

			Matcher matcher = pattern.matcher(urlString);

			if (matcher.matches()) {
				return entry.getValue();
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean reload() {
		try {
			if (this.file.exists()) {
				String jsonContent = FileUtils.readFileToString(this.file, StringUtil.CODEING_UTF8);
				this.typeMapping = JSON.parseObject(jsonContent, HashMap.class);
			}

		} catch (Exception e) {
			// Nothing to do
			return false;
		}

		return true;
	}

}
