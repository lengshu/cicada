/**
 *
 */
package org.aquarius.cicada.workbench.analyser;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.StringJoiner;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.aquarius.cicada.core.impl.monitor.DefaultProcessMonitor;
import org.aquarius.cicada.core.model.result.AnalyserResult;
import org.aquarius.cicada.core.model.result.DownloadUrlResult;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.spi.AbstractDownloadUrlAnalyser;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.aquarius.cicada.core.spi.web.IWebAccessorService.Visitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.log.LogUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * Use java script to parse movie download url.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractBrowserJavaScriptDownloadUrlAnalyser extends AbstractDownloadUrlAnalyser {

	private static final String ForExternalDownloadTemplate = "window.forExternalDownload={0};";

	private static final int MaxRetryCount = 8;

	private String name;

	private String[] names;

	private Logger log = LogUtil.getLogger(getClass());

	/**
	 * @param name
	 * @param script
	 */
	public AbstractBrowserJavaScriptDownloadUrlAnalyser(String name) {
		super();
		this.name = name;

		this.names = StringUtils.split(this.name, ",");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAcceptable(String urlString) {
		return StringUtil.containsAnyIgnoreCase(urlString, this.names);
	}

	/**
	 * 
	 * @return
	 */
	protected abstract String getScript();

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public AnalyserResult doAnalyseDownloadUrl(String dynamicUrl, String referUrl, boolean forExternal, IProcessMonitor processMonitor) throws IOException {

		Visitor<AnalyserResult> visitor = new Visitor<AnalyserResult>() {

			@Override
			public AnalyserResult visit(IWebAccessor webAccessor) {

				String forExternalDownloadScript = MessageFormat.format(ForExternalDownloadTemplate, forExternal);
				IProcessMonitor realProcessMonitor = DefaultProcessMonitor.wrapProgressMonitor(processMonitor);

				String currentUrl = convertDownloadUrl(dynamicUrl);

				String script = getScript();
				long waitTime = 4000;

				for (int i = 0; i < MaxRetryCount; i++) {

					DownloadUrlResult downloadUrlResult = null;

					try {

						if (realProcessMonitor.isCanceled()) {
							String errorMessage = RuntimeManager.getInstance().getNlsResource().getValue(MovieNlsMessageConstant.OperationCanceled);
							return AnalyserResult.createErrorResult(MessageFormat.format(errorMessage, getName()));
						}

						if (StringUtils.isNotEmpty(currentUrl)) {
							webAccessor.get(currentUrl, realProcessMonitor);
						}

						currentUrl = null;
						SystemUtil.sleepQuietly(waitTime);

						String json = ObjectUtils.toString(webAccessor.syncExecuteScript(forExternalDownloadScript, script));

						downloadUrlResult = JSON.parseObject(json, DownloadUrlResult.class);

					} catch (Exception e) {
						AbstractBrowserJavaScriptDownloadUrlAnalyser.this.log.error("findUrl ", e);
					}

					if (null == downloadUrlResult) {
						continue;
					}

					if (downloadUrlResult.isHasError()) {
						String errorMessage = downloadUrlResult.getErrorMessage();
						return AnalyserResult.createErrorResult(MessageFormat.format(errorMessage, getName()));
					}

					if (downloadUrlResult.isShouldRetry()) {
						currentUrl = downloadUrlResult.getRedirectUrl();
						waitTime = downloadUrlResult.getWaitTime();
						continue;
					}

					if (MapUtils.isNotEmpty(downloadUrlResult.getUrlMap())) {

						String urlString = MovieUtil.findDownloadUrlByPixel(downloadUrlResult.getUrlMap());

						if (StringUtils.isNotBlank(urlString)) {
							return new AnalyserResult(urlString, downloadUrlResult.getRequestHeaders());
						} else {
							String errorMessage = RuntimeManager.getInstance().getNlsResource()
									.getValue(MovieNlsMessageConstant.AbstractDownloadUrlAnalyser_ErrorNoSuitablePixelUrls);

							StringJoiner stringJoiner = new StringJoiner(",");

							for (String key : downloadUrlResult.getUrlMap().keySet()) {
								if (StringUtils.isNotBlank(key)) {
									stringJoiner.add(key);
								}
							}

							return AnalyserResult.createErrorResult(MessageFormat.format(errorMessage, stringJoiner.toString()));
						}
					}
				}

				String errorMessage = RuntimeManager.getInstance().getNlsResource()
						.getValue(MovieNlsMessageConstant.AbstractDownloadUrlAnalyser_ErrorEmptyUrls);
				return AnalyserResult.createErrorResult(MessageFormat.format(errorMessage, getName()));
			}

		};

		return WebAccessorHelper.visit(visitor);
	}

	/**
	 *
	 * @param downloadUrl
	 * @return
	 */
	protected String convertDownloadUrl(String downloadUrl) {
		return downloadUrl;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.name;
	}

}
