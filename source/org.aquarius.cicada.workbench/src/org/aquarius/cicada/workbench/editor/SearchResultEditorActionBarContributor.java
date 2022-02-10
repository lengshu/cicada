/**
 * 
 */
package org.aquarius.cicada.workbench.editor;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.action.CopyInfoDropDownAction;
import org.aquarius.cicada.workbench.editor.action.DownloadAction;
import org.aquarius.cicada.workbench.editor.action.GenerateDownloadUrlsDropDownAction;
import org.aquarius.cicada.workbench.editor.action.OpenMovieUrlAction;
import org.aquarius.cicada.workbench.editor.action.PlayMovieAction;
import org.aquarius.cicada.workbench.editor.action.RefreshMovieAction;
import org.aquarius.cicada.workbench.editor.action.UpdateScoreDropDownAction;
import org.aquarius.cicada.workbench.editor.action.UpdateStateDropDownAction;
import org.aquarius.ui.action.IActionBarContributor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * The action contributor for search movies.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class SearchResultEditorActionBarContributor extends EditorActionBarContributor implements IActionBarContributor {

	private IAction refreshMovieAction;

	private IAction openMovieUrlAction;

	private IAction copyInfoDropDownAction;

	private IAction generateDownloadUrlsDropDownAction;

	private IAction playMovieAction;

	private IAction downloadAction;

	private IAction updateStateDropDownAction;

	private IAction updateScoreDropDownAction;

	/**
	 *
	 */
	public SearchResultEditorActionBarContributor() {
		super();

		this.refreshMovieAction = new RefreshMovieAction(Messages.SiteEditorActionBarContributor_RefreshMovie);
		this.openMovieUrlAction = new OpenMovieUrlAction(Messages.SiteEditorActionBarContributor_OpenSelectedMovies);
		this.copyInfoDropDownAction = new CopyInfoDropDownAction(Messages.SiteEditorActionBarContributor_Copy);

		this.playMovieAction = new PlayMovieAction(Messages.SiteEditorActionBarContributor_PlayMovie);

		this.generateDownloadUrlsDropDownAction = new GenerateDownloadUrlsDropDownAction(Messages.SiteEditorActionBarContributor_GenerateDownloadUrls);
		this.downloadAction = new DownloadAction(Messages.SiteEditorActionBarContributor_DownloadSelectedMovies, false);

		this.updateStateDropDownAction = new UpdateStateDropDownAction(Messages.SiteEditorActionBarContributor_ChangeState);

		this.updateScoreDropDownAction = new UpdateScoreDropDownAction(Messages.SiteEditorActionBarContributor_ChangeScore);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);

		this.contribute(toolBarManager);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void contribute(IContributionManager contributionManager) {

		contributionManager.add(this.refreshMovieAction);

		contributionManager.add(new Separator());
		contributionManager.add(this.openMovieUrlAction);
		contributionManager.add(this.playMovieAction);

		contributionManager.add(new Separator());
		contributionManager.add(this.copyInfoDropDownAction);
		contributionManager.add(this.generateDownloadUrlsDropDownAction);

		contributionManager.add(new Separator());
		contributionManager.add(this.downloadAction);
		contributionManager.add(this.updateStateDropDownAction);
		contributionManager.add(this.updateScoreDropDownAction);

	}

}
