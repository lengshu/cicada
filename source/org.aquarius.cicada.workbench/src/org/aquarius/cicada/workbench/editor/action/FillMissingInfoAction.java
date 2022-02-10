package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.cicada.workbench.job.FillMissingInfoJob;

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
	protected boolean doRun(SiteMultiPageEditor siteEditor) {

		Site site = siteEditor.getAdapter(Site.class);

		if (this.selectAll) {
			new FillMissingInfoJob(this.getText(), site, site.getMovieList()).schedule();
		} else {
			new FillMissingInfoJob(this.getText(), site, siteEditor.getSelectedMovieList()).schedule();
		}

		return false;
	}

}
