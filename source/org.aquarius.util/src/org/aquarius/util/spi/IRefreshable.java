/**
 *
 */
package org.aquarius.util.spi;

/**
 *
 * A subclass means it may support data refresh.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IRefreshable {

	/**
	 * Full refresh to view new data.<BR>
	 */
	public void refresh();

}
