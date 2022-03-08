package org.aquarius.downloader.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.core.spi.AbstractProgressListener;
import org.aquarius.downloader.ui.DownloadActivator;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.downloader.ui.service.ITaskService;
import org.aquarius.downloader.ui.view.action.AddTableAction;
import org.aquarius.downloader.ui.view.action.CopyInfoTableAction;
import org.aquarius.downloader.ui.view.action.DeleteTableAction;
import org.aquarius.downloader.ui.view.action.ExportTableAction;
import org.aquarius.downloader.ui.view.action.ImportTableAction;
import org.aquarius.downloader.ui.view.action.OpenFileTableAction;
import org.aquarius.downloader.ui.view.action.OpenFolderTableAction;
import org.aquarius.downloader.ui.view.action.OpenSourceUrlTableAction;
import org.aquarius.downloader.ui.view.action.PauseTableAction;
import org.aquarius.downloader.ui.view.action.StartTableAction;
import org.aquarius.downloader.ui.view.table.LengthColumnLabelProvider;
import org.aquarius.downloader.ui.view.table.RemaingTimeColumnLabelProvider;
import org.aquarius.downloader.ui.view.table.StateViewerFilter;
import org.aquarius.downloader.ui.view.table.TaskContentProvider;
import org.aquarius.downloader.ui.view.tree.TaskStateLabelProvider;
import org.aquarius.ui.action.ReflectionAction;
import org.aquarius.ui.key.KeyBinder;
import org.aquarius.ui.key.KeyBinderManager;
import org.aquarius.ui.table.label.BeanPropertyColumnLableProvider;
import org.aquarius.ui.table.label.ImageLabelProvider;
import org.aquarius.ui.table.label.IndexLabelProvider;
import org.aquarius.ui.table.label.NumberPropertyColumnLableProvider;
import org.aquarius.ui.tree.TreeNodeContentProvider;
import org.aquarius.ui.util.PersistenceUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.DesktopUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.collection.Entry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Download Task View.<BR>
 * It use a tree to category.<BR>
 * A table is used to show tasks.<BR>
 * Many actions are created to manage tasks.<BR>
 */

public class DownloadView extends ViewPart implements ITaskService {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.aquarius.downloader.ui.view.DownloadView"; //$NON-NLS-1$

	public static final String PluginId = "org.aquarius.downloader.ui"; //$NON-NLS-1$

	private static final String TaskTableColumnWidthKey = DownloadView.class.getName() + "TaskTableColumnWidthKey.1";

	private static final String SplitRatioKey = DownloadView.class.getName() + ".SashFormWeights";

	private static final int StateAll = -1;

	private SashForm sashForm;

	private TableViewer taskTableViewer;

	private TreeViewer treeViewer;

	private StateViewerFilter stateViewerFilter = new StateViewerFilter();

	// private TaskContentProvider taskContentProvider;

	private Action addAction;

	private Action startAction;
	private Action pauseAction;

	private Action deleteAction;
	private Action deleteFileAction;

	private Action openFileAction;
	private Action openFolderAction;

	private Action openSourceUrlAction;

	private Action exportAction;

	private Action importAction;

	private Action copyAllAction;

	private Action refreshAction;

	// private Action updateAction;

	private Action moveUpAction;

	private Action moveDownAction;

	private Action moveTopAction;

	private Action moveBottomAction;

	private List<Action> copyActionList = new ArrayList<Action>();

	private Map<Object, Image> imageMapping = new HashMap<>();

