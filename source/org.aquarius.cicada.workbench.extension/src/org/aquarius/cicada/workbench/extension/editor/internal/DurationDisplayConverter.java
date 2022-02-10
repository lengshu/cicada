/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * Display duration.<BR>
 * The format of duration is "hh:mm:ss"<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DurationDisplayConverter extends DisplayConverter {

	private static final SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");

	private static final Pattern DigitalPattern = Pattern.compile("\\d+");

	private Map<String, Range> map = new HashMap<>();

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {

		if (canonicalValue instanceof Date) {
			return durationFormat.format(canonicalValue);
		}

		if (canonicalValue instanceof String) {

			return canonicalValue;
		}

		if (canonicalValue instanceof Range) {
			Range range = (Range) canonicalValue;

			String rangeString = "=" + range.toString();

			this.map.putIfAbsent(rangeString, range);

			return rangeString;
		}

		return ObjectUtils.toString(canonicalValue);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object displayToCanonicalValue(Object displayValue) {

		Object value = this.map.get(displayValue);
		if (null != value) {
			return value;
		}

		if (displayValue instanceof String) {

			String stringValue = (String) displayValue;

			if (StringUtils.isEmpty(stringValue)) {
				return displayValue;
			}

			return parseToRange(stringValue);
		}

		return displayValue;
	}

	/**
	 * @param stringValue
	 * @return
	 */
	private Object parseToRange(String stringValue) {
		Matcher matcher = DigitalPattern.matcher(stringValue);
		boolean first = true;
		Range range = new Range();
		range.setEnd(Integer.MAX_VALUE);

		while (matcher.find()) {
			String groupValue = matcher.group();
			int intValue = NumberUtils.toInt(groupValue);
			if (first) {
				range.setStart(intValue);
				first = false;
			} else {
				range.setEnd(intValue);
				break;
			}
		}

		return range;
	}

}
