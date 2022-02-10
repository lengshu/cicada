/**
 *
 */
package org.aquarius.cicada.core.spi.base;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.WebAccessorHelper;
import org.aquarius.cicada.core.model.result.AnalyserResult;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.core.spi.AbstractDownloadUrlAnalyser;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.log.LogUtil;
import org.aquarius.util.net.HttpUtil;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * to parse fvs json format.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractFvsDownloadUrlAnalyser extends AbstractDownloadUrlAnalyser {

	private Logger logger = LogUtil.getLogger(getClass());

	private static final String[] marks = new String[] { "/v/", "/f/" };

	/**
	 *
	 */
	public AbstractFvsDownloadUrlAnalyser() {
		super();
		this.setPriority(Medium);
	}

	/**
	 *
	 * @param dynamicUrl
	 * @return
	 */
	public static String wrapUrl(String dynamicUrl) {

		String newUrl = wrapUrl(dynamicUrl, "/api/source/");

		if (StringUtils.isEmpty(newUrl)) {
			for (String mark : marks) {
				if (StringUtils.contains(dynamicUrl, mark)) {
					return StringUtils.replace(dynamicUrl, mark, "/api/source");
				}
			}
		} else {
			return newUrl;
		}

		return dynamicUrl;
	}

	/**
	 * {@inheritDoc}}
	 *
	 * @throws IOException
	 */
	@Override
	public AnalyserResult doAnalyseDownloadUrl(String dynamicUrl, String referUrl, boolean forExternal, IProcessMonitor processMonitor) throws IOException {

		String videoQueryUrl = wrapUrl(dynamicUrl);

		String jsonResult = null;

		if (forExternal) {
			Map<String, String> parameters = new HashMap<>();

			if (null == referUrl) {
				referUrl = dynamicUrl;
			}

			try {
				URL url = new URL(referUrl);
				parameters.put("referer", url.getHost());
			} catch (Exception e) {

			}

			jsonResult = WebAccessorHelper.postPage(videoQueryUrl, parameters);
		} else {
			jsonResult = HttpUtil.doPost(videoQueryUrl, null);
		}

		try {
			JSONObject rootObject = JSONObject.parseObject(jsonResult);
			if (null == rootObject) {
				return null;
			}
			JSONArray dataObjects = rootObject.getJSONArray("data");

			Map<String, String> fileUrls = new HashMap<String, String>();
			for (int i = 0; i < dataObjects.size(); i++) {
				JSONObject fileObject = dataObjects.getJSONObject(i);
				fileUrls.put(StringUtils.upperCase(fileObject.getString("label")), fileObject.getString("file"));
			}

			String newUrl = MovieUtil.findDownloadUrlByPixel(fileUrls);

			if (StringUtils.isNotBlank(newUrl)) {
				return new AnalyserResult(newUrl, null);
			} else {

				if (fileUrls.isEmpty()) {
					String errorMessage = RuntimeManager.getInstance().getNlsResource()
							.getValue(MovieNlsMessageConstant.AbstractDownloadUrlAnalyser_ErrorEmptyUrls);
					String message = MessageFormat.format(errorMessage, getName());
					return AnalyserResult.createErrorResult(message);
				} else {
					String errorMessage = RuntimeManager.getInstance().getNlsResource()
							.getValue(MovieNlsMessageConstant.AbstractDownloadUrlAnalyser_ErrorNoSuitablePixelUrls);
					String message = MessageFormat.format(errorMessage, fileUrls.keySet().toString());
					return AnalyserResult.createErrorResult(message);
				}
			}

		} catch (Exception e) {
			this.logger.error("analyseDownloadUrl", e);
			return AnalyserResult.createErrorResult(e.getLocalizedMessage());
		}

	}

}
