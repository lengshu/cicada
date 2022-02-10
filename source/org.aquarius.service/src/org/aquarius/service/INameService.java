/**
 *
 */
package org.aquarius.service;

/**
 * Service interface .
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface INameService<T extends INameService<?>> extends Comparable<T> {

	/**
	 *
	 * @return
	 */
	public String getName();
}
