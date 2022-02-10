/**
 *
 */
package org.aquarius.ui.job;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.AssertUtil;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * To solve conflicts.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OrderedSchedulingRule implements ISchedulingRule {

	// private static final OrderedSchedulingRule instance = new
	// OrderedSchedulingRule();

	private String id;

	/**
	 * @param id
	 */
	public OrderedSchedulingRule(String id) {
		super();

		AssertUtil.assertNotNull(id);
		this.id = id;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean contains(ISchedulingRule rule) {
		if (rule instanceof OrderedSchedulingRule) {
			OrderedSchedulingRule otherRule = (OrderedSchedulingRule) rule;
			return StringUtils.equals(this.id, otherRule.id);
		}

		return false;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		return this.contains(rule);
	}

}
