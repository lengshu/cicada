package org.aquarius.cicada.workbench.action.search;

import org.aquarius.cicada.workbench.SearchKeywordModel;
import org.aquarius.cicada.workbench.dialog.SearchMovieByKeywordDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Search movies in all sites by a keyword.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SearchMovieByKeywordAction extends Action {

	private IWorkbenchWindow window;

	private SearchKeywordModel searchKeywordModel;

	/**
	 * 
	 * @param window
	 * @param label
	 * @param searchKeywordModel
	 */
	public SearchMovieByKeywordAction(IWorkbenchWindow window, String label, SearchKeywordModel searchKeywordModel) {
		super(label);

		this.window = window;
		this.searchKeywordModel = searchKeywordModel;

	}

	@Override
	public void run() {

		Shell dialogShell = this.window.getShell();
		SearchMovieByKeywordDialog dialog = new SearchMovieByKeywordDialog(dialogShell, this.searchKeywordModel);

		dialog.open();
	}

}
