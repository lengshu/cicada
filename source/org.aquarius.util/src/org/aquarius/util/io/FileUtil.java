/**
 *
 */
package org.aquarius.util.io;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * File function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FileUtil {

	public static String ValidFileRegex = "^[^\\/\\\\<>\\*\\?\\:\"\\|#]{1,32}";

	/**
	 * No instances needed.
	 */
	private FileUtil() {
	}

	/**
	 * If the file exists,then find a non-duplicated file name.<BR>
	 * For a file of "dodo.txt" ,it will try to find with "dodo_{number}.txt".<BR>
	 *
	 * @param fileName
	 * @return
	 */
	public static String findNonDuplicatedFileName(String fileName) {
		File file = new File(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		String baseNameWithPath = FilenameUtils.removeExtension(fileName) + "_";

		int count = 1;

		while (file.exists()) {
			fileName = baseNameWithPath + (count++);

			if (StringUtils.isNotEmpty(extension)) {
				fileName = fileName + "." + extension;
			}

			file = new File(fileName);
		}

		return fileName;
	}

	/**
	 * Try to find a file name to backup.<BR>
	 * The new name should be added with ".old";<BR>
	 * If some duplicated file exist, it will try to find non-duplicated file.<BR>
	 *
	 * for example,the file name is "dodo.json" ,"dodo.json.old" or
	 * "dodo.json_{count}.old" will be found.<BR>
	 *
	 * @param fileName
	 * @return
	 */
	public static String autoRenameFileForBackup(String fileName) {

		File file = new File(fileName);
		if (file.exists()) {
			String newFileName = fileName + ".old";
			newFileName = findNonDuplicatedFileName(newFileName);

			file.renameTo(new File(newFileName));
			return newFileName;
		} else {
			return null;
		}

	}

	/**
	 * Return whether a file path is valid.<BR>
	 *
	 * @param path
	 * @return
	 */
	public static boolean isPathValid(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException ex) {
			return false;
		}

		return true;
	}

	/**
	 * Return the file name by the download url.<BR>
	 *
	 * @param url
	 * @return
	 */
	public static String computeDownloadFileName(String url) {
		if (url.endsWith("/")) {
			url = StringUtils.removeEnd(url, "/");
		}

		return StringUtils.substringAfterLast(url, "/");
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isValid(File file) {
		return (null != file) && (file.exists());
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String getRealFilePath(String path) {
		return path.replace("/", File.separator).replace("\\", File.separator);
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String getHttpURLPath(String path) {
		return path.replace("\\", "/");
	}

	/**
	 * 
	 * @param file
	 */
	public static void forceDeleteFileQuietly(File file) {
		try {
			FileUtils.forceDelete(file);
		} catch (Exception e) {
			// Nothing to do
		}

		try {
			FileUtils.forceDeleteOnExit(file);
		} catch (Exception e) {
			// Nothing to do
		}
	}

}
