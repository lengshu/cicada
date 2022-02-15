/**
 *
 */
package org.aquarius.cicada.workbench.editor.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.service.Filter;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.FilterSite;
import org.aquarius.cicada.workbench.editor.SiteBrowserEditor;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.manager.HistoryManager;
import org.aquarius.cicada.workbench.nebula.TextAssistWithFixedWidth;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.NumberUtil;
import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.nebula.widgets.chips.CloseEvent;
import org.eclipse.nebula.widgets.chips.CloseListener;
import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * The composite to filter movies.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FilterContentComposite extends Composite {

	private static final String Key = "_Key_"; //$NON-NLS-1$

	private static final String Type = "type"; //$NON-NLS-1$

	private static final int TypeAll = 0;

	private static final int TypeChannel = 1;

	private static final int TypeActor = 2;

	private static final int TypeTag = 4;

	private static final int TypeCategory = 8;

	private ScrolledComposite rootPane;

	private List<String> keywordList;

	private Composite keywordChipsPane;

	private boolean pauseEvent = false;

	private TitledSeparator keywordTitledSeparator;

	private FilterSite filterSite;

	private SiteBrowserEditor siteEditor;

	private CloseListener mainCloseListener;

	private SelectionAdapter mainSelectionListener;

	private Composite contentPane;

	private TextAssistWithFixedWidth textAssist;

	private Map<String, String> nameMapping;

	/**
	 * @param parent
	 * @param style
	 */
	public FilterContentComposite(SiteBrowserEditor siteEditor, FilterSite filterSite, Composite parent, int style) {
		super(parent, style);

		this.siteEditor = siteEditor;
		this.filterSite = filterSite;

		this.nameMapping = MovieHelper.buildChannelNameMapping(this.filterSite.getSite(), true);

		this.mainCloseListener = new CloseListener() {

			@Override
			public void onClose(CloseEvent event) {

				if (FilterContentComposite.this.pauseEvent) {
					return;
				}

				Chips chips = (Chips) event.widget;
				FilterContentComposite.this.keywordList.remove(chips.getText());

				if (StringUtils.equals(chips.getText(), FilterContentComposite.this.textAssist.getText())) {
					FilterContentComposite.this.textAssist.setText(""); //$NON-NLS-1$
					inputKeyword(""); //$NON-NLS-1$
				}

				if (StringUtils.equals(chips.getText(), filterSite.getFilter().getKeyword())) {
					FilterContentComposite.this.textAssist.setText(""); //$NON-NLS-1$
					inputKeyword(""); //$NON-NLS-1$
				}

				Composite parent = chips.getParent();
				chips.dispose();
				parent.layout();
			}
		};

		this.mainSelectionListener = new SelectionAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {

				if (FilterContentComposite.this.pauseEvent) {
					return;
				}

				Chips chips = (Chips) event.widget;
				Integer value = (Integer) chips.getData(Type);
				int type = NumberUtil.getIntValue(value.intValue());

				String text = null;
				try {
					text = (String) chips.getData(Key);
				} catch (Exception e) {
					// Nothing to do
				}

				if (StringUtils.isEmpty(text)) {
					text = chips.getText();
				}

				List<String> filterContentList = null;

				switch (type) {
				case TypeAll:

					if (chips.getSelection()) {
						FilterContentComposite.this.textAssist.setText(text);
						inputKeyword(text);
					} else {
						inputKeyword(""); //$NON-NLS-1$
						FilterContentComposite.this.textAssist.setText(""); //$NON-NLS-1$
					}

					return;
				case TypeChannel:
					filterContentList = filterSite.getFilter().getChannelNameList();
					break;
				case TypeActor:
					filterContentList = filterSite.getFilter().getActorList();
					break;
				case TypeTag:
					filterContentList = filterSite.getFilter().getTagList();
					break;
				case TypeCategory:
					filterContentList = filterSite.getFilter().getCategoryList();
					break;
				}

				boolean changed = false;
				if (chips.getSelection()) {
					if (!filterContentList.contains(text)) {
						filterContentList.add(text);
						changed = true;
					}
				} else {
					if (filterContentList.contains(text)) {
						filterContentList.remove(text);
						changed = true;
					}
				}

				if (changed) {
					refresh();
				}

			}
		};

		this.setLayout(new FillLayout());

		this.createControl();
	}

	/**
	 * Create the internal controls.<BR>
	 */
	private void createControl() {

		this.rootPane = new ScrolledComposite(this, SWT.FLAT | SWT.V_SCROLL);
		this.rootPane.setLayout(new FillLayout());

		this.contentPane = new Composite(this.rootPane, SWT.BORDER);
		this.contentPane.setLayout(new GridLayout(1, false));

		this.textAssist = this.createAssistText(this.contentPane);

		this.keywordList = HistoryManager.getInstance().getTitleHistoryList();

		Filter filter = this.filterSite.getFilter();

		this.keywordTitledSeparator = this.createTitledSeparator(this.contentPane, Messages.FilterContentComposite_Keyword);
		List<String> selectedKeywordList = new ArrayList<>();
		if (StringUtils.isNotBlank(filter.getKeyword())) {
			selectedKeywordList.add(filter.getKeyword());
		}
		this.keywordChipsPane = this.createChips(this.contentPane, this.keywordList, selectedKeywordList, TypeAll, this.mainCloseListener,
				this.mainSelectionListener);

		List<String> channelNameList = this.filterSite.getSite().getChannelNames();
		if (null != channelNameList && channelNameList.size() > 1) {
			this.createChips(this.contentPane, Messages.FilterContentComposite_Channel, channelNameList, filter.getChannelNameList(), TypeChannel, null,
					this.mainSelectionListener);
		}

		this.createStateChips(this.contentPane);

		this.createChips(this.contentPane, Messages.FilterContentComposite_Actor, this.filterSite.getSite().getActors(), filter.getActorList(), TypeActor, null,
				this.mainSelectionListener);
		this.createChips(this.contentPane, Messages.FilterContentComposite_Tag, this.filterSite.getSite().getTags(), filter.getTagList(), TypeTag, null,
				this.mainSelectionListener);

		this.createChips(this.contentPane, Messages.FilterContentComposite_Category, this.filterSite.getSite().getCategories(), filter.getCategoryList(),
				TypeCategory, null, this.mainSelectionListener);

		this.rootPane.setContent(this.contentPane);
		this.rootPane.setExpandVertical(true);
		this.rootPane.setExpandHorizontal(true);
		this.rootPane.setAlwaysShowScrollBars(true);
		this.rootPane.addControlListener(ControlListener.controlResizedAdapter(e -> {
			Rectangle r = this.rootPane.getClientArea();
			this.rootPane.setMinSize(this.contentPane.computeSize(r.width, SWT.DEFAULT));
		}));

	}

	/**
	 * @param pane
	 */
	private void createStateChips(Composite pane) {
		TitledSeparator titledSeparator = new TitledSeparator(pane, SWT.LEFT);
		titledSeparator.setText(Messages.FilterContentComposite_State);
		titledSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite chipsPane = new Composite(pane, SWT.BORDER);
		chipsPane.setLayout(createRowLayout());

		SelectionListener stateListener = new SelectionAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {

				if (FilterContentComposite.this.pauseEvent) {
					return;
				}

				Chips chips = (Chips) event.widget;
				Integer state = (Integer) chips.getData();

				if (chips.getSelection()) {
					FilterContentComposite.this.filterSite.getFilter().getStateSet().add(state);
				} else {
					FilterContentComposite.this.filterSite.getFilter().getStateSet().remove(state);
				}

				refresh();
			}
		};

		Map<Integer, String> stateMap = MovieHelper.getMovieStateMap();

		for (Entry<Integer, String> entry : stateMap.entrySet()) {
			Chips chips = new Chips(chipsPane, SWT.CHECK);

			chips.setData(entry.getKey());
			chips.setText(entry.getValue());

			chips.addSelectionListener(stateListener);
		}

		chipsPane.setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	/**
	 *
	 * @param pane
	 */
	private TextAssistWithFixedWidth createAssistText(Composite pane) {

		Composite parent = new Composite(pane, SWT.None);
		parent.setLayout(new GridLayout(2, false));

		TextAssistContentProvider textAssistContentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				return MovieHelper.findKeywords(FilterContentComposite.this.filterSite.getSite(), entry, this.getMaxNumberOfLines());
			}
		};
		TextAssistWithFixedWidth textAssist = new TextAssistWithFixedWidth(parent, SWT.BORDER, textAssistContentProvider);
		textAssist.setNumberOfLines(20);

		Text text = textAssist.getTextControl();
		if (SwtUtil.isValid(text)) {
			PromptSupport.setPrompt(Messages.SiteBrowserEditor_EnterKeywordToSearchInAll, text);
			PromptSupport.setFontStyle(SWT.ITALIC, text);
		}

		Filter filter = this.filterSite.getFilter();

		if (StringUtils.isNotBlank(filter.getKeyword())) {
			textAssist.setText(filter.getKeyword());
		}

		textAssist.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				if (FilterContentComposite.this.pauseEvent) {
					return;
				}

				String keyword = FilterContentComposite.this.textAssist.getText();
				inputKeyword(keyword);
			}

		});

		Button resetButton = new Button(parent, SWT.PUSH);
		resetButton.setText(Messages.FilterContentComposite_ResetLabel);

		resetButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetFilter();
			}

		});

		textAssist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		resetButton.setLayoutData(new GridData());

		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return textAssist;
	}

	/**
	 * Reset the filter.<BR>
	 */
	protected void resetFilter() {
		this.pauseEvent = true;

		try {
			this.filterSite.getFilter().reset();

			this.textAssist.setText(""); //$NON-NLS-1$

			List<Chips> chipsList = SwtUtil.findChildren(this.contentPane, true, Chips.class);

			if (null != chipsList) {
				for (Chips chips : chipsList) {
					if (chips.getSelection()) {
						chips.setSelection(false);
					}
				}
			}

		} finally {
			this.pauseEvent = false;
		}

		this.refresh();
	}

	/**
	 * @param textAssist
	 */
	protected void inputKeyword(String keyword) {

		if (StringUtils.isEmpty(keyword)) {
			keyword = ""; //$NON-NLS-1$
		} else {
			if (!this.keywordList.contains(keyword)) {

				if (this.keywordList.size() >= WorkbenchActivator.getDefault().getConfiguration().getHistoryKeywordCount()) {
					Control[] chipsArray = this.keywordChipsPane.getChildren();
					if (ArrayUtils.isNotEmpty(chipsArray)) {
						chipsArray[0].dispose();
					}
				}

				this.createSingleChips(TypeAll, this.mainCloseListener, this.mainSelectionListener, this.keywordChipsPane, keyword);

				HistoryManager.getInstance().addTitleHistory(keyword);
			}
		}

		Filter filter = this.filterSite.getFilter();
		String oldKeyword = filter.getKeyword();

		if (!StringUtils.equalsIgnoreCase(oldKeyword, keyword)) {
			filter.setKeyword(keyword);
			refresh();
		}

		Control[] chipsArray = this.keywordChipsPane.getChildren();
		if (ArrayUtils.isNotEmpty(chipsArray)) {
			for (Control control : chipsArray) {

				Chips chips = (Chips) control;
				chips.setSelection(keyword.equals(chips.getText()));
			}
		}

		this.keywordChipsPane.layout(true, true);
		this.contentPane.layout(new Control[] { this.keywordChipsPane });
	}

	/**
	 * Filter the movies ,then refresh to show the filtered result.<BR>
	 */
	protected void refresh() {
		this.filterSite.doFilter();
		this.siteEditor.refreshContent();

	}

	/**
	 * 
	 * @param pane
	 * @param title
	 * @param nameList
	 * @param selectedNameList
	 * @param type
	 * @param closeListener
	 * @param selectionListener
	 * @return
	 */
	private Composite createChips(Composite pane, String title, List<String> nameList, List<String> selectedNameList, int type, CloseListener closeListener,
			SelectionListener selectionListener) {

		if (CollectionUtils.isNotEmpty(nameList)) {
			createTitledSeparator(pane, title);
			return createChips(pane, nameList, selectedNameList, type, closeListener, selectionListener);
		}

		return null;

	}

	/**
	 * @param pane
	 * @param title
	 * @param style
	 */
	private TitledSeparator createTitledSeparator(Composite pane, String title) {
		TitledSeparator titledSeparator = new TitledSeparator(pane, SWT.LEFT);
		titledSeparator.setText(title);
		titledSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return titledSeparator;
	}

	/**
	 * @param pane
	 * @param nameList
	 */
	private Composite createChips(Composite pane, List<String> nameList, List<String> selectedNameList, int type, CloseListener closeListener,
			SelectionListener selectionListener) {

		if (null == selectedNameList) {
			selectedNameList = new ArrayList<>();
		}

		Composite chipsPane = new Composite(pane, SWT.BORDER);
		chipsPane.setLayout(createRowLayout());

		for (int i = 0; i < nameList.size(); i++) {
			String name = nameList.get(i);

			Chips chips = createSingleChips(type, closeListener, selectionListener, chipsPane, name);

			if (selectedNameList.contains(name)) {
				chips.setSelection(true);
			}

			if (i > 100) {
				break;
			}
		}

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 26;
		gridData.grabExcessVerticalSpace = true;
		chipsPane.setLayoutData(gridData);

		return chipsPane;
	}

	/**
	 * @param type
	 * @param mainCloseListener
	 * @param mainSelectionListener
	 * @param chipPane
	 * @param name
	 */
	private Chips createSingleChips(int type, CloseListener closeListener, SelectionListener selectionListener, Composite chipPane, String name) {
		Chips chips = null;
		if (type == TypeAll) {
			chips = new Chips(chipPane, SWT.CLOSE | SWT.CHECK);
		} else {
			chips = new Chips(chipPane, SWT.CHECK);
		}

		chips.setData(Type, type);
		chips.setData(Key, name);

		if (type == TypeChannel) {
			String newName = this.nameMapping.get(name);
			if (StringUtils.isEmpty(newName)) {
				newName = name;
			}

			chips.setText(newName);
		} else {
			chips.setText(name);
		}

		if (null != closeListener) {
			chips.addCloseListener(closeListener);
		}

		if (null != selectionListener) {
			chips.addSelectionListener(selectionListener);
		}

		return chips;
	}

	/**
	 * @return
	 */
	private RowLayout createRowLayout() {
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);

		rowLayout.wrap = true;
		rowLayout.pack = true;
		rowLayout.marginLeft = 2;
		rowLayout.marginTop = 2;
		rowLayout.marginRight = 2;
		rowLayout.marginBottom = 2;
		rowLayout.spacing = 5;

		return rowLayout;
	}

}
