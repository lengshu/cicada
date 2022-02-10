/**
 * 
 */
package org.aquarius.util;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang.StringUtils;

/**
 * Function provider for locale.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class LocaleUtil {

	/**
	 * 
	 */
	private LocaleUtil() {
		// Nothing to do
	}

	/**
	 * Find a suitable sub folder by the current locale.<BR>
	 * 
	 * @param rootFolder
	 * @return
	 */
	public static String findSubFolderByLocale(String rootFolder) {
		Collection<String> candidateNames = findDefaultCandaidateLocaleNames();

		for (String candidateName : candidateNames) {
			String subFolderString = rootFolder + File.separator + candidateName;

			File subFolder = new File(subFolderString);

			if (subFolder.exists()) {
				return subFolder.getAbsolutePath();
			}
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public static Collection<String> findDefaultCandaidateLocaleNames() {
		return findCandaidateLocaleNames(Locale.getDefault());

	}

	/**
	 * 
	 * @param currentLocale
	 * @return
	 */
	public static Collection<String> findCandaidateLocaleNames(Locale currentLocale) {
		Collection<String> candidateNames = new ListOrderedSet<>();

		String languageTag = currentLocale.toLanguageTag();
		candidateNames.add(languageTag);
		languageTag = StringUtils.replace(languageTag, "-", "_");
		candidateNames.add(languageTag);
		candidateNames.add(currentLocale.getLanguage());

		candidateNames.add("en-US");
		candidateNames.add("en_US");
		candidateNames.add("en");

		return candidateNames;

	}

}
