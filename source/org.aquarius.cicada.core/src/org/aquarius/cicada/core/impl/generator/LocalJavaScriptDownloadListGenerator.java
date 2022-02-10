/**
 *
 */
package org.aquarius.cicada.core.impl.generator;

import java.io.File;
import java.util.List;

import javax.script.Invocable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.script.LocalJavaScriptEngineObject;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;

/**
 * Use java script to generate download list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LocalJavaScriptDownloadListGenerator extends AbstractDownloadListGenerator {

	private LocalJavaScriptEngineObject localJavaScriptEngineObject;

	/**
	 * @param name
	 * @param script
	 */
	public LocalJavaScriptDownloadListGenerator(File file) {
		super();

		this.localJavaScriptEngineObject = new LocalJavaScriptEngineObject(file);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.localJavaScriptEngineObject.getName();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String generateDownloadList(List<Movie> movieList, String downloadFolder) {

		try {

			Invocable invocable = (Invocable) this.localJavaScriptEngineObject.getScriptEngine();
			Object result = invocable.invokeFunction("doGenerateDownloadList", movieList, downloadFolder);

			return ObjectUtils.toString(result);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isUsePrimitiveUrl() {

		try {
			Invocable invocable = (Invocable) this.localJavaScriptEngineObject.getScriptEngine();

			Object result = invocable.invokeFunction("isUsePrimitiveUrl");
			String resultString = ObjectUtils.toString(result, "true");
			return BooleanUtils.toBoolean(resultString);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
