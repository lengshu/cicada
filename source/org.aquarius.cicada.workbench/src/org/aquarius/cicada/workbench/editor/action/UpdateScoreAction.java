package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.view.GalleryView;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;

/**
 * Update the selected movies to the specified score.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class UpdateScoreAction extends AbstractSelectionAction {

	private int score;

	/**
	 *
	 * @param label
	 */
	public UpdateScoreAction(String label, int score) {
		super(label);

		this.score = score;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> movieList) {
		for (Movie movie : movieList) {
			movie.setScore(this.score);
		}

		RuntimeManager.getInstance().getStoreService().insertOrUpdateMovies(movieList);

		updateGalleryViews();

		return RefreshType.Update;
	}

	/**
	 * 
	 */
	private void updateGalleryViews() {
		IWorkbenchPage workbenchPage = SwtUtil.findActiveWorkbenchPage();

		if (null != workbenchPage) {
			IViewReference[] viewReferences = workbenchPage.getViewReferences();

			for (IViewReference viewReference : viewReferences) {

				if (GalleryView.ViewId.equals(viewReference.getId())) {
					IViewPart viewPart = viewReference.getView(false);
					if (viewPart instanceof GalleryView) {
						GalleryView galleryView = (GalleryView) viewPart;
						galleryView.updateLabel();
					}
				}

			}
		}
	}

}
