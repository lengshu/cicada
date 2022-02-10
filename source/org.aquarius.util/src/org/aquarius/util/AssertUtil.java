/**
 *
 */

package org.aquarius.util;

/**
 * Assert function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class AssertUtil {

	/**
	 * No instances needed.<BR>
	 *
	 */
	private AssertUtil() {
	}

	/**
	 * Assert value is true.<BR>
	 * Or throw IllegalArgumentException with specified message.<BR>
	 *
	 * @param value
	 * @param errorMessage
	 */
	public static void assertTrue(boolean value, String errorMessage) {
		if (false == value) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * Assert value is not null.<BR>
	 * Or throw IllegalArgumentException with specified message.<BR>
	 *
	 * @param parameter
	 * @param errorMessage
	 */
	public static void assertNotNull(Object parameter, String errorMessage) {
		if (null == parameter) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * Assert value is not null.<BR>
	 *
	 * @param parameter
	 */
	public static void assertNotNull(Object parameter) {
		assertNotNull(parameter, "The parameter should not be null.");
	}

	/**
	 * Assert value is null.<BR>
	 *
	 * @param parameter
	 */
	public static void assertNull(Object parameter) {
		assertNull(parameter, "The parameter should  be null.");
	}

	/**
	 * Assert value is null.<BR>
	 * Or throw IllegalArgumentException with specified message.<BR>
	 *
	 * @param parameter
	 * @param errorMessage
	 */
	public static void assertNull(Object parameter, String errorMessage) {
		if (null != parameter) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

}
