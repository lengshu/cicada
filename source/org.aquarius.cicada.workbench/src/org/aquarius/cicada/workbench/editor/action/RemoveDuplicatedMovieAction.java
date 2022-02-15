package org.aquarius.cicada.workbench.editor.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.enu.RefreshType;

/**
 * Remove the duplicated movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RemoveDuplicatedMovieAction extends AbstractSiteEditorAction {

	/**
	 *
	 * @param label
	 */
	public RemoveDuplicatedMovieAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_REMOVE_DUPLICATED_MOVIES);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_REMOVE_DUPLICATED_MOVIES);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/findDuplicated.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType doRun(SiteMultiPageEditor siteEditor) {
		Site site = siteEditor.getMovieSite();

		List<Movie> movieList = site.getMovieList();
		List<Movie> duplicatedMovieList = new ArrayList<>();

		Set<String> urlSet = new TreeSet<String>();

		for (Movie movie : movieList) {
			if (urlSet.contains(movie.getPageUrl())) {
				duplicatedMovieList.add(movie);
			} else {
				urlSet.add(movie.getPageUrl());
			}
		}

		movieList.removeAll(duplicatedMovieList);

		RuntimeManager.getInstance().getStoreService().deleteMovies(duplicatedMovieList);

		String message = MessageFormat.format(Messages.RemoveDuplicatedMovieAction_TooltipMessage, duplicatedMovieList.size());
		TooltipUtil.showInfoTip(Messages.RemoveDuplicatedMovieAction_TooltipTitle, message);

		return RefreshType.Refresh;
	}

}
