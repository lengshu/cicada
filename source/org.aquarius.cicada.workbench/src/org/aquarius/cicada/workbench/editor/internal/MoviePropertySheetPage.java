/**
 *
 */
package org.aquarius.cicada.workbench.editor.internal;

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
import org.aquarius.cicada.workbench.editor.action.CopyInfoDropDownAction;
import org.aquarius.cicada.workbench.editor.action.DownloadAction;
import org.aquarius.cicada.workbench.editor.action.GenerateDownloadUrlsDropDownAction;
import org.aquarius.cicada.workbench.editor.action.OpenMovieUrlAction;
import org.aquarius.cicada.workbench.editor.action.PlayMovieAction;
import org.aquarius.cicada.workbench.editor.action.RefreshMovieAction;
import org.aquarius.cicada.workbench.editor.action.SaveImageAction;
import org.aquarius.cicada.workbench.editor.action.ShowImageAction;
import org.aquarius.cicada.workbench.editor.action.factory.EditorActionFactory;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.service.IHttpCacheService.LoadFinishListener;
import org.aquarius.ui.base.SelectionProviderAdapter;
import org.aquarius.ui.control.ImageCanvas;
import org.aquarius.ui.key.KeyBinder;
import org.aquarius.ui.key.KeyBinderManager;
import org.aquarius.ui.util.AdapterUtil;
import org.aquarius.ui.util.ImageUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.net.HttpUtil;
import org.aquarius.util.spi.IElementNavigator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * Property sheet page.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MoviePropertySheetPage extends Page implements IPropertySheetPage, LoadFinishListener<Serializable> {

	private static String SashFormWeights = MoviePropertySheetPage.class.getName() + ".SashFormWeights"; //$NON-NLS-1$

	private static String LayoutType = MoviePropertySheetPage.class.getName() + ".LayoutType"; //$NON-NLS-1$

	private SashForm rootPane;

	private ImageCanvas imageCanvas;

	private Text infoLabel;

	private Movie currentMovie;

	private MenuManager contextMenuManager;

	private List<IAction> actionList = new ArrayList<>();

	private IAction changeLayoutAction;

	private ShowImageAction showImageAction;

	private SaveImageAction saveImageAction;

	private IAction refreshMovieAction;
	private IAction openMovieUrlAction;
	private IAction downloadAction;
	private IAction playMovieAction;
	private IAction copyInfoDropDownAction;
	private IAction generateDownloadUrlsDropDownAction;

	private IAction externalAnalyserDropdownAction;

	private int layoutType = SWT.HORIZONTAL;

	private SelectionProviderAdapter selectionProvider;

	private IElementNavigator<Movie> elementNavigator;

	/**
	 *
	 */
	public MoviePropertySheetPage() {
		super();
		this.selectionProvider = new SelectionProviderAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public ISelection getSelection() {

				if (null == MoviePropertySheetPage.this.currentMovie) {
					return StructuredSelection.EMPTY;
				}

				return new StructuredSelection(MoviePropertySheetPage.this.currentMovie);
			}
		};

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void createControl(Composite parent) {

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

		makeActions();

		this.contextMenuManager = new MenuManager();
		this.makeContributions(this.contextMenuManager, null, null);

		this.imageCanvas.setMenu(this.contextMenuManager.createContextMenu(this.imageCanvas));

		this.imageCanvas.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				MoviePropertySheetPage.this.showImageAction.run();
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

	}

	/**
	 *
	 */
	private void makeActions() {

		this.changeLayoutAction = new Action(Messages.MoviePropertySheetPage_ChangeLayoutActionLabel, SWT.None) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void runWithEvent(Event event) {
				changeLayout();
			}
		};

		this.changeLayoutAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/changeLayout.png")); //$NON-NLS-1$

		this.showImageAction = new ShowImageAction(Messages.MoviePropertySheetPage_ShowImageActionLabel);

		this.saveImageAction = new SaveImageAction(Messages.MoviePropertySheetPage_SaveImageActionLabel);

		this.refreshMovieAction = new RefreshMovieAction(Messages.SiteEditorActionBarContributor_RefreshMovie);
		this.openMovieUrlAction = new OpenMovieUrlAction(Messages.SiteEditorActionBarContributor_OpenSelectedMovies);
		this.downloadAction = new DownloadAction(Messages.SiteEditorActionBarContributor_DownloadSelectedMovies, false);
		this.playMovieAction = new PlayMovieAction(Messages.SiteEditorActionBarContributor_PlayMovie);
		this.copyInfoDropDownAction = new CopyInfoDropDownAction(Messages.SiteEditorActionBarContributor_Copy);
		this.generateDownloadUrlsDropDownAction = new GenerateDownloadUrlsDropDownAction(Messages.SiteEditorActionBarContributor_GenerateDownloadUrls);

		this.externalAnalyserDropdownAction = EditorActionFactory.getInstance()
				.createExternalAnalyserAction(Messages.SiteEditorActionBarContributor_ExternalAnalyser);

		this.actionList.add(this.externalAnalyserDropdownAction);
		this.actionList.add(this.changeLayoutAction);
		// this.actionList.add(this.showImageAction);
		this.actionList.add(this.saveImageAction);

		this.actionList.add(this.refreshMovieAction);
		this.actionList.add(this.openMovieUrlAction);
		this.actionList.add(this.downloadAction);
		this.actionList.add(this.playMovieAction);
		this.actionList.add(this.copyInfoDropDownAction);
		this.actionList.add(this.generateDownloadUrlsDropDownAction);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		pageSite.setSelectionProvider(this.selectionProvider);
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
	 * {@inheritDoc}}
	 */
	@Override
	public Control getControl() {
		return this.rootPane;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		super.makeContributions(menuManager, toolBarManager, statusLineManager);

		addAction(menuManager, toolBarManager, this.changeLayoutAction);
		addAction(menuManager, toolBarManager, this.showImageAction);
		addAction(menuManager, toolBarManager, this.saveImageAction);
		addAction(menuManager, toolBarManager, new Separator());

		addAction(menuManager, toolBarManager, this.refreshMovieAction);
		addAction(menuManager, toolBarManager, this.openMovieUrlAction);
		addAction(menuManager, toolBarManager, this.downloadAction);
		addAction(menuManager, toolBarManager, new Separator());

		addAction(menuManager, toolBarManager, this.playMovieAction);
		addAction(menuManager, toolBarManager, this.copyInfoDropDownAction);
		addAction(menuManager, toolBarManager, this.generateDownloadUrlsDropDownAction);
		addAction(menuManager, toolBarManager, this.externalAnalyserDropdownAction);

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
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		this.elementNavigator = AdapterUtil.getAdapter(part, IElementNavigator.class);
		Movie movie = null;

		Object object = SwtUtil.findFirstElement(selection);
		if (object instanceof Movie) {
			movie = (Movie) object;
		}

		if (this.currentMovie != movie) {
			updateInfo(movie);
		} else {
			clearInfo();
		}

		setActionState(null != this.currentMovie);

		if ((null != this.elementNavigator) && (null != movie)) {
			this.elementNavigator.locate(movie);
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
	private void clearInfo() {

		if (!SwtUtil.isValid(this.rootPane)) {
			return;
		}

		this.infoLabel.setText(""); //$NON-NLS-1$
		this.imageCanvas.setImageData(null);

	}

	/**
	 * @param movie
	 */
	private void updateInfo(Movie movie) {

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
			this.updateInfo(movie);
		}
	}

	public void right() {
		if (null == this.elementNavigator) {
			return;
		}

		Movie movie = this.elementNavigator.nextElement();
		if (null != movie) {
			this.updateInfo(movie);
		}
	}
}
