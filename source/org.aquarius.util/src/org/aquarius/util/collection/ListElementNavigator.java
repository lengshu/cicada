/**
 * 
 */
package org.aquarius.util.collection;

import java.util.List;

import org.aquarius.util.spi.IElementNavigator;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class ListElementNavigator<T> implements IElementNavigator<T> {

	private List<T> elementList;

	private int currentLocation = -1;

	/**
	 * @param elementList
	 */
	public ListElementNavigator(List<T> elementList) {
		super();
		this.elementList = elementList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasPreviousElement() {
		int index = this.currentLocation - 1;
		return (index >= 0) && ((this.elementList.size() > index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNextElement() {
		int index = this.currentLocation + 1;
		return (index >= 0) && ((this.elementList.size() > index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T skipElement(int step) {
		int index = this.currentLocation + step;
		return movieLocation(index);
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	private T movieLocation(int index) {
		if ((index >= 0) && ((this.elementList.size() > index))) {
			this.currentLocation = index;
			return this.elementList.get(index);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean locate(T element) {
		int location = this.elementList.indexOf(element);

		if (location >= 0) {
			this.currentLocation = location;
			return true;
		} else {
			return false;
		}
	}

}
