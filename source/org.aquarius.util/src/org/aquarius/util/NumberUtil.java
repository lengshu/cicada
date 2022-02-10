/**
 *
 */
package org.aquarius.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Number function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NumberUtil {

	private static final Pattern NumberRegex = Pattern.compile("(\\d+(\\.\\d+)?)");

	/**
	 *
	 */
	private NumberUtil() {
		// No instances needed.
	}

	/**
	 * Return the integer value.<BR>
	 *
	 * @param value if it is null,it will return 0.
	 * @return
	 */
	public static int getIntValue(Integer value) {
		return getIntValue(value, 0);
	}

	/**
	 * Return the integer value.<BR>
	 *
	 * @param value        if it is null,it will return the specified default value.
	 *
	 * @param defaultValue
	 * @return
	 */
	public static int getIntValue(Integer value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			return value.intValue();
		}
	}

	/**
	 * parse the string to get the first number.<BR>
	 *
	 * @param numberString
	 * @return
	 */
	public static String parseFirstNumber(String numberString) {
		Matcher matcher = NumberRegex.matcher(numberString);
		if (matcher.find()) {
			return matcher.group(0);
		}

		return null;
	}

	/**
	 * parse the string to get the first number.<BR>
	 *
	 * @param numberString
	 * @return
	 */
	public static String[] parseNumbers(String numberString) {
		Matcher matcher = NumberRegex.matcher(numberString);

		List<String> resultList = new ArrayList<>();

		while (matcher.find()) {
			resultList.add(matcher.group());
		}

		String[] values = new String[resultList.size()];
		return resultList.toArray(values);
	}

}
