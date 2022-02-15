package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.job.SaveImageJob;
import org.aquarius.ui.util.ImageUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.enu.RefreshType;
import org.eclipse.swt.widgets.FileDialog;

/**
 * Save a image to local folder.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SaveImageAction extends AbstractSelectionAction {

	/**
	 *
	 * @param label
	 */
	public SaveImageAction(String label) {
		super(label);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/save.png")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> selectedMovieList) {
		if (CollectionUtils.isEmpty(selectedMovieList)) {
			return RefreshType.None;
		}

		FileDialog fileDialog = new FileDialog(SwtUtil.findShell());
		String fileName = fileDialog.open();

		if (StringUtils.isBlank(fileName)) {
			return RefreshType.None;
		}

		String extensionName = FilenameUtils.getExtension(fileName);
		boolean isSupportedImageFormat = ImageUtil.isSupportedImageFormat(extensionName);

		if (!isSupportedImageFormat) {
			TooltipUtil.showErrorTip(Messages.ErrorDialogTitle, Messages.SaveImageAction_UnsupportImageFormatMessage);
			return RefreshType.None;
		}

		Movie movie = selectedMovieList.get(0);
		SaveImageJob saveImageJob = new SaveImageJob(this.getText(), movie.getImageUrl(), fileName);
		saveImageJob.schedule();

		return RefreshType.None;
	}

}
