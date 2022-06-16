package org.aquarius.cicada.workbench.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.dialog.DownloadExternalUrlDialog;
import org.aquarius.cicada.workbench.job.ParseMovieJob;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.ui.validator.UrlInputValidator;
import org.aquarius.util.StringUtil;
import org.aquarius.util.collection.CollectionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Parse a movie by a specified url.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ParseMovieAction extends Action {

	private final IWorkbenchWindow window;

	/**
	 *
	 * @param window
	 * @param label
	 */
	public ParseMovieAction(IWorkbenchWindow window, String label) {
		this.window = window;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_PARSE_MOVIE);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_PARSE_MOVIE);

		setImageDescriptor(org.aquarius.cicada.workbench.WorkbenchActivator.getImageDescriptor("/icons/parse.png")); //$NON-NLS-1$

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {
		if (this.window != null) {

			IInputValidator validator = new UrlInputValidator();
			String content = ClipboardUtil.getStringFromClipboard();

			if (StringUtils.isNotBlank(validator.isValid(content))) {
				content = "";
			}

			DownloadExternalUrlDialog dialog = new DownloadExternalUrlDialog(this.window.getShell(), Messages.ParseMovieAction_DownloadDialogTitle,
					Messages.ParseMovieAction_DownloadDialogMessage, content, validator);

			if (dialog.open() == Dialog.OK) {
				String[] urlStrings = StringUtil.toLines(dialog.getValue());

				List<String> urlStringList = CollectionUtil.removeDuplicated(urlStrings);

				ParseMovieJob job = new ParseMovieJob(Messages.ParseMovieAction_ParseMovie, dialog.isAutoDownload(), dialog.getUrlRedirector(), urlStringList);

				job.setUser(true);
				job.schedule();
			}
		}
	}
}
