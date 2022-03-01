/**
 * 
 */
package org.aquarius.ui.util;

import java.util.StringJoiner;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.util.StringUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Table;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public final class PersistenceUtil {

	/**
	 * 
	 */
	private PersistenceUtil() {
		// No instances needed.
	}

	/**
	 * While the sash form is disposed.<BR>
	 * Data will be stored.<BR>
	 * 
	 * @param store
	 * @param key
	 * @param sashForm
	 */
	public static void addSaveSupport(IPreferenceStore store, String key, SashForm sashForm) {
		sashForm.addDisposeListener(e -> {
			PersistenceUtil.saveUiData(store, key, sashForm);
		});
	}

	/**
	 * While the table is disposed.<BR>
	 * Data will be stored.<BR>
	 * 
	 * @param store
	 * @param key
	 * @param table
	 */
	public static void addSaveSupport(IPreferenceStore store, String key, Table table) {
		table.addDisposeListener(e -> {
			PersistenceUtil.saveUiData(store, key, table);
		});
	}

	/**
	 * Restore the weights of a sashform.<BR>
	 * 
	 * @param store
	 * @param key
	 * @param sashForm
	 */
	public static void restoreUiData(IPreferenceStore store, String key, SashForm sashForm) {

		if (!SwtUtil.isValid(sashForm)) {
			return;
		}

		String stringValue = store.getString(key);

		int[] intValues = StringUtil.splitIntoInteger(stringValue, StringUtil.ContentSeparator);
		if (ArrayUtils.isEmpty(intValues) || (intValues.length != sashForm.getWeights().length)) {
			return;
		}

		sashForm.setWeights(intValues);
	}

	/**
	 * Restore the weights of a sashform.<BR>
	 * 
	 * @param store
	 * @param key
	 * @param sashForm
	 */
	public static void saveUiData(IPreferenceStore store, String key, SashForm sashForm) {

		if (!SwtUtil.isValid(sashForm)) {
			return;
		}
		String stringValue = StringUtils.join(ArrayUtils.toObject(sashForm.getWeights()), StringUtil.ContentSeparator);
		store.setValue(key, stringValue);
	}

	/**
	 * Restore the weights of a sashform.<BR>
	 * 
	 * @param store
	 * @param key
	 * @param table
	 */
	public static void restoreUiData(IPreferenceStore store, String key, Table table) {

		if (!SwtUtil.isValid(table)) {
			return;
		}

		String stringValue = store.getString(key);

		int[] intValues = StringUtil.splitIntoInteger(stringValue, StringUtil.ContentSeparator);
		if (ArrayUtils.isEmpty(intValues) || (intValues.length != table.getColumnCount())) {
			return;
		}

		for (int i = 0; i < intValues.length; i++) {
			table.getColumn(i).setWidth(intValues[i]);
		}

	}

	/**
	 * Restore the weights of a sashform.<BR>
	 * 
	 * @param store
	 * @param key
	 * @param table
	 */
	public static void saveUiData(IPreferenceStore store, String key, Table table) {

		if (!SwtUtil.isValid(table)) {
			return;
		}

		StringJoiner stringJoiner = new StringJoiner(StringUtil.ContentSeparator);

		for (int i = 0; i < table.getColumnCount(); i++) {
			String stringValue = table.getColumn(i).getWidth() + "";
			stringJoiner.add(stringValue);
		}

		store.setValue(key, stringJoiner.toString());
	}

}
