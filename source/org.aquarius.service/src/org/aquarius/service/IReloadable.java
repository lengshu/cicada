/**
 * 
 */
package org.aquarius.service;

/**
 * Implementing the interface to indicates whether the current object can be
 * reload.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public interface IReloadable {

	/**
	 * 
	 * This object support reload.<BR>
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean reload();

}
