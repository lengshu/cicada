package org.aquarius.cicada.workbench.action.search;

import org.aquarius.cicada.workbench.manager.HistoryManager;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Clear movie search history.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ClearSearchHistoryAction extends Action {

	private IWorkbenchWindow window;

	/**
	 * 
	 * @param window
	 * @param label
	 */
	public ClearSearchHistoryAction(IWorkbenchWindow window, String label) {
		super(label);

		this.window = window;

	}

	@Override
	public void run() {

		HistoryManager.getInstance().clearHistories();

	}

}
