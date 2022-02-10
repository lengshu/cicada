/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * Use char to improve performance.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class BooleanDisplayConverter extends DisplayConverter {

	public static String TrueSymbol = "√";

	public static String FalseSymbol = "×";

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		// return Boolean.valueOf(displayValue.toString());
		if (displayValue == null) {
			return null;
		}

		String stringValue = displayValue.toString();
		stringValue = StringUtils.replace(stringValue, TrueSymbol, "true");
		stringValue = StringUtils.replace(stringValue, FalseSymbol, "false");

		return stringValue;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue == null) {
			return null;
		}

		String stringValue = canonicalValue.toString();
		stringValue = StringUtils.replace(stringValue, "true", TrueSymbol);
		stringValue = StringUtils.replace(stringValue, "false", FalseSymbol);

		return stringValue;
	}

}
