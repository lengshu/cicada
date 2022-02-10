package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.aquarius.util.DesktopUtil;

/**
 * Open the main page of the site in a browser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class OpenSiteUrlAction extends AbstractSiteEditorAction {

	/**
	 *
	 * @param label
	 */
	public OpenSiteUrlAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_OPEN_SITE_URL);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_OPEN_SITE_URL);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/openSiteUrl.png"));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doRun(SiteMultiPageEditor siteEditor) {

		String[] urls = siteEditor.getMovieSite().getMovieParser().getSiteUrls();
		DesktopUtil.openWebpages(urls);

		return false;
	}

}
