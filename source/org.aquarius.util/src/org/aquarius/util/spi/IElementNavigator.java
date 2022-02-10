/**
 * 
 */
package org.aquarius.util.spi;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public interface IElementNavigator<T> {

	public boolean hasPreviousElement();

	public boolean hasNextElement();

	public T nextElement();

	public T previousElement();

	public boolean locate(T element);

}
