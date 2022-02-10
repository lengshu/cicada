/**
 * 
 */
package org.aquarius.ui.message;

/**
 * Used to show message.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public interface IMessageShower {

	/**
	 * Constant for a regular message (value 0).
	 * <p>
	 * Typically this indicates that the message should be shown without an icon.
	 * </p>
	 */
	public static final int NONE = 0;

	/**
	 * Constant for an info message (value 1).
	 */
	public static final int INFORMATION = 1;

	/**
	 * Constant for a warning message (value 2).
	 */
	public static final int WARNING = 2;

	/**
	 * Constant for an error message (value 3).
	 */
	public static final int ERROR = 3;

	public void setMessage(String message, int type);

	public default void setErrorMessage(String message) {
		this.setMessage(message, ERROR);
	}

	public default void setWarningMessage(String message) {
		this.setMessage(message, WARNING);
	}

	public default void setInfoMessage(String message) {
		this.setMessage(message, INFORMATION);
	}

	public default void clear() {
		this.setMessage(null, NONE);
	}

}
