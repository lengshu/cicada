package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.job.RefreshMovieJob;
import org.aquarius.util.enu.RefreshType;

/**
 * Parse movie info.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshMovieAction extends AbstractSelectionAction {

	/**
	 *
	 * @param text
	 */
	public RefreshMovieAction(String text) {
		super(text);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_REFRESH_MOVIE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_REFRESH_MOVIE);
		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/refreshMovie.png"));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> selectedMovieList) {

		new RefreshMovieJob(Messages.RefreshMovieJob_Name, selectedMovieList).schedule();

		return RefreshType.None;
	}

}