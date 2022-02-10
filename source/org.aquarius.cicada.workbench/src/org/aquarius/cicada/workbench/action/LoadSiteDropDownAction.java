package org.aquarius.cicada.workbench.action;

import java.util.Collection;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.menu.AbstractMenuCreator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * For drop down menu to load sites.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LoadSiteDropDownAction extends Action {

	private IWorkbenchWindow window;

	public LoadSiteDropDownAction(IWorkbenchWindow window, String label, int style) {
		super(label, style);

		this.window = window;

		initMenu();

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_LOAD_SITE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_LOAD_SITE);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/loadSite.png"));
	}

	/**
	 * init the menu by multi sites.<BR>
	 */
	private void initMenu() {

		this.setMenuCreator(new AbstractMenuCreator() {

			@Override
			protected void createEntries(Menu menu) {
				Collection<String> siteNames = RuntimeManager.getInstance().getSupportImportSiteNames();

				for (String siteName : siteNames) {
					Action action = new LoadSiteAction(LoadSiteDropDownAction.this.window, siteName);
					ActionContributionItem item = new ActionContributionItem(action);
					item.fill(menu, -1);
				}
			}
		});
	}

}
