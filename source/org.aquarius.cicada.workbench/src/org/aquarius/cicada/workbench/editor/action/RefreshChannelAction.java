package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.cicada.workbench.job.RefreshChannelJob;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.enu.RefreshType;

/**
 * Refresh the channel to get the newest movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshChannelAction extends AbstractSiteEditorAction {

	private MovieChannel movieChannel;

	/**
	 *
	 * @param text
	 */
	public RefreshChannelAction(String text, MovieChannel movieChannel) {
		super(text);
		this.movieChannel = movieChannel;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType doRun(SiteMultiPageEditor siteEditor) {
		Site site = siteEditor.getMovieSite();

		if (site.isRefreshing()) {
			TooltipUtil.showErrorTip(Messages.WarnDialogTitle, Messages.RefreshSiteAction_RefreshingErrorMessage);
			return RefreshType.None;
		}

		RefreshChannelJob refreshSiteJob = new RefreshChannelJob(Messages.RefreshChannelAction_RefreshChannel, this.movieChannel);
		refreshSiteJob.schedule();

		return RefreshType.None;
	}
}