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
import org.aquarius.cicada.workbench.editor.action.UpdateScoreDropDownAction;
import org.aquarius.cicada.workbench.editor.action.UpdateStateDropDownAction;
import org.aquarius.cicada.workbench.editor.action.factory.EditorActionFactory;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.service.IHttpCacheService.LoadFinishListener;
import org.aquarius.ui.control.ImageCanvas;
import org.aquarius.ui.key.KeyBinder;
import org.aquarius.ui.key.KeyBinderManager;
import org.aquarius.ui.util.AdapterUtil;
import org.aquarius.ui.util.ImageUtil;
import org.aquarius.ui.util.SwtUtil;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
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

	private static String SashFormWeights = GalleryView.class.getName() + ".SashFormWeights"; //$NON-NLS-1$

	private static String LayoutType = GalleryView.class.getName() + ".LayoutType"; //$NON-NLS-1$

	private static final String SecondaryId = "detach";

	private SashForm rootPane;

	private ImageCanvas imageCanvas;

	private Text infoLabel;

	private Movie currentMovie;

	private MenuManager contextMenuManager;

	private List<IAction> actionList = new ArrayList<>();

	private IAction changeLayoutAction;

	private IAction showViewAction;

	private IAction pinViewAction;

	// private ShowImageAction showImageAction;

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

	/**
	 * 
	 */
	public GalleryView() {
		IWorkbenchPage page = WorkbenchUtil.getActivePage();
		page.addPartListener(this);

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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		IActionBars actionBars = site.getActionBars();
		this.makeContributions(actionBars.getMenuManager(), actionBars.getToolBarManager());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		IPreferenceStore preferenceStore = WorkbenchActivator.getDefault().getPreferenceStore();
		preferenceStore.setDefault(LayoutType, SWT.VERTICAL);

		this.layoutType = preferenceStore.getInt(LayoutType);

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
			IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
			String weightString = store.getString(SashFormWeights);

			this.rootPane.setWeights(new int[] { 68, 32 });
			if (StringUtils.isNotEmpty(weightString)) {
				try {
					int[] wights = StringUtil.splitIntoInteger(weightString, ",");
					this.rootPane.setWeights(wights);

				} catch (Exception e) {
					// Nothing to do
				}
			}
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
				GalleryView.this.showViewAction.run();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				Rectangle rect = GalleryView.this.imageCanvas.getBounds();

				if (e.x < (rect.width * 0.3)) {
					left();
				}

				if (e.x > (rect.width * 0.7)) {
					right();
				}
			}

		});

		KeyBinderManager.bind(this.imageCanvas, this, KeyBinder.LeftKeyBinder, KeyBinder.RightKeyBinder);

		this.rootPane.addDisposeListener(e -> {

			if (SwtUtil.isValid(this.rootPane)) {
				int[] weights = this.rootPane.getWeights();
				String weightString = StringUtil.joinInteger(weights, ",");

				IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
				store.setValue(SashFormWeights, weightString);
			}

		});

		setActionState(null != this.currentMovie);
	}

	/**
	 *
	 */
	private void makeActions() {

		this.pinViewAction = new Action("Pin", IAction.AS_CHECK_BOX) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				pin();
			}
		};

		this.pinViewAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/pin.png"));

		this.changeLayoutAction = new Action(Messages.MoviePropertySheetPage_ChangeLayoutActionLabel, SWT.None) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				changeLayout();
			}
		};

		this.changeLayoutAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/changeLayout.png")); //
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

		this.showViewAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/showImage.png"));

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

		this.actionList.add(this.pinViewAction);
		this.actionList.add(this.externalAnalyserDropdownAction);
		this.actionList.add(this.changeLayoutAction);
		this.actionList.add(this.showViewAction);
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
	protected void pin() {
		this.pinned = !this.pinned;
		this.pinViewAction.setChecked(this.pinned);
	}

	/**
	 * @throws Exception
	 * 
	 */
	protected void showNewView() throws Exception {

		IWorkbenchPage workbenchPage = WorkbenchUtil.getActivePage();

		IViewReference viewReference = workbenchPage.findViewReference(GalleryView.ViewId, SecondaryId);
		if (null != viewReference) {
			IViewPart viewPart = viewReference.getView(false);

			if (null != viewPart) {
				return;
			}
		}

		IViewPart viewPart = workbenchPage.showView(GalleryView.ViewId, SecondaryId, IWorkbenchPage.VIEW_ACTIVATE);

		IViewSite viewSite = viewPart.getViewSite();

		EModelService modelService = viewSite.getService(EModelService.class);
		MPartSashContainerElement mpartService = viewSite.getService(MPart.class);

		if (!mpartService.isOnTop()) {
			modelService.detach(mpartService, 120, 120, 640, 640);
		}

		if (viewPart instanceof GalleryView) {
			GalleryView galleryView = (GalleryView) viewPart;

			galleryView.elementNavigator = this.elementNavigator;

			galleryView.updateSelection(new StructuredSelection(this.currentMovie));

			galleryView.pinViewAction.run();
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
		preferenceStore.setValue(LayoutType, this.layoutType);

		this.rootPane.setOrientation(this.layoutType);

		this.updateLayout();
	}

	/**
	 * 
	 * @param menuManager
	 * @param toolBarManager
	 */
	private void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager) {

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

		if (!SwtUtil.isValid(this.rootPane)) {
			return;
		}

		this.currentMovie = movie;

		if (null == this.currentMovie) {
			this.infoLabel.setText(""); //$NON-NLS-1$
			this.imageCanvas.setImageData(null);
			return;
		}

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

	public void left() {
		if (null == this.elementNavigator) {
			return;
		}

		Movie movie = this.elementNavigator.previousElement();
		if (null != movie) {
			this.doUpdateInfo(movie);
		}
	}

	public void right() {
		if (null == this.elementNavigator) {
			return;
		}

		Movie movie = this.elementNavigator.nextElement();
		if (null != movie) {
			this.doUpdateInfo(movie);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof SiteMultiPageEditor) {
			this.elementNavigator = AdapterUtil.getAdapter(part, IElementNavigator.class);

			ISelectionProvider selectionProvider = ((SiteMultiPageEditor) part).getEditorSite().getSelectionProvider();
			ISelection selection = null;
			if (null != selectionProvider) {
				selection = selectionProvider.getSelection();
			}

			updateSelection(selection);
		}
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

			this.doClearInfo();
			this.elementNavigator = null;
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

//		if (this.currentMovie != movie) {
//			doUpdateInfo(movie);
//		} else {
//			doClearInfo();
//		}

		setActionState(null != this.currentMovie);

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
	 * {@inheritDoc}
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if (part instanceof SiteMultiPageEditor) {
			this.elementNavigator = AdapterUtil.getAdapter(part, IElementNavigator.class);

			ISelectionProvider selectionProvider = ((SiteMultiPageEditor) part).getEditorSite().getSelectionProvider();
			ISelection selection = null;
			if (null != selectionProvider) {
				selection = selectionProvider.getSelection();
			}

			updateSelection(selection);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
