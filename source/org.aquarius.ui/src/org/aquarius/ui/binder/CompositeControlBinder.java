/**
 *
 */
package org.aquarius.ui.binder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.AssertUtil;

/**
 * @author aquarius.github@gmail.com
 *
 */
public final class CompositeControlBinder {

	private List<IControlBinder<?, ?>> binderList = new ArrayList<>();

	/**
	 * add a binder.
	 *
	 * @param binder this parameter should not be null.
	 */
	public void add(IControlBinder<?, ?> binder) {
		AssertUtil.assertNotNull(binder);
		this.binderList.add(binder);
	}

	/**
	 * remove a binder.
	 *
	 * @param binder
	 * @return
	 */
	public boolean remove(IControlBinder<?, ?> binder) {
		return this.binderList.remove(binder);
	}

	/**
	 * Return all control binders.<BR>
	 *
	 * @return
	 */
	public List<IControlBinder<?, ?>> getAllBinders() {
		return this.binderList;
	}

	/**
	 * invoke all binders to load data.<BR>
	 */
	public void load() {
		this.binderList.stream().forEach(IControlBinder::load);
	}

	/**
	 * invoke all binders to save data.<BR>
	 */
	public void save() {
		this.binderList.stream().forEach(IControlBinder::save);
	}

	/**
	 * invoke all binders to validate data.<BR>
	 *
	 * @param traverseAll if this value is false, any validate error will make this
	 *                    method return.<BR>
	 * @return
	 */
	public String validate(boolean traverseAll) {
		StringJoiner stringJoiner = new StringJoiner("\r\n");

		for (IControlBinder<?, ?> controlBinder : this.binderList) {
			String message = controlBinder.validate();
			if (StringUtils.isNotBlank(message)) {

				if (traverseAll) {
					stringJoiner.add(message);
				} else {
					return message;
				}
			}
		}

		this.binderList.stream().forEach(binder -> {

		});

		return stringJoiner.toString();
	}

	/**
	 * Check whether this controls are dirty or not.
	 *
	 * @return
	 */
	public boolean isDirty() {

		for (IControlBinder<?, ?> controlBinder : this.binderList) {
			if (controlBinder.isDirty()) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Reset dirty state.<BR>
	 * It means all change are acceppted and saved.<BR>
	 */
	public void resetDirtyState() {
		this.binderList.stream().forEach(IControlBinder::resetDirtyState);
	}

}
