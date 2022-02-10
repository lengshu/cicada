/**
 * 
 */
package org.aquarius.cicada.core.impl.redirector;

import java.io.File;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.log.LogUtil;
import org.aquarius.util.DesktopUtil;
import org.aquarius.util.StringUtil;
import org.slf4j.Logger;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class BrowserPostUrlRedirector extends AbstractUrlRedirector {

	private File file;

	private String name;

	private boolean usePrimitiveUrl;

	private static final Logger log = LogUtil.getLogger(BrowserPostUrlRedirector.class);

	/**
	 * 
	 */
	public BrowserPostUrlRedirector(File file, boolean usePrimitiveUrl) {
		super();

		this.file = file;
		this.usePrimitiveUrl = usePrimitiveUrl;
		this.name = file.getName();

		if (StringUtils.contains(this.name, ".")) {
			this.name = StringUtils.substringBefore(this.name, ".");
		}

		this.setPriority(Low);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUsePrimitiveUrl() {
		return this.usePrimitiveUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redirect(String... urlStrings) {

		if (ArrayUtils.isEmpty(urlStrings)) {
			return;
		}

		for (String urlString : urlStrings) {

			try {
				String newUrlString = this.file.toURI().toString() + "?url=" + URLEncoder.encode(urlString, StringUtil.CODEING_UTF8);

				newUrlString = createHtmlLauncher(newUrlString);
				DesktopUtil.openWebpage(newUrlString);
			} catch (Exception e) {
				log.error("create template file error ", e);
			}
		}
	}

	/**
	 * 
	 * @param targetUrl
	 * @return
	 * @throws Exception
	 */
	private String createHtmlLauncher(String targetUrl) throws Exception {
		File launcherTempFile = File.createTempFile("_html_redirect_", "template_.html");
		launcherTempFile.deleteOnExit();
		String htmlContent = "<meta http-equiv=\"refresh\" content=\"0; url=" + targetUrl + "\" />";

		FileUtils.writeStringToFile(launcherTempFile, htmlContent, StringUtil.CODEING_UTF8);

		return "file:///" + launcherTempFile.getAbsolutePath().replace("\\", "/");
	}
}
