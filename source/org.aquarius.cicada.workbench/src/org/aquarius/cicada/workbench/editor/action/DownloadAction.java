package org.aquarius.cicada.workbench.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.job.DownloadMovieJob;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Download movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadAction extends AbstractSelectionAction {

	private boolean chooseSource = false;

	public DownloadAction(String label, boolean chooseSource) {
		super(label);

		this.chooseSource = chooseSource;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_DOWNLOAD);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_DOWNLOAD);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/download.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void internalRun(List<Movie> selectedMovieList) {

		int count = 0;
		List<Movie> filteredMovieList = new ArrayList<>();
		for (Movie movie : selectedMovieList) {
			if (!Movie.isSupportDownload(movie)) {
				count++;
			} else {
				filteredMovieList.add(movie);
			}
		}

		if (count > 0) {
			IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
			if (!SwtUtil.openConfirm(getShell(), Messages.DownloadAction_ConfirmDialogTitle, Messages.DownloadAction_ConfirmDialogMessage, store,
					MovieConfiguration.Key_ConfirmDownloadMovie)) {
				new DownloadMovieJob(Messages.DownloadAction_DownloadMovieJobName, filteredMovieList, true, this.chooseSource).schedule();
				return;
			}
		}

		ISelectionProvider selectionProvider = getSelectionProvider();

		if (null != selectionProvider) {
			selectionProvider.setSelection(null);
		}
		new DownloadMovieJob(Messages.DownloadAction_DownloadMovieJobName, selectedMovieList, true, this.chooseSource).schedule();

	}

}