	@Override
	public void createPartControl(Composite parent) {

		this.imageMapping.put(StateAll, DownloadActivator.getImageDescriptor("/icons/all.png").createImage()); //$NON-NLS-1$

//		this.imageMapping.put(DownloadTask.StateDelete,
//				DownloadActivator.getImageDescriptor("/icons/delete.png").createImage()); //$NON-NLS-1$

		this.imageMapping.put(DownloadTask.StateError, DownloadActivator.getImageDescriptor("/icons/error.png").createImage()); //$NON-NLS-1$
		this.imageMapping.put(DownloadTask.StateFinish, DownloadActivator.getImageDescriptor("/icons/finish.png").createImage()); //$NON-NLS-1$
		this.imageMapping.put(DownloadTask.StateWaiting, DownloadActivator.getImageDescriptor("/icons/init.png").createImage()); //$NON-NLS-1$
//		this.imageMapping.put(DownloadTask.StateStart,
//				DownloadActivator.getImageDescriptor("/icons/start.png").createImage()); //$NON-NLS-1$
		this.imageMapping.put(DownloadTask.StatePause, DownloadActivator.getImageDescriptor("/icons/pause.png").createImage()); //$NON-NLS-1$
//		this.imageMapping.put(DownloadTask.StateResume,
//				DownloadActivator.getImageDescriptor("/icons/resume.png").createImage()); //$NON-NLS-1$
		this.imageMapping.put(DownloadTask.StateRunning, DownloadActivator.getImageDescriptor("/icons/running.png").createImage()); //$NON-NLS-1$

		this.sashForm = new SashForm(parent, SWT.NONE);
		this.sashForm.setSashWidth(8);

		buildTree(this.sashForm);
		buildTable(this.sashForm);

		this.sashForm.setWeights(new int[] { 8, 92 });

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		initDownloadListener();
		changeTaskState(StateAll);

		initKeyListeners();

		this.restoreUiData();

		IPreferenceStore store = DownloadActivator.getDefault().getPreferenceStore();

		PersistenceUtil.addSaveSupport(store, TaskTableColumnWidthKey, this.taskTableViewer.getTable());
		PersistenceUtil.addSaveSupport(store, SplitRatioKey, this.sashForm);
	}

	/**
	 *
	 */
	private void initKeyListeners() {

		Map<KeyBinder, IAction> mapping = new HashMap<>();
		mapping.put(KeyBinder.DeleteKeyBinder, this.deleteAction);
		mapping.put(KeyBinder.ShiftDeleteKeyBinder, this.deleteFileAction);
		mapping.put(KeyBinder.CopyKeyBinder, this.copyAllAction);

		mapping.put(KeyBinder.createKeyBinder("ctrl+down", "run"), this.moveDownAction);
		mapping.put(KeyBinder.createKeyBinder("ctrl+up", "run"), this.moveUpAction);

		mapping.put(KeyBinder.createKeyBinder("alt+down", "run"), this.moveBottomAction);
		mapping.put(KeyBinder.createKeyBinder("alt+up", "run"), this.moveTopAction);

		KeyBinderManager.bind(this.taskTableViewer.getTable(), mapping);

	}

