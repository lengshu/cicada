/**
 *
 */
package org.aquarius.service.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.aquarius.service.INameService;
import org.aquarius.service.IReloadable;
import org.aquarius.util.AssertUtil;

/**
 *
 * A service manager which can register and find services.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ServiceManager<T extends INameService<?>> {

	/**
	 * Use tree set to sort the services
	 */
	private Set<T> services = new ListOrderedSet<T>();

	/**
	 * register a service.
	 *
	 * @param service it should not be null.
	 */
	public synchronized void registerService(T service) {

		AssertUtil.assertNotNull(service);
		AssertUtil.assertNotNull(service.getName());

		if (service instanceof IReloadable) {
			ReloadManager.getInstance().register((IReloadable) service);
		}

		this.services.add(service);
	}

	/**
	 * unregister a service.
	 *
	 * @param service it should not be null.
	 */
	public synchronized void unregisterService(T service) {

		AssertUtil.assertNotNull(service);
		AssertUtil.assertNotNull(service.getName());

		this.services.remove(service);
	}

	/**
	 *
	 * register multi serivces.
	 *
	 * @param services it can't be null.
	 */
	public synchronized void registerServices(T... services) {

		AssertUtil.assertNotNull(services);

		for (T service : services) {
			this.registerService(service);

			if (service instanceof IReloadable) {
				ReloadManager.getInstance().register((IReloadable) service);
			}

		}
	}

	/**
	 *
	 * unregister multi serivces.
	 *
	 * @param services it can't be null.
	 */
	public synchronized void unregisterServices(T... services) {

		AssertUtil.assertNotNull(services);

		for (T serivce : services) {
			this.unregisterService(serivce);

			ReloadManager.getInstance().unregister((IReloadable) serivce);
		}
	}

	/**
	 * Find the first priority service with a specified name.
	 *
	 * @param name
	 * @return
	 */
	public synchronized T findService(String name) {

		for (T service : this.services) {
			if (service.getName().equals(name)) {
				return service;
			}
		}

		return null;
	}

	/**
	 * Return multi services with the specified service name
	 *
	 * @param name
	 * @return
	 */
	public synchronized List<T> findServices(String name) {

		ListOrderedSet<T> serviceSet = new ListOrderedSet<>();

		for (T service : this.services) {
			if (service.getName().equals(name)) {
				serviceSet.add(service);
			}
		}

		return serviceSet.asList();
	}

	/**
	 * Return all the registered services with asc order.
	 *
	 * @return
	 */
	public synchronized List<T> getAllServices() {
		return new ArrayList<T>(this.services);
	}

	/**
	 * Return all the names of registered services with asc order.
	 *
	 * @return
	 */
	public synchronized List<String> getAllNames() {
		ListOrderedSet<String> nameSet = new ListOrderedSet<>();

		for (T service : this.services) {
			nameSet.add(service.getName());
		}

		return nameSet.asList();
	}

}
