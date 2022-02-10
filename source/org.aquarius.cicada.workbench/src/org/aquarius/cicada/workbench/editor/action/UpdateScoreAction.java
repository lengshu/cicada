package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorMultiSelectionAction;

/**
 * Update the selected movies to the specified score.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateScoreAction extends AbstractSiteEditorMultiSelectionAction {

	private int score;

	/**
	 *
	 * @param label
	 */
	public UpdateScoreAction(String label, int score) {
		super(label);

		this.score = score;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean internalRun(SiteMultiPageEditor siteEditor, List<Movie> selectedMovieList) {

		for (Movie movie : selectedMovieList) {
			movie.setScore(this.score);
		}

		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(selectedMovieList);

		return true;
	}

}
