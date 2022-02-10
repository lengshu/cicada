/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.config.editor.action.DeleteSiteConfigAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * The contributor to toolbar and menu.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteConfigEditorActionBarContributor extends EditorActionBarContributor {

	private IAction deleteAction;

	private IAction applyAction;

	/**
	 *
	 */
	public SiteConfigEditorActionBarContributor() {
		makeActions();
	}

	/**
	 * create actions.<BR>
	 */
	private void makeActions() {
		this.deleteAction = new DeleteSiteConfigAction(Messages.SiteConfigEditorActionBarContributor_Delete);
		// this.applyAction = new
		// ApplySiteConfigAction(Messages.SiteConfigEditorActionBarContributor_Apply);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);

		toolBarManager.add(this.deleteAction);
		// toolBarManager.add(this.applyAction);
	}

}
