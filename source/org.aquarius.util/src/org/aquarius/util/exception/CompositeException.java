/**
 *
 */
package org.aquarius.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.util.AssertUtil;

/**
 *
 * Composite exceptions.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CompositeException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<Exception> nestedExceptionList = new ArrayList<>();

	/**
	 * the size of the nested exceptions.<BR>
	 *
	 * @return
	 */
	public int size() {
		return this.nestedExceptionList.size();
	}

	/**
	 * Return whether the nested exceptions is empty.
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return this.nestedExceptionList.isEmpty();
	}

	/**
	 * add a exception.
	 *
	 * @param e
	 * @return
	 */
	public boolean add(Exception e) {
		AssertUtil.assertNotNull(e);
		return this.nestedExceptionList.add(e);
	}

	/**
	 * remove a exception.
	 *
	 * @param e
	 * @return
	 */
	public boolean remove(Exception e) {
		return this.nestedExceptionList.remove(e);
	}

	/**
	 * clear all nested exceptions.
	 */
	public void clear() {
		this.nestedExceptionList.clear();
	}

	/**
	 * Return the specified exception.
	 *
	 * @param index
	 * @return
	 */
	public Exception get(int index) {
		return this.nestedExceptionList.get(index);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void printStackTrace(PrintStream s) {
		updateStackTrace();
		super.printStackTrace(s);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void printStackTrace(PrintWriter s) {
		updateStackTrace();
		super.printStackTrace(s);
	}

	/**
	 *
	 */
	private void updateStackTrace() {
		List<StackTraceElement> stackTraceList = new ArrayList<>();

		for (Exception exception : this.nestedExceptionList) {
			CollectionUtils.addAll(stackTraceList, exception.getStackTrace());
		}

		this.setStackTrace(stackTraceList.toArray(new StackTraceElement[stackTraceList.size()]));
	}

}
