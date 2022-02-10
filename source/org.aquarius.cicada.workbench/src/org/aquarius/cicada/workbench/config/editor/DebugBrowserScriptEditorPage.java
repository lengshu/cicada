/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import org.aquarius.cicada.core.model.ScriptDefinition;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * Editor to debug script.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DebugBrowserScriptEditorPage extends EditorPart implements org.eclipse.ui.IPropertyListener {

	private DebugBrowserScriptControlFactory debugScriptControlFactory = new DebugBrowserScriptControlFactory();

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		this.debugScriptControlFactory.doSave(monitor);
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
		this.setInput(input);
		this.setSite(site);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDirty() {
		return this.debugScriptControlFactory.isDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.debugScriptControlFactory.createControl(parent);

		this.debugScriptControlFactory.addListenerObject(this);
	}

	/**
	 * @return the targetClass
	 */
	public Class<?> getTargetClass() {
		return this.debugScriptControlFactory.getTargetClass();
	}

	/**
	 * @param targetClass the targetClass to set
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.debugScriptControlFactory.setTargetClass(targetClass);
	}

	/**
	 * @return the scriptDefinition
	 */
	public ScriptDefinition getScriptDefinition() {
		return this.debugScriptControlFactory.getScriptDefinition();
	}

	/**
	 * @param scriptDefinition the scriptDefinition to set
	 */
	public void setScriptDefinition(ScriptDefinition scriptDefinition) {
		this.debugScriptControlFactory.setScriptDefinition(scriptDefinition);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		this.firePropertyChange(propId);
	}

}
