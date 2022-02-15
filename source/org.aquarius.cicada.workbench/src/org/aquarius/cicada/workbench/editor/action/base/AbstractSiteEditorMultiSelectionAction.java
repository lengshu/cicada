package org.aquarius.cicada.workbench.editor.action.base;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Support movie multi selection.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSiteEditorMultiSelectionAction extends AbstractSiteEditorAction {

	public AbstractSiteEditorMultiSelectionAction(String label) {
		super(label);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType doRun(SiteMultiPageEditor siteEditor) {
		List<Movie> selectedMovieList = siteEditor.getSelectedMovieList();

		if (CollectionUtils.isEmpty(selectedMovieList)) {
			MessageDialog.openWarning(siteEditor.getSite().getShell(), Messages.AbstractSiteEditorMultiSelectionAction_WarningDialogTitle,
					Messages.AbstractSiteEditorMultiSelectionAction_WarningDialogMessage);
			return RefreshType.None;
		}

		return this.internalRun(siteEditor, selectedMovieList);
	}

	/**
	 * execute the real operation.<BR>
	 *
	 * @param siteEditor
	 * @param selectedMovieList
	 */
	protected abstract RefreshType internalRun(SiteMultiPageEditor siteEditor, List<Movie> selectedMovieList);

}