	/**
	 * create download task listener to update ui.<BR>
	 */
	private void initDownloadListener() {

		DownloadManager.getInstance().addListener(new AbstractProgressListener() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void onFinish(DownloadTask... downloadTasks) {
				Display.getDefault().asyncExec(() -> {
					DownloadView.this.taskTableViewer.refresh();
				});
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void onUpdate(DownloadTask... downloadTasks) {

				Display.getDefault().asyncExec(() -> {
					DownloadView.this.taskTableViewer.update(downloadTasks, null);
				});

			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void onAdd(DownloadTask... downloadTasks) {
				Display.getDefault().asyncExec(() -> {
					DownloadView.this.taskTableViewer.refresh();
				});
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void onDelete(DownloadTask... downloadTasks) {
				Display.getDefault().asyncExec(() -> {
					DownloadView.this.taskTableViewer.refresh();
				});
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void onPause(DownloadTask... downloadTasks) {
				Display.getDefault().asyncExec(() -> {
					DownloadView.this.taskTableViewer.refresh();
				});
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void onError(DownloadTask... downloadTasks) {
				Display.getDefault().asyncExec(() -> {
					DownloadView.this.taskTableViewer.refresh();
				});
			}

		});
	}

	/**
	 * Build tree for task state.<BR>
	 *
	 * @param parent
	 */
	private void buildTree(Composite parent) {
		this.treeViewer = new TreeViewer(parent, SWT.FLAT | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

		DefaultMutableTreeNode allNode = new DefaultMutableTreeNode(new Entry<Integer, String>(StateAll, Messages.DownloadView_StateAll));

		DefaultMutableTreeNode runningNode = new DefaultMutableTreeNode(
				new Entry<Integer, String>(DownloadTask.StateRunning, Messages.DownloadView_StateRunning));
		DefaultMutableTreeNode pauseNode = new DefaultMutableTreeNode(new Entry<Integer, String>(DownloadTask.StatePause, Messages.DownloadView_StatePause));
		DefaultMutableTreeNode initNode = new DefaultMutableTreeNode(new Entry<Integer, String>(DownloadTask.StateWaiting, Messages.DownloadView_StateInit));
//		DefaultMutableTreeNode startNode = new DefaultMutableTreeNode(
//				new Entry<Integer, String>(DownloadTask.StateStart, Messages.DownloadView_StateStart));
		DefaultMutableTreeNode finishNode = new DefaultMutableTreeNode(new Entry<Integer, String>(DownloadTask.StateFinish, Messages.DownloadView_StateFinish));
		DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode(new Entry<Integer, String>(DownloadTask.StateError, Messages.DownloadView_StateError));
//		DefaultMutableTreeNode deleteNode = new DefaultMutableTreeNode(
//				new Entry<Integer, String>(DownloadTask.StateDelete, Messages.DownloadView_StateDelete));

		allNode.add(runningNode);
		allNode.add(initNode);
		allNode.add(pauseNode);
		// allNode.add(startNode);
		allNode.add(finishNode);
		allNode.add(errorNode);
		// allNode.add(deleteNode);

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode.add(allNode);

		this.treeViewer.setContentProvider(new TreeNodeContentProvider());
		this.treeViewer.setLabelProvider(new TaskStateLabelProvider(this.imageMapping));

		this.treeViewer.setInput(rootNode);

		this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) SwtUtil.findFirstElement(selection);

				Entry<?, ?> entry = (Entry<?, ?>) node.getUserObject();

				if (entry.getKey() instanceof Integer) {
					Integer value = (Integer) entry.getKey();
					changeTaskState(value);
				}
			}
		});

	}

	/**
	 * When the tree state is changed, the task table need to be refreshed.<BR>
	 *
	 * @param state
	 */
	private void changeTaskState(int state) {
		if (this.stateViewerFilter.getState() != state) {
			this.stateViewerFilter.setState(state);
			this.taskTableViewer.refresh();
		}
	}

	/**
	 * Build a task table to show tasks.<BR>
	 */
	private void buildTable(Composite parent) {

		this.taskTableViewer = new TableViewer(parent, SWT.FLAT | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		// this.taskContentProvider = new TaskContentProvider();
		this.taskTableViewer.setContentProvider(new TaskContentProvider());
		this.taskTableViewer.setFilters(this.stateViewerFilter);
		getSite().setSelectionProvider(this.taskTableViewer);

		Table table = this.taskTableViewer.getTable();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableViewerColumn indexTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		indexTableColumn.setLabelProvider(new IndexLabelProvider(this.taskTableViewer));
		indexTableColumn.getColumn().setWidth(64);

		final TableViewerColumn stateTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		stateTableColumn.setLabelProvider(new ImageLabelProvider("state", this.imageMapping)); //$NON-NLS-1$
		stateTableColumn.getColumn().setWidth(24);
		stateTableColumn.getColumn().setText(" "); //$NON-NLS-1$

		final TableViewerColumn titleTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		titleTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("title")); //$NON-NLS-1$
		titleTableColumn.getColumn().setWidth(600);
		titleTableColumn.getColumn().setText(Messages.DownloadView_ColumnTitle);
		titleTableColumn.getColumn().setAlignment(SWT.LEFT);

		final TableViewerColumn sourceHostTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		sourceHostTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("sourceHost")); //$NON-NLS-1$
		sourceHostTableColumn.getColumn().setWidth(100);
		sourceHostTableColumn.getColumn().setText(Messages.DownloadView_ColumnSourceHost);

		final TableViewerColumn downloadHostTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		downloadHostTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("downloadHost")); //$NON-NLS-1$
		downloadHostTableColumn.getColumn().setWidth(140);
		downloadHostTableColumn.getColumn().setText(Messages.DownloadView_ColumnDownloadHost);

