/**
 *
 */
package org.aquarius.cicada.workbench.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.impl.parser.ConfigBrowserMovieParser;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.spi.AbstractMovieParser;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.io.FileUtil;

/**
 * Manage site config.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class SiteConfigManager {

	private static final SiteConfigManager instance = new SiteConfigManager();

	/**
	 *
	 */
	private SiteConfigManager() {
		// Just one instance
	}

	/**
	 * @return the instance
	 */
	public static SiteConfigManager getInstance() {
		return instance;
	}

	/**
	 * Return all site configs.<BR>
	 *
	 * @return
	 */
	public List<SiteConfig> getEnableSiteConfigs() {
		List<SiteConfig> configList = new ArrayList<>();
		List<AbstractMovieParser> movieParserList = RuntimeManager.getInstance().getAllMovieParsers();

		for (AbstractMovieParser movieParser : movieParserList) {
			if (movieParser instanceof ConfigBrowserMovieParser) {
				SiteConfig siteConfig = ((ConfigBrowserMovieParser) movieParser).getSiteConfig();

				if (siteConfig.isEnable()) {
					configList.add(siteConfig);
				}
			}
		}

		return configList;
	}

	/**
	 * Return all site configs.<BR>
	 *
	 * @return
	 */
	public List<SiteConfig> getAllSiteConfigs() {
		List<SiteConfig> configList = new ArrayList<>();
		List<AbstractMovieParser> movieParserList = RuntimeManager.getInstance().getAllMovieParsers();

		for (AbstractMovieParser movieParser : movieParserList) {
			if (movieParser instanceof ConfigBrowserMovieParser) {
				SiteConfig siteConfig = ((ConfigBrowserMovieParser) movieParser).getSiteConfig();
				configList.add(siteConfig);
			}
		}

		return configList;
	}

	/**
	 * Find the site config for the specified site name;
	 *
	 * @param siteName
	 * @return
	 */
	public SiteConfig findSiteConfig(String siteName) {
		List<AbstractMovieParser> movieParserList = RuntimeManager.getInstance().getAllMovieParsers();

		for (AbstractMovieParser movieParser : movieParserList) {
			if (movieParser instanceof ConfigBrowserMovieParser) {
				SiteConfig siteConfig = ((ConfigBrowserMovieParser) movieParser).getSiteConfig();

				if (StringUtils.equalsIgnoreCase(siteConfig.getSiteName(), siteName)) {
					return siteConfig;
				}
			}
		}

		return null;
	}

	/**
	 * Apply a new site config.<BR>
	 *
	 * @param siteConfig
	 * @throws IOException
	 */
	public void applySiteConfig(SiteConfig siteConfig) throws IOException {

		AssertUtil.assertNotNull(siteConfig);

		SiteConfig oldSiteConfig = this.findSiteConfig(siteConfig.getSiteName());

		if (null == oldSiteConfig) {
			ConfigBrowserMovieParser browserMovieParser = new ConfigBrowserMovieParser(siteConfig);

			RuntimeManager.getInstance().registerMovieParsers(browserMovieParser);
		} else {

			AbstractMovieParser movieParser = RuntimeManager.getInstance().findMovieParserBySite(siteConfig.getSiteName());

			if (movieParser instanceof ConfigBrowserMovieParser) {
				ConfigBrowserMovieParser browserMovieParser = (ConfigBrowserMovieParser) movieParser;
				browserMovieParser.reload();
			}
		}

	}

	/**
	 * Remove the specified site config.
	 *
	 * @param siteConfig
	 */
	public void removeSiteConfig(SiteConfig siteConfig) {
		List<AbstractMovieParser> movieParserList = RuntimeManager.getInstance().getAllMovieParsers();

		for (AbstractMovieParser movieParser : movieParserList) {
			if (movieParser instanceof ConfigBrowserMovieParser) {
				SiteConfig oldSiteConfig = ((ConfigBrowserMovieParser) movieParser).getSiteConfig();

				if (oldSiteConfig == siteConfig) {
					RuntimeManager.getInstance().unregisterMovieParsers(movieParser);
					FileUtil.forceDeleteFileQuietly(siteConfig.getFolder());
				}
			}
		}
	}

}
