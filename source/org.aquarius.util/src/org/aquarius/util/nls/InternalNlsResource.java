/**
 *
 */
package org.aquarius.util.nls;

import java.util.ResourceBundle;

/**
 * Use resource bundle to support nls.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class InternalNlsResource implements NlsResource {

	private ResourceBundle resourceBundle;

	/**
	 * @param resourceBundle
	 */
	public InternalNlsResource(ResourceBundle resourceBundle) {
		super();
		this.resourceBundle = resourceBundle;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getValue(String key, String defaultValue) {

		if (this.resourceBundle.containsKey(key)) {
			return this.resourceBundle.getString(key);
		} else {
			return defaultValue;
		}
	}

}
