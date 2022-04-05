package org.aquarius.cicada.workbench.extension.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.nls.MovieNlsMessageConstant;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.control.IMovieListRefreshable;
import org.aquarius.cicada.workbench.editor.SiteEditorInput;
import org.aquarius.cicada.workbench.editor.action.DeleteAction;
import org.aquarius.cicada.workbench.editor.action.LockAction;
import org.aquarius.cicada.workbench.extension.FilterByActorAction;
import org.aquarius.cicada.workbench.extension.Messages;
import org.aquarius.cicada.workbench.extension.WorkbenchExtensionActivator;
import org.aquarius.cicada.workbench.extension.editor.internal.AnotherComboBoxCellEditor;
import org.aquarius.cicada.workbench.extension.editor.internal.AnotherFilterRowComboBoxCellEditor;
import org.aquarius.cicada.workbench.extension.editor.internal.AnotherTextCellEditor;
import org.aquarius.cicada.workbench.extension.editor.internal.BooleanDisplayConverter;
import org.aquarius.cicada.workbench.extension.editor.internal.DurationComparator;
import org.aquarius.cicada.workbench.extension.editor.internal.DurationDisplayConverter;
import org.aquarius.cicada.workbench.extension.editor.internal.HashCodeRowIdAccessor;
import org.aquarius.cicada.workbench.extension.editor.internal.ListComboBoxDataProvider;
import org.aquarius.cicada.workbench.extension.editor.internal.ListContentProposalProvider;
import org.aquarius.cicada.workbench.extension.editor.internal.MapDisplayConverter;
import org.aquarius.cicada.workbench.extension.editor.internal.NatTableConstant;
import org.aquarius.cicada.workbench.extension.editor.internal.Range;
import org.aquarius.cicada.workbench.extension.editor.internal.ScoreDisplayConverter;
import org.aquarius.cicada.workbench.extension.validator.NotBlankDataValidator;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.ui.action.IActionBarContributor;
import org.aquarius.ui.editor.BaseTableEditorPart;
import org.aquarius.ui.key.KeyBinder;
import org.aquarius.ui.key.KeyBinderManager;
import org.aquarius.util.DesktopUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.collection.ListElementNavigator;
import org.aquarius.util.nls.NlsResource;
import org.aquarius.util.spi.IElementNavigator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.DefaultGlazedListsFilterStrategy;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupReorderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.ISelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

