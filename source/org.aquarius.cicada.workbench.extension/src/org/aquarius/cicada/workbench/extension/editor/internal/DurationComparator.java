/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * compare duration.<BR>
 * The format of duration is "hh:mm:ss"<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DurationComparator implements Comparator<Object> {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int compare(Object o1, Object o2) {
		Date date = (Date) o1;
		Range range = (Range) o2;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int minute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

		if ((minute >= range.getStart()) && (minute < range.getEnd())) {
			return 0;
		} else {
			return 1;
		}
	}

}
