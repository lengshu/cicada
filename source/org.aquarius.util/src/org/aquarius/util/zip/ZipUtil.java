/**
 * 
 */
package org.aquarius.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip feature util.
 * 
 * @author aquarius.github@gmail.com
 *
 */
public final class ZipUtil {

	private static final int BUFFER_SIZE = 1024;

	/**
	 * 
	 */
	private ZipUtil() {
		super();
	}

	/*
	 * 
	 * Zips a file at a location and places the resulting zip file at the toLocation
	 * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
	 */

	public static void zipFileAtPath(String[] sourcePaths, String toLocation) throws IOException {
		// ArrayList<String> contentList = new ArrayList<String>();

		BufferedInputStream origin = null;
		FileOutputStream dest = new FileOutputStream(toLocation);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		for (String sourcePath : sourcePaths) {
			File sourceFile = new File(sourcePath);
			if (sourceFile.isDirectory()) {
				// zipSubFolder(out, sourceFile, sourceFile.getParent().length());
				zipSubFolder(out, sourceFile, sourceFile.getPath().length());
			} else {
				byte data[] = new byte[BUFFER_SIZE];
				FileInputStream fi = new FileInputStream(sourcePath);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
					out.write(data, 0, count);
				}
			}
		}
		out.close();
	}

	/*
	 * 
	 * Zips a subfolder
	 */

	private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {

		File[] fileList = folder.listFiles();
		BufferedInputStream origin = null;
		for (File file : fileList) {
			if (file.isDirectory()) {
				zipSubFolder(out, file, basePathLength);
			} else {
				byte data[] = new byte[BUFFER_SIZE];
				String unmodifiedFilePath = file.getPath();
				String relativePath = unmodifiedFilePath.substring(basePathLength + 1);

				FileInputStream fi = new FileInputStream(unmodifiedFilePath);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				ZipEntry entry = new ZipEntry(relativePath);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
		}
	}

	/*
	 * gets the last path component
	 * 
	 * Example: getLastPathComponent("downloads/example/fileToZip"); Result:
	 * "fileToZip"
	 */
	public static String getLastPathComponent(String filePath) {
		String[] segments = filePath.split("/");
		String lastPathComponent = segments[segments.length - 1];
		return lastPathComponent;
	}

	/**
	 * 
	 * @param zipFile
	 * @param location
	 * @param overwrite
	 * @throws IOException
	 */
	public static void unzip(String zipFile, String location, boolean overwrite) throws IOException {

		int size;
		byte[] buffer = new byte[BUFFER_SIZE];

		if (!location.endsWith("/")) {
			location += "/";
		}
		File f = new File(location);
		if (!f.isDirectory()) {
			f.mkdirs();
		}

		ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
		try {
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				String path = location + ze.getName();
				File outFile = new File(path);

				if (ze.isDirectory()) {
					if (!outFile.isDirectory()) {
						outFile.mkdirs();
					}
				} else {
					// check for and create parent directories if they don't
					// exist
					File parentDir = outFile.getParentFile();
					if (null != parentDir) {
						if (!parentDir.isDirectory()) {
							parentDir.mkdirs();
						}
					}

					if ((!overwrite) || (!outFile.exists())) {
						// unzip the file
						FileOutputStream out = new FileOutputStream(outFile, false);
						BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
						try {
							while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
								fout.write(buffer, 0, size);
							}

							zin.closeEntry();
						} finally {
							fout.flush();
							fout.close();
						}
					}
				}
			}

		} finally {
			zin.close();
		}
	}
}
