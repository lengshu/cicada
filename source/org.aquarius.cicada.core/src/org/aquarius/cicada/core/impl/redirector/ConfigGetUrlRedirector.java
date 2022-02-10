/**
 * 
 */
package org.aquarius.cicada.core.impl.redirector;

import java.text.MessageFormat;

import org.apache.commons.lang.ArrayUtils;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.util.DesktopUtil;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class ConfigGetUrlRedirector extends AbstractUrlRedirector {

	private String name;

	private String urlPattern;

	private boolean usePrimitiveUrl;

	/**
	 * @param name
	 * @param urlPattern
	 */
	public ConfigGetUrlRedirector(String name, String urlPattern, boolean usePrimitiveUrl) {
		super();
		this.name = name;
		this.urlPattern = urlPattern;
		this.usePrimitiveUrl = usePrimitiveUrl;

		this.setPriority(Low);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUsePrimitiveUrl() {
		return this.usePrimitiveUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redirect(String... urlStrings) {

		if (ArrayUtils.isEmpty(urlStrings)) {
			return;
		}

		for (String urlString : urlStrings) {
			String newUrlString = MessageFormat.format(this.urlPattern, urlString);

			try {
				DesktopUtil.openWebpage(newUrlString);
			} catch (Exception e) {
				// Nothing to do
			}
		}
	}

}
