/**
 *
 */
package org.aquarius.cicada.workbench.editor;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.action.ClearSiteAction;
import org.aquarius.cicada.workbench.editor.action.CopyInfoDropDownAction;
import org.aquarius.cicada.workbench.editor.action.DeleteAction;
import org.aquarius.cicada.workbench.editor.action.DownloadAction;
import org.aquarius.cicada.workbench.editor.action.FillMissingInfoDropDownAction;
import org.aquarius.cicada.workbench.editor.action.GenerateDownloadUrlsDropDownAction;
import org.aquarius.cicada.workbench.editor.action.OpenMovieUrlAction;
import org.aquarius.cicada.workbench.editor.action.OpenSiteUrlAction;
import org.aquarius.cicada.workbench.editor.action.PlayMovieAction;
import org.aquarius.cicada.workbench.editor.action.RefreshDropdownAction;
import org.aquarius.cicada.workbench.editor.action.RefreshEditorAction;
import org.aquarius.cicada.workbench.editor.action.RefreshMovieAction;
import org.aquarius.cicada.workbench.editor.action.RemoveDuplicatedMovieAction;
import org.aquarius.cicada.workbench.editor.action.UpdateScoreDropDownAction;
import org.aquarius.cicada.workbench.editor.action.UpdateStateDropDownAction;
import org.aquarius.cicada.workbench.editor.action.debug.OpenDebugPageAction;
import org.aquarius.cicada.workbench.editor.action.factory.EditorActionFactory;
import org.aquarius.ui.action.IActionBarContributor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Action contributor for site editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteEditorActionBarContributor extends EditorActionBarContributor implements IActionBarContributor {

	private IAction removeDuplicatedMovieAction;

	private IAction refreshSiteAction;

	private IAction refreshMovieAction;

	private IAction openSiteUrlAction;

	private IAction openMovieUrlAction;

	private IAction copyInfoDropDownAction;

	private IAction generateDownloadUrlsDropDownAction;

	private IAction deleteAction;

	private IAction playMovieAction;

	private IAction downloadAction;

	private IAction updateStateDropDownAction;

	private IAction updateScoreDropDownAction;

	private IAction fillMissingInfoAction;

	private IAction refreshEditorAction;

	private IAction clearSiteAction;

	private IAction externalAnalyserDropdownAction;

	private IAction openDebugPageAction;

	/**
	 *
	 */
	public SiteEditorActionBarContributor() {
		super();

		// this.refreshSiteAction = new
		// RefreshSiteAction(Messages.SiteEditorActionBarContributor_RefreshSite);

		this.refreshSiteAction = new RefreshDropdownAction(Messages.SiteEditorActionBarContributor_RefreshSite);

		this.refreshMovieAction = new RefreshMovieAction(Messages.SiteEditorActionBarContributor_RefreshMovie);
		this.removeDuplicatedMovieAction = new RemoveDuplicatedMovieAction(Messages.SiteEditorActionBarContributor_RemoveDuplicatedMovies);

		this.openSiteUrlAction = new OpenSiteUrlAction(Messages.SiteEditorActionBarContributor_OpenSite);
		this.openMovieUrlAction = new OpenMovieUrlAction(Messages.SiteEditorActionBarContributor_OpenSelectedMovies);
		this.copyInfoDropDownAction = new CopyInfoDropDownAction(Messages.SiteEditorActionBarContributor_Copy);
		this.deleteAction = new DeleteAction(Messages.SiteEditorActionBarContributor_DeleteSelectedMovies);

		this.playMovieAction = new PlayMovieAction(Messages.SiteEditorActionBarContributor_PlayMovie);

		this.generateDownloadUrlsDropDownAction = new GenerateDownloadUrlsDropDownAction(Messages.SiteEditorActionBarContributor_GenerateDownloadUrls);
		this.downloadAction = new DownloadAction(Messages.SiteEditorActionBarContributor_DownloadSelectedMovies, false);

		this.updateStateDropDownAction = new UpdateStateDropDownAction(Messages.SiteEditorActionBarContributor_ChangeState);

		this.updateScoreDropDownAction = new UpdateScoreDropDownAction(Messages.SiteEditorActionBarContributor_ChangeScore);

		this.fillMissingInfoAction = new FillMissingInfoDropDownAction();

		this.refreshEditorAction = new RefreshEditorAction();

		this.clearSiteAction = new ClearSiteAction(Messages.SiteEditorActionBarContributor_ClearSite);

		this.externalAnalyserDropdownAction = EditorActionFactory.getInstance()
				.createExternalAnalyserAction(Messages.SiteEditorActionBarContributor_ExternalAnalyser);

		this.openDebugPageAction = new OpenDebugPageAction(Messages.SiteEditorActionBarContributor_OpenForDebug);
		this.openDebugPageAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/debug.png")); //$NON-NLS-1$
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

		contributionManager.add(this.refreshSiteAction);
		contributionManager.add(this.refreshMovieAction);

		contributionManager.add(new Separator());
		contributionManager.add(this.openSiteUrlAction);
		contributionManager.add(this.openMovieUrlAction);
		contributionManager.add(this.playMovieAction);
		contributionManager.add(this.refreshEditorAction);

		contributionManager.add(new Separator());
		contributionManager.add(this.copyInfoDropDownAction);
		contributionManager.add(this.generateDownloadUrlsDropDownAction);

		if (null != this.externalAnalyserDropdownAction) {
			contributionManager.add(this.externalAnalyserDropdownAction);
		}

		contributionManager.add(new Separator());
		contributionManager.add(this.downloadAction);
		contributionManager.add(this.updateStateDropDownAction);
		contributionManager.add(this.updateScoreDropDownAction);
		contributionManager.add(this.fillMissingInfoAction);

		if (RuntimeManager.isDebug()) {
			contributionManager.add(this.openDebugPageAction);
		}

		contributionManager.add(new Separator());
		contributionManager.add(this.deleteAction);
		contributionManager.add(this.removeDuplicatedMovieAction);

		if (WorkbenchActivator.getDefault().getConfiguration().isShowClearSiteAction()) {
			contributionManager.add(new Separator());
			contributionManager.add(this.clearSiteAction);
		}

	}

}
