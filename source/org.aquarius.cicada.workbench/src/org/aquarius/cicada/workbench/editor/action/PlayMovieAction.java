package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.job.PlayMovieJob;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Play the selected movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class PlayMovieAction extends AbstractSelectionAction {

	/**
	 *
	 * @param label
	 */
	public PlayMovieAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_PLAY_MOVIE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_PLAY_MOVIE);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/playMovie.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> selectedMovieList) {

		String mediaPlayerCommand = WorkbenchActivator.getDefault().getConfiguration().getMediaPlayerCommand();

		if (StringUtils.isBlank(mediaPlayerCommand)) {
			MessageDialog.openWarning(getShell(), Messages.PlayMovieAction_MediaPlayerCommandErrorTitle,
					Messages.PlayMovieAction_MediaPlayerCommandErrorMessage);
			return RefreshType.None;
		}

		if (CollectionUtils.isNotEmpty(selectedMovieList)) {
			Movie movie = selectedMovieList.get(0);

			new PlayMovieJob("PlayMovie", movie).schedule();

		}

		return RefreshType.None;
	}

}
