package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSelectionAction;
import org.aquarius.cicada.workbench.job.ExternalAnalyserJob;
import org.aquarius.util.enu.RefreshType;

/**
 * Update the selected movies to the specified score.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ExternalAnalyserAction extends AbstractSelectionAction {

	private AbstractUrlRedirector urlRedirector;

	/**
	 * 
	 * 
	 * @param label
	 * @param template
	 */
	public ExternalAnalyserAction(AbstractUrlRedirector urlRedirector) {
		super(urlRedirector.getName());

		this.urlRedirector = urlRedirector;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RefreshType internalRun(List<Movie> movieList) {
		new ExternalAnalyserJob(Messages.ExternalAnalyserAction_JobName, movieList, this.urlRedirector).schedule();

		return RefreshType.None;
	}

}
