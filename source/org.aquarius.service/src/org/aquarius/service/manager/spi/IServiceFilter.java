/**
 * 
 */
package org.aquarius.service.manager.spi;

/**
 * It's used to filter a service.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public interface IServiceFilter<T> {

	/**
	 * If a object is filtered,means this service of the object is disabled.<BR>
	 * 
	 * @param object
	 * @return
	 */
	public boolean isFiltered(T object);

}
