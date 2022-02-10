/**
 *
 */
package org.aquarius.cicada.workbench.config.editor.action;

import org.aquarius.cicada.workbench.config.editor.SiteConfigEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * The base class for site config editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSiteConfigEditorAction extends Action {

	/**
	 *
	 */
	public AbstractSiteConfigEditorAction() {
		super();
	}

	/**
	 * @param text
	 */
	public AbstractSiteConfigEditorAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public AbstractSiteConfigEditorAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AbstractSiteConfigEditorAction(String text, int style) {
		super(text, style);
	}

	/**
	 * Return the current site config editor.<BR>
	 *
	 * @return
	 */
	protected SiteConfigEditor getSiteConfigEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorPart editorPart = window.getActivePage().getActiveEditor();

		if (editorPart instanceof SiteConfigEditor) {
			return (SiteConfigEditor) editorPart;
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		SiteConfigEditor siteConfigEditor = this.getSiteConfigEditor();

		if (null != siteConfigEditor) {
			this.doRun(siteConfigEditor);
		}
	}

	/**
	 * @param siteConfigEditor
	 */
	protected abstract void doRun(SiteConfigEditor siteConfigEditor);
}
