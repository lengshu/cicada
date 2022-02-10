/**
 *
 */
package org.aquarius.cicada.core.impl.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.ScriptDefinition;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.spi.AbstractBrowserMovieParser;
import org.aquarius.log.LogUtil;
import org.aquarius.service.IReloadable;
import org.aquarius.util.SystemUtil;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * It uses a folder to load all confiruation.<BR>
 * The folder includes site config and javascripts.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ConfigBrowserMovieParser extends AbstractBrowserMovieParser implements IReloadable {

	private SiteConfig siteConfig;

	private static final Logger logger = LogUtil.getLogger(ConfigBrowserMovieParser.class);

	private Pattern pattern = null;

	/**
	 * @throws IOException
	 *
	 */
	public ConfigBrowserMovieParser(SiteConfig siteConfig) throws IOException {
		super();

		this.siteConfig = siteConfig;

		init();

	}

	/**
	 *
	 */
	private void init() {
		this.pattern = null;

		if (StringUtils.isNotBlank(this.siteConfig.getParseMovieIdRegex())) {
			try {
				this.pattern = Pattern.compile(this.siteConfig.getParseMovieIdRegex(), Pattern.CASE_INSENSITIVE);
			} catch (Exception e) {
				logger.error("compile regex " + this.siteConfig.getParseMovieIdRegex(), e);
			}
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean reload() {

		try {
			if (reloadSiteConfig(this.siteConfig)) {
				init();
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected ScriptDefinition getParseListScriptDefinition() {
		return this.siteConfig.getParseListScriptDefinition();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected ScriptDefinition getParseLinkScriptDefinition() {
		return this.siteConfig.getParseLinkScriptDefinition();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean isParseLink() {
		return StringUtils.isNotBlank(this.siteConfig.getParseLinkScriptDefinition().getScript());
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected ScriptDefinition getParseDetailScriptDefinition() {
		return this.siteConfig.getParseDetailScriptDefinition();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.siteConfig.getSiteName();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<MovieChannel> getChannels() {
		return this.siteConfig.getMovieChannelList();
	}

	/**
	 * Take the sub folders of a specified folder as configuration locations .
	 *
	 * @param folderName
	 * @return
	 */
	public static List<ConfigBrowserMovieParser> loadFolders(String folderName) {
		File folder = new File(folderName);

		List<ConfigBrowserMovieParser> result = new ArrayList<ConfigBrowserMovieParser>();

		if (folder.exists()) {
			File[] files = folder.listFiles();
			for (File file : files) {

				if (file.isDirectory()) {
					try {
						SiteConfig siteConfig = loadSiteConfig(file);

						if (null != siteConfig) {
							ConfigBrowserMovieParser parser = new ConfigBrowserMovieParser(siteConfig);
							result.add(parser);
						}

					} catch (Exception e) {
						logger.error("load parser width config file. ", e);
					}
				}

			}
		}

		return result;
	}

	/**
	 * Load site config from a specified folder.
	 *
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	private static synchronized SiteConfig loadSiteConfig(File folder) throws IOException {

		File configFile = FileUtils.getFile(folder, "config.json");
		if (!configFile.exists()) {
			return null;
		}

		SiteConfig siteConfig = new SiteConfig();
		siteConfig.setFolder(folder);
		siteConfig.setEnable(true);

		if (doLoadSiteConfig(siteConfig.getFolder(), siteConfig)) {
			return siteConfig;
		} else {
			return null;
		}

	}

	/**
	 * Reload the site config
	 *
	 * @param siteConfig
	 * @throws IOException
	 */
	private static synchronized boolean reloadSiteConfig(SiteConfig siteConfig) throws IOException {

		return doLoadSiteConfig(siteConfig.getFolder(), siteConfig);

	}

	/**
	 * load the files of a specified and write value the config.
	 *
	 * @param folder
	 * @param siteConfig
	 * @throws IOException
	 */
	private static synchronized boolean doLoadSiteConfig(File folder, SiteConfig siteConfig) throws IOException {

		if (!folder.exists()) {
			return false;
		}

		File configFile = FileUtils.getFile(folder, "config.json");

		String json = FileUtils.readFileToString(configFile, "UTF-8");
		SiteConfig newSiteConfig = JSON.parseObject(json, SiteConfig.class);

		try {
			PropertyUtils.copyProperties(siteConfig, newSiteConfig);
		} catch (Exception e) {
			throw new RuntimeException("");
		}

		siteConfig.setFolder(folder);
		if (StringUtils.isEmpty(siteConfig.getSiteName())) {
			siteConfig.setSiteName(folder.getName());
		}

		List<MovieChannel> movieChannels = siteConfig.getMovieChannelList();
		if (CollectionUtils.isNotEmpty(movieChannels)) {
			for (MovieChannel movieChannel : movieChannels) {
				movieChannel.setSiteName(siteConfig.getSiteName());
			}
		}

		{
			File listFile = FileUtils.getFile(folder, "list.js");
			if (listFile.exists()) {
				String parseListScript = FileUtils.readFileToString(listFile, "UTF-8");
				siteConfig.getParseListScriptDefinition().setScript(parseListScript);
			}
		}

		{
			File detailFile = FileUtils.getFile(folder, "detail.js");
			if (detailFile.exists()) {
				String parseDetailScript = FileUtils.readFileToString(detailFile, "UTF-8");
				siteConfig.getParseDetailScriptDefinition().setScript(parseDetailScript);
			}
		}

		{
			File linkFile = FileUtils.getFile(folder, "link.js");
			if (linkFile.exists()) {
				String linkScript = FileUtils.readFileToString(linkFile, "UTF-8");
				siteConfig.getParseLinkScriptDefinition().setScript(linkScript);
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isSupportImport() {
		return this.siteConfig.isSupportImport() && this.siteConfig.isEnable();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean supportJumpToLast() {
		return this.siteConfig.isSupportJumpToLast();
	}

	@Override
	public boolean supportPaging() {
		return this.siteConfig.isSupportPaging();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected int getWaitTime() {
		return this.siteConfig.getWaitTime() * SystemUtil.TimeSecond;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getCheckDuplicationCount() {
		int checkDuplicationCount = this.siteConfig.getCheckDuplicationCount();

		if (checkDuplicationCount < 1) {
			return 1;
		} else {
			return checkDuplicationCount;
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String[] getSiteUrls() {
		return new String[] { this.siteConfig.getMainPage() };
	}

	/**
	 * @return the siteConfig
	 */
	public SiteConfig getSiteConfig() {
		return this.siteConfig;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isSupportAlbum() {
		return this.siteConfig.isSupportAlbum();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String parseMovieId(Movie movie) {

		if (null != this.pattern) {
			Matcher matcher = this.pattern.matcher(movie.getPageUrl());

			if (matcher.find()) {
				return matcher.group();
			}
		}

		return super.parseMovieId(movie);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean hasAutoRefreshChannel() {
		return this.siteConfig.hasAutoRefreshChannel();
	}

}
