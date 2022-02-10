/**
 *
 */
package org.aquarius.cicada.core.impl.generator.eagle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class EagleModel {

	private List<EagleTask> tasks = new ArrayList<>();

	/**
	 * @return the tasks
	 */
	public List<EagleTask> getTasks() {
		return this.tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(List<EagleTask> tasks) {
		this.tasks = tasks;
	}

}
