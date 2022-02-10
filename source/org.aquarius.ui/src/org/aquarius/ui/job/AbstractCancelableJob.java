/**
 *
 */
package org.aquarius.ui.job;

import org.eclipse.core.runtime.jobs.Job;

/**
 * Just a base class for the funture.<BR>
 *
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractCancelableJob extends Job {

	public static final String FamilyName = AbstractCancelableJob.class.getName();

	/**
	 * @param name
	 */
	public AbstractCancelableJob(String name) {
		super(name);
		this.setUser(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean belongsTo(Object family) {
		return FamilyName.equals(family);
	}

	/**
	 * Compute a family name to process conflict.
	 *
	 * @param siteName
	 * @return
	 */

	public String getFamilyName(String siteName) {
		return this.getClass().getName() + ":" + siteName;
	}
}
