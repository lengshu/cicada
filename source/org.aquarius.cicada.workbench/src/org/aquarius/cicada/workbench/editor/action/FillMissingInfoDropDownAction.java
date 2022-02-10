package org.aquarius.cicada.workbench.editor.action;

import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.menu.ActionListMenuCreator;
import org.eclipse.jface.action.Action;

/**
 * Fill the missing actor and publish date.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FillMissingInfoDropDownAction extends Action {

	/**
	 *
	 * @param label
	 */
	public FillMissingInfoDropDownAction() {
		super("");

		this.setText(Messages.FillMissingInfoDropDownAction_Label);
		this.setDescription(Messages.FillMissingInfoDropDownAction_Description);
		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/fillMissingInfo.png"));

		FillMissingInfoAction justSelectedAction = new FillMissingInfoAction(Messages.FillMissingInfoDropDownAction_FillMissingInfoForSelectedMovie, false); // $NON-NLS-1$

		FillMissingInfoAction allAction = new FillMissingInfoAction(Messages.FillMissingInfoDropDownAction_FillMissingInfoForAllMovie, true); // $NON-NLS-1$

		this.setMenuCreator(new ActionListMenuCreator(justSelectedAction, allAction));
	}

}
