package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorMultiSelectionAction;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Delete selected movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DeleteAction extends AbstractSiteEditorMultiSelectionAction {

	public DeleteAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_DELETE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_DELETE);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/delete.png"));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(SiteMultiPageEditor siteEditor, List<Movie> selectedMovieList) {

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

		if (SwtUtil.openConfirm(siteEditor.getSite().getShell(), Messages.DeleteAction_ConfirmDialogTitle, Messages.DeleteAction_ConfirmDialogMessage, store,
				MovieConfiguration.Key_ConfirmDeleteMovie)) {
			RuntimeManager.getInstance().getStoreService().deleteMovies(selectedMovieList);
			siteEditor.getMovieSite().removeMovies(selectedMovieList);

			return RefreshType.Refresh;
		} else {
			return RefreshType.None;
		}

	}

}