/**
 * Provide a natTable to manage movies.<BR>
 * It use NatTable to show movie list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteNatTableEditor extends BaseTableEditorPart implements IMovieListRefreshable {

	private static int MaxVisibleItem = 12;

	public static int COLUMN_POSITION_TITLE = 0;

	public static int COLUMN_POSITION_SITE = 1;

	public static int COLUMN_POSITION_CHANNEL = 2;

	public static int COLUMN_POSITION_ACTOR = 3;

	public static int COLUMN_POSITION_TAG = 4;

	public static int COLUMN_POSITION_CATEGORY = 5;

	public static int COLUMN_POSITION_STATE = 6;

	public static int COLUMN_POSITION_PUBLISH_DATE = 7;

	public static int COLUMN_POSITION_UNIID = 8;

	public static int COLUMN_POSITION_DURATION = 9;

	public static int COLUMN_POSITION_DETAILED = 10;

	public static int COLUMN_POSITION_SCORE = 11;

	public static int COLUMN_POSITION_PAGEURL = 12;

	public static int COLUMN_POSITION_MEMO = 13;

	private static final String Property_Prefix = "aquarius_";

	public final static TreeMap<Integer, String> propertyMapping = new TreeMap<>();

	// property names of the movie class
	final static String[] propertyNames = { "title", "site", "channel", "actor", "tag", "category", "state", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			"publishDate", "uniId", "duration", //$NON-NLS-3$
			"detailed", "score", "pageUrl", "memo" }; //$NON-NLS-1$

	static {
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			propertyMapping.put(i, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + propertyName);
		}
	}

	private IElementNavigator<Movie> elementNavigator;

	// mapping from property to label, needed for column header labels
	private Map<String, String> propertyToLabelMap = new HashMap<>();

	private BodyLayerStack<Movie> bodyLayer;

	private FilterRowHeaderComposite<Movie> filterRowHeaderLayer;

	private NatTable natTable;

	private RowSelectionProvider<Movie> rowSelectionProvider;

	private FilterByActorAction filterByActorAction;

	private LockAction lockAction;

	public final IEditableRule DYNAMIC_EDITABLE = new IEditableRule() {

		@Override
		public boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry) {

			if (isEditing()) {
				return true;
			} else {
				return cell.getRowPosition() < 2;
			}

		}

		@Override
		public boolean isEditable(int columnIndex, int rowIndex) {
			return isEditing();
		}
	};

	/**
	 *
	 */
	public SiteNatTableEditor() {
		super();
	}

	/**
	 * 
	 */
	private void loadNlsResources() {
		NlsResource nlsResource = RuntimeManager.getInstance().getNlsResource();

		{
			this.propertyToLabelMap.put("title", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyTitle)); //$NON-NLS-1$
			this.propertyToLabelMap.put("site", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertySite)); //$NON-NLS-1$
			this.propertyToLabelMap.put("channel", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyChannel)); //$NON-NLS-1$
			this.propertyToLabelMap.put("actor", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyActor)); //$NON-NLS-1$

			this.propertyToLabelMap.put("tag", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyTag)); //$NON-NLS-1$
			this.propertyToLabelMap.put("category", //$NON-NLS-1$
					nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyCategory));

			this.propertyToLabelMap.put("state", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyState)); //$NON-NLS-1$
			this.propertyToLabelMap.put("publishDate", //$NON-NLS-1$
					nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyPublishDate));
			this.propertyToLabelMap.put("uniId", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyUniId)); //$NON-NLS-1$
			this.propertyToLabelMap.put("duration", //$NON-NLS-1$
					nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyDuration));

			this.propertyToLabelMap.put("detailed", //$NON-NLS-1$
					nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyDetailed));
			this.propertyToLabelMap.put("score", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyScore));
			this.propertyToLabelMap.put("memo", nlsResource.getValue(MovieNlsMessageConstant.Movie_PropertyMemo));

			this.propertyToLabelMap.put("pageUrl", "URL");
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void createPartControl(Composite parent) {

		loadNlsResources();

		this.natTable = this.createNatTable(parent);

		this.loadState();

		this.natTable.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent event) {

				if (WorkbenchActivator.getDefault().getConfiguration().isDoubleClickToOpenUrl()) {

					int oldRow = SiteNatTableEditor.this.natTable.getRowPositionByY(event.y);
					int newRow = LayerUtil.convertRowPosition(SiteNatTableEditor.this.natTable, oldRow, SiteNatTableEditor.this.bodyLayer.bodyDataLayer);

					if (oldRow == newRow) {
						return;
					}

					int rowCount = SiteNatTableEditor.this.bodyLayer.bodyDataLayer.getRowCount();
					if (newRow < rowCount && newRow >= 0) {
						Movie movie = SiteNatTableEditor.this.bodyLayer.bodyDataProvider.getRowObject(newRow);
						DesktopUtil.openWebpages(movie.getPageUrl());
					}
				}

			}
		});

		this.rowSelectionProvider = new RowSelectionProvider<Movie>(this.bodyLayer.selectionLayer, this.bodyLayer.bodyDataProvider, false);
		new NatTableMovieToolTip(this.bodyLayer.bodyDataLayer, this.natTable, GridRegion.BODY);

		this.initKeyListeners();

		this.elementNavigator = new ListElementNavigator<>(this.bodyLayer.filterList);

		this.getEditorSite().setSelectionProvider(this.rowSelectionProvider);
	}

	/**
	 * bind key listeners.
	 */
	private void initKeyListeners() {

		Map<KeyBinder, IAction> mapping = new HashMap<>();
		mapping.put(KeyBinder.DeleteKeyBinder, new DeleteAction("")); //$NON-NLS-1$
		// mapping.put(KeyBinder.EnterKeyBinder, new OpenMovieUrlAction(""));
		// //$NON-NLS-1$

		KeyBinderManager.bind(this.natTable, mapping);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void refreshContent() {
		Site site = this.getEditorInput().getAdapter(Site.class);

		this.bodyLayer.sortedList.getReadWriteLock().writeLock().lock();
		try {
			// deactivate
			this.bodyLayer.glazedListsEventLayer.deactivate();
			// clear
			this.bodyLayer.sortedList.clear();
			// addall
			this.bodyLayer.sortedList.addAll(site.getMovieList());
		} finally {
			this.bodyLayer.sortedList.getReadWriteLock().writeLock().unlock();
			// activate
			this.bodyLayer.glazedListsEventLayer.activate();
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> getSelectedMovieList() {

		// NatTable RowSelectionModel use hash map to store selected movies.
		// It does not use tree ,may because the data element may not be comparable
		// So get the selected rows ,then resort rows.

		ISelectionModel rowSelectionModel = this.bodyLayer.selectionLayer.getSelectionModel();

		try {
			List<Movie> movieList = new ArrayList<>();

			Set<org.eclipse.nebula.widgets.nattable.coordinate.Range> rangeSet = rowSelectionModel.getSelectedRowPositions();

			if (CollectionUtils.isEmpty(rangeSet)) {
				return movieList;
			}

			IRowDataProvider<Movie> rowDataProvider = this.bodyLayer.bodyDataProvider;

			Set<Integer> rowIndexSet = new TreeSet<>();

			// For row selection model
//			for (org.eclipse.nebula.widgets.nattable.coordinate.Range range : rangeSet) {
//				int rowIndex = this.bodyLayer.selectionLayer.getRowIndexByPosition(range.start);
//				rowIndexSet.add(rowIndex);
//			}

			for (org.eclipse.nebula.widgets.nattable.coordinate.Range range : rangeSet) {
				for (int index = range.start; index < range.end; index++) {
					int rowIndex = this.bodyLayer.selectionLayer.getRowIndexByPosition(index);
					rowIndexSet.add(rowIndex);
				}
			}

			for (Integer rowIndex : rowIndexSet) {
				Movie movie = rowDataProvider.getRowObject(rowIndex);
				movieList.add(movie);
			}

			return movieList;
		} catch (Exception e) {

			// return rowSelectionModel.getSelectedRowObjects();
			return null;
		}

	}

	/**
	 * Create a nat natTable to show movies.
	 *
	 * @param parent
	 * @return
	 */
	public NatTable createNatTable(Composite parent) {

		// create a new ConfigRegistry which will be needed for GlazedLists handling
		ConfigRegistry configRegistry = new ConfigRegistry();
		GridLayer gridLayer = createGridLayer(configRegistry);

		NatTable natTable = new NatTable(parent, gridLayer, false);

		natTable.setConfigRegistry(configRegistry);

		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());

		natTable.addConfiguration(new SingleClickSortConfiguration());
		// add filter row configuration
		natTable.addConfiguration(new RowConfiguration());

		createMenuConfiguration(natTable);

		// Setup selection styling
		createSelectionConfiguration(natTable);

		natTable.configure();

		this.createContextMenu(natTable);
		return natTable;
	}

	/**
	 * Create the menu.
	 *
	 * @param natTable
	 */
	private void createMenuConfiguration(NatTable natTable) {
		natTable.addConfiguration(new HeaderMenuConfiguration(natTable) {
			@Override
			protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {
				return super.createColumnHeaderMenu(natTable).withColumnChooserMenuItem();
			}
		});

		// Column group header menu
		final Menu columnGroupHeaderMenu = new PopupMenuBuilder(natTable).withRenameColumnGroupMenuItem().withRemoveColumnGroupMenuItem().build();

		natTable.addConfiguration(new AbstractUiBindingConfiguration() {
			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerFirstMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_GROUP_HEADER, MouseEventMatcher.RIGHT_BUTTON),
						new PopupMenuAction(columnGroupHeaderMenu));
			}
		});
	}

	/**
	 * Set the style for selection cell.
	 *
	 * @param natTable
	 */
	private void createSelectionConfiguration(NatTable natTable) {
		DefaultSelectionStyleConfiguration selectionStyle = new DefaultSelectionStyleConfiguration();
		selectionStyle.selectionFont = GUIHelper.getFont(new FontData("Arial", 9, SWT.BOLD)); //$NON-NLS-1$
		selectionStyle.selectionFgColor = GUIHelper.COLOR_BLACK;
		selectionStyle.anchorBgColor = GUIHelper.COLOR_DARK_GRAY;
		selectionStyle.anchorBorderStyle = new BorderStyle(1, GUIHelper.COLOR_DARK_GRAY, LineStyleEnum.SOLID);

		// Add all style configurations to NatTable
		natTable.addConfiguration(selectionStyle);
	}

	/**
	 * Create content menu for nat natTable.
	 */
	private void createContextMenu(Control control) {

		this.filterByActorAction = new FilterByActorAction(Messages.SiteNatTableEditor_FilterBySelectedActor, this);
		this.filterByActorAction.setImageDescriptor(WorkbenchExtensionActivator.getImageDescriptor("/icons/filterByActor.png")); //$NON-NLS-1$

		this.lockAction = new LockAction();

		IEditorActionBarContributor editorActionBarContributor = this.getEditorSite().getActionBarContributor();

		if (editorActionBarContributor instanceof IActionBarContributor) {

			IActionBarContributor actionBarContributor = (IActionBarContributor) editorActionBarContributor;
			MenuManager menuManager = new MenuManager();
			actionBarContributor.contribute(menuManager);

			menuManager.add(new Separator());
			menuManager.add(this.filterByActorAction);
			menuManager.add(this.lockAction);

			control.setMenu(menuManager.createContextMenu(control));
		}
	}

	/**
	 * Create grid layer
	 *
	 * @param configRegistry
	 * @return
	 */
	private GridLayer createGridLayer(ConfigRegistry configRegistry) {
		Site site = this.getEditorInput().getAdapter(Site.class);

		IColumnPropertyAccessor<Movie> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<>(propertyNames);

		this.bodyLayer = createBodyLayer(site, columnPropertyAccessor, configRegistry);

		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, this.propertyToLabelMap);
		DataLayer columnHeaderDataLayer = new DataLayer(columnHeaderDataProvider);

		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, this.bodyLayer, this.bodyLayer.selectionLayer);

		SortHeaderLayer<Movie> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(this.bodyLayer.sortedList, columnPropertyAccessor, configRegistry, columnHeaderDataLayer));

		// Note: The column header layer is wrapped in a filter row composite.
		// This plugs in the filter row functionality

		DefaultGlazedListsFilterStrategy<Movie> defaultGlazedListsFilterStrategy = new DefaultGlazedListsFilterStrategy<Movie>(this.bodyLayer.filterList,
				columnPropertyAccessor, configRegistry);

		this.filterRowHeaderLayer = new FilterRowHeaderComposite<>(defaultGlazedListsFilterStrategy, sortHeaderLayer, columnHeaderDataLayer.getDataProvider(),
				configRegistry);

		DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(this.bodyLayer.bodyDataProvider);
		RowHeaderLayer rowHeaderLayer = createRowLayout(rowHeaderDataProvider);

		ILayer cornerLayer = new CornerLayer(new DataLayer(new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider)), rowHeaderLayer,
				this.filterRowHeaderLayer);

		GridLayer gridLayer = new GridLayer(this.bodyLayer, this.filterRowHeaderLayer, rowHeaderLayer, cornerLayer);

