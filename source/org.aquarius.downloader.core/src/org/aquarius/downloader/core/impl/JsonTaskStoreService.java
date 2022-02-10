/**
 *
 */
package org.aquarius.downloader.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.spi.AbstractTaskStoreService;
import org.aquarius.log.LogUtil;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.io.FileUtil;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Use json file to store file
 *
 * @author aquarius.github@gmail.com
 *
 */
public class JsonTaskStoreService extends AbstractTaskStoreService {

	private File jsonFile;

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * @param jsonFile
	 */
	public JsonTaskStoreService(File jsonFile) {
		super();

		AssertUtil.assertNotNull(jsonFile, "The file should not be null.");

		this.jsonFile = jsonFile;
		if (this.jsonFile.isDirectory()) {
			throw new IllegalArgumentException("The file of '" + jsonFile.getAbsolutePath() + "' should not be directory.");
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSaveTasks(List<DownloadTask> downloadTaskList) {
		SerializeConfig config = new SerializeConfig(true);

		String json = JSON.toJSONString(downloadTaskList, config, SerializerFeature.PrettyFormat);
		try {
			FileUtils.write(this.jsonFile, json, "UTF-8");
		} catch (IOException e) {
			this.logger.error("save the file '" + this.jsonFile.getAbsolutePath() + "' error,so next save action will be retried.");
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<DownloadTask> loadTasks() {

		if (this.jsonFile.exists()) {
			try (InputStream inputStream = new FileInputStream(this.jsonFile)) {

				return this.parseTasks(inputStream);

			} catch (Exception e) {
				FileUtil.autoRenameFileForBackup(this.jsonFile.getAbsolutePath());
				this.logger.error("Load the file '" + this.jsonFile.getAbsolutePath() + "' error,so a new download list will be created.");
			}
		}

		return new ArrayList<DownloadTask>();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<DownloadTask> parseTasks(InputStream inputStream) throws IOException {
		ParserConfig parserConfig = new ParserConfig(true);

		String json = IOUtils.toString(inputStream, StringUtil.CODEING_UTF8);

		List<DownloadTask> downloadTaskList = JSON.parseArray(json, DownloadTask.class, parserConfig);

		if (null == downloadTaskList) {
			return new ArrayList<DownloadTask>();
		}

		for (DownloadTask downloadTask : downloadTaskList) {
			downloadTask.restore();
		}

		return downloadTaskList;
	}

	/**
	 * {@inheritDoc}}
	 *
	 * @throws IOException
	 */
	@Override
	public void exportTasks(List<DownloadTask> downloadTaskList, OutputStream outputStream) throws IOException {
		SerializeConfig config = new SerializeConfig(true);
		String json = JSON.toJSONString(downloadTaskList, config);

		IOUtils.write(json, outputStream);
	}

}
