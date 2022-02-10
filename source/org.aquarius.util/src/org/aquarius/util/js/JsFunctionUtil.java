/**
 *
 */
package org.aquarius.util.js;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class JsFunctionUtil {

	/**
	 *
	 */
	public JsFunctionUtil() {
		super();
	}

	public Document visitWebByGet(String urlString) throws IOException {
		return Jsoup.parse(new URL(urlString), 10000);
	}

	public Document visitWebByPost(String urlString, String parameter) throws IOException {
		Connection connection = this.createHttpConnection(urlString);
		connection.data(parameter);

		return connection.post();
	}

	public Connection createHttpConnection(String urlString) {
		return Jsoup.connect(urlString);
	}
}
