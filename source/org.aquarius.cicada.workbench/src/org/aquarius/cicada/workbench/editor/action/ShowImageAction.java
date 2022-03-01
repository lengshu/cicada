package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.action.ICommandIds;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.cicada.workbench.view.GalleryView;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * Play the selected movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ShowImageAction extends AbstractSelectionAction {

	private static final String SecondaryId = "detach";

	/**
	 *
	 * @param label
	 */
	public ShowImageAction(String label) {
		super(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EDITOR_SHOW_IMAGE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EDITOR_SHOW_IMAGE);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/showImage.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> selectedMovieList) {
		if (CollectionUtils.isEmpty(selectedMovieList)) {
			return RefreshType.None;
		}

		IWorkbenchPage workbenchPage = WorkbenchUtil.getActivePage();

		try {

			IViewReference viewReference = workbenchPage.findViewReference(GalleryView.ViewId, SecondaryId);
			if (null != viewReference) {
				IViewPart viewPart = viewReference.getView(false);

				if (null != viewPart) {
					return RefreshType.None;
				}
			}

			IViewPart viewPart = workbenchPage.showView(GalleryView.ViewId, "detach", IWorkbenchPage.VIEW_ACTIVATE);

			IViewSite viewSite = viewPart.getViewSite();

			EModelService modelService = viewSite.getService(EModelService.class);
			MPartSashContainerElement mpartService = viewSite.getService(MPart.class);

			if (!mpartService.isOnTop()) {
				modelService.detach(mpartService, 120, 120, 640, 640);
			}

			if (viewPart instanceof GalleryView) {
				GalleryView galleryView = (GalleryView) viewPart;

				// IEditorPart editorPart = WorkbenchUtil.getActiveEditor();
				// galleryView.partActivated(editorPart,);
			}

		} catch (PartInitException e) {
			e.printStackTrace();
			// Nothing to do
		}

		return RefreshType.None;

		// get editorpart somehow which you wanted to open it.
		// EditorPart openEditor =
		// /*IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(),
		// module, MyEditorID, false); */

		// get editor site
		// IWorkbenchPartSite site = openEditor.getSite();

//		Movie movie = selectedMovieList.get(0);

//		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				.getActivePart();
//
//		Shell shell = new Shell(SwtUtil.findDisplay());
//		shell.setText(movie.getTitle());
//		shell.setLayout(new FillLayout());
//
//		Map<String, String> headers = new HashMap<String, String>();
//		headers.put(HttpUtil.Referer, movie.getPageUrl());
//
//		new UrlImageCanvas(shell, SWT.None, movie.getImageUrl(), headers);
//
//		Rectangle rect = Display.getDefault().getPrimaryMonitor().getClientArea();
//
//		int width = rect.width / 2;
//		int height = (rect.width) / 3;
//
//		shell.setSize(width, height);
//		shell.open();

	}

}
