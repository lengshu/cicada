package org.aquarius.cicada.workbench;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.spi.web.IWebAccessorVisible;
import org.aquarius.cicada.core.web.WebAccessorManager;
import org.aquarius.cicada.workbench.action.ClearCacheAction;
import org.aquarius.cicada.workbench.action.CompactDatabaseAction;
import org.aquarius.cicada.workbench.action.CreateSiteConfigAction;
import org.aquarius.cicada.workbench.action.DebugBrowserAction;
import org.aquarius.cicada.workbench.action.EditSiteConfigDropDownAction;
import org.aquarius.cicada.workbench.action.HelpAction;
import org.aquarius.cicada.workbench.action.LoadSiteDropDownAction;
import org.aquarius.cicada.workbench.action.NewBrowserAction;
import org.aquarius.cicada.workbench.action.OpenPerspectiveAction;
import org.aquarius.cicada.workbench.action.OpenSiteDropDownAction;
import org.aquarius.cicada.workbench.action.OpenUrlAction;
import org.aquarius.cicada.workbench.action.ParseMovieAction;
import org.aquarius.cicada.workbench.action.ResetPerspectiveAction;
import org.aquarius.cicada.workbench.action.ResetResourceAction;
import org.aquarius.cicada.workbench.action.ShowBrowserAction;
import org.aquarius.cicada.workbench.action.UpdateConfigAction;
import org.aquarius.cicada.workbench.action.debug.HotReloadAction;
import org.aquarius.cicada.workbench.action.debug.ShowLocalScriptEditorAction;
import org.aquarius.cicada.workbench.action.search.SearchMovieByKeywordDropDownAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IAction exitAction;

	private IAction aboutAction;

	private Action helpAction;

	private Action onlineHelpAction;

	private Action openOfficalSiteAction;

	private Action openIssuesAction;

	private IAction loadSiteAction;

	private IAction parseMovieAction;

	private IAction createSiteConfigAction;

	private IAction newBrowserAction;

	private IAction showBrowserAction;

	private IAction compactDatabaseAction;

	private EditSiteConfigDropDownAction editSiteConfigDropDownAction;

	private OpenSiteDropDownAction openSiteDropDownAction;

	private SearchMovieByKeywordDropDownAction searchMovieByKeywordAction;

	private IAction clearCacheAction;

	private IAction resetResourceAction;

	private IAction hotReloadAction;

	private IAction debugBrowserAction;

	private IAction resetPerspectiveAction;

	private IWorkbenchWindow window;

	private IAction saveAction;

	private IAction saveAllAction;

	private IAction saveAsAction;

	private IAction closeAction;

	private IAction closeAllAction;

	private IAction preferenceAction;

	private IAction updateConfigAction;

	/**
	 *
	 * @param configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		this.window = window;

		this.saveAction = ActionFactory.SAVE.create(window);
		this.saveAction.setText(Messages.ApplicationActionBarAdvisor_Save);
		register(this.saveAction);

		this.saveAllAction = ActionFactory.SAVE_ALL.create(window);
		this.saveAllAction.setText(Messages.ApplicationActionBarAdvisor_SaveAll);
		register(this.saveAllAction);

		this.saveAsAction = ActionFactory.SAVE_AS.create(window);
		this.saveAsAction.setText(Messages.ApplicationActionBarAdvisor_SaveAS);
		register(this.saveAsAction);

		this.closeAction = ActionFactory.CLOSE.create(window);
		this.closeAction.setText(Messages.ApplicationActionBarAdvisor_Close);
		register(this.closeAction);

		this.closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		this.closeAllAction.setText(Messages.ApplicationActionBarAdvisor_CloseAll);
		register(this.closeAllAction);

		this.exitAction = ActionFactory.QUIT.create(window);
		this.exitAction.setText(Messages.ApplicationActionBarAdvisor_Exit);
		register(this.exitAction);

		this.preferenceAction = ActionFactory.PREFERENCES.create(window);
		this.preferenceAction.setText(Messages.ApplicationActionBarAdvisor_Preferences);
		register(this.preferenceAction);

		this.aboutAction = ActionFactory.ABOUT.create(window);
		this.aboutAction.setText(Messages.ApplicationActionBarAdvisor_About);
		register(this.aboutAction);

		this.updateConfigAction = new UpdateConfigAction(Messages.ApplicationActionBarAdvisor_UpdateConfig);
		this.updateConfigAction.setToolTipText(Messages.ApplicationActionBarAdvisor_UpdateConfigToolTip);

		this.helpAction = new HelpAction(Messages.ApplicationActionBarAdvisor_Help);
		register(this.helpAction);

		this.onlineHelpAction = new OpenUrlAction(Messages.ApplicationActionBarAdvisor_OnlineHelp,
				"https://github.com/aquariusStudio/cicada/blob/master/README.md"); //$NON-NLS-1$
		this.openOfficalSiteAction = new OpenUrlAction(Messages.ApplicationActionBarAdvisor_OpenOfficalSite, "https://github.com/aquariusStudio/cicada"); //$NON-NLS-1$
		this.openIssuesAction = new OpenUrlAction(Messages.ApplicationActionBarAdvisor_OpenIssuesAction, "https://github.com/aquariusStudio/cicada/issues"); //$NON-NLS-1$

		this.loadSiteAction = new LoadSiteDropDownAction(window, Messages.ApplicationActionBarAdvisor_ImportMovieSite, SWT.DROP_DOWN);
		register(this.loadSiteAction);

		this.newBrowserAction = new NewBrowserAction(Messages.ApplicationActionBarAdvisor_NewBrowser);

		this.openSiteDropDownAction = new OpenSiteDropDownAction(Messages.ApplicationActionBarAdvisor_OpenSite);

		this.debugBrowserAction = new DebugBrowserAction(Messages.ApplicationActionBarAdvisor_DebugBrowser);
		this.resetPerspectiveAction = new ResetPerspectiveAction(Messages.ApplicationActionBarAdvisor_ResetPerspective);

		this.searchMovieByKeywordAction = new SearchMovieByKeywordDropDownAction(this.window, Messages.ApplicationActionBarAdvisor_SearchMovieByKeyword);

		this.compactDatabaseAction = new CompactDatabaseAction(Messages.ApplicationActionBarAdvisor_CompactDatabase);
		register(this.compactDatabaseAction);

		this.clearCacheAction = new ClearCacheAction(Messages.ApplicationActionBarAdvisor_ClearCache);
		register(this.clearCacheAction);

		this.resetResourceAction = new ResetResourceAction(Messages.ApplicationActionBarAdvisor_ResetResource);

		this.parseMovieAction = new ParseMovieAction(window, Messages.ApplicationActionBarAdvisor_ParseMovie);
		register(this.parseMovieAction);

		this.createSiteConfigAction = new CreateSiteConfigAction(window, Messages.ApplicationActionBarAdvisor_CreateSiteConfig);
		register(this.createSiteConfigAction);

		this.editSiteConfigDropDownAction = new EditSiteConfigDropDownAction(window, Messages.ApplicationActionBarAdvisor_EditSiteConfig);
		register(this.editSiteConfigDropDownAction);

		this.hotReloadAction = new HotReloadAction(Messages.SiteEditorActionBarContributor_HotReload);
		register(this.hotReloadAction);

		IWebAccessorVisible visibleState = WebAccessorManager.getInstance().getDefaultWebAccessorService().getVisibleState();

		if (visibleState != IWebAccessorVisible.none) {
			this.showBrowserAction = new ShowBrowserAction(Messages.ApplicationActionBarAdvisor_ShowBrowser);
			register(this.showBrowserAction);
		}
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_File, IWorkbenchActionConstants.M_FILE);
		fileMenu.add(this.saveAction);
		// fileMenu.add(this.saveAsAction);
		fileMenu.add(this.saveAllAction);
		fileMenu.add(new Separator());
		fileMenu.add(this.closeAction);
		fileMenu.add(this.closeAllAction);

		fileMenu.add(new Separator());
		fileMenu.add(this.parseMovieAction);
		fileMenu.add(new Separator());
		fileMenu.add(this.exitAction);

		MenuManager windowMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_Window, IWorkbenchActionConstants.M_WINDOW);

		MenuManager viewMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_OpenView);
		IContributionItem viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(this.window);
		viewMenu.add(viewList);

		MenuManager perspectiveMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_OpenPerspective);
		IContributionItem perspectiveList = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(this.window);

		IPerspectiveRegistry perspectiveRegistry = this.window.getWorkbench().getPerspectiveRegistry();

		perspectiveMenu.add(new OpenPerspectiveAction(this.window, perspectiveRegistry.findPerspectiveWithId(BrowserPerspective.ID)));
		perspectiveMenu.add(new OpenPerspectiveAction(this.window, perspectiveRegistry.findPerspectiveWithId(TablePerspective.ID)));

		perspectiveMenu.add(new Separator());
		perspectiveMenu.add(this.resetPerspectiveAction);
		perspectiveMenu.add(new Separator());
		perspectiveMenu.add(perspectiveList);

		windowMenu.add(perspectiveMenu);
		windowMenu.add(viewMenu);

		windowMenu.add(this.preferenceAction);

		MenuManager operationMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_Operations);

		MenuManager openSiteMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_OpenSite);
		operationMenu.add(openSiteMenu);

		openSiteMenu.setRemoveAllWhenShown(true);

		openSiteMenu.addMenuListener((manager) -> {
			List<IContributionItem> items = this.openSiteDropDownAction.createActionItems();

			for (IContributionItem item : items) {
				manager.add(item);
			}
		});

		MenuManager debugMenu = new MenuManager(Messages.ActionDebug);
		operationMenu.add(debugMenu);

		debugMenu.add(this.newBrowserAction);
		debugMenu.add(this.showBrowserAction);
		debugMenu.add(this.debugBrowserAction);

		ShowLocalScriptEditorAction showLocalScriptEditorAction = new ShowLocalScriptEditorAction(Messages.ApplicationActionBarAdvisor_ShowLocalScriptEditor);
		debugMenu.add(showLocalScriptEditorAction);

		if (RuntimeManager.isDebug()) {
			debugMenu.add(this.hotReloadAction);
		}

		operationMenu.add(new Separator());

		operationMenu.add(this.compactDatabaseAction);
		operationMenu.add(this.clearCacheAction);
		operationMenu.add(this.resetResourceAction);
		operationMenu.add(new Separator());

		operationMenu.add(this.createSiteConfigAction);

		MenuManager editSiteMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_EditSiteConfig);
		operationMenu.add(editSiteMenu);

		editSiteMenu.setRemoveAllWhenShown(true);

		editSiteMenu.addMenuListener((manager) -> {
			List<IContributionItem> items = this.editSiteConfigDropDownAction.createActionItems();

			for (IContributionItem item : items) {
				manager.add(item);
			}
		});

		operationMenu.add(editSiteMenu);

		operationMenu.add(new Separator());

		MenuManager searchMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_SearchMovieByKeyword);

		searchMenu.setRemoveAllWhenShown(true);

		searchMenu.addMenuListener((manager) -> {
			List<IContributionItem> items = this.searchMovieByKeywordAction.createActionItems();

			for (IContributionItem item : items) {
				manager.add(item);
			}
		});

		operationMenu.add(searchMenu);

		// Help
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_Help, IWorkbenchActionConstants.M_HELP);

		helpMenu.add(this.helpAction);
		helpMenu.add(this.onlineHelpAction);
		helpMenu.add(this.openOfficalSiteAction);
		helpMenu.add(this.openIssuesAction);
		helpMenu.add(new Separator());

		helpMenu.add(this.updateConfigAction);
		helpMenu.add(new Separator());

		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		helpMenu.add(new Separator());

		helpMenu.add(this.aboutAction);

		menuBar.add(fileMenu);
		menuBar.add(operationMenu);
		menuBar.add(windowMenu);
		// Add a group marker indicating where action set menus will appear.
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(helpMenu);

	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$

		toolbar.add(this.loadSiteAction);
		toolbar.add(this.searchMovieByKeywordAction);
		toolbar.add(this.parseMovieAction);
		toolbar.add(new Separator());
	}

}
