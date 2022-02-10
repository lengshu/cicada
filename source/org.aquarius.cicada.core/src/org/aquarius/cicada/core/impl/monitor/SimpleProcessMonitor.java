/**
 *
 */
package org.aquarius.cicada.core.impl.monitor;

import org.aquarius.cicada.core.spi.IProcessMonitor;

/**
 * A simple implementation for IProcessMonitor
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SimpleProcessMonitor implements IProcessMonitor {

	private boolean canceled = false;

	private String name;

	@Override
	public void cancel() {
		this.canceled = true;
	}

	@Override
	public boolean isCanceled() {
		return this.canceled;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setName(String name) {
		this.name = name;

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

}
