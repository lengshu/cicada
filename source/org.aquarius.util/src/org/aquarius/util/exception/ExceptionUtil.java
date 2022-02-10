/**
 *
 */
package org.aquarius.util.exception;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class ExceptionUtil {

	/**
	 *
	 */
	private ExceptionUtil() {
		// No instance needed
	}

	/**
	 * If the exception is a runtime exception, throw it.<BR>
	 * Else create a runtime exception with the specified exception and throw
	 * it.<BR>
	 *
	 * @param exception
	 */
	public static void throwRuntimeException(Throwable exception) {
		if (null != exception) {
			if (exception instanceof RuntimeException) {
				throw (RuntimeException) exception;
			} else {
				throw new RuntimeException(exception);
			}
		}
	}

}
