/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.util.StringUtil;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * Score display convertor
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ScoreDisplayConverter extends DisplayConverter {

	/**
	 * 
	 */
	private static final String BlankString = " ";
	private String symbol;

	/**
	 *
	 */
	public ScoreDisplayConverter() {
		this(StringUtil.Star);
	}

	/**
	 * Use specified symbol to display score.<BR>
	 *
	 * @param symbol
	 */
	public ScoreDisplayConverter(String symbol) {
		super();
		this.symbol = symbol;

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof String) {
			String stringValue = (String) displayValue;

			String[] arrayValues = StringUtils.split(stringValue, NatTableConstant.TextDelimiter);

			if (arrayValues.length == 0) {
				return null;
			}

			StringJoiner stringJoiner = new StringJoiner(NatTableConstant.TextDelimiter);

			for (String value : arrayValues) {
				stringJoiner.add(StringUtils.countMatches(value, this.symbol) + "");
			}

			return stringJoiner.toString();
		}

		return displayValue;
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {

		if (canonicalValue instanceof String) {
			String stringValue = (String) canonicalValue;

			String[] arrayValues = StringUtils.split(stringValue, NatTableConstant.TextDelimiter);

			if (arrayValues.length == 0) {
				return BlankString;
			}

			StringJoiner stringJoiner = new StringJoiner(NatTableConstant.TextDelimiter);

			for (String value : arrayValues) {
				int intValue = NumberUtils.toInt(value, 0);
				if (0 == intValue) {
					stringJoiner.add(BlankString);
				} else {
					stringJoiner.add(StringUtil.repeatSymbol(this.symbol, intValue));
				}
			}

			return stringJoiner.toString();
		}

		if (canonicalValue instanceof Integer) {
			Integer intValue = (Integer) canonicalValue;
			if (0 == intValue) {
				return BlankString;
			}

			return StringUtil.repeatSymbol(this.symbol, intValue);
		}

		return canonicalValue;
	}

}
