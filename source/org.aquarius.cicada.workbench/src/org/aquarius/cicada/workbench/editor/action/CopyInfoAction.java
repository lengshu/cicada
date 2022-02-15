package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.ui.dialog.ListInfoDialog;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.util.enu.RefreshType;

/**
 * Copy the specified attribute information or all the information in the
 * selected movie.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CopyInfoAction extends AbstractSelectionAction {

	private String property;

	public CopyInfoAction(String label, String property) {
		super(label);

		this.property = property;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> selectedMovieList) {
		String content = MovieHelper.copyMovieInfo(selectedMovieList, this.property);

		if (WorkbenchActivator.getDefault().getConfiguration().isShowInfoDialog()) {
			new ListInfoDialog(getShell(), Messages.CopyInfoAction_Title, content).open();
		} else {
			ClipboardUtil.setClipboardString(content);
		}

		return RefreshType.None;
	}

}
