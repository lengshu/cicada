/**
 *
 */
package org.aquarius.cicada.core.impl.analyser;

import org.aquarius.cicada.core.spi.base.AbstractFvsDownloadUrlAnalyser;

/**
 * for the site of "asian club".<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DefaultFvsDownloadUrlAnalyser extends AbstractFvsDownloadUrlAnalyser {

	private String name;

	/**
	 * @param name
	 */
	public DefaultFvsDownloadUrlAnalyser(String name) {
		super();
		this.name = name;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return this.name;
	}

}
