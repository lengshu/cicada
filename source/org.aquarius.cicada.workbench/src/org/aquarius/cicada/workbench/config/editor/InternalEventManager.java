/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import org.eclipse.core.runtime.ListenerList;

/**
 * use event manager simply.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class InternalEventManager {

	/**
	 * An empty array that can be returned from a call to {@link #getListeners()}
	 * when {@link #listenerList} is <code>null</code>.
	 */
	private static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * A collection of objects listening to changes to this manager. This collection
	 * is <code>null</code> if there are no listeners.
	 */
	private volatile transient ListenerList<Object> listenerList = null;

	/**
	 * Adds a listener to this manager that will be notified when this manager's
	 * state changes. This method has no effect if the same listener is already
	 * registered.
	 *
	 * @param listener The listener to be added; must not be <code>null</code>.
	 */
	public synchronized final void addListenerObject(final Object listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}

		if (this.listenerList == null) {
			this.listenerList = new ListenerList<>(ListenerList.IDENTITY);
		}

		this.listenerList.add(listener);
	}

	/**
	 * Clears all of the listeners from the listener list.
	 */
	public final void clearListeners() {
		this.listenerList = null;
	}

	/**
	 * Returns an array containing all the listeners attached to this event manager.
	 * The resulting array is unaffected by subsequent adds or removes. If there are
	 * no listeners registered, the result is an empty array. Use this method when
	 * notifying listeners, so that any modifications to the listener list during
	 * the notification will have no effect on the notification itself.
	 * <p>
	 * Note: Callers of this method <b>must not</b> modify the returned array.
	 * </p>
	 *
	 * @return The listeners currently attached; may be empty, but never
	 *         <code>null</code>
	 */
	public final Object[] getListeners() {
		final ListenerList<Object> list = this.listenerList;
		if (list == null) {
			return EMPTY_ARRAY;
		}

		return list.getListeners();
	}

	/**
	 * Whether one or more listeners are attached to the manager.
	 *
	 * @return <code>true</code> if listeners are attached to the manager;
	 *         <code>false</code> otherwise.
	 */
	public final boolean isListenerAttached() {
		return this.listenerList != null;
	}

	/**
	 * Removes a listener from this manager. Has no effect if the same listener was
	 * not already registered.
	 *
	 * @param listener The listener to be removed; must not be <code>null</code>.
	 */
	public synchronized final void removeListenerObject(final Object listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}

		if (this.listenerList != null) {
			this.listenerList.remove(listener);

			if (this.listenerList.isEmpty()) {
				this.listenerList = null;
			}
		}
	}
}