//		CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
//		final GroupByHeaderLayer groupByHeaderLayer = new GroupByHeaderLayer(this.bodyLayer.groupByModel,
//				gridLayer, columnHeaderDataProvider);
//		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
//		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		return gridLayer;
	}

	/**
	 * @param site
	 * @param columnPropertyAccessor
	 */
	private BodyLayerStack<Movie> createBodyLayer(Site site, IColumnPropertyAccessor<Movie> columnPropertyAccessor, ConfigRegistry configRegistry) {

		BodyLayerStack<Movie> bodyLayer = new BodyLayerStack<>(site.getMovieList(), columnPropertyAccessor, configRegistry);

		registerColumnLabels(bodyLayer);

		return bodyLayer;
	}

	/**
	 *
	 * @param rowHeaderDataProvider
	 * @return
	 */
	private RowHeaderLayer createRowLayout(DefaultRowHeaderDataProvider rowHeaderDataProvider) {
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
		rowHeaderDataLayer.setDefaultColumnWidth(60);
		rowHeaderDataLayer.setColumnsResizableByDefault(true);

		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, this.bodyLayer, this.bodyLayer.selectionLayer);

		return rowHeaderLayer;
	}

	/**
	 * The layer stack
	 *
	 * @author aquarius.github@gmail.com
	 *
	 * @param <T>
	 */
	class BodyLayerStack<T> extends AbstractLayerTransform {

		private final SortedList<T> sortedList;
		private final FilterList<T> filterList;

		private final IRowDataProvider<T> bodyDataProvider;

		private final DataLayer bodyDataLayer;

		private GlazedListsEventLayer<T> glazedListsEventLayer;

		private ColumnReorderLayer columnReorderLayer;
		private ColumnGroupReorderLayer columnGroupReorderLayer;
		private ColumnHideShowLayer columnHideShowLayer;
		private ColumnGroupExpandCollapseLayer columnGroupExpandCollapseLayer;
		private SelectionLayer selectionLayer;
		private ViewportLayer viewportLayer;

		// private final GroupByModel groupByModel = new GroupByModel();

		private InternalUpdateDataCommandHandler updateDataCommandHandler;

		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor, ConfigRegistry configRegistry) {
			// wrapping of the list to show into GlazedLists
			// see http://publicobject.com/glazedlists/ for further information
			EventList<T> eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator
			// because the Comparator will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);
			// wrap the SortedList with the FilterList
			this.filterList = new FilterList<>(this.sortedList);

			this.bodyDataProvider = new ListDataProvider<>(this.filterList, columnPropertyAccessor);
			this.bodyDataLayer = new DataLayer(this.bodyDataProvider) {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void configure(IConfigRegistry configRegistry, UiBindingRegistry uiBindingRegistry) {
					ICellEditor stateCellEditor = new AnotherComboBoxCellEditor(MovieHelper.getMovieStateList());

					configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, stateCellEditor, DisplayMode.NORMAL,
							getColumnConfigLabel(COLUMN_POSITION_STATE));
					configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
							getColumnConfigLabel(COLUMN_POSITION_STATE));

					super.configure(configRegistry, uiBindingRegistry);
				}

			};