		final TableViewerColumn totalLengthTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		totalLengthTableColumn.setLabelProvider(new LengthColumnLabelProvider("remoteFileLength")); //$NON-NLS-1$
		totalLengthTableColumn.getColumn().setWidth(90);
		totalLengthTableColumn.getColumn().setText(Messages.DownloadView_ColumnFileLength);

		final TableViewerColumn finishedLengthTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		finishedLengthTableColumn.setLabelProvider(new LengthColumnLabelProvider("finishedLength")); //$NON-NLS-1$
		finishedLengthTableColumn.getColumn().setWidth(90);
		finishedLengthTableColumn.getColumn().setText(Messages.DownloadView_ColumnFinishedLength);

		final TableViewerColumn remaingTimeTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		remaingTimeTableColumn.setLabelProvider(new RemaingTimeColumnLabelProvider()); // $NON-NLS-1$
		remaingTimeTableColumn.getColumn().setWidth(90);
		remaingTimeTableColumn.getColumn().setText(Messages.DownloadView_ColumnRemaingTime);

		final TableViewerColumn speedTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		speedTableColumn.setLabelProvider(new NumberPropertyColumnLableProvider("speed", "# K", SystemUtil.DiskSizeInK)); //$NON-NLS-1$ //$NON-NLS-2$
		speedTableColumn.getColumn().setWidth(90);
		speedTableColumn.getColumn().setText(Messages.DownloadView_ColumnSpeed);

		final TableViewerColumn percentTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		percentTableColumn.setLabelProvider(new NumberPropertyColumnLableProvider("percent", "#.##%", 100)); //$NON-NLS-1$ //$NON-NLS-2$
		percentTableColumn.getColumn().setWidth(90);
		percentTableColumn.getColumn().setText(Messages.DownloadView_ColumnPercent);

