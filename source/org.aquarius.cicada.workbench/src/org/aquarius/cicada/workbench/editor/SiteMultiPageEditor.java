/**
 *
 */
package org.aquarius.cicada.workbench.editor;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.control.IMovieListRefreshable;
import org.aquarius.cicada.workbench.editor.internal.MoviePropertySheetPage;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.base.SelectionProviderAdapter;
import org.aquarius.ui.editor.BaseTableEditorPart;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.slf4j.Logger;

/**
 * This page editor is multi,because it can use extension to support more
 * editors.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class SiteMultiPageEditor extends MultiPageEditorPart implements IMovieListRefreshable, ISelectionChangedListener {

	public static final String EditorID = "org.aquarius.cicada.workbench.editor.site"; //$NON-NLS-1$

	public static final String SearchEditorID = "org.aquarius.cicada.workbench.editor.search"; //$NON-NLS-1$

	private Logger logger = LogUtil.getLogger(this.getClass());

	private SiteBrowserEditor browserEditor;

	private MoviePropertySheetPage propertyPage;

	// private FilterSite filterSite;

	private SelectionProviderAdapter selectionProvider;

	private boolean editing = false;

	/**
	 *
	 */
	public SiteMultiPageEditor() {
		super();

		this.selectionProvider = new SelectionProviderAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public ISelection getSelection() {

				List<Movie> movieList = getSelectedMovieList();
				if (CollectionUtils.isEmpty(movieList)) {
					return StructuredSelection.EMPTY;
				}

				return new StructuredSelection(movieList);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void setSelection(ISelection selection) {

				List<Movie> movieList = MovieHelper.getMovieList(selection);

				IEditorPart editorPart = getActiveEditor();

				if (editorPart instanceof IMovieListRefreshable) {
					IMovieListRefreshable movieListRefreshable = (IMovieListRefreshable) editorPart;
					movieListRefreshable.setSelectedMovieList(movieList);
				}
			}

		};
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createPages() {

		this.browserEditor = new SiteBrowserEditor(this.getMovieSite());

		try {
			int index = this.addPage(this.browserEditor, getEditorInput());
			this.setPageText(index, Messages.SiteMultiPageEditor_Browser);
		} catch (PartInitException e) {
			this.logger.error("createPages", e); //$NON-NLS-1$
		}

		if (!WorkbenchActivator.getDefault().getConfiguration().isDisableExtensionTable()) {
			loadExtensionTable();
		}

		doUpdateSelectionProvider(this.browserEditor);

		this.propertyPage = new MoviePropertySheetPage();

		String defaultEditor = WorkbenchActivator.getDefault().getConfiguration().getDefaultEditor();

		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editorPart = this.getEditor(i);

			if (StringUtils.equalsIgnoreCase(editorPart.getClass().getName(), defaultEditor)) {
				if (i != this.getActivePage()) {
					this.setActivePage(i);
				}

				break;
			}
		}
	}

	/**
	 * Load multi editor from extensions.<BR>
	 */
	private void loadExtensionTable() {

		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui.editors"); //$NON-NLS-1$

		for (IConfigurationElement configurationElement : configurationElements) {
			IConfigurationElement[] children = configurationElement.getChildren("contentTypeBinding"); //$NON-NLS-1$

			for (IConfigurationElement child : children) {
				String contentTypeId = child.getAttribute("contentTypeId"); //$NON-NLS-1$

				if (StringUtils.equals(contentTypeId, EditorID)) {
					String className = configurationElement.getAttribute("class"); //$NON-NLS-1$

					if (!StringUtils.equals(className, this.getClass().getName())) {

						String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
						try {
							Object object = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$

							if ((object instanceof IEditorPart) && (object instanceof IMovieListRefreshable)) {
								IEditorPart editorPart = (IEditorPart) object;

								if (editorPart instanceof IPageChangedListener) {
									this.addPageChangedListener((IPageChangedListener) editorPart);
								}

								int index = this.addPage(editorPart, getEditorInput());
								this.setPageText(index, name);

								doUpdateSelectionProvider(editorPart);
							}
						} catch (CoreException e) {
							this.logger.error("load page extension.", e); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	/**
	 * Load multi editor from extensions.<BR>
	 */
	public static Map<String, String> getEditorNames() {

		Map<String, String> editorNames = new ListOrderedMap<>();
		editorNames.put(SiteBrowserEditor.class.getName(), Messages.SiteMultiPageEditor_Browser);

		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui.editors"); //$NON-NLS-1$

		for (IConfigurationElement configurationElement : configurationElements) {
			IConfigurationElement[] children = configurationElement.getChildren("contentTypeBinding"); //$NON-NLS-1$

			for (IConfigurationElement child : children) {
				String contentTypeId = child.getAttribute("contentTypeId"); //$NON-NLS-1$

				if (StringUtils.equals(contentTypeId, EditorID)) {
					String className = configurationElement.getAttribute("class"); //$NON-NLS-1$

					if (!StringUtils.equals(className, SiteMultiPageEditor.class.getName())) {

						String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
						editorNames.put(className, name);
					}
				}
			}
		}

		return editorNames;
	}

	/**
	 * @param editorPart
	 */
	private void doUpdateSelectionProvider(IEditorPart editorPart) {
		ISelectionProvider selectionProvider = editorPart.getAdapter(ISelectionProvider.class);
		if (null != selectionProvider) {
			selectionProvider.addSelectionChangedListener(this);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editorPart = this.getEditor(i);

			if (editorPart instanceof BaseTableEditorPart) {
				BaseTableEditorPart tableEditorPart = (BaseTableEditorPart) editorPart;

				if (tableEditorPart.isDirty()) {
					tableEditorPart.doSave(monitor);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void dispose() {

		this.doSave(new NullProgressMonitor());

		super.dispose();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IEditorSite createSite(IEditorPart editor) {

		IEditorActionBarContributor actionBarContributor = this.getEditorSite().getActionBarContributor();

		return new MultiPageEditorSite(this, editor) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public IEditorActionBarContributor getActionBarContributor() {
				return actionBarContributor;
			}
		};
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSaveAs() {
		// Noting to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * @return
	 */
	public Site getMovieSite() {
		return this.getEditorInput().getAdapter(Site.class);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IEditorSite editorSite, IEditorInput editorInput) throws PartInitException {
		super.init(editorSite, editorInput);
		this.setInput(editorInput);
		this.setSite(editorSite);

		Site movieSite = this.getMovieSite();
		// this.filterSite = new FilterSite(movieSite);
		this.setPartName(movieSite.getSiteName());

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void refreshContent() {
		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editorPart = this.getEditor(i);

			if (editorPart instanceof IMovieListRefreshable) {
				IMovieListRefreshable movieListRefreshable = (IMovieListRefreshable) editorPart;
				movieListRefreshable.refreshContent();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateContent() {
		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editorPart = this.getEditor(i);

			if (editorPart instanceof IMovieListRefreshable) {
				IMovieListRefreshable movieListRefreshable = (IMovieListRefreshable) editorPart;
				movieListRefreshable.updateContent();
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<Movie> getSelectedMovieList() {
		IEditorPart editorPart = this.getActiveEditor();

		if (editorPart instanceof IMovieListRefreshable) {
			IMovieListRefreshable movieListRefreshable = (IMovieListRefreshable) editorPart;
			return movieListRefreshable.getSelectedMovieList();
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void update(List<Movie> movieList) {
		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editorPart = this.getEditor(i);

			if (editorPart instanceof IMovieListRefreshable) {
				IMovieListRefreshable movieListRefreshable = (IMovieListRefreshable) editorPart;
				movieListRefreshable.update(movieList);
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if (adapter == IPropertySheetPage.class) {
			return (T) this.propertyPage;
		}

		if (adapter == ISelectionProvider.class) {
			return (T) this.selectionProvider;
		}

		if (adapter == Site.class) {
			return (T) this.getMovieSite();
		}

		if (adapter == SiteBrowserEditor.class) {
			return (T) this.browserEditor;
		}

		return super.getAdapter(adapter);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		this.propertyPage.selectionChanged(this, event.getSelection());
	}

	/**
	 * @return the editing
	 */
	public boolean isEditing() {
		return this.editing;
	}

	/**
	 * @param editing the editing to set
	 */
	public void setEditing(boolean editing) {

		if (this.editing == editing) {
			return;
		}

		this.editing = editing;

		for (int i = 0; i < this.getPageCount(); i++) {
			IEditorPart editorPart = this.getEditor(i);

			if (editorPart instanceof BaseTableEditorPart) {
				BaseTableEditorPart tableEditorPart = (BaseTableEditorPart) editorPart;
				tableEditorPart.setEditing(editing);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.browserEditor.updateOutline(this.getActiveEditor() == this.browserEditor);
		super.setFocus();
	}

}
