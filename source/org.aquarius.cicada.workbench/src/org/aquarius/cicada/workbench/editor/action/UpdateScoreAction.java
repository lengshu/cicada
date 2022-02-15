package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.util.enu.RefreshType;

/**
 * Update the selected movies to the specified score.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateScoreAction extends AbstractSelectionAction {

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
	 * {@inheritDoc}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> movieList) {
		for (Movie movie : movieList) {
			movie.setScore(this.score);
		}

		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(movieList);

		return RefreshType.Update;
	}

}
