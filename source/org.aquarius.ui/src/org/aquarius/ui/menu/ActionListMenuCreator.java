/**
 *
 */
package org.aquarius.ui.menu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.widgets.Menu;

/**
 * Use a predefined action to define menu.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ActionListMenuCreator extends AbstractMenuCreator {

	private List<Action> actionList = new ArrayList<>();

	/**
	 *
	 */
	public ActionListMenuCreator(List<Action> actionList) {
		this.actionList.addAll(actionList);
	}

	/**
	 *
	 */
	public ActionListMenuCreator(Action... actions) {
		CollectionUtils.addAll(this.actionList, actions);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createEntries(Menu menu) {
		for (Action action : this.actionList) {
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(menu, -1);
		}
	}

}
