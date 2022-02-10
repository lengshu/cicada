/**
 *
 */
package org.aquarius.cicada.workbench.monitor;

import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A proxy for eclipse progress monitor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ProcessMonitorProxy implements IProcessMonitor {

	private IProgressMonitor monitor;

	/**
	 * @param monitor
	 */
	public ProcessMonitorProxy(IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void cancel() {
		this.monitor.setCanceled(true);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isCanceled() {
		return this.monitor.isCanceled();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setName(String name) {
		this.monitor.setTaskName(name);
	}

}
