package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.job.GenerateDownloadUrlJob;

/**
 * Generate download url.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class GenerateDownloadUrlsAction extends AbstractSelectionAction {

	private AbstractDownloadListGenerator downloadListGenerator;

	public GenerateDownloadUrlsAction(String label, AbstractDownloadListGenerator downloadListGenerator) {
		super(label);

		this.downloadListGenerator = downloadListGenerator;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void internalRun(List<Movie> selectedMovieList) {
		new GenerateDownloadUrlJob(Messages.GenerateDownloadUrlsAction_GenerateDownloadUrlsJob, this.downloadListGenerator, selectedMovieList).schedule();

	}

}
