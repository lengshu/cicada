/**
 *
 */
package org.aquarius.cicada.workbench.editor.action.base;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.ui.util.AdapterUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Base class to provide selection.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSelectionAction extends Action {

	private ISelectionProvider lastSelectionProvider;

	/**
	 * @param text
	 */
	public AbstractSelectionAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AbstractSelectionAction(String text, int style) {
		super(text, style);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		ISelectionProvider selectionProvider = getSelectionProvider();

		if (null == selectionProvider) {
			return;
		}

		ISelection selection = selectionProvider.getSelection();
		List<Movie> movieList = MovieHelper.getMovieList(selection);

		if (!movieList.isEmpty()) {
			RefreshType refreshType = this.internalRun(movieList);
			if (refreshType == RefreshType.None) {
				return;
			}

			SiteMultiPageEditor siteEditor = AbstractSiteEditorAction.getSiteEditor();

			if (null == siteEditor) {
				return;
			}

			if (refreshType == RefreshType.Update) {
				siteEditor.updateContent();
			}

			if (refreshType == RefreshType.Refresh) {
				siteEditor.refreshContent();
			}
		}
	}

	/**
	 * @return
	 */
	protected ISelectionProvider getSelectionProvider() {

		IWorkbenchPart workbenchPart = WorkbenchUtil.getActivePart();

		if (null == workbenchPart) {
			return null;
		}

		ISelectionProvider selectionProvider = workbenchPart.getSite().getSelectionProvider();
		if ((null == selectionProvider) || (selectionProvider.getSelection() == null) || (selectionProvider.getSelection().isEmpty())) {
			selectionProvider = AdapterUtil.getAdapter(workbenchPart, ISelectionProvider.class);
		}

		this.lastSelectionProvider = selectionProvider;

		return selectionProvider;
	}

	/**
	 * @return the lastSelectionProvider
	 */
	public ISelectionProvider getLastSelectionProvider() {
		return this.lastSelectionProvider;
	}

	/**
	 * Return the shell.<BR>
	 *
	 * @return
	 */
	protected Shell getShell() {
		return SwtUtil.findShell();
	}

	/**
	 * @param movieList
	 */
	protected abstract RefreshType internalRun(List<Movie> movieList);
}
