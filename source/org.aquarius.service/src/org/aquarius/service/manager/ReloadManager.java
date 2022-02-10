/**
 * 
 */
package org.aquarius.service.manager;

import java.util.Collection;
import java.util.HashSet;

import org.aquarius.log.LogUtil;
import org.aquarius.service.IReloadable;
import org.slf4j.Logger;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public final class ReloadManager {

	private static final ReloadManager instance = new ReloadManager();

	private Logger log = LogUtil.getLogger(getClass());

	private Collection<IReloadable> elements = new HashSet<>();

	/**
	 * 
	 */
	private ReloadManager() {
		// Just one instancee.<BR>
	}

	/**
	 * Reload all elements.<BR>
	 */
	public void reloadAll() {

		for (IReloadable reloadable : this.elements) {
			try {

				reloadable.reload();

			} catch (Exception e) {

				this.log.error("reloadAll", e);

			}
		}

	}

	/**
	 * @return the instance
	 */
	public static ReloadManager getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param reloadable
	 */
	public void register(IReloadable reloadable) {
		if (null != reloadable) {
			this.elements.add(reloadable);
		}
	}

	/**
	 * 
	 * @param reloadable
	 */
	public void unregister(IReloadable reloadable) {
		if (null != reloadable) {
			this.elements.remove(reloadable);
		}
	}

}