		final TableViewerColumn retryCountTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		retryCountTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("retryCount")); //$NON-NLS-1$
		retryCountTableColumn.getColumn().setWidth(40);
		retryCountTableColumn.getColumn().setText(Messages.DownloadView_ColumnRetry);

		final TableViewerColumn currentThreadCountTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		currentThreadCountTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("currentThreadCount")); //$NON-NLS-1$
		currentThreadCountTableColumn.getColumn().setWidth(80);
		currentThreadCountTableColumn.getColumn().setText(Messages.DownloadView_ColumnThreads);

		final TableViewerColumn errorMessageTableColumn = new TableViewerColumn(this.taskTableViewer, SWT.NONE);
		BeanPropertyColumnLableProvider errorPropertyColumnLableProvider = new BeanPropertyColumnLableProvider("errorMessage");
		errorPropertyColumnLableProvider.setUseTooltip(true);
		errorMessageTableColumn.setLabelProvider(errorPropertyColumnLableProvider); // $NON-NLS-1$
		errorMessageTableColumn.getColumn().setWidth(80);
		errorMessageTableColumn.getColumn().setText(Messages.DownloadView_ColumnErrorMessage);

		this.taskTableViewer.setInput(DownloadManager.getInstance());

		final Color cyan = table.getShell().getDisplay().getSystemColor(SWT.COLOR_CYAN);
		final Color green = table.getShell().getDisplay().getSystemColor(SWT.COLOR_GREEN);

		TableColumn paintColumn = percentTableColumn.getColumn();
		int index = table.indexOf(paintColumn);
		SwtUtil.updatePercentColumn(table, index, cyan, green);

	}

	/**
	 * Add a context menu for operations.<BR>
	 */
	private void hookContextMenu() {
//		MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
//		menuManager.setRemoveAllWhenShown(true);
//		menuManager.addMenuListener(new IMenuListener() {
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				DownloadView.this.fillContextMenu(manager);
//			}
//		});
//
//		Menu menu = menuManager.createContextMenu(this.taskTableViewer.getControl());
//		this.taskTableViewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuManager, this.taskTableViewer);

		MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		DownloadView.this.fillContextMenu(menuManager);

		Table table = this.taskTableViewer.getTable();
		Menu menu = menuManager.createContextMenu(table);

		table.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.button == 3) {
					menu.setVisible(true);
				}
			}
		});

		getSite().registerContextMenu(menuManager, this.taskTableViewer);
	}

	/**
	 * add action to action bar.<BR>
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 *
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {

		// manager.add(this.addAction);
		manager.add(this.startAction);
		manager.add(this.pauseAction);
		manager.add(new Separator());

		manager.add(this.deleteAction);
		manager.add(this.deleteFileAction);
		manager.add(new Separator());

		manager.add(this.refreshAction);
		// manager.add(this.updateAction);
		manager.add(new Separator());

		manager.add(this.openFileAction);
		manager.add(this.openFolderAction);
		manager.add(this.openSourceUrlAction);
		manager.add(new Separator());

		for (Action action : this.copyActionList) {
			manager.add(action);
		}

		manager.add(new Separator());
		manager.add(this.importAction);
		manager.add(this.exportAction);
	}

	/**
	 * Fill context menu.<BR>
	 *
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {

		// manager.add(this.addAction);
		manager.add(this.startAction);
		manager.add(this.pauseAction);
		manager.add(new Separator());

		manager.add(this.deleteAction);
		manager.add(this.deleteFileAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.add(this.openFileAction);
		manager.add(this.openFolderAction);
		manager.add(this.openSourceUrlAction);
		manager.add(new Separator());

		for (Action action : this.copyActionList) {
			manager.add(action);
		}

		manager.add(new Separator());
		manager.add(this.moveUpAction);
		manager.add(this.moveDownAction);
		manager.add(this.moveTopAction);
		manager.add(this.moveBottomAction);

		manager.add(new Separator());
		manager.add(this.importAction);
		manager.add(this.exportAction);
	}

	/**
	 * Add action to the toolbar.<BR>
	 *
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		// manager.add(this.addAction);
		manager.add(this.startAction);
		manager.add(this.pauseAction);

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.add(this.refreshAction);
		// manager.add(this.updateAction);
	}

	/**
	 * create actions.<BR>
	 *
	 */
	private void makeActions() {

		this.addAction = new AddTableAction(this);
		this.addAction.setText(Messages.DownloadView_ActionAdd);
		this.addAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/add.png")); //$NON-NLS-1$

		this.pauseAction = new PauseTableAction(this);
		this.pauseAction.setText(Messages.DownloadView_ActionPause);
		this.pauseAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/pause.png")); //$NON-NLS-1$

		this.startAction = new StartTableAction(this);
		this.startAction.setText(Messages.DownloadView_ActionStart);
		this.startAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/start.png")); //$NON-NLS-1$

		this.deleteAction = new DeleteTableAction(this, false);
		this.deleteAction.setText(Messages.DownloadView_ActionDelete);
		this.deleteAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/delete.png")); //$NON-NLS-1$

		this.deleteFileAction = new DeleteTableAction(this, true);
		this.deleteFileAction.setText(Messages.DownloadView_ActionDeleteFiles);
		this.deleteFileAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/deleteAll.png")); //$NON-NLS-1$

		this.refreshAction = new ReflectionAction(this, "refreshTasks");
		this.refreshAction.setText(Messages.DownloadView_ActionRefresh);
		this.refreshAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/refresh.png")); //$NON-NLS-1$

		// this.updateAction = new ReflectionAction(this, "updateTasks");
		// this.updateAction.setText(Messages.DownloadView_ActionUpdate);
		// this.updateAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,
		// "/icons/update.png")); //$NON-NLS-1$

		this.moveUpAction = new ReflectionAction(this, "moveUp");
		this.moveUpAction.setText(Messages.DownloadView_ActionMoveUp);
		this.moveUpAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/moveUp.png")); //$NON-NLS-1$

		this.moveDownAction = new ReflectionAction(this, "moveDown");
		this.moveDownAction.setText(Messages.DownloadView_ActionMoveDown);
		this.moveDownAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/moveDown.png")); //$NON-NLS-1$

		this.moveTopAction = new ReflectionAction(this, "moveTop");
		this.moveTopAction.setText(Messages.DownloadView_ActionMoveTop);
		this.moveTopAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/moveTop.png")); //$NON-NLS-1$

		this.moveBottomAction = new ReflectionAction(this, "moveBottom");
		this.moveBottomAction.setText(Messages.DownloadView_ActionMoveBottom);
		this.moveBottomAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/moveBottom.png")); //$NON-NLS-1$

		this.openFileAction = new OpenFileTableAction(this);
		this.openFileAction.setText(Messages.DownloadView_ActionOpenFile);
		this.openFileAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/openFile.png")); //$NON-NLS-1$

		this.openFolderAction = new OpenFolderTableAction(this);
		this.openFolderAction.setText(Messages.DownloadView_ActionOpenFolder);
		this.openFolderAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/openFolder.png")); //$NON-NLS-1$

		this.openSourceUrlAction = new OpenSourceUrlTableAction(this);
		this.openSourceUrlAction.setText(Messages.DownloadView_ActionOpenRefer);
		this.openSourceUrlAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/openBrowser.png")); //$NON-NLS-1$

		this.importAction = new ImportTableAction(this);
		this.importAction.setText(Messages.DownloadView_ActionImport);
		this.importAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/import.png")); //$NON-NLS-1$

		this.exportAction = new ExportTableAction(this);
		this.exportAction.setText(Messages.DownloadView_ActionExport);
		this.exportAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(PluginId, "/icons/export.png")); //$NON-NLS-1$

		{
			CopyInfoTableAction copyTitleAction = new CopyInfoTableAction(this, "title"); //$NON-NLS-1$
			copyTitleAction.setText(Messages.DownloadView_ActionCopyTitle);
			this.copyActionList.add(copyTitleAction);

			CopyInfoTableAction copyLocationAction = new CopyInfoTableAction(this, "file"); //$NON-NLS-1$
			copyLocationAction.setText(Messages.DownloadView_ActionCopyLocation);
			this.copyActionList.add(copyLocationAction);

			Action copyUrlAction = new CopyInfoTableAction(this, "downloadUrl"); //$NON-NLS-1$
			copyUrlAction.setText(Messages.DownloadView_ActionCopyUrl);
			this.copyActionList.add(copyUrlAction);

			this.copyAllAction = new CopyInfoTableAction(this, null);
			this.copyAllAction.setText(Messages.DownloadView_ActionCopyAll);
			this.copyActionList.add(this.copyAllAction);
		}

	}

	/**
	 * 
	 */
	private void hookDoubleClickAction() {
		this.taskTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = DownloadView.this.taskTableViewer.getStructuredSelection();
				DownloadTask downloadTask = (DownloadTask) selection.getFirstElement();

				switch (DownloadManager.getInstance().getConfiguration().getDoubleClickAction()) {

				case DownloadConfiguration.DoubleClickAction_StartOrPause:
					startOrPause(downloadTask);
					break;

				case DownloadConfiguration.DoubleClickAction_OpenUrl:
					DesktopUtil.openWebpages(downloadTask.getPageUrl());
					break;

				case DownloadConfiguration.DoubleClickAction_OpenFile:
					DesktopUtil.openFile(downloadTask.getFile());
					break;

				default:
					DesktopUtil.openFile(downloadTask.getFolder());
					break;
				}

			}
		});
	}

	/**
	 * @param downloadTask
	 */
	protected void startOrPause(DownloadTask downloadTask) {

		if (downloadTask.getState() == DownloadTask.StateFinish) {
			DesktopUtil.openFile(downloadTask.getFolder());
		}

		if (downloadTask.isAllowDownload()) {
			DownloadManager.getInstance().pauseDownloadTask(downloadTask);
		} else {
			DownloadManager.getInstance().resumeDownloadTask(downloadTask);
		}

		refreshTasks();
	}

	@Override
	public void setFocus() {
		this.taskTableViewer.getControl().setFocus();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<DownloadTask> getSelectedTasks() {
		return this.taskTableViewer.getStructuredSelection().toList();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<DownloadTask> getAllTasks() {
		return DownloadManager.getInstance().getAllTasks();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void refreshTasks() {
		this.taskTableViewer.refresh();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void updateTasks() {

		Table table = this.taskTableViewer.getTable();
		for (int i = 0; i < table.getItemCount(); i++) {
			Object element = this.taskTableViewer.getElementAt(i);
			this.taskTableViewer.update(element, null);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void resumeTasks(List<DownloadTask> downloadTaskList) {
		DownloadTask[] tasks = downloadTaskList.toArray(new DownloadTask[downloadTaskList.size()]);
		DownloadManager.getInstance().resumeDownloadTask(tasks);
		refreshTasks();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void pauseTasks(List<DownloadTask> downloadTaskList) {
		DownloadTask[] tasks = downloadTaskList.toArray(new DownloadTask[downloadTaskList.size()]);
		DownloadManager.getInstance().pauseDownloadTask(tasks);
		refreshTasks();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deleteTasks(List<DownloadTask> downloadTaskList, boolean alsoDeleteFiles) {
		DownloadTask[] tasks = downloadTaskList.toArray(new DownloadTask[downloadTaskList.size()]);
		DownloadManager.getInstance().deleteDownloadTask(alsoDeleteFiles, tasks);
		refreshTasks();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void dispose() {

		SwtUtil.disposeResourceQuietly(this.imageMapping.values());

		super.dispose();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void moveUp() {

		List<DownloadTask> takskList = this.taskTableViewer.getStructuredSelection().toList();

		DownloadManager.getInstance().moveUp(takskList);

		this.refreshTasks();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void moveDown() {
		List<DownloadTask> takskList = this.taskTableViewer.getStructuredSelection().toList();

		DownloadManager.getInstance().moveDown(takskList);

		this.refreshTasks();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void moveTop() {
		List<DownloadTask> takskList = this.taskTableViewer.getStructuredSelection().toList();

		DownloadManager.getInstance().moveTop(takskList);

		this.refreshTasks();

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void moveBottom() {
		List<DownloadTask> takskList = this.taskTableViewer.getStructuredSelection().toList();

		DownloadManager.getInstance().moveBottom(takskList);

		this.refreshTasks();

	}

	private void restoreUiData() {
		IPreferenceStore store = DownloadActivator.getDefault().getPreferenceStore();

		PersistenceUtil.restoreUiData(store, TaskTableColumnWidthKey, this.taskTableViewer.getTable());

		PersistenceUtil.restoreUiData(store, SplitRatioKey, this.sashForm);

	}

}