//
//			this.bodyDataLayer = new GroupByDataLayer<>(
//					this.groupByModel,
//					this.filterList,
//					columnPropertyAccessor,
//					configRegistry);

			this.updateDataCommandHandler = new InternalUpdateDataCommandHandler(this.bodyDataLayer);
			this.bodyDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
			this.bodyDataLayer.registerCommandHandler(this.updateDataCommandHandler);

			this.bodyDataLayer.setDefaultColumnWidth(64);
			this.bodyDataLayer.setDefaultColumnWidthByPosition(COLUMN_POSITION_TITLE, 400);
			this.bodyDataLayer.setDefaultColumnWidthByPosition(COLUMN_POSITION_TAG, 128);
			this.bodyDataLayer.setColumnsResizableByDefault(true);

			// layer for event handling of GlazedLists and PropertyChanges
			this.glazedListsEventLayer = new GlazedListsEventLayer<>(this.bodyDataLayer, this.filterList);

			updateColumns();

			this.selectionLayer = createSelectionLayer(this.bodyDataProvider, this.columnGroupExpandCollapseLayer);

			this.viewportLayer = new ViewportLayer(this.selectionLayer);

			final FreezeLayer freezeLayer = new FreezeLayer(this.selectionLayer);
			final CompositeFreezeLayer compositeFreezeLayer = new CompositeFreezeLayer(freezeLayer, this.viewportLayer, this.selectionLayer);

			setUnderlyingLayer(compositeFreezeLayer);
		}

		/**
		 * @param columnGroupModel
		 */
		private void updateColumns() {

			ColumnGroupModel[] columnGroupModel = new ColumnGroupModel[] { new ColumnGroupModel(), new ColumnGroupModel() };

			this.columnReorderLayer = new ColumnReorderLayer(this.glazedListsEventLayer);
			this.columnGroupReorderLayer = new ColumnGroupReorderLayer(this.columnReorderLayer, columnGroupModel[columnGroupModel.length - 1]);
			this.columnHideShowLayer = new ColumnHideShowLayer(this.columnGroupReorderLayer);

			this.columnHideShowLayer.hideColumnIndexes(COLUMN_POSITION_PAGEURL);
			this.columnHideShowLayer.hideColumnIndexes(COLUMN_POSITION_MEMO);

			IEditorInput editorInput = getEditorInput();
			if (editorInput instanceof SiteEditorInput) {
				SiteEditorInput siteEditorInput = (SiteEditorInput) editorInput;

				if (!siteEditorInput.isVirtual()) {
					this.columnHideShowLayer.hideColumnIndexes(COLUMN_POSITION_SITE);
				}
			}

			this.columnGroupExpandCollapseLayer = new ColumnGroupExpandCollapseLayer(this.columnHideShowLayer, columnGroupModel);
		}

		/**
		 * Create the selection layer
		 *
		 * @param columnGroupExpandCollapseLayer
		 * @param bodyDataProvider
		 *
		 */
		private SelectionLayer createSelectionLayer(IDataProvider bodyDataProvider, ColumnGroupExpandCollapseLayer columnGroupExpandCollapseLayer) {
			SelectionLayer selectionLayer = new SelectionLayer(columnGroupExpandCollapseLayer);

			if (WorkbenchActivator.getDefault().getConfiguration().isTableFullSelection()) {
				selectionLayer.setSelectionModel(
						new RowSelectionModel<Object>(selectionLayer, (IRowDataProvider<Object>) bodyDataProvider, new HashCodeRowIdAccessor<Object>()));
			}

			return selectionLayer;
		}

	}

	/**
	 * 
	 * @author aquarius.github@hotmail.com
	 *
	 */
	class RowConfiguration extends AbstractRegistryConfiguration {

		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {

			configStyle(configRegistry);

			configComparator(configRegistry);

			configDisplayConvertor(configRegistry);

			configRowHeaderEditors(configRegistry);

		}

		/**
		 * Set the horizontal alignment of most cells to left.<BR>
		 *
		 * @param configRegistry
		 */
		private void configStyle(IConfigRegistry configRegistry) {
			Style style = new Style();
			style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);

			for (int i = COLUMN_POSITION_TITLE; i <= COLUMN_POSITION_UNIID; i++) {
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, getColumnConfigLabel(i));
			}

			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, getColumnConfigLabel(COLUMN_POSITION_PAGEURL));
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, getColumnConfigLabel(COLUMN_POSITION_MEMO));

		}

		/**
		 * Duration is special,it is "HH:mm:ss".<BR>
		 * So a special comparator is needed.
		 *
		 * @param configRegistry
		 */
		private void configComparator(IConfigRegistry configRegistry) {
			// Configure custom comparator on the rating column

			Comparator<?> comparator = new DurationComparator();
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, comparator, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DURATION));

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE, TextMatchingMode.REGULAR_EXPRESSION, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DURATION));

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE, TextMatchingMode.EXACT, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_STATE));

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE, TextMatchingMode.EXACT, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_SCORE));

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE, TextMatchingMode.EXACT, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_CHANNEL));

		}

		/**
		 * @param configRegistry
		 */
		private void configDisplayConvertor(IConfigRegistry configRegistry) {
			IDisplayConverter displayConverter = createStateDisplayCovertor();

			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_STATE));
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_STATE));

			displayConverter = new DefaultDateDisplayConverter("yyyy-MM-dd") {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public Object displayToCanonicalValue(Object displayValue) {
					try {
						return super.displayToCanonicalValue(displayValue);
					} catch (Exception e) {
						return displayValue;
					}
				}
			}; // $NON-NLS-1$

			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_PUBLISH_DATE));
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_PUBLISH_DATE));

			displayConverter = new DurationDisplayConverter();
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DURATION));

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DURATION));

			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new BooleanDisplayConverter(), DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DETAILED));

			displayConverter = new ScoreDisplayConverter();
			// displayConverter = new DefaultDisplayConverter();
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_SCORE));

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_SCORE));
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_CONTENT_DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_SCORE));

			displayConverter = createChannelDisplayCovertor();

			if (null != displayConverter) {
				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
						getColumnConfigLabel(COLUMN_POSITION_CHANNEL));
				configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER, displayConverter, DisplayMode.NORMAL,
						getColumnConfigLabel(COLUMN_POSITION_CHANNEL));
			}

		}

		/**
		 * @param configRegistry
		 */
		private void configRowHeaderEditors(IConfigRegistry configRegistry) {

			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_DELIMITER, NatTableConstant.TextDelimiter); // $NON-NLS-1$

			Site site = getEditorInput().getAdapter(Site.class);

			ICellEditor titleCellEditor = createTextCellEditor();

			// For title
			IDataValidator titleDataValidator = new NotBlankDataValidator();
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, titleCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_TITLE));
			configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, titleDataValidator, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_TITLE));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_TITLE));

			// For actor
			// ICellEditor actorCellEditor = createComboCellEditor(site.getActors());
			ICellEditor actorCellEditor = createTextCellEditor(site.getActors());
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, actorCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_ACTOR));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_ACTOR));

			// For channel
			List<String> channelNameList = site.getChannelNames();
			ICellEditor channelCellEditor = createComboCellEditor(channelNameList);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, channelCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_CHANNEL));

			// For uniId
			ICellEditor uniIdCellEditor = createTextCellEditor();
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, uniIdCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_UNIID));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_UNIID));

			// For tag
			ICellEditor tagCellEditor;
			List<String> tags = site.getTags();
			if (isUseTextForNatTableRowFilter(tags)) {
				tagCellEditor = createTextCellEditor(tags);
			} else {
				tagCellEditor = createComboCellEditor(tags);
			}

			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, tagCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_TAG));

			// For category
			ICellEditor categoryCellEditor;
			List<String> categories = site.getCategories();
			if (isUseTextForNatTableRowFilter(categories)) {
				tagCellEditor = createTextCellEditor(categories);
			} else {
				tagCellEditor = createComboCellEditor(categories);
			}
			categoryCellEditor = createComboCellEditor(site.getCategories());
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, categoryCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_CATEGORY));

			// For duration
			ICellEditor durationCellEditor = createDurationComboCellEditor();
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, durationCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DURATION));

			// For publish date
			ICellEditor dateCellEditor = createTextCellEditor();

			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, dateCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_PUBLISH_DATE));
