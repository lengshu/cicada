/**
 *
 */
package org.aquarius.util.io;

import java.io.Closeable;

/**
 * IO Function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class IOUtil {
	/**
	 *
	 */
	private IOUtil() {
		// No instance needed
	}

	/**
	 * Close a resource without exception
	 *
	 * @param resource
	 */
	public static final void closeQuietly(Closeable resource) {
		try {
			if (null != resource) {
				resource.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Nothing to do,just a log
		}
	}

	/**
	 * Close a resource without exception
	 *
	 * @param resource
	 */
	public static final void closeQuietly(AutoCloseable resource) {
		try {
			if (null != resource) {
				resource.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Nothing to do,just a log
		}
	}

}
