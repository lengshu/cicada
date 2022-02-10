/**
 *
 */
package org.aquarius.cicada.core.spi.web;

import java.io.Closeable;

import org.aquarius.service.INameService;

/**
 * @author aquarius.github@gmail.com
 *
 */
public interface IWebAccessorService extends INameService<IWebAccessorService>, Closeable {

	@FunctionalInterface
	public interface Visitor<T> {
		public T visit(IWebAccessor webAccessor);
	}

	String AboutBlank = "about:blank";

	@FunctionalInterface
	public interface Initializer<T> {
		public void init(T contenxt);
	}

	/**
	 * Some web accessor need to be inited.
	 */
	public default <T> void start(Initializer<T> initializer) {

	}

	/**
	 * Because web accessor like swt browser of chrome need to manage resources.<BR>
	 * So a visitor is designed to visit web pages like a sand box.<BR>
	 *
	 * @param <T>
	 * @param visitor
	 * @return
	 */
	public <T> T visit(Visitor<T> visitor);

	/**
	 * Sometime ,the user need do some operations to get better experience like
	 * logining and language setting.<BR>
	 */
	public default void openBrowserForSetting() {

	}

	/**
	 * Open a specified web page for user setting.
	 *
	 * @param urlString
	 */
	public default void openBrowserForSetting(String urlString) {

	}

	/**
	 * Make the browser visible to debug.
	 *
	 * @param visible
	 */
	public default void setVisible(boolean visible) {

	}

	/**
	 * Whether the web accessor can work or not.
	 *
	 * @return
	 */
	public default boolean isValid() {
		return true;
	}

	/**
	 * Return the web accessor visible state.<BR>
	 *
	 * @return
	 */
	public default IWebAccessorVisible getVisibleState() {
		return IWebAccessorVisible.hide;
	}

}
