/**
 *
 */
package org.aquarius.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * As the base class.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class BaseTableEditorPart extends EditorPart {

	private boolean editing;

	/**
	 *
	 * @return
	 */
	public boolean isEditable() {
		return false;
	}

	/**
	 * @return the editing
	 */
	public boolean isEditing() {
		return this.editing && this.isEditable();
	}

	/**
	 * @param editing the editing to set
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSaveAs() {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setSite(site);
		this.setInput(input);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		// Nothing to od

	}

}
