/**
 * 
 */
package org.aquarius.cicada.core.script;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.service.IReloadable;
import org.aquarius.util.StringUtil;
import org.aquarius.util.io.FileUtil;
import org.aquarius.util.js.JavaScriptEngineManageer;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public final class LocalJavaScriptEngineObject implements IReloadable {

	private File file;

	private String name;

	private String[] names;

	private String script;

	private ScriptEngine scriptEngine;

	/**
	 * 
	 */
	public LocalJavaScriptEngineObject(File file) {
		this.file = file;

		this.name = FilenameUtils.getBaseName(file.getName());
		this.names = StringUtils.split(this.name, ",");

		this.reload();
	}

	/**
	 * @param name
	 * @param script
	 */
	public LocalJavaScriptEngineObject(String name, String script) {
		super();

		this.name = name;
		this.names = StringUtils.split(this.name, ",");

		this.script = script;

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the names
	 */
	public String[] getNames() {
		return this.names;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean reload() {
		if (FileUtil.isValid(this.file)) {
			try {
				this.script = FileUtils.readFileToString(this.file, StringUtil.CODEING_UTF8);
				return true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return false;
		}
	}

	/**
	 * Get the java script engien.<BR>
	 * If the engine is null,a new engine will be created.<BR>
	 * 
	 * @throws ScriptException
	 */
	public synchronized ScriptEngine getScriptEngine() throws ScriptException {

		if (null == this.scriptEngine) {
			this.scriptEngine = JavaScriptEngineManageer.getInstance().createEngine();
			this.scriptEngine.eval(this.script);
		}

		return this.scriptEngine;
	}
}