//			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
//					SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
//					getColumnConfigLabel(COLUMN_POSITION_PUBLISH_DATE));

			// For state
			// ICellEditor stateCellEditor =
			// createComboCellEditor(MovieHelper.getMovieStateList());
			ICellEditor stateCellEditor = new AnotherComboBoxCellEditor(MovieHelper.getMovieStateList());

			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, stateCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_STATE));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_STATE));

			// For detailed
			List<Boolean> symbolList = new ArrayList<>();
			symbolList.add(Boolean.TRUE);
			symbolList.add(Boolean.FALSE);

			ICellEditor detailedCellEditor = new AnotherComboBoxCellEditor(symbolList, MaxVisibleItem);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, detailedCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_DETAILED));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_DETAILED));

			// For score
			List<Integer> scoreList = new ArrayList<>();
			for (int i = 0; i <= 5; i++) {
				scoreList.add(i);
			}

			// ICellEditor scoreCellEditor = new AnotherComboBoxCellEditor(scoreList,
			// MaxVisibleItem);
			ICellEditor scoreCellEditor = createComboCellEditor(scoreList);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, scoreCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_SCORE));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_SCORE));

			// For memo
			ICellEditor memoCellEditor = createTextCellEditor();
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, memoCellEditor, DisplayMode.NORMAL,
					getColumnConfigLabel(COLUMN_POSITION_MEMO));
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, SiteNatTableEditor.this.DYNAMIC_EDITABLE, DisplayMode.EDIT,
					getColumnConfigLabel(COLUMN_POSITION_MEMO));

		}

	}

	/**
	 * Register the alias name for index.
	 *
	 * @param bodyLayer
	 */
	private void registerColumnLabels(AbstractLayer bodyLayer) {

		final ColumnOverrideLabelAccumulator columnLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyLayer);
		bodyLayer.setConfigLabelAccumulator(columnLabelAccumulator);

		for (int i = COLUMN_POSITION_TITLE; i <= COLUMN_POSITION_MEMO; i++) {
			columnLabelAccumulator.registerColumnOverrides(i, getColumnConfigLabel(i));
		}

	}

	/**
	 * Duration is special.<BR>
	 * Use a combo cell to filter.<BR>
	 *
	 * @return
	 */
	private ICellEditor createDurationComboCellEditor() {

		List<Range> durationRangeList = new ArrayList<>();
		durationRangeList.add(new Range(0, 20));
		durationRangeList.add(new Range(20, 60));
		durationRangeList.add(new Range(60, 180));
		durationRangeList.add(new Range(180, Integer.MAX_VALUE));

		IComboBoxDataProvider dataProvider = new ListComboBoxDataProvider(durationRangeList);

		ComboBoxCellEditor comboBoxCellEditor = new AnotherComboBoxCellEditor(dataProvider);

		return comboBoxCellEditor;
	}

	private TextCellEditor createTextCellEditor() {
		TextCellEditor textCellEditor = new AnotherTextCellEditor();
		textCellEditor.setErrorDecorationEnabled(true);
		textCellEditor.setDecorationPositionOverride(SWT.LEFT | SWT.TOP);

		return textCellEditor;
	}

	/**
	 * Create a special text cell editor to filter movies.<BR>
	 *
	 * @param dataList
	 * @return
	 */
	private TextCellEditor createTextCellEditor(List<String> dataList) {

		ListContentProposalProvider contentProposalProvider = new ListContentProposalProvider(dataList);
		contentProposalProvider.setFiltering(true);
		KeyStroke keystroke = null;
		try {
			keystroke = KeyStroke.getInstance(WorkbenchActivator.getDefault().getConfiguration().getAutoActivationKey()); // $NON-NLS-1$
		} catch (Exception e) {
			try {
				keystroke = KeyStroke.getInstance("Alt+/");
			} catch (Exception e1) {
				// Nothing to do
			}
		}
		String backspace = "\b"; //$NON-NLS-1$
		String delete = "\u007F"; //$NON-NLS-1$

		char[] autoActivationChars = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" + backspace //$NON-NLS-1$
				+ delete).toCharArray();

		TextCellEditor textCellEditor = new AnotherTextCellEditor();
		textCellEditor.enableContentProposal(new TextContentAdapter(), contentProposalProvider, keystroke, autoActivationChars);

		return textCellEditor;
	}

	/**
	 * Create a combo cell editor with specified data.<BR>
	 *
	 * @param dataList
	 * @return
	 */
	private ComboBoxCellEditor createComboCellEditor(List<?> dataList) {

		IComboBoxDataProvider dataProvider = new ListComboBoxDataProvider(dataList);

		ComboBoxCellEditor comboBoxCellEditor = new AnotherFilterRowComboBoxCellEditor(dataProvider, MaxVisibleItem);

		return comboBoxCellEditor;
	}

	/**
	 * A simple mapping display convertor with map.<BR>
	 *
	 * @return
	 */
	private IDisplayConverter createStateDisplayCovertor() {
		final Map<Integer, String> statesMap = MovieHelper.getMovieStateMap();
		return new MapDisplayConverter(statesMap);
	}

	/**
	 * A simple mapping display convertor with map.<BR>
	 *
	 * @return
	 */
	private IDisplayConverter createChannelDisplayCovertor() {

		Site site = this.getEditorInput().getAdapter(Site.class);
		final Map<String, String> nameMapping = MovieHelper.buildChannelNameMapping(site, true);
		return new MapDisplayConverter(nameMapping);

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void update(List<Movie> movieList) {
		this.natTable.update();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if (adapter == IElementNavigator.class) {
			return (T) this.elementNavigator;
		}

		return super.getAdapter(adapter);
	}

	/**
	 * Filter the nat table by a actor
	 *
	 * @param actor
	 */
	public void filterByActor(String actor) {
		actor = StringUtils.replace(actor, ",", "|");
		this.filterRowHeaderLayer.getFilterRowDataLayer().getFilterRowDataProvider().setDataValue(COLUMN_POSITION_ACTOR, 0, actor);
	}

	/**
	 * Return the column config label
	 *
	 * @param position
	 * @return
	 */
	private static String getColumnConfigLabel(int position) {
		return FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + position;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {

//		Collection<Movie> movieList = this.bodyLayer.updateDataCommandHandler.getModifiedMovieList();
//
//		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(movieList);
//
//		this.bodyLayer.updateDataCommandHandler.clearData();

		// FIXME
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDirty() {

		if (!this.isEditing()) {
			return false;
		}

		if (!this.natTable.commitAndCloseActiveCellEditor()) {
			if (this.natTable.getActiveCellEditor() != null) {
				this.natTable.getActiveCellEditor().commit(MoveDirectionEnum.NONE, true, true);
			}
		}

		boolean dirty = this.bodyLayer.updateDataCommandHandler.isDirty();

		return dirty;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isEditable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {

		this.saveState();
		super.dispose();
	}

	/**
	 * 
	 * 
	 */
	private void saveState() {

		if (!checkPersist()) {
			return;
		}

		Properties properties = new Properties();
		this.natTable.saveState(Property_Prefix, properties);
		String fileName = getPersistFileName();

		try (OutputStream outputStream = new FileOutputStream(fileName)) {

			properties.store(outputStream, null);

		} catch (IOException e) {
			// Nothing to do
		}

	}

	/**
	 * 
	 */
	private boolean checkPersist() {
		if (!WorkbenchActivator.getDefault().getConfiguration().isPersistNatTableConfig()) {
			return false;
		}

		Site site = this.getEditorInput().getAdapter(Site.class);

		return !MovieHelper.isVirtualSite(site);
	}

	/**
	 * @return
	 */
	private String getPersistFileName() {
		String workingFolder = Starter.getInstance().getWorkingFolder();
		workingFolder = workingFolder + File.separator + "persist";

		Site site = this.getEditorInput().getAdapter(Site.class);
		return workingFolder + File.separator + site.getSiteName() + ".properties";
	}

	/**
	 * 
	 *
	 */
	private void loadState() {
		if (!checkPersist()) {
			return;
		}

		String fileName = getPersistFileName();
		Properties properties = new Properties();

		try (InputStream inputStream = new FileInputStream(fileName)) {
			properties.load(inputStream);

		} catch (IOException e) {
			// Nothing to do
		}

		this.natTable.loadState(Property_Prefix, properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateContent() {
		this.natTable.redraw();
	}

	/**
	 * 
	 * @param elements
	 * @return
	 */
	private static boolean isUseTextForNatTableRowFilter(Collection<?> elements) {
		return WorkbenchActivator.getDefault().getConfiguration().isUseTextForNatTableRowFilter() && elements.size() > 60;
	}

}
