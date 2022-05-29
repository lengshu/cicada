package org.aquarius.cicada.workbench.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.aquarius.ui.menu.ActionListMenuCreator;
import org.aquarius.util.StringUtil;
import org.eclipse.jface.action.Action;

/**
 * Drop down menu for update score actions.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateScoreDropDownAction extends Action {

	/**
	 *
	 * @param label
	 */
	public UpdateScoreDropDownAction(String label) {
		super(label);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/updateScore.png"));

		List<Action> actionList = new ArrayList<>();

		for (int i = 0; i <= 5; i++) {
			String scoreLabel = StringUtil.repeatSymbol(StringUtil.Star, i);
			UpdateScoreAction scoreAction = new UpdateScoreAction(scoreLabel, i);
			actionList.add(scoreAction);
		}

		this.setMenuCreator(new ActionListMenuCreator(actionList));
	}

}
