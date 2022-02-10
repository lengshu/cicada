package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.cicada.workbench.job.RefreshSiteJob;
import org.aquarius.ui.util.TooltipUtil;

/**
 * Refresh the site to get the newest movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshSiteAction extends AbstractSiteEditorAction {

	/**
	 *
	 * @param text
	 */
	public RefreshSiteAction(String text) {
		super(text);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_REFRESH_SITE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_REFRESH_SITE);
		// setImageDescriptor(
		// org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/refreshSite.png"));
		// //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doRun(SiteMultiPageEditor siteEditor) {
		Site site = siteEditor.getMovieSite();

		if (site.isRefreshing()) {
			TooltipUtil.showErrorTip(Messages.WarnDialogTitle, Messages.RefreshSiteAction_RefreshingErrorMessage);
			return false;
		}

		RefreshSiteJob refreshSiteJob = new RefreshSiteJob(Messages.RefreshSiteAction_RefreshSite, site.getSiteName(), false);
		refreshSiteJob.schedule();
		return false;
	}
}