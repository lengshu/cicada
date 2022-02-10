/**
 *
 */
package org.aquarius.log;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.slf4j.helpers.MarkerIgnoringBase;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class EclipseLogger extends MarkerIgnoringBase {

	private static final String Trace = "Trace";
	private static final String Debug = "Debug";

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private ILog log;

	private String bundleName;

	private String name;

	private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * @param log
	 */
	public EclipseLogger(ILog log, String name) {
		super();
		this.name = name;
		this.log = log;
		this.bundleName = this.log.getBundle().getSymbolicName();

		if (null == this.name) {
			this.name = "";
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return "eclipse";
	}

	/**
	 * Use eclipse logger to log event.<BR>
	 *
	 * @param loggingEvent
	 */
	private void doEclipseLog(String message, Throwable exception, int level) {
		Status status = new Status(level, this.bundleName, message, exception);
		this.log.log(status);
	}

	/**
	 * Output log to system console.<BR>
	 *
	 * @param marker
	 * @param message
	 * @param exception
	 */
	private void doOutputToConsole(String marker, String message, Throwable exception) {

		String dateTimeString = this.dateTimeFormat.format(new Date());

		String output = this.name + "    " + dateTimeString + "    " + marker + "    " + message;
		System.out.println(output);

		if (null != exception) {
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void trace(String msg) {
		doTrace(msg, null);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void trace(String format, Object arg) {
		doTrace(format, null, arg);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void trace(String format, Object arg1, Object arg2) {
		doTrace(format, null, arg1, arg2);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void trace(String format, Object... arguments) {
		doTrace(format, null, arguments);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void trace(String msg, Throwable t) {
		doTrace(msg, t);
	}

	/**
	 * output with trace level.<BR>
	 * Just to system console.<BR>
	 *
	 * @param format
	 * @param exception
	 * @param arguments
	 */
	private void doTrace(String format, Throwable exception, Object... arguments) {
		String message = MessageFormat.format(format, arguments);
		doOutputToConsole(Trace, message, exception);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void debug(String msg) {
		this.doDebug(msg, null);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void debug(String format, Object arg) {
		this.doDebug(format, null, arg);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void debug(String format, Object arg1, Object arg2) {
		this.doDebug(format, null, arg1, arg2);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void debug(String format, Object... arguments) {
		this.doDebug(format, null, arguments);

	}

	/**
	 * output with debug level.<BR>
	 * Just to system console.<BR>
	 *
	 * @param format
	 * @param arguments
	 */
	private void doDebug(String format, Throwable exception, Object... arguments) {
		String message = MessageFormat.format(format, arguments);
		doOutputToConsole(Debug, message, exception);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void debug(String msg, Throwable exception) {
		doOutputToConsole(Debug, msg, exception);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void info(String msg) {
		this.doInfo(msg, null);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void info(String format, Object arg) {
		this.doInfo(format, null, arg);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void info(String format, Object arg1, Object arg2) {
		this.doInfo(format, null, arg1, arg2);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void info(String format, Object... arguments) {
		this.doInfo(format, null, arguments);
	}

	/**
	 * output with info level.<BR>
	 * Use eclipse logger to log info.<BR>
	 *
	 * @param format
	 * @param arguments
	 */
	private void doInfo(String format, Throwable exception, Object... arguments) {
		String message = MessageFormat.format(format, arguments);
		this.doEclipseLog(message, exception, Status.INFO);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void info(String msg, Throwable t) {
		doInfo(msg, t);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void warn(String msg) {
		doWarn(msg, null);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void warn(String format, Object arg) {
		doWarn(format, null, arg);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void warn(String format, Object... arguments) {
		doWarn(format, null, arguments);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void warn(String format, Object arg1, Object arg2) {
		doWarn(format, null, arg1, arg2);
	}

	/**
	 * Output with warn level.<BR>
	 * Use eclipse logger to log info.<BR>
	 *
	 * @param format
	 * @param exception
	 * @param arguments
	 */
	private void doWarn(String format, Throwable exception, Object... arguments) {
		String message = MessageFormat.format(format, arguments);
		this.doEclipseLog(message, exception, Status.WARNING);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void warn(String msg, Throwable exception) {
		doWarn(msg, exception);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void error(String msg) {
		this.doError(msg, null);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void error(String format, Object arg) {
		this.doError(format, null, arg);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void error(String format, Object arg1, Object arg2) {
		this.doError(format, null, arg1, arg2);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void error(String format, Object... arguments) {
		this.doError(format, null, arguments);
	}

	/***
	 *
	 * @param format
	 * @param exception
	 * @param arguments
	 */
	private void doError(String format, Throwable exception, Object... arguments) {
		String message = MessageFormat.format(format, arguments);
		this.doEclipseLog(message, exception, Status.ERROR);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void error(String msg, Throwable exception) {
		this.doError(msg, exception);
	}

}
