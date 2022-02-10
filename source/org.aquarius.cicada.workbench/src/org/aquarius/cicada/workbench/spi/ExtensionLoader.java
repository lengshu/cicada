/**
 *
 */
package org.aquarius.cicada.workbench.spi;

import org.aquarius.service.INameService;

/**
 * @author aquarius.github@gmail.com
 *
 */
public interface ExtensionLoader extends INameService<INameService<?>> {

	/**
	 *
	 */
	public void load();

	/**
	 * {@inheritDoc}
	 */
	@Override
	default String getName() {
		return this.getClass().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	default int compareTo(INameService<?> object) {

		if (this == object) {
			return 0;
		}

		return 1;
	}

}
