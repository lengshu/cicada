/**
 *
 */
package org.aquarius.service.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.util.AssertUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class ServiceLocator<T> {

	private Map<Class<?>, Set<T>> services = new HashMap<Class<?>, Set<T>>();

	public synchronized void registerService(Class<T> clazz, T service) {

		AssertUtil.assertNotNull(service);
		Set<T> values = this.services.get(clazz);

		if (null == values) {
			values = new TreeSet<T>();
			values.add(service);
		}

		values.add(service);
	}

	public synchronized T findService(Class<T> clazz) {
		AssertUtil.assertNotNull(clazz);

		Set<T> values = this.services.get(clazz);

		if (CollectionUtils.isEmpty(values)) {
			return null;
		}

		return values.iterator().next();
	}

	public synchronized List<T> findServices(Class<T> clazz) {
		AssertUtil.assertNotNull(clazz);

		Set<T> values = this.services.get(clazz);

		if (null == values || values.isEmpty()) {
			return new ArrayList<T>();
		}

		return new ArrayList<T>(values);
	}

}
