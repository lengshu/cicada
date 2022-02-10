/**
 *
 */
package org.aquarius.ui.page.field;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A file field editor with specified name.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FileFieldEditorWithName extends FileFieldEditor {

	private String fileName;

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 * @param fileName
	 */
	public FileFieldEditorWithName(String name, String labelText, Composite parent, String fileName) {
		super(name, labelText, parent);
		this.fileName = fileName;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean checkState() {
		boolean flag = super.checkState();

		if (!flag) {
			return false;
		}

		String path = getTextControl().getText();
		String fileName = FilenameUtils.getName(path);

		if (SystemUtils.IS_OS_WINDOWS) {
			flag = StringUtils.equalsIgnoreCase(this.fileName, fileName);
		} else {
			flag = StringUtils.equals(this.fileName, fileName);
		}

		if (!flag) {
			super.setErrorMessage(" the file name must be " + this.fileName);
		}

		return flag;
	}

}
