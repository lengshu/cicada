/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.control.IMovieListRefreshable;
import org.aquarius.ui.util.AdapterUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * For async operation.
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class JobUtil {

	/**
	 *
	 */
	private JobUtil() {
		// No instance needed
	}

	/**
	 * Find the active editor to refresh movies.<BR>
	 *
	 * @param moiveList
	 */
	public static void updateMoviesInUI(List<Movie> moiveList) {

		if (SwtUtil.isUThread()) {
			doUpdateMoviesInUI(moiveList);
		} else {
			Display display = Display.getDefault();
			display.asyncExec(() -> doUpdateMoviesInUI(moiveList));
		}
	}

	/***
	 * do the real operation in ui thread.<BR>
	 *
	 * @param moiveList
	 */
	private static void doUpdateMoviesInUI(List<Movie> moiveList) {

		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = workbenchPage.getActiveEditor();

		IMovieListRefreshable movieListRefreshable = AdapterUtil.getAdapter(editorPart, IMovieListRefreshable.class);

		if (null != movieListRefreshable) {
			movieListRefreshable.update(moiveList);
		}

	}
}
