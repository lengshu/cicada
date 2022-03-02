/**
 *
 */
package org.aquarius.cicada.workbench.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.menu.ActionListMenuCreator;
import org.eclipse.jface.action.Action;

/**
 * Drop down menu for copy action.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CopyInfoDropDownAction extends Action {

	private CopyInfoAction copyAllAction;

	/**
	 * @param text
	 */
	public CopyInfoDropDownAction(String text) {
		super(text);

		this.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/copy.png")); //$NON-NLS-1$

		List<Action> actionList = new ArrayList<>();

		CopyInfoAction copyTitleAction = new CopyInfoAction(Messages.CopyInfoDropDownAction_CopyTitles, Movie.PropertyTitle);
		CopyInfoAction copyPageUrlAction = new CopyInfoAction(Messages.CopyInfoDropDownAction_CopyPageUrls, Movie.PropertyPageUrl);

		this.copyAllAction = new CopyInfoAction(Messages.CopyInfoDropDownAction_CopyAll, ""); // $NON-NLS-2$

		actionList.add(copyTitleAction);
		actionList.add(copyPageUrlAction);
		actionList.add(this.copyAllAction);

		this.setMenuCreator(new ActionListMenuCreator(actionList));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		this.copyAllAction.run();
	}

}
