/**
 * 
 */
package org.aquarius.util;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public abstract class LockObject {

	private Object owner;

	/**
	 * 
	 */
	public LockObject() {
		super();
	}

	/**
	 * 
	 * @param owner
	 */
	protected synchronized boolean lock(Object owner) {
		AssertUtil.assertNotNull(owner, "the owner should not be null.");

		if (null == this.owner) {
			this.owner = owner;
			return true;
		} else {
			return this.owner == owner;
		}
	}

	/**
	 * 
	 * @param owner
	 * @return
	 */
	public synchronized boolean isOwner(Object owner) {
		if (null == owner) {
			return false;
		}

		return this.owner == owner;
	}

	protected synchronized boolean unlock(Object owner) {
		AssertUtil.assertNotNull(owner, "the owner should not be null.");

		if (null == this.owner) {
			return true;
		}
		if (owner == this.owner) {
			this.owner = null;
			return true;
		} else {
			return false;
		}
	}
}
