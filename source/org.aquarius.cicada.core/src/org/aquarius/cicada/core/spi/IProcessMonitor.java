/**
 *
 */
package org.aquarius.cicada.core.spi;

/**
 * A process monitor .<BR>
 * In fact,it is designed to avoid refer Eclipse progress monitor directly.
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IProcessMonitor {

	/**
	 * Cancel the task
	 */
	public void cancel();

	/**
	 * Whether the task is canceled.
	 *
	 * @return
	 */
	public boolean isCanceled();

	/**
	 * Update the task name
	 *
	 * @param name
	 */
	public void setName(String name);

}
