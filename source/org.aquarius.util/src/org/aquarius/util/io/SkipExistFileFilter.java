/**
 * 
 */
package org.aquarius.util.io;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * When copy or delete files,ignore the existing file.<BR>
 * 
 * @author aquarius.github@gmail.com
 *
 */
public class SkipExistFileFilter implements FileFilter {

	private String srcDir;
	private String destDir;

	/**
	 * @param srcDir
	 * @param destDir
	 */
	public SkipExistFileFilter(File srcDir, File destDir) {
		super();
		this.srcDir = FilenameUtils.normalize(srcDir.getAbsolutePath());
		this.destDir = FilenameUtils.normalize(destDir.getAbsolutePath());
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean accept(File file) {

		if (file.isDirectory()) {
			return true;
		}

		String relativePath = StringUtils.remove(FilenameUtils.normalize(file.getAbsolutePath()), this.srcDir);

		String targetFileName = this.destDir + File.separator + relativePath;
		File targetFile = new File(FilenameUtils.normalize(targetFileName));

		return !targetFile.exists();

	}

}
