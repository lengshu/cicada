package org.aquarius.cicada.workbench.editor.action.base;

import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * The base class for site editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractSiteEditorAction extends Action {

	/**
	 *
	 * @param text
	 */
	public AbstractSiteEditorAction(String text) {
		super(text);
	}

	/**
	 *
	 * @param label
	 * @param style
	 */
	public AbstractSiteEditorAction(String label, int style) {
		super(label, style);

	}

	/**
	 * Return the current editor.<BR>
	 *
	 * @return
	 */
	protected SiteMultiPageEditor getSiteEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorPart editorPart = window.getActivePage().getActiveEditor();

		if (editorPart instanceof SiteMultiPageEditor) {
			return (SiteMultiPageEditor) editorPart;
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		SiteMultiPageEditor siteEditor = this.getSiteEditor();

		if (null != siteEditor) {
			boolean needRefresh = this.doRun(siteEditor);

			if (needRefresh) {
				siteEditor.refresh();
			}
		}
	}

	/**
	 * execute the real operation.<BR>
	 *
	 * @param siteEditor
	 */
	protected abstract boolean doRun(SiteMultiPageEditor siteEditor);

}
