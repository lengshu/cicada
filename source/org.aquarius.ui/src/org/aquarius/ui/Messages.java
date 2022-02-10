/**
 *
 */
package org.aquarius.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.aquarius.ui.messages"; //$NON-NLS-1$

	public static String ErrorDialogTitle;

	public static String InfoDialogTitle;

	public static String SuccessDialogTitle;

	public static String WarnDialogTitle;

	public static String ConfirmDialogTitle;

	public static String AbstractEntryTableViewerFactory_KeyColumnTitle;

	public static String AbstractEntryTableViewerFactory_ValueColumnTitle;

	public static String SwtUtil_ToggleMessage;

	public static String CheckedTableControlFactory_MoveUp;

	public static String CheckedTableControlFactory_MoveDown;

	public static String CheckedTableControlFactory_Remove;

	public static String DuplicatedValidator_DuplicatedErrorMessage;

	public static String LengthValidator_LengthLessThan;

	public static String LengthValidator_NotBlankMessage;

	public static String ListInfoDialog_Close;

	public static String ListInfoDialog_CopyToClipboard;

	public static String ListInfoDialog_SaveAs;

	public static String UrlInputValidator_InputUrlsNotValid;

	public static String UrlInputValidator_PleaseInputUrls;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
