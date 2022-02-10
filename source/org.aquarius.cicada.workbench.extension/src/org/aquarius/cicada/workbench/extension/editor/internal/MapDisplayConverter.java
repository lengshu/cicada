/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * use map to display.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MapDisplayConverter extends DisplayConverter {

	private BidiMap<String, String> bidiMap = new DualHashBidiMap<>();

	/**
	 * @param map
	 */
	public MapDisplayConverter(Map<?, String> map) {
		super();

		for (Entry<?, String> entry : map.entrySet()) {
			this.bidiMap.put(ObjectUtils.toString(entry.getKey()), entry.getValue());
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {

		if (canonicalValue instanceof String) {
			String stringValue = (String) canonicalValue;

			String[] arrayValues = StringUtils.split(stringValue, NatTableConstant.TextDelimiter);

			if (arrayValues.length == 0) {
				return "";
			}

			StringJoiner stringJoiner = new StringJoiner(NatTableConstant.TextDelimiter);

			for (String value : arrayValues) {
				Object newValue = doGetCanonicalToDisplayValue(value);
				stringJoiner.add(ObjectUtils.toString(newValue, ""));
			}

			return stringJoiner.toString();
		}

		if (canonicalValue instanceof Integer) {
			return this.doGetCanonicalToDisplayValue(canonicalValue.toString());
		}

		return canonicalValue;
	}

	/**
	 * @param displayValue
	 * @return
	 */
	private Object doGetCanonicalToDisplayValue(Object canonicalValue) {
		Object value = this.bidiMap.get(canonicalValue);
		if (null == value) {
			return canonicalValue;
		} else {
			return value;
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object displayToCanonicalValue(Object displayValue) {

		if (displayValue instanceof String) {
			String stringValue = (String) displayValue;

			String[] arrayValues = StringUtils.split(stringValue, NatTableConstant.TextDelimiter);

			if (arrayValues.length == 0) {
				return "";
			}

			StringJoiner stringJoiner = new StringJoiner(NatTableConstant.TextDelimiter);

			for (String value : arrayValues) {
				Object newValue = doGetDisplayToCanonicalValue(value);
				stringJoiner.add(ObjectUtils.toString(newValue, ""));
			}

			return stringJoiner.toString();
		}

		return doGetDisplayToCanonicalValue(displayValue);
	}

	/**
	 * @param displayValue
	 * @return
	 */
	private Object doGetDisplayToCanonicalValue(Object displayValue) {
		Object value = this.bidiMap.getKey(displayValue);
		if (null == value) {
			return displayValue;
		} else {
			return value;
		}
	}

}
