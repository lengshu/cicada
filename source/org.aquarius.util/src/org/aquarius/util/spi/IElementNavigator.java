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

	public default T nextElement() {
		return skipElement(1);
	}

	public default T previousElement() {
		return skipElement(-1);
	}

	public boolean locate(T element);

	public T skipElement(int step);

}
