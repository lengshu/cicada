/**
 *
 */
package org.aquarius.cicada.core.web.jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author aquarius.github@gmail.com
 *
 */
class JSoupWebAccessor implements IWebAccessor {

	private Document doc;

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean get(String urlString, IProcessMonitor processMonitor) {

		try {
			this.doc = Jsoup.parse(new URL(urlString), DefaultWaitTimeMills);
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getHtmlSource() {
		if (null == this.doc) {
			return null;
		} else {
			return this.doc.toString();
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object syncExecuteScript(String... scripts) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object post(String urlString, Map<String, String> parameters) {

		try {
			this.doc = Jsoup.connect(urlString).ignoreContentType(true).data(parameters).post();
			return this.doc.body().text();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object asyncExecuteScript(int timeMillis, String resultName, String... scripts) {
		throw new UnsupportedOperationException("Not supported yet");

	}

}
