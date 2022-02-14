/**
 * 
 */
package org.aquarius.cicada.core.impl.processor;

import java.io.File;

import javax.script.Invocable;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.script.LocalJavaScriptEngineObject;
import org.aquarius.cicada.core.spi.AbstractMovieInfoProcssor;
import org.aquarius.log.LogUtil;
import org.aquarius.service.IReloadable;
import org.slf4j.Logger;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class LocalJavaScriptMovieInfoProcssor extends AbstractMovieInfoProcssor implements IReloadable {

	private Logger log = LogUtil.getLogger(getClass());

	private LocalJavaScriptEngineObject localJavaScriptEngineObject;

	/**
	 * 
	 */
	public LocalJavaScriptMovieInfoProcssor(File file) {
		super();

		this.localJavaScriptEngineObject = new LocalJavaScriptEngineObject(file);
	}

	/**
	 * 
	 */
	public LocalJavaScriptMovieInfoProcssor(String name, File file) {
		super();

		this.localJavaScriptEngineObject = new LocalJavaScriptEngineObject(name, file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.localJavaScriptEngineObject.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAcceptable(String urlString) {
		return StringUtils.containsIgnoreCase(urlString, this.localJavaScriptEngineObject.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Movie movie) {
		try {
			Invocable invocable = (Invocable) this.localJavaScriptEngineObject.getScriptEngine();
			invocable.invokeFunction("doProcess", movie);

		} catch (Exception e) {
			this.log.error(this.localJavaScriptEngineObject.getName() + " process error ", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean reload() {
		return this.localJavaScriptEngineObject.reload();
	}

}
