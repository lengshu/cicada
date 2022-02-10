/**
 *
 */
package org.aquarius.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * String function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class StringUtil {

	public static final String CODEING_UTF8 = "UTF-8";

	public static final String CODEING_ANSI = "ISO-8859-1";

	public static final String ContentSeparator = ",";

	public static String Star = "★";

	public static List<String> NullStringList = Collections.unmodifiableList(new ArrayList<>());

	/**
	 * No instances will be created.
	 */
	private StringUtil() {
	}

	/**
	 * Return whether the strings equals.<BR>
	 * If both of them are null,empty or blank,they will be treated as equals.<BR>
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static boolean equalsIgnoreBlank(String string1, String string2) {
		if (StringUtils.isBlank(string1) && StringUtils.isBlank(string2)) {
			return true;
		}

		return StringUtils.equals(string1, string2);
	}

	/**
	 * Return whether the strings equals with ignore case.<BR>
	 * If both of them are null,empty or blank,they will be treated as equals.<BR>
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static boolean equalsIgnoreBlankAndCase(String string1, String string2) {
		if (StringUtils.isBlank(string1) && StringUtils.isBlank(string2)) {
			return true;
		}

		return StringUtils.equalsIgnoreCase(string1, string2);
	}

	/**
	 * Remove special char to support file.<BR>
	 *
	 * @param title
	 * @return
	 */
	public static String getTitleForFile(String title) {

		for (int i = 0; i < 2; i++) {
			title = StringUtils.replace(title, "?", " ");
			title = StringUtils.replace(title, "!", " ");
			title = StringUtils.replace(title, "@", " ");
			title = StringUtils.replace(title, "|", " ");
			title = StringUtils.replace(title, ".", " ");
			title = StringUtils.replace(title, "\"", " ");
			title = StringUtils.replace(title, "'", " ");
			title = StringUtils.replace(title, "&", " ");
			title = StringUtils.replace(title, "*", " ");
			title = StringUtils.replace(title, "/", " ");
			title = StringUtils.replace(title, ":", " ");
			// title = StringUtils.replace(title, "[", " ");
			// title = StringUtils.replace(title, "]", " ");
		}

		return title.trim();
	}

	/**
	 * Split a text to multi string by line.<BR>
	 *
	 * @param text
	 * @return
	 */
	public static String[] toLines(String text) {
		if (StringUtils.isEmpty(text)) {
			return new String[] {};
		} else {
			return text.split("\\r?\\n");
		}
	}

	/**
	 * If the content contains any keyword with ignore case.<BR>
	 * Then return true.<BR>
	 *
	 * @param content
	 * @param keywords
	 * @return
	 */
	public static boolean containsAnyIgnoreCase(String content, String... keywords) {
		if (ArrayUtils.isEmpty(keywords)) {
			return false;
		}

		if (StringUtils.isBlank(content)) {
			return false;
		}

		for (String keyword : keywords) {
			if (StringUtils.containsIgnoreCase(content, keyword)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * If one of the contents contains the keyword.<BR>
	 * Then return true.
	 *
	 * @param keyword
	 * @param contents
	 * @return
	 */
	public static boolean findKeywordInRangeIgnoreCase(String keyword, String... contents) {
		if (ArrayUtils.isEmpty(contents)) {
			return false;
		}

		if (StringUtils.isBlank(keyword)) {
			return true;
		}

		boolean hasWildcardSymbol = StringUtil.hasWildcardSymbol(keyword);

		for (String content : contents) {

			if (hasWildcardSymbol) {
				if (StringUtil.isWildcardMatch(content, keyword, false)) {
					return true;
				}
			} else {
				if (StringUtils.containsIgnoreCase(content, keyword)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Return specified count symbol.<BR>
	 *
	 * @param symbol
	 * @param count
	 * @return
	 */
	public static String repeatSymbol(String symbol, int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			builder.append(symbol);
		}

		return builder.toString();
	}

	/**
	 * Return whether the keyword has wild card symbol of "*" or "?".<BR>
	 * 
	 * @param keyword
	 * @return
	 */
	public static boolean hasWildcardSymbol(String keyword) {
		return StringUtils.containsIgnoreCase(keyword, "*") || StringUtils.containsIgnoreCase(keyword, "?");
	}

	/**
	 * 
	 *
	 * This method is used match 2 strings wildly.<BR>
	 * The char of '*' and '?' can be used.<BR>
	 *
	 * For Example: isWildcardMatch("username", "u*er*",true) --> true
	 * isWildcardMatch("username", "u?er*",true) --> true
	 * isWildcardMatch("username", "u*ter*",true) --> false
	 * isWildcardMatch("username", "U*er*",true) --> false
	 * isWildcardMatch("username", "U*er*",false) --> true
	 *
	 * @param keyword
	 * @param wildcardMatcher
	 * @param caseSensitive
	 * @return
	 */
	public static boolean isWildcardMatch(String keyword, String wildcardMatcher, boolean caseSensitive) {
		if (keyword == null && wildcardMatcher == null) {
			return true;
		}
		if (keyword == null || wildcardMatcher == null) {
			return false;
		}
		if (!caseSensitive) {
			keyword = keyword.toLowerCase();
			wildcardMatcher = wildcardMatcher.toLowerCase();
		}
		String[] splitValues = splitOnTokens(wildcardMatcher);
		boolean chars = false;
		int index = 0;
		int wildIndex = 0;
		Stack<int[]> backStack = new Stack<>();

		// loop around a backtrack stack, to handle complex * matching
		do {
			if (backStack.size() > 0) {
				int[] array = backStack.pop();
				wildIndex = array[0];
				index = array[1];
				chars = true;
			}

			// loop whilst tokens and text left to process
			while (wildIndex < splitValues.length) {
				if (splitValues[wildIndex].equals("?")) {
					// ? so move to next text char
					index++;
					chars = false;

				} else if (splitValues[wildIndex].equals("*")) {
					// set any chars status
					chars = true;
					if (wildIndex == splitValues.length - 1) {
						index = keyword.length();
					}

				} else {
					// matching text token
					if (chars) {
						// any chars then try to locate text token
						index = keyword.indexOf(splitValues[wildIndex], index);
						if (index == -1) {
							// token not found
							break;
						}
						int repeat = keyword.indexOf(splitValues[wildIndex], index + 1);
						if (repeat >= 0) {
							backStack.push(new int[] { wildIndex, repeat });
						}
					} else {
						// matching from current position
						if (!keyword.startsWith(splitValues[wildIndex], index)) {
							// couldnt match token
							break;
						}
					}

					// matched text token, move text index to end of matched token
					index += splitValues[wildIndex].length();
					chars = false;
				}

				wildIndex++;
			}

			// full match
			if (wildIndex == splitValues.length && index == keyword.length()) {
				return true;
			}

		} while (backStack.size() > 0);

		return false;
	}

	/**
	 * 将字符串按照'?'和'*'进行分解。<BR>
	 *
	 * Split a string into multi tokens by *'' and '?'.
	 *
	 * @param text
	 * @return
	 */
	private static String[] splitOnTokens(String text) {
		// used by wildcardMatch
		// package level so a unit test may run on this

		if (text.indexOf("?") == -1 && text.indexOf("*") == -1) {
			return new String[] { text };
		}

		char[] array = text.toCharArray();
		ArrayList<String> list = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '?' || array[i] == '*') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer.setLength(0);
				}
				if (array[i] == '?') {
					list.add("?");
				} else if (list.size() == 0 || (i > 0 && list.get(list.size() - 1).equals("*") == false)) {
					list.add("*");
				}
			} else {
				buffer.append(array[i]);
			}
		}
		if (buffer.length() != 0) {
			list.add(buffer.toString());
		}

		return list.toArray(new String[0]);
	}

	public static String findFirst(Pattern pattern, String sourceString) {
		Matcher matcher = pattern.matcher(sourceString);

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	public static List<String> find(Pattern pattern, String sourceString) {
		List<String> list = new ArrayList<>();

		Matcher matcher = pattern.matcher(sourceString);

		while (matcher.find()) {
			list.add(matcher.group());
		}

		return list;
	}

	public static String format(String pattern, List<String> contents) {
		String[] strings = contents.toArray(new String[0]);
		return MessageFormat.format(pattern, strings);
	}

	/**
	 * Convert a string to a map.<BR>
	 * The format should look like: "All,zh_CN:所有,zh_TW:所有,ja:全て"。<BR>
	 * 
	 * @param mapString
	 * @return
	 */
	public static final Map<String, String> convertToMap(String mapString) {
		return convertToMap(mapString, ",", ":");
	}

	/**
	 * Convert a string to a map.<BR>
	 * The format should look like: "All,zh_CN:所有,zh_TW:所有,ja:全て"。<BR>
	 * "," is the separator,":" is the text delimiter.<BR>
	 * 
	 * @param mapString
	 * @param separator
	 * @param textDelimiter
	 * @return
	 */
	public static final Map<String, String> convertToMap(String mapString, String separator, String textDelimiter) {
		String[] stringArray = StringUtils.split(mapString, separator);

		Map<String, String> map = new HashMap<>();

		if (ArrayUtils.isNotEmpty(stringArray)) {
			for (String stringValue : stringArray) {
				if (StringUtils.contains(stringValue, textDelimiter)) {
					String key = StringUtils.substringBefore(stringValue, textDelimiter);
					String value = StringUtils.substringAfter(stringValue, textDelimiter);

					map.put(key, value);
				} else {
					map.put("", stringValue);
				}
			}
		}

		return map;
	}

	/**
	 * join the integer array to a string.
	 * 
	 * @param values
	 * @param separator
	 * @return
	 */
	public static String joinInteger(int[] values, String separator) {
		AssertUtil.assertNotNull(values);
		AssertUtil.assertNotNull(separator);

		StringJoiner stringJoiner = new StringJoiner(separator);

		for (int value : values) {
			stringJoiner.add(Integer.toString(value));
		}

		return stringJoiner.toString();
	}

	/**
	 * Split a string and convert the results to int[];
	 * 
	 * @param value
	 * @param separator
	 * @return
	 */
	public static int[] splitIntoInteger(String value, String separator) {
		AssertUtil.assertNotNull(value);
		AssertUtil.assertNotNull(separator);

		String[] stringValues = StringUtils.split(value, separator);

		if (ArrayUtils.isNotEmpty(stringValues)) {
			int[] results = new int[stringValues.length];

			for (int i = 0; i < results.length; i++) {
				String stringValue = stringValues[i];
				results[i] = NumberUtils.toInt(stringValue);
			}

			return results;
		} else {
			return new int[0];
		}
	}

}
