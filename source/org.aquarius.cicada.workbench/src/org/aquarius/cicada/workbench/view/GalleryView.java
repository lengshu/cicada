/**
 * 
 */
package org.aquarius.cicada.workbench.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.CopyInfoDropDownAction;
import org.aquarius.cicada.workbench.editor.action.DownloadAction;
import org.aquarius.cicada.workbench.editor.action.GenerateDownloadUrlsDropDownAction;
import org.aquarius.cicada.workbench.editor.action.OpenMovieUrlAction;
import org.aquarius.cicada.workbench.editor.action.PlayMovieAction;
import org.aquarius.cicada.workbench.editor.action.RefreshMovieAction;
import org.aquarius.cicada.workbench.editor.action.SaveImageAction;
import org.aquarius.cicada.workbench.editor.action.UpdateScoreAction;
import org.aquarius.cicada.workbench.editor.action.UpdateScoreDropDownAction;
import org.aquarius.cicada.workbench.editor.action.UpdateStateDropDownAction;
import org.aquarius.cicada.workbench.editor.action.factory.EditorActionFactory;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.service.IHttpCacheService.LoadFinishListener;
import org.aquarius.ui.base.SelectionProviderAdapter;
import org.aquarius.ui.control.ImageCanvas;
import org.aquarius.ui.key.KeyBinder;
import org.aquarius.ui.key.KeyBinderManager;
import org.aquarius.ui.util.AdapterUtil;
import org.aquarius.ui.util.ImageUtil;
import org.aquarius.ui.util.PersistenceUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.ObjectHolder;
import org.aquarius.util.StringUtil;
import org.aquarius.util.net.HttpUtil;
import org.aquarius.util.spi.IElementNavigator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class GalleryView extends ViewPart implements ISelectionChangedListener, IPartListener, LoadFinishListener<Serializable> {

	public static String ViewId = GalleryView.class.getName();

	private static String SashFormWeightsKey = GalleryView.class.getName() + ".SashFormWeightsKey"; //$NON-NLS-1$

	private static String LayoutTypeKey = GalleryView.class.getName() + ".LayoutType"; //$NON-NLS-1$

	private static String UseKeyboardKey = GalleryView.class.getName() + ".UseKeyboardKey"; //$NON-NLS-1$

	private static final String SecondaryId = "detach"; //$NON-NLS-1$

	private SashForm rootPane;

	private ImageCanvas imageCanvas;

	private Text infoLabel;

	private Movie currentMovie;

	private MenuManager contextMenuManager;

	private List<IAction> actionList = new ArrayList<>();

	private IAction changeLayoutAction;

	private IAction showViewAction;

	private IAction pinViewAction;

	private IAction useKeyboardAction;

	private SaveImageAction saveImageAction;

	private IAction refreshMovieAction;
	private IAction openMovieUrlAction;
	private IAction downloadAction;
	private IAction playMovieAction;
	private IAction copyInfoDropDownAction;
	private IAction generateDownloadUrlsDropDownAction;

	private IAction updateScoreDropDownAction;

	private IAction updateStateDropDownAction;

	private IAction externalAnalyserDropdownAction;

	private int layoutType = SWT.HORIZONTAL;

	private IElementNavigator<Movie> elementNavigator;

	private boolean pinned = false;

	private String title;

	private ObjectHolder<Boolean> useKeyboard = new ObjectHolder<Boolean>(Boolean.TRUE);

	/**
	 * 
	 */
	public GalleryView() {
		IWorkbenchPage page = WorkbenchUtil.getActivePage();
		page.addPartListener(this);

		try {

			IEditorReference[] editorReferences = page.getEditorReferences();

			if (null != editorReferences) {
				for (IEditorReference editorReference : editorReferences) {
					editorReference.getPart(false).getSite().getSelectionProvider().addSelectionChangedListener(this);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		makeActions();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();

		IWorkbenchPage page = WorkbenchUtil.getActivePage();
		page.removePartListener(this);

		IPreferenceStore preferenceStore = WorkbenchActivator.getDefault().getPreferenceStore();
		preferenceStore.setValue(UseKeyboardKey, this.useKeyboard.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		IActionBars actionBars = site.getActionBars();
		this.makeContributions(actionBars.getMenuManager(), actionBars.getToolBarManager());

		site.setSelectionProvider(new SelectionProviderAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public ISelection getSelection() {

				if (null == GalleryView.this.currentMovie) {
					return super.getSelection();
				} else {
					return new StructuredSelection(GalleryView.this.currentMovie);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		IPreferenceStore preferenceStore = WorkbenchActivator.getDefault().getPreferenceStore();
		preferenceStore.setDefault(LayoutTypeKey, SWT.VERTICAL);
		preferenceStore.setDefault(UseKeyboardKey, true);

		this.layoutType = preferenceStore.getInt(LayoutTypeKey);

		this.rootPane = new SashForm(parent, SWT.NONE | this.layoutType);
		this.rootPane.setSashWidth(8);

		Composite imageParent = new Composite(this.rootPane, SWT.None);
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 4;
		fillLayout.marginWidth = 4;
		imageParent.setLayout(fillLayout);

		this.imageCanvas = new ImageCanvas(imageParent, SWT.NONE);
		this.infoLabel = new Text(this.rootPane, SWT.WRAP | SWT.BORDER);
		this.infoLabel.setEditable(false);

		{

			this.rootPane.setWeights(new int[] { 68, 32 });

			IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
			PersistenceUtil.restoreUiData(store, SashFormWeightsKey, this.rootPane);
		}

		this.contextMenuManager = new MenuManager();
		this.makeContributions(this.contextMenuManager, null);

		this.imageCanvas.setMenu(this.contextMenuManager.createContextMenu(this.imageCanvas));

		this.imageCanvas.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// GalleryView.this.showViewAction.run();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDown(MouseEvent e) {

				if (e.button == 1) {
					Rectangle rect = GalleryView.this.imageCanvas.getBounds();
					// Rectangle rect = GalleryView.this.imageCanvas.getPaintRect();

					if (e.x < (rect.width * 0.3)) {
						left();
					}

					if (e.x > (rect.width * 0.7)) {
						right();
					}
				}

			}

		});

		KeyBinderManager.bind(this.useKeyboard, this.imageCanvas, this, KeyBinder.LeftKeyBinder, KeyBinder.RightKeyBinder, KeyBinder.PageDownKeyBinder,
				KeyBinder.PageUpKeyBinder, KeyBinder.UpKeyBinder, KeyBinder.DownKeyBinder);

		Map<KeyBinder, IAction> mapping = new HashMap<>();
		mapping.put(KeyBinder.CtrlEnterKeyBinder, this.downloadAction);
		mapping.put(KeyBinder.EnterKeyBinder, this.openMovieUrlAction);
		mapping.put(KeyBinder.CopyKeyBinder, this.copyInfoDropDownAction);

		addScoreListener();

		KeyBinderManager.bind(this.useKeyboard, this.imageCanvas, mapping);

		PersistenceUtil.addSaveSupport(preferenceStore, SashFormWeightsKey, this.rootPane);
		setActionState(false);

		this.useKeyboard.setValue(preferenceStore.getBoolean(UseKeyboardKey));
		this.useKeyboardAction.setChecked(this.useKeyboard.getValue());
	}

	/**
	 * 
	 */
	private void addScoreListener() {

		Map<Character, IAction> actions = new HashMap<>();

		for (byte i = 0; i <= 5; i++) {
			String scoreLabel = StringUtil.repeatSymbol(StringUtil.Star, i);
			UpdateScoreAction scoreAction = new UpdateScoreAction(scoreLabel, i);

			actions.put((i + "").charAt(0), scoreAction);
		}

		this.imageCanvas.addKeyListener(new KeyAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void keyReleased(KeyEvent e) {

				IAction action = actions.get(e.character);
				if (null != action) {
					action.run();
				}
			}

		});
	}

	/**
	 *
	 */
	private void makeActions() {

		makeNewActions();

		this.showViewAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/showImage.png")); //$NON-NLS-1$

		this.saveImageAction = new SaveImageAction(Messages.MoviePropertySheetPage_SaveImageActionLabel);

		this.refreshMovieAction = new RefreshMovieAction(Messages.SiteEditorActionBarContributor_RefreshMovie);
		this.openMovieUrlAction = new OpenMovieUrlAction(Messages.SiteEditorActionBarContributor_OpenSelectedMovies);
		this.downloadAction = new DownloadAction(Messages.SiteEditorActionBarContributor_DownloadSelectedMovies, false);
		this.playMovieAction = new PlayMovieAction(Messages.SiteEditorActionBarContributor_PlayMovie);
		this.copyInfoDropDownAction = new CopyInfoDropDownAction(Messages.SiteEditorActionBarContributor_Copy);
		this.generateDownloadUrlsDropDownAction = new GenerateDownloadUrlsDropDownAction(Messages.SiteEditorActionBarContributor_GenerateDownloadUrls);

		this.updateStateDropDownAction = new UpdateStateDropDownAction(Messages.SiteEditorActionBarContributor_ChangeState);

		this.updateScoreDropDownAction = new UpdateScoreDropDownAction(Messages.SiteEditorActionBarContributor_ChangeScore);

		this.externalAnalyserDropdownAction = EditorActionFactory.getInstance()
				.createExternalAnalyserAction(Messages.SiteEditorActionBarContributor_ExternalAnalyser);

		this.actionList.add(this.externalAnalyserDropdownAction);
		this.actionList.add(this.updateScoreDropDownAction);

		this.actionList.add(this.saveImageAction);

		this.actionList.add(this.refreshMovieAction);
		this.actionList.add(this.openMovieUrlAction);
		this.actionList.add(this.downloadAction);
		this.actionList.add(this.playMovieAction);
		this.actionList.add(this.copyInfoDropDownAction);
		this.actionList.add(this.generateDownloadUrlsDropDownAction);
	}

	/**
	 * 
	 */
	private void makeNewActions() {
		this.pinViewAction = new Action(Messages.GalleryView_Pin, IAction.AS_CHECK_BOX) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				pin();
			}
		};

		this.pinViewAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/pin.png")); //$NON-NLS-1$

		this.useKeyboardAction = new Action(Messages.GalleryView_UseKeyboard, IAction.AS_CHECK_BOX) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {

				GalleryView.this.useKeyboard.setValue(this.isChecked());
			}
		};

		this.useKeyboardAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/keyboard.png"));
		this.useKeyboardAction.setToolTipText(Messages.GalleryView_UseKeyboardDescription);
		this.useKeyboardAction.setDescription(Messages.GalleryView_UseKeyboardDescription);

		this.changeLayoutAction = new Action(Messages.MoviePropertySheetPage_ChangeLayoutActionLabel, SWT.None) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				changeLayout();
			}
		};

		this.changeLayoutAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/changeLayout.png")); // //$NON-NLS-1$
		this.showViewAction = new Action(Messages.MoviePropertySheetPage_ShowImageActionLabel) {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				try {
					showNewView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};

		this.actionList.add(this.pinViewAction);
		this.actionList.add(this.showViewAction);
		this.actionList.add(this.useKeyboardAction);
		this.actionList.add(this.changeLayoutAction);
	}

	/**
	 * 
	 */
	protected void pin() {

		if (null == this.elementNavigator) {
			return;
		}

		this.pinned = !this.pinned;
		this.pinViewAction.setChecked(this.pinned);

		String partName = this.getPartName();
		partName = StringUtils.substringBefore(partName, "(");

		if (this.pinned) {
			this.setPartName(partName + "(" + this.title + ")");
		} else {
			this.setPartName(partName);
		}

	}

	/**
	 * @throws Exception
	 * 
	 */
	protected void showNewView() throws Exception {

		IWorkbenchPage workbenchPage = WorkbenchUtil.getActivePage();

//		boolean exist = false;
//
//		IViewReference viewReference = workbenchPage.findViewReference(GalleryView.ViewId, SecondaryId);
//		if (null != viewReference) {
//			IViewPart viewPart = viewReference.getView(false);
//
//			if (null != viewPart) {
//				exist = true;
//			}
//		}

		IViewPart viewPart = workbenchPage.showView(GalleryView.ViewId, SecondaryId, IWorkbenchPage.VIEW_ACTIVATE);

		IViewSite viewSite = viewPart.getViewSite();

		MPartSashContainerElement mpartService = viewSite.getService(MPart.class);
		mpartService.setOnTop(true);

		EModelService modelService = viewSite.getService(EModelService.class);
		Shell shell = viewSite.getShell();
		Rectangle rectangle = shell.getBounds();
		modelService.detach(mpartService, rectangle.x, rectangle.y, rectangle.width, rectangle.height);

		if (viewPart instanceof GalleryView) {
			GalleryView galleryView = (GalleryView) viewPart;

			galleryView.elementNavigator = this.elementNavigator;
			galleryView.title = this.title;

			galleryView.updateSelection(new StructuredSelection(this.currentMovie));

			galleryView.pinViewAction.run();

			galleryView.getViewSite().getShell().setActive();
		}
	}

	/**
	 *
	 */
	private void changeLayout() {

		if (this.layoutType == SWT.HORIZONTAL) {
			this.layoutType = SWT.VERTICAL;
		} else {
			this.layoutType = SWT.HORIZONTAL;
		}

		IPreferenceStore preferenceStore = WorkbenchActivator.getDefault().getPreferenceStore();
		preferenceStore.setValue(LayoutTypeKey, this.layoutType);

		this.rootPane.setOrientation(this.layoutType);

		this.updateLayout();
	}

	/**
	 * 
	 * @param menuManager
	 * @param toolBarManager
	 */
	private void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager) {

		addAction(menuManager, null, this.useKeyboardAction);

		addAction(menuManager, toolBarManager, this.pinViewAction);
		addAction(menuManager, toolBarManager, this.showViewAction);
		addAction(menuManager, toolBarManager, this.changeLayoutAction);
		addAction(menuManager, toolBarManager, this.saveImageAction);
		addAction(menuManager, toolBarManager, new Separator());

		addAction(menuManager, toolBarManager, this.refreshMovieAction);
		addAction(menuManager, toolBarManager, this.openMovieUrlAction);
		addAction(menuManager, toolBarManager, this.downloadAction);
		addAction(menuManager, toolBarManager, this.playMovieAction);
		addAction(menuManager, toolBarManager, new Separator());

		addAction(menuManager, toolBarManager, this.copyInfoDropDownAction);
		addAction(menuManager, toolBarManager, this.generateDownloadUrlsDropDownAction);
		addAction(menuManager, toolBarManager, this.externalAnalyserDropdownAction);
		addAction(menuManager, toolBarManager, new Separator());

		addAction(menuManager, toolBarManager, this.updateStateDropDownAction);
		addAction(menuManager, toolBarManager, this.updateScoreDropDownAction);

	}

	private void addAction(IMenuManager menuManager, IToolBarManager toolBarManager, IAction action) {
		if (null != menuManager) {
			menuManager.add(action);
		}

		if (null != toolBarManager) {
			toolBarManager.add(action);
		}
	}

	private void addAction(IMenuManager menuManager, IToolBarManager toolBarManager, IContributionItem item) {
		if (null != menuManager) {
			menuManager.add(item);
		}
		if (null != toolBarManager) {
			toolBarManager.add(item);
		}
	}

	/**
	 * @param enabled
	 */
	private void setActionState(boolean enabled) {
		for (IAction action : this.actionList) {
			action.setEnabled(enabled);
		}
	}

	/**
	 *
	 */
	private void doClearInfo() {

		this.setActionState(false);

		if (!SwtUtil.isValid(this.rootPane)) {
			return;
		}

		this.infoLabel.setText(""); //$NON-NLS-1$
		this.imageCanvas.setImageData(null);

	}

	/**
	 * @param movie
	 */
	private void doUpdateInfo(Movie movie) {

		this.currentMovie = movie;

		if (null == this.currentMovie) {
			this.doClearInfo();
			return;
		}

		if (!SwtUtil.isValid(this.rootPane)) {
			return;
		}

		this.setActionState(true);
		this.infoLabel.setText(MovieHelper.getInfoForTooltip(movie));

		String imageUrl = this.currentMovie.getImageUrl();

		// WebP image format is still not supported.
		// So image from youtube will be ignored.

		String refererUrl = WorkbenchActivator.getDefault().getNetworkConfiguration().getReferer(imageUrl, null);

		Map<String, String> headers = null;
		if (StringUtils.isNotEmpty(refererUrl)) {
			headers = new HashMap<String, String>();
			headers.put(HttpUtil.Referer, refererUrl);
		}

		Serializable result = RuntimeManager.getInstance().getCacheService().getElement(imageUrl, headers, this);

		if (null == result) {
			this.imageCanvas.setImageData(null);
		}
	}

	/**
	 *
	 */
	private void updateLayout() {

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void loadFinished(String urlString, Serializable value) {

		byte[] bytes = (byte[]) value;
		if (SwtUtil.isUThread() && SwtUtil.isValid(this.imageCanvas)) {
			update(urlString, bytes);
		} else {
			Display.getDefault().asyncExec(() -> update(urlString, bytes));
		}
	}

	/**
	 * @param urlString
	 * @param value
	 */
	private void update(String urlString, byte[] value) {
		this.doUpdate(urlString, value);
		this.updateLayout();
	}

	/**
	 * @param urlString
	 * @param value
	 */
	private void doUpdate(String urlString, Object value) {

		if (this.currentMovie == null) {
			return;
		}

		if (null == value) {
			return;
		}

		if (StringUtils.equals(urlString, this.currentMovie.getImageUrl())) {
			byte[] bytes = (byte[]) value;

			if (SwtUtil.isValid(this.imageCanvas)) {
				ImageData imageData = ImageUtil.convertToDefaultQuietly(bytes);
				this.imageCanvas.setImageData(imageData);

			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partActivated(IWorkbenchPart part) {
		switchSelection(part);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		switchSelection(part);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partClosed(IWorkbenchPart part) {
		if (this.pinned) {
			return;
		}

		if (part instanceof SiteMultiPageEditor) {
			part.getSite().getSelectionProvider().removeSelectionChangedListener(this);

			this.elementNavigator = null;
			this.doClearInfo();

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partOpened(IWorkbenchPart part) {

		if (this.pinned) {
			return;
		}

		if (part instanceof SiteMultiPageEditor) {
			part.getSite().getSelectionProvider().addSelectionChangedListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		updateSelection(selection);
	}

	/**
	 * @param selection
	 */
	private void updateSelection(ISelection selection) {

		if (this.pinned) {
			return;
		}

		Object object = SwtUtil.findFirstElement(selection);

		Movie movie = null;
		if (object instanceof Movie) {
			movie = (Movie) object;
		}

		doUpdateInfo(movie);

		if ((null != this.elementNavigator) && (null != movie)) {
			this.elementNavigator.locate(movie);
		}
	}

	/**
	 * @return the pinned
	 */
	public boolean isPinned() {
		return this.pinned;
	}

	/**
	 * @param pinned the pinned to set
	 */
	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	/**
	 * @param part
	 */
	private void switchSelection(IWorkbenchPart part) {

		if (this.isPinned()) {
			return;
		}

		if (part instanceof SiteMultiPageEditor) {

			this.elementNavigator = AdapterUtil.getAdapter(part, IElementNavigator.class);

			ISelectionProvider selectionProvider = ((SiteMultiPageEditor) part).getEditorSite().getSelectionProvider();
			ISelection selection = SwtUtil.getSelection(selectionProvider);

			updateSelection(selection);

			this.title = part.getTitle();
		}
	}

	/**
	 * 
	 */
	public void left() {
		skip(-1);
	}

	/**
	 * 
	 */
	public void right() {
		skip(1);
	}

	/**
	 * 
	 */
	public void pageUp() {
		skip(-20);
	}

	/**
	 * 
	 */
	public void pageDown() {
		skip(20);
	}

	/**
	 * 
	 */
	public void up() {
		pageUp();
	}

	/**
	 * 
	 */
	public void down() {
		pageDown();
	}

	/**
	 * 
	 */
	public void skip(int step) {
		if (null == this.elementNavigator) {
			return;
		}

		Movie movie = this.elementNavigator.skipElement(step);
		if (null != movie) {
			this.doUpdateInfo(movie);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// TODO Nothing to do

	}

	public void updateLabel() {

		if (SwtUtil.isValid(this.infoLabel)) {
			this.infoLabel.setText(MovieHelper.getInfoForTooltip(this.currentMovie));
		}
	}

}
