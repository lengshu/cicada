/**
 * 
 */
package org.aquarius.cicada.workbench.job;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class SaveImageJob extends AbstractCancelableJob {

	private String urlString;

	private String fileName;

	/**
	 * 
	 * @param name
	 * @param urlString
	 * @param fileName
	 */
	public SaveImageJob(String name, String urlString, String fileName) {
		super(name);

		this.urlString = urlString;
		this.fileName = fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			BufferedImage image = ImageIO.read(new URL(this.urlString));
			String extensionName = FilenameUtils.getExtension(this.fileName);

			ImageIO.write(image, extensionName, new File(this.fileName));

			TooltipUtil.showInfoTip(Messages.InfoDialogTitle, Messages.SaveImageJob_FinishedMessage);

		} catch (IOException e) {
			TooltipUtil.showErrorTip(Messages.ErrorDialogTitle, e.getLocalizedMessage());
			return new Status(IStatus.WARNING, WorkbenchActivator.PLUGIN_ID, "Failed to execute save image ", e); //$NON-NLS-1$
		}

		return Status.OK_STATUS;
	}

}
