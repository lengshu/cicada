package org.aquarius.cicada.workbench.action.debug;

import org.aquarius.cicada.workbench.config.editor.DebugLocalScriptControlFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

/**
 * Show the local script editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ShowLocalScriptEditorAction extends Action {

	/**
	 *
	 * @param label
	 */
	public ShowLocalScriptEditorAction(String label) {
		super(label);

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		Shell shell = new Shell(SWT.SHELL_TRIM);

		DebugLocalScriptControlFactory factory = new DebugLocalScriptControlFactory();

		shell.setLayout(new FillLayout());

		factory.createControl(shell);

		shell.setSize(1200, 800);
		shell.open();

	}
}
