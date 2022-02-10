package org.aquarius.cicada.workbench.editor.action;

import java.util.List;

import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.editor.action.base.AbstractSiteEditorMultiSelectionAction;
import org.aquarius.cicada.workbench.job.ExternalAnalyserJob;

/**
 * Update the selected movies to the specified score.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ExternalAnalyserAction extends AbstractSiteEditorMultiSelectionAction {

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
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean internalRun(SiteMultiPageEditor siteEditor, List<Movie> selectedMovieList) {

		new ExternalAnalyserJob(Messages.ExternalAnalyserAction_JobName, selectedMovieList, this.urlRedirector).schedule();

		return false;
	}

}
