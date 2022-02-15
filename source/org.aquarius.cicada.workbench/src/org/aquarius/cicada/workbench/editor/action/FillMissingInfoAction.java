package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.cicada.workbench.job.FillMissingInfoJob;
import org.aquarius.util.enu.RefreshType;

/**
 * Fill the missing actor and publish date.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FillMissingInfoAction extends AbstractSiteEditorAction {

	private boolean selectAll;

	/**
	 *
	 * @param label
	 */
	public FillMissingInfoAction(String label, boolean selectAll) {
		super(label); // $NON-NLS-1$

		this.selectAll = selectAll;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType doRun(SiteMultiPageEditor siteEditor) {

		Site site = siteEditor.getAdapter(Site.class);

		if (this.selectAll) {
			new FillMissingInfoJob(this.getText(), site, site.getMovieList(), false).schedule();
		} else {
			new FillMissingInfoJob(this.getText(), site, siteEditor.getSelectedMovieList(), true).schedule();
		}

		return RefreshType.None;
	}

}
