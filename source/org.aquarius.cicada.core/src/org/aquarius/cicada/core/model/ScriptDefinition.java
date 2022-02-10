/**
 *
 */
package org.aquarius.cicada.core.model;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class ScriptDefinition {

	private transient String script;

	private String checkFlag;

	private String webAccessorName;

	private String scriptEngine = "javascript";

	/**
	 * @return the script
	 */
	public String getScript() {
		return this.script;
	}

	/**
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * @return the checkFlag
	 */
	public String getCheckFlag() {
		return this.checkFlag;
	}

	/**
	 * @param checkFlag the checkFlag to set
	 */
	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}

	/**
	 * @return the webAccessorName
	 */
	public String getWebAccessorName() {
		return this.webAccessorName;
	}

	/**
	 * @param webAccessorName the webAccessorName to set
	 */
	public void setWebAccessorName(String webAccessorName) {
		this.webAccessorName = webAccessorName;
	}

	/**
	 * @return the scriptEngine
	 */
	public String getScriptEngine() {
		return this.scriptEngine;
	}

	/**
	 * @param scriptEngine the scriptEngine to set
	 */
	public void setScriptEngine(String scriptEngine) {
		this.scriptEngine = scriptEngine;
	}
}
