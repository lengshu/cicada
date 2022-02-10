/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.aquarius.cicada.core.spi.web.IWebAccessorService.Visitor;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.log.LogUtil;
import org.aquarius.util.SystemUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;

/**
 * Init cookie job.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class InitCookieJob extends Job {

	private static final int Period = 7;

	private Logger logger = LogUtil.getLogger(getClass());

	private static final int RetryCount = 3;

	/**
	 * @param name
	 */
	public InitCookieJob(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		IPreferenceStore preferenceStore = WorkbenchActivator.getDefault().getPreferenceStore();
		List<SiteConfig> siteConfigList = SiteConfigManager.getInstance().getEnableSiteConfigs();

		long lastTime = preferenceStore.getLong(InitCookieJob.class.getName());
		lastTime = lastTime + Period * (SystemUtil.TimeDay);

		if (lastTime > System.currentTimeMillis()) {
			return Status.OK_STATUS;
		}

		WebAccessorHelper.visit(new Visitor<Object>() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public Object visit(IWebAccessor webAccessor) {

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.WEEK_OF_YEAR, 4);
				Date targetDate = calendar.getTime();

				for (SiteConfig siteConfig : siteConfigList) {
					String cookieString = siteConfig.getCookie();

					if (StringUtils.isBlank(cookieString)) {
						continue;
					}

					String[] cookieArray = StringUtils.split(cookieString, ";");

					if (ArrayUtils.isEmpty(cookieArray)) {
						continue;
					}

					if (webAccessor.get(siteConfig.getMainPage(), DefaultProcessMonitor.createInstance())) {

						StringJoiner stringJoiner = new StringJoiner(";");
						for (String cookie : cookieArray) {
							if (!StringUtils.contains(cookie, "expires")) {
								cookie = cookie + "; expires=" + targetDate.toString();
							}

							stringJoiner.add(cookie);
						}

						String script = "document.cookie =\"" + stringJoiner.toString() + "\";";

						// Try 3 times to set cookie.
						for (int i = 1; i <= RetryCount; i++) {
							try {
								SystemUtil.sleepQuietly(2000);
								webAccessor.syncExecuteScript(script);

							} catch (Exception e) {
								InitCookieJob.this.logger.error("init cookie in a job. ", e);

								if (i == RetryCount) {
									return null;
								}
							}
						}

					}
				}

				preferenceStore.setValue(InitCookieJob.class.getName(), targetDate.getTime());

				return null;
			}

		});

		return Status.OK_STATUS;
	}

}
