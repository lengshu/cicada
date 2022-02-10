/**
 *
 */
package org.aquarius.cicada.core.impl.monitor;

import org.aquarius.cicada.core.spi.IProcessMonitor;

/**
 *
 * Null implementation.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DefaultProcessMonitor implements IProcessMonitor {

	private boolean canceled = false;

	private String name;

	/**
	 *
	 */
	private DefaultProcessMonitor() {
		super();
	}

	/**
	 * @return the instance
	 */
	public static DefaultProcessMonitor createInstance() {
		return new DefaultProcessMonitor();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void cancel() {
		this.canceled = true;
	}

	/**
	 * {@inheritDoc}}
	 */
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
	 * 
	 * @param processMonitor
	 * @return
	 */
	public static IProcessMonitor wrapProgressMonitor(IProcessMonitor processMonitor) {
		if (null == processMonitor) {
			return DefaultProcessMonitor.createInstance();
		}

		return processMonitor;
	}

}
