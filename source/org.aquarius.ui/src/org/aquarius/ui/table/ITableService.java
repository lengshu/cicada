/**
 *
 */
package org.aquarius.ui.table;

/**
 * Operations for table.<BR>
 * 
 * @author aquarius.github@gmail.com
 *
 */
public interface ITableService {

	/**
	 * Move the selected elements up.<BR>
	 */
	public void moveUp();

	/**
	 * Move the selected elements down.<BR>
	 */
	public void moveDown();

	/**
	 * Remove the selected elements.<BR>
	 */
	public void remove();

	/**
	 * Move the selected elements to the top.<BR>
	 */
	public void moveTop();

	/**
	 * Move the selected elements to the bottom.<BR>
	 */
	public void moveBottom();

}
