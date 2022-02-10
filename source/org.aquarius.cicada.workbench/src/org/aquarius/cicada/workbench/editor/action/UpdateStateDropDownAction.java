package org.aquarius.cicada.workbench.editor.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.ui.menu.ActionListMenuCreator;
import org.eclipse.jface.action.Action;

/**
 * Drop down menu for update state actions.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateStateDropDownAction extends Action {

	/**
	 *
	 * @param label
	 */
	public UpdateStateDropDownAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_UPDATE_STATE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_UPDATE_STATE);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/updateState.png"));

		List<Action> actionList = new ArrayList<>();

		Map<Integer, String> movieStates = MovieHelper.getMovieStateMap();
		for (Entry<Integer, String> entry : movieStates.entrySet()) {
			UpdateStateAction stateAction = new UpdateStateAction(entry.getValue(), entry.getKey());
			actionList.add(stateAction);
		}

		this.setMenuCreator(new ActionListMenuCreator(actionList));
	}

}
