/**
 *
 */
package org.aquarius.cicada.workbench.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.service.Filter;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.RuntimeConstant;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.control.IMovieListRefreshable;
import org.aquarius.cicada.workbench.control.MovieListBrowserComposite;
import org.aquarius.cicada.workbench.editor.action.ReloadBrowserAction;
import org.aquarius.cicada.workbench.editor.internal.FilterContentComposite;
import org.aquarius.cicada.workbench.editor.internal.FilterContentOutlinePage;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.nebula.ChoiceChangeListener;
import org.aquarius.cicada.workbench.nebula.MultiChoiceWithFixedWidth;
import org.aquarius.cicada.workbench.nebula.TextAssistWithFixedWidth;
import org.aquarius.ui.action.IActionBarContributor;
import org.aquarius.ui.editor.BaseTableEditorPart;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.collection.ListElementNavigator;
import org.aquarius.util.spi.IElementNavigator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.alibaba.fastjson.JSON;

/**
 * Use browser to show movie list.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteBrowserEditor extends BaseTableEditorPart implements IMovieListRefreshable {

	private MovieListBrowserComposite movieListBrowserComposite = null;

	private FilterContentOutlinePage contentOutlinePage;

	private int filterType = RuntimeConstant.FilterTypeNone;

	private SashForm sashForm;

	private FilterSite filterSite;

	private ReloadBrowserAction reloadBrowserAction;

	private Action showFilterOutlineAction;

	private IElementNavigator<Movie> elementNavigator;

	private boolean activePage = false;

	/**
	 * @param movieSite
	 */
	public SiteBrowserEditor(Site movieSite) {
		super();
		this.filterSite = new FilterSite(movieSite);

		String filterTypeString = WorkbenchActivator.getDefault().getConfiguration().getFilterType();
		this.filterType = NumberUtils.toInt(filterTypeString, RuntimeConstant.FilterTypeInner);

		this.elementNavigator = new ListElementNavigator<>(this.filterSite.getFilterMovieList());

		makeActions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IEditorSite editorSite, IEditorInput editorInput) throws PartInitException {
		super.init(editorSite, editorInput);

		if (editorInput instanceof SiteEditorInput) {
			SiteEditorInput siteEditorInput = (SiteEditorInput) editorInput;

			if (!siteEditorInput.isSupportFilter()) {
				this.filterType = RuntimeConstant.FilterTypeNone;
			}
		}

		this.loadState();

	}

	/**
	 * 
	 */
	private void makeActions() {
		this.reloadBrowserAction = new ReloadBrowserAction(this, Messages.ReloadBrowserAction_ReloadBrowser);

		this.showFilterOutlineAction = new Action(Messages.SiteBrowserEditor_HideUseFilterOutline) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				if (SiteBrowserEditor.this.sashForm.getMaximizedControl() == null) {
					SiteBrowserEditor.this.sashForm.setMaximizedControl(SiteBrowserEditor.this.movieListBrowserComposite);
					SiteBrowserEditor.this.showFilterOutlineAction.setText(Messages.SiteBrowserEditor_ShowUseFilterOutline);

				} else {
					SiteBrowserEditor.this.sashForm.setMaximizedControl(null);
					SiteBrowserEditor.this.showFilterOutlineAction.setText(Messages.SiteBrowserEditor_HideUseFilterOutline);
				}
			}
		};

	}

	/**
	 * 
	 */
	public void reload() {
		this.movieListBrowserComposite.refreshContent();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void createPartControl(Composite parent) {

		Composite rootPane = new Composite(parent, SWT.None);
		rootPane.setLayout(new GridLayout());

		switch (this.filterType) {
		case RuntimeConstant.FilterTypeSimple:

			buildCoolBar(rootPane);

			this.movieListBrowserComposite = new MovieListBrowserComposite(rootPane, SWT.NONE);
			this.movieListBrowserComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			break;
		case RuntimeConstant.FilterTypeOutline:

			this.movieListBrowserComposite = new MovieListBrowserComposite(rootPane, SWT.NONE);
			this.movieListBrowserComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			this.contentOutlinePage = new FilterContentOutlinePage(this, this.filterSite);
			break;
		case RuntimeConstant.FilterTypeInner:

			this.sashForm = new SashForm(rootPane, SWT.FLAT);

			this.movieListBrowserComposite = new MovieListBrowserComposite(this.sashForm, SWT.NONE);
			new FilterContentComposite(this, this.filterSite, this.sashForm, SWT.NONE);

			this.sashForm.setWeights(new int[] { 80, 20 });

			this.sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			break;

		default:
			this.movieListBrowserComposite = new MovieListBrowserComposite(rootPane, SWT.NONE);
			this.movieListBrowserComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		this.movieListBrowserComposite.setService(this.filterSite);

		if (!WorkbenchActivator.getDefault().getConfiguration().isUseBrowserMenu()) {
			Browser browser = this.movieListBrowserComposite.getBrowser();
			updateContextMenu(browser);
		}
	}

	/**
	 * Use self-defined menu.<BR>
	 *
	 * @param control
	 */
	private void updateContextMenu(Control control) {
		IEditorActionBarContributor editorActionBarContributor = this.getEditorSite().getActionBarContributor();

		if (editorActionBarContributor instanceof IActionBarContributor) {

			IActionBarContributor actionBarContributor = (IActionBarContributor) editorActionBarContributor;

			MenuManager menuManager = new MenuManager();
			actionBarContributor.contribute(menuManager);

			menuManager.add(new Separator());
			menuManager.add(this.reloadBrowserAction);

			if (this.filterType == RuntimeConstant.FilterTypeInner) {
				menuManager.add(this.showFilterOutlineAction);
			}

			Menu menu = menuManager.createContextMenu(control);
			control.setMenu(menu);
		}
	}

	/**
	 * Create a cool bar to support filter function.<BR>
	 *
	 * @param rootPane
	 */
	private void buildCoolBar(Composite rootPane) {

		Composite coolBar = new Composite(rootPane, SWT.FLAT);
		coolBar.setLayout(new RowLayout(SWT.HORIZONTAL));
		coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createMultiChoiceForChannel(coolBar);
		createMultiChoiceForState(coolBar);
		createMultiChoiceForActor(coolBar);
		createMultiChoiceForTag(coolBar);
		createMultiChoiceForCategory(coolBar);

		createSearchText(coolBar);
	}

	/**
	 * Create the state choice for search.<BR>
	 *
	 * @param parent
	 * @return
	 */
	private MultiChoiceWithFixedWidth<Integer> createMultiChoiceForState(Composite parent) {

		final Map<Integer, String> statesMap = MovieHelper.getMovieStateMap();

		MultiChoiceWithFixedWidth<Integer> multiChoice = createMultiChoice(parent, statesMap.keySet());

		multiChoice.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return statesMap.get(element);
			}
		});

		multiChoice.setChoiceChangeListener(new ChoiceChangeListener() {

			@Override
			public void changed() {
				List<Integer> states = multiChoice.getSelection();
				Filter filter = SiteBrowserEditor.this.filterSite.getFilter();

				if (!CollectionUtils.isEqualCollection(states, filter.getStateSet())) {
					filter.setStateSet(new TreeSet<Integer>(states));
					refreshContent();
				}
			}
		});

		PromptSupport.setPrompt(Messages.SiteBrowserEditor_SearchPromptForState, multiChoice.getTextControl());

		Filter filter = this.filterSite.getFilter();
		updateSelection(multiChoice, filter.getStateSet());

		return multiChoice;

	}

	/**
	 * Create the channel choice for search.<BR>
	 *
	 * @param parent
	 * @return
	 */
	private MultiChoiceWithFixedWidth<String> createMultiChoiceForChannel(Composite parent) {

		Site site = getEditorInput().getAdapter(Site.class);
		if (MovieHelper.isVirtualSite(site)) {
			return null;
		}

		final List<String> channelNameList = site.getChannelNames();

		if (channelNameList.size() < 2) {
			return null;
		}

		Map<String, String> nameMapping = MovieHelper.buildChannelNameMapping(site, true);
		List<String> channelDisplayNameList = new ArrayList<>(nameMapping.values());

		MultiChoiceWithFixedWidth<String> multiChoice = createMultiChoice(parent, channelDisplayNameList);

		multiChoice.setChoiceChangeListener(new ChoiceChangeListener() {

			@Override
			public void changed() {

				List<String> selectedChannelNameList = new ArrayList<>();

				int[] selectionIndex = multiChoice.getSelectedIndex();
				if (ArrayUtils.isNotEmpty(selectionIndex)) {
					for (int index : selectionIndex) {

						String channelName = channelNameList.get(index);
						selectedChannelNameList.add(channelName);
					}
				}

				Filter filter = SiteBrowserEditor.this.filterSite.getFilter();

				if (!CollectionUtils.isEqualCollection(selectedChannelNameList, filter.getChannelNameList())) {
					filter.setChannelNameList(selectedChannelNameList);
					refreshContent();
				}
			}
		});

		PromptSupport.setPrompt(Messages.SiteBrowserEditor_SearchPromptForChannel, multiChoice.getTextControl());

		Filter filter = this.filterSite.getFilter();
		updateSelection(multiChoice, filter.getCategoryList());

		return multiChoice;

	}

	/**
	 * 
	 * @param multiChoice
	 * @param selectionList
	 */
	private <T> void updateSelection(MultiChoiceWithFixedWidth<T> multiChoice, Collection<T> selectionList) {
		if (CollectionUtils.isNotEmpty(selectionList)) {
			multiChoice.setSelection(new HashSet<T>(selectionList));
		}
	}

	/**
	 * Create the actor choice for search.<BR>
	 *
	 * @param parent
	 * @return
	 */
	private MultiChoiceWithFixedWidth<String> createMultiChoiceForActor(Composite parent) {

		Site site = getEditorInput().getAdapter(Site.class);
		MultiChoiceWithFixedWidth<String> multiChoice = createMultiChoice(parent, site.getActors());

		multiChoice.setChoiceChangeListener(new ChoiceChangeListener() {

			@Override
			public void changed() {
				List<String> actorList = multiChoice.getSelection();
				Filter filter = SiteBrowserEditor.this.filterSite.getFilter();

				if (!CollectionUtils.isEqualCollection(actorList, filter.getActorList())) {
					filter.setActorList(actorList);
					refreshContent();
				}
			}
		});

		PromptSupport.setPrompt(Messages.SiteBrowserEditor_SearchPromptForActor, multiChoice.getTextControl());

		Filter filter = this.filterSite.getFilter();
		updateSelection(multiChoice, filter.getActorList());

		return multiChoice;

	}

	/**
	 * create multi choice control.<BR>
	 *
	 * @param parent
	 * @param site
	 * @return
	 */
	private <T> MultiChoiceWithFixedWidth<T> createMultiChoice(Composite parent, Collection<T> contentList) {

		List<T> stringList = new ArrayList<>();
		stringList.addAll(contentList);

		MultiChoiceWithFixedWidth<T> multiChoice = new MultiChoiceWithFixedWidth<>(parent, SWT.FLAT, stringList);
		multiChoice.setFixedWidth(200);
		multiChoice.setEditable(false);

		return multiChoice;
	}

	/**
	 * Create the tag choice for search.<BR>
	 *
	 * @param parent
	 * @return
	 */
	private MultiChoiceWithFixedWidth<String> createMultiChoiceForTag(Composite parent) {

		Site site = getEditorInput().getAdapter(Site.class);
		MultiChoiceWithFixedWidth<String> multiChoice = createMultiChoice(parent, site.getTags());

		multiChoice.setChoiceChangeListener(new ChoiceChangeListener() {

			@Override
			public void changed() {
				List<String> tagList = multiChoice.getSelection();
				Filter filter = SiteBrowserEditor.this.filterSite.getFilter();

				if (!CollectionUtils.isEqualCollection(tagList, filter.getTagList())) {
					filter.setTagList(tagList);
					refreshContent();
				}
			}
		});

		PromptSupport.setPrompt(Messages.SiteBrowserEditor_SearchPromptForTag, multiChoice.getTextControl());

		Filter filter = this.filterSite.getFilter();
		updateSelection(multiChoice, filter.getTagList());

		return multiChoice;

	}

	/**
	 * Create the category choice for search.<BR>
	 *
	 * @param parent
	 * @return
	 */
	private MultiChoiceWithFixedWidth<String> createMultiChoiceForCategory(Composite parent) {

		Site site = getEditorInput().getAdapter(Site.class);
		MultiChoiceWithFixedWidth<String> multiChoice = createMultiChoice(parent, site.getCategories());

		multiChoice.setChoiceChangeListener(new ChoiceChangeListener() {

			@Override
			public void changed() {
				List<String> categoryList = multiChoice.getSelection();
				Filter filter = SiteBrowserEditor.this.filterSite.getFilter();

				if (!CollectionUtils.isEqualCollection(categoryList, filter.getCategoryList())) {
					filter.setCategoryList(categoryList);
					refreshContent();
				}
			}
		});

		PromptSupport.setPrompt(Messages.SiteBrowserEditor_SearchPromptForCategory, multiChoice.getTextControl());

		Filter filter = this.filterSite.getFilter();
		updateSelection(multiChoice, filter.getCategoryList());

		return multiChoice;

	}

	/**
	 * Create the text for search.<BR>
	 *
	 * @param parent
	 * @return
	 */
	private TextAssist createSearchText(Composite parent) {

		TextAssistContentProvider textAssistContentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				Site site = getEditorInput().getAdapter(Site.class);

				return MovieHelper.findKeywords(site, entry, this.getMaxNumberOfLines());
			}
		};
		TextAssistWithFixedWidth textAssist = new TextAssistWithFixedWidth(parent, SWT.BORDER, textAssistContentProvider);
		textAssist.setNumberOfLines(20);
		textAssist.setFixedWidth(320);

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
				Filter filter = SiteBrowserEditor.this.filterSite.getFilter();
				String oldKeyword = filter.getKeyword();

				if (!StringUtils.equalsIgnoreCase(oldKeyword, textAssist.getText())) {
					filter.setKeyword(textAssist.getText());
					refreshContent();
				}
			}
		});

		return textAssist;

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void refreshContent() {
		this.filterSite.doFilter();
		this.movieListBrowserComposite.refreshContent();

	}

	/**
	 * @return
	 * @see org.aquarius.cicada.workbench.control.MovieListBrowserComposite#getSelectedMovieList()
	 */
	@Override
	public List<Movie> getSelectedMovieList() {
		return this.movieListBrowserComposite.getSelectedMovieList();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if (adapter == ISelectionProvider.class) {
			return (T) this.movieListBrowserComposite.getSelectionProvider();
		}

		if (adapter == IContentOutlinePage.class) {
			if (this.filterType == RuntimeConstant.FilterTypeOutline) {
				return (T) this.contentOutlinePage;
			}
		}

		if (adapter == IElementNavigator.class) {
			return (T) this.elementNavigator;
		}

		if (adapter == Browser.class) {
			if (SwtUtil.isValid(this.movieListBrowserComposite)) {
				return (T) this.movieListBrowserComposite.getBrowser();
			}
		}

		return super.getAdapter(adapter);
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
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedMovieList(List<Movie> movieList) {
		this.movieListBrowserComposite.setSelectedMovieList(movieList);
	}

	/**
	 * @return
	 */
	private String getPersistFileName() {
		String persistFolder = Starter.getInstance().getPersistFolder();
		persistFolder = persistFolder + File.separator + "nattable";

		Site site = this.filterSite.getSite();
		return persistFolder + File.separator + site.getSiteName() + ".json";
	}

	/**
	 * 
	 */
	private void loadState() {
		if (!checkPersist()) {
			return;
		}

		String fileName = this.getPersistFileName();

		try {
			String json = FileUtils.readFileToString(new File(fileName), StringUtil.CODEING_UTF8);
			Filter filter = JSON.parseObject(json, Filter.class);
			this.filterSite.setFilter(filter);

		} catch (IOException e) {
			// Nothing to do
		}
	}

	private void saveState() {
		if (!checkPersist()) {
			return;
		}

		String json = JSON.toJSONString(this.filterSite.getFilter(), true);
		String fileName = this.getPersistFileName();

		try {
			FileUtils.write(new File(fileName), json, "UTF-8");
		} catch (IOException e) {
			// Nothing to do
		}
	}

	/**
	 * 
	 */
	private boolean checkPersist() {
		if (!WorkbenchActivator.getDefault().getConfiguration().isPersistBrowserConfig()) {
			return false;
		}

		Site site = this.getEditorInput().getAdapter(Site.class);

		return !MovieHelper.isVirtualSite(site);
	}

	/**
	 * 
	 */
	public void updateOutline(boolean activePage) {
		if (this.filterType == RuntimeConstant.FilterTypeOutline) {
			this.contentOutlinePage.setVisible(activePage);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateContent() {
		// Nothing to do
	}
}
