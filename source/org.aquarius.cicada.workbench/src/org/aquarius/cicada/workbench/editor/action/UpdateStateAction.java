package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorMultiSelectionAction;

/**
 * Update the selected movies to the specified state.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateStateAction extends AbstractSiteEditorMultiSelectionAction {

	private int state;

	/**
	 *
	 * @param label
	 */
	public UpdateStateAction(String label, int state) {
		super(label);

		this.state = state;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean internalRun(SiteMultiPageEditor siteEditor, List<Movie> selectedMovieList) {

		for (Movie movie : selectedMovieList) {
			movie.setState(this.state);
		}

		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(selectedMovieList);

		return true;
	}

}
