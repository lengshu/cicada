package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.util.DesktopUtil;

/**
 * Open the selected movies in the browser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenMovieUrlAction extends AbstractSelectionAction {

	/**
	 *
	 * @param label
	 */
	public OpenMovieUrlAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_OPEN_MOVIE_URL);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_OPEN_MOVIE_URL);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/openMovieUrl.png"));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void internalRun(List<Movie> selectedMovieList) {

		for (Movie movie : selectedMovieList) {
			DesktopUtil.openWebpages(movie.getPageUrl());
		}
	}

}
