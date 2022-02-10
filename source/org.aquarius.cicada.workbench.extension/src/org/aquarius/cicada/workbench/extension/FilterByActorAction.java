/**
 *
 */
package org.aquarius.cicada.workbench.extension;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.extension.editor.SiteNatTableEditor;
import org.eclipse.jface.action.Action;

/**
 * Filter the nat table by the selected movie first actor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FilterByActorAction extends Action {

	private SiteNatTableEditor siteNatTableEditor;

	/**
	 * @param text
	 * @param siteNatTableEditor
	 */
	public FilterByActorAction(String text, SiteNatTableEditor siteNatTableEditor) {
		super(text);
		this.siteNatTableEditor = siteNatTableEditor;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		List<Movie> movieList = this.siteNatTableEditor.getSelectedMovieList();

		if (CollectionUtils.isEmpty(movieList)) {
			return;
		}

		if (movieList.size() > 1) {
			return;
		}

		Movie movie = movieList.get(0);

		String[] actors = MovieUtil.split(movie.getActor());

		if (ArrayUtils.isEmpty(actors)) {
			return;
		}

		this.siteNatTableEditor.filterByActor(actors[0]);

		super.run();
	}

}
