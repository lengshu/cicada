/**
 *
 */
package org.aquarius.log;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log util class to support Eclipse Log Viewer.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LogUtil {

	/**
	 *
	 */
	private LogUtil() {
		// No instances needed.
	}

	/**
	 * Create a logger with the eclipse bundle logger for the class.<BR>
	 *
	 * @param clazz
	 * @return
	 */
	public static final Logger getLogger(Class<?> clazz) {
		ClassLoader classLoader = clazz.getClassLoader();

		if (classLoader instanceof EquinoxClassLoader) {
			EquinoxClassLoader equinoxClassLoader = (EquinoxClassLoader) classLoader;

			Bundle bundle = equinoxClassLoader.getBundle();
			ILog log = Platform.getLog(bundle);

			return new EclipseLogger(log, clazz.getName());
		}

		return LoggerFactory.getLogger(clazz);
	}
}
