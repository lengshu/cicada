/**
 *
 */
package org.aquarius.util.nls;

/**
 * NLS resource.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface NlsResource {

	/**
	 * Return the string for the key in the default locale.<BR>
	 *
	 * @param key
	 * @return
	 */
	public default String getValue(String key) {
		return this.getValue(key, "error=" + key);
	}

	/**
	 * Return the string for the key in the default locale.<BR>
	 *
	 * @param key
	 * @param defaultValue if the value is not exist,use the default value to
	 *                     replace.<BR>
	 * @return
	 */
	public String getValue(String key, String defaultValue);

}
