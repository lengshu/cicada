/**
 * 
 */
package org.aquarius.util.mark;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public interface IUrlAcceptable {

	/**
	 * Return whether the renamer can support the specified site.<BR>
	 * 
	 * @param urlString
	 * @return
	 */
	public boolean isAcceptable(String urlString);

}
