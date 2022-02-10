package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorAction;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;

/**
 * Lock or unlock the editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LockAction extends AbstractSiteEditorAction {

	/**
	 *
	 * @param label
	 */
	public LockAction() {
		super("", SWT.CHECK);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_LOCK);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_LOCK);

		this.setText(Messages.LockAction_LockLabel);
		this.setDescription(Messages.LockAction_LockDescription);
		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/lock.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doRun(SiteMultiPageEditor siteEditor) {

		if (this.isChecked()) {

			siteEditor.doSave(new NullProgressMonitor());
			siteEditor.setEditing(false);

			this.setText(Messages.LockAction_LockLabel);
			this.setDescription(Messages.LockAction_LockDescription);
			setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/lock.png")); //$NON-NLS-1$

		} else {
			siteEditor.setEditing(true);

			this.setText(Messages.LockAction_UnlockLabel);
			this.setDescription(Messages.LockAction_UnlockDescription);
			setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/unlock.png")); //$NON-NLS-1$

		}

		this.setChecked(!isChecked());

		return false;
	}

}
