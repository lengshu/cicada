/**
 *
 */
package org.aquarius.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.util.AssertUtil;

/**
 * collection function provider.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CollectionUtil {

	/**
	 * No instances will be created.
	 */
	private CollectionUtil() {

	}

	/**
	 * Split a content to a map.<BR>
	 * For example, the string "a=1,b=2" will be split into a=1 and b=2.
	 *
	 * @param content
	 * @return
	 */
	public static Map<String, String> splitToMap(String content) {
		Map<String, String> map = new HashMap<>();

		if (StringUtils.isEmpty(content)) {
			return map;
		}

		String[] entries = StringUtils.split(content, ",");

		for (String entry : entries) {
			String[] stringArray = StringUtils.split(entry, "=");
			if (stringArray.length == 2) {
				map.put(stringArray[0], stringArray[1]);
			}
		}

		return map;
	}

	/**
	 *
	 * Sub a specified amount element from the list.<BR>
	 *
	 * @param <T>
	 * @param list
	 * @param start
	 * @param end   if the end is over the size of the list,it will be the size of
	 *              the list.<BR>
	 * @return
	 */
	public static <T> List<T> subList(List<T> list, int start, int end) {

		AssertUtil.assertTrue((start >= 0), "The start should >0");

		if (start == end) {
			return new ArrayList<T>();
		}

		if (end >= list.size()) {
			end = list.size();
		}

		return new ArrayList<T>(list.subList(start, end));
	}

	/**
	 *
	 * Sub a specified amount element from the list and remove them from the
	 * list.<BR>
	 *
	 * @param <T>
	 * @param list
	 * @param start
	 * @param end
	 * @return
	 */
	public static <T> List<T> removeList(List<T> list, int start, int end) {

		AssertUtil.assertTrue((start >= 0), "The start should >0");

		if (end >= list.size()) {
			end = list.size();
		}

		List<T> resultList = new ArrayList<>();
		for (int i = start; i < end; i++) {
			resultList.add(list.remove(start));
		}

		return resultList;
	}

	/**
	 * Return the first element of the list.<BR>
	 *
	 * @param <T>
	 * @param col
	 * @return
	 */
	public static <T> T findFirstElement(Collection<T> col) {
		if (CollectionUtils.isEmpty(col)) {
			return null;
		} else {
			return IterableUtils.get(col, 0);
		}
	}

	/**
	 * Remove duplicated elements。<BR>
	 * 
	 * @param <T>
	 * @param strings
	 * @return
	 */
	public static <T> List<T> removeDuplicated(T[] elements) {
		Set<T> set = new ListOrderedSet<>();

		CollectionUtils.addAll(set, elements);

		return new ArrayList<T>(set);
	}

	/**
	 * Remove duplicated elements。<BR>
	 * 
	 * @param <T>
	 * @param strings
	 * @return
	 */
	public static <T> List<String> convertToStringList(Collection<T> elements) {
		List<String> list = new ArrayList<>();

		for (T object : elements) {
			list.add(ObjectUtils.toString(object));
		}

		return list;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public static final String toCookieString(Map<?, ?> map) {
		StringJoiner stringJoiner = new StringJoiner("; ");

		for (Entry<?, ?> entry : map.entrySet()) {
			stringJoiner.add(ObjectUtils.toString(entry.getKey()) + "=" + ObjectUtils.toString(entry.getValue()));
		}

		return stringJoiner.toString();
	}
}
