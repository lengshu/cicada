/**
 *
 */
package org.aquarius.util.js;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Engine manager for javascript.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class JavaScriptEngineManageer {

	private static final JavaScriptEngineManageer instance = new JavaScriptEngineManageer();

	private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

	/**
	 *
	 */
	private JavaScriptEngineManageer() {
		super();
	}

	/**
	 * @return the instance
	 */
	public static JavaScriptEngineManageer getInstance() {
		return instance;
	}

	/**
	 * Create a javascript engine.<BR>
	 *
	 * @return
	 */
	public ScriptEngine createEngine() {
		return this.scriptEngineManager.getEngineByName("javascript");
	}

}
