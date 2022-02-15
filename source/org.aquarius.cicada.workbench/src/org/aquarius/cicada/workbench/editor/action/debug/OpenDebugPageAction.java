package org.aquarius.cicada.workbench.editor.action.debug;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.util.enu.RefreshType;

/**
 * Copy the specified attribute information or all the information in the
 * selected movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenDebugPageAction extends AbstractSelectionAction {

	/**
	 * 
	 * @param label
	 */
	public OpenDebugPageAction(String label) {
		super(label);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> selectedMovieList) {

		Movie movie = selectedMovieList.get(0);

		BrowserUtil.openDebugPage(movie.getPageUrl());

		return RefreshType.None;
	}

}
