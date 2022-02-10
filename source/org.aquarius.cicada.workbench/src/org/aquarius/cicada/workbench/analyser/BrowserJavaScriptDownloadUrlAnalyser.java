/**
 *
 */
package org.aquarius.cicada.workbench.analyser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.aquarius.service.IReloadable;
import org.aquarius.util.StringUtil;
import org.aquarius.util.io.FileUtil;

/**
 * Use java script to parse movie download url.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class BrowserJavaScriptDownloadUrlAnalyser extends AbstractBrowserJavaScriptDownloadUrlAnalyser implements IReloadable {

	private File file;

	private String script;

	/**
	 * @param file
	 * @throws Exception
	 */
	public BrowserJavaScriptDownloadUrlAnalyser(File file) throws Exception {
		super(FilenameUtils.getBaseName(file.getName()));
		this.file = file;
		this.reload();
	}

	/**
	 * 
	 * @param siteName
	 * @param file
	 * @throws Exception
	 */
	public BrowserJavaScriptDownloadUrlAnalyser(String siteName, File file) throws Exception {
		super(siteName);
		this.file = file;
		this.reload();
	}

	/**
	 * @param file
	 * @throws Exception
	 */
	public BrowserJavaScriptDownloadUrlAnalyser(String name, String script) throws Exception {
		super(name);

		this.script = script;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean reload() {

		if (FileUtil.isValid(this.file)) {
			try {
				this.script = FileUtils.readFileToString(this.file, StringUtil.CODEING_UTF8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getScript() {
		return this.script;
	}
}
