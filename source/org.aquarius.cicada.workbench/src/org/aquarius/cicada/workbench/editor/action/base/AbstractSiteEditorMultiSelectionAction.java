package org.aquarius.cicada.workbench.editor.action.base;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
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
	protected boolean doRun(SiteMultiPageEditor siteEditor) {
		List<Movie> selectedMovieList = siteEditor.getSelectedMovieList();

		if (CollectionUtils.isEmpty(selectedMovieList)) {
			MessageDialog.openWarning(siteEditor.getSite().getShell(), Messages.AbstractSiteEditorMultiSelectionAction_WarningDialogTitle,
					Messages.AbstractSiteEditorMultiSelectionAction_WarningDialogMessage);
			return false;
		}

		return this.internalRun(siteEditor, selectedMovieList);
	}

	/**
	 * execute the real operation.<BR>
	 *
	 * @param siteEditor
	 * @param selectedMovieList
	 */
	protected abstract boolean internalRun(SiteMultiPageEditor siteEditor, List<Movie> selectedMovieList);

}
