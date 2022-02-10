package org.aquarius.cicada.workbench.action.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.SearchKeywordModel;
import org.aquarius.cicada.workbench.action.base.AbstractDropDownAction;
import org.aquarius.cicada.workbench.manager.HistoryManager;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Search movies in all sites by a keyword.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SearchMovieByKeywordDropDownAction extends AbstractDropDownAction {

	private IWorkbenchWindow window;

	/**
	 * 
	 * @param window
	 * @param label
	 */
	public SearchMovieByKeywordDropDownAction(IWorkbenchWindow window, String label) {
		super(label, IAction.AS_DROP_DOWN_MENU);

		this.window = window;

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/search.png")); //$NON-NLS-1$

	}

	/**
	 * 
	 * @return
	 */
	@Override
	public List<IContributionItem> createActionItems() {

		List<IContributionItem> list = new ArrayList<>();

		{
			SearchMovieByKeywordAction searchAction = new SearchMovieByKeywordAction(SearchMovieByKeywordDropDownAction.this.window,
					Messages.SearchMovieByKeywordDropDownAction_NewSearch, null);
			ActionContributionItem item = new ActionContributionItem(searchAction);

			list.add(item);
			list.add(new Separator());
		}

		List<SearchKeywordModel> modelList = HistoryManager.getInstance().getSearchKeywordModelHistoryList();
		for (SearchKeywordModel model : modelList) {
			SearchMovieByKeywordAction searchAction = new SearchMovieByKeywordAction(SearchMovieByKeywordDropDownAction.this.window, model.getKeyword(), model);
			ActionContributionItem item = new ActionContributionItem(searchAction);

			list.add(item);

		}

		{
			if (CollectionUtils.isNotEmpty(modelList)) {
				list.add(new Separator());
			}

			ClearSearchHistoryAction clearAction = new ClearSearchHistoryAction(SearchMovieByKeywordDropDownAction.this.window,
					Messages.SearchMovieByKeywordDropDownAction_ClearSearchHistory);
			ActionContributionItem item = new ActionContributionItem(clearAction);

			list.add(item);

		}

		return list;
	}

}
