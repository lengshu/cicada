/**
 * 
 */
package org.aquarius.cicada.workbench.action;

import java.util.HashMap;
import java.util.Map;

import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class OpenPerspectiveAction extends Action {

	private IWorkbenchWindow window;

	private IPerspectiveDescriptor descriptor;

	/**
	 * 
	 */
	public OpenPerspectiveAction(IWorkbenchWindow window, final IPerspectiveDescriptor descriptor) {
		super();
		this.window = window;
		this.descriptor = descriptor;

		this.setText(this.descriptor.getLabel());
		this.setImageDescriptor(this.descriptor.getImageDescriptor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {

		IHandlerService handlerService = this.window.getService(IHandlerService.class);
		ICommandService commandService = this.window.getService(ICommandService.class);

		Command command = commandService.getCommand(IWorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE);
		Map<String, String> parameters = new HashMap<>();
		parameters.put(IWorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE_PARM_ID, this.descriptor.getId());

		ParameterizedCommand pCommand = ParameterizedCommand.generateCommand(command, parameters);
		try {
			handlerService.executeCommand(pCommand, null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			StatusManager.getManager().handle(new Status(IStatus.WARNING, WorkbenchActivator.PLUGIN_ID,
					"Failed to execute " + IWorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE, e)); //$NON-NLS-1$
		}
	}

}
