/**
 *
 */
package org.aquarius.cicada.workbench.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.menu.ActionListMenuCreator;
import org.eclipse.jface.action.Action;

/**
 * Drop down for generating multi download urls.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class GenerateDownloadUrlsDropDownAction extends Action {

	/**
	 * @param text
	 */
	public GenerateDownloadUrlsDropDownAction(String text) {
		super(text);

		this.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/generateDownloadUrl.png"));

		List<Action> actionList = new ArrayList<>();
		List<AbstractDownloadListGenerator> generatorList = RuntimeManager.getInstance().getAllDownloadListGenerators();

		for (AbstractDownloadListGenerator generator : generatorList) {
			GenerateDownloadUrlsAction action = new GenerateDownloadUrlsAction(generator.getName(), generator);
			actionList.add(action);
		}

		this.setMenuCreator(new ActionListMenuCreator(actionList));
	}

}
