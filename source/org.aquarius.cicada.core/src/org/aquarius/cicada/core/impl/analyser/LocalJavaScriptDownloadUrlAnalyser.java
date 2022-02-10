/**
 *
 */
package org.aquarius.cicada.core.impl.analyser;

import java.io.File;
import java.io.IOException;

import javax.script.Invocable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aquarius.cicada.core.model.result.AnalyserResult;
import org.aquarius.cicada.core.model.result.DownloadUrlResult;
import org.aquarius.cicada.core.script.LocalJavaScriptEngineObject;
import org.aquarius.cicada.core.spi.AbstractDownloadUrlAnalyser;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.service.IReloadable;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;

import com.alibaba.fastjson.JSON;

/**
 * Use javascript to parse download url.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LocalJavaScriptDownloadUrlAnalyser extends AbstractDownloadUrlAnalyser implements IReloadable {

	private LocalJavaScriptEngineObject localJavaScriptEngineObject;

	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	public LocalJavaScriptDownloadUrlAnalyser(File file) throws Exception {
		super();
		this.localJavaScriptEngineObject = new LocalJavaScriptEngineObject(file);

	}

	/**
	 * 
	 * @param name
	 * @param script
	 * @throws Exception
	 */
	public LocalJavaScriptDownloadUrlAnalyser(String name, String script) throws Exception {
		super();
		this.localJavaScriptEngineObject = new LocalJavaScriptEngineObject(name, script);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAcceptable(String urlString) {
		return StringUtil.containsAnyIgnoreCase(urlString, this.localJavaScriptEngineObject.getNames());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	public boolean reload() {

		return this.localJavaScriptEngineObject.reload();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public AnalyserResult doAnalyseDownloadUrl(String dynamicUrl, String referUrl, boolean forExternal, IProcessMonitor processMonitor) throws IOException {

		int RetryCount = 3;
		for (int i = 0; i < RetryCount; i++) {
			try {

				Invocable invocable = (Invocable) this.localJavaScriptEngineObject.getScriptEngine();
				Object result = invocable.invokeFunction("doAnalyseDownloadUrl", dynamicUrl, referUrl, forExternal);

				String resultString = ObjectUtils.toString(result);

				DownloadUrlResult downloadUrlResult = JSON.parseObject(resultString, DownloadUrlResult.class);

				if (null != downloadUrlResult) {

					String urlString = MovieUtil.findDownloadUrlByPixel(downloadUrlResult.getUrlMap());

					if (StringUtils.isNotBlank(urlString)) {
						AnalyserResult analyserResult = new AnalyserResult(urlString, downloadUrlResult.getRequestHeaders());
						analyserResult.setHls(downloadUrlResult.isHls());
						return analyserResult;
					}

					if (!downloadUrlResult.isShouldRetry()) {
						return AnalyserResult.createErrorResult("Result is empty,please check.");
					}
				}

			} catch (Exception e) {

				if (i == (RetryCount)) {
					return AnalyserResult.createErrorResult(ExceptionUtils.getFullStackTrace(e));
				} else {
					SystemUtil.sleepQuietly(1000);
				}
			}
		}

		return AnalyserResult.createErrorResult("Result is empty,please check.");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.localJavaScriptEngineObject.getName();
	}

}
