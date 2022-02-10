/**
 * 
 */
package org.aquarius.cicada.workbench.editor.action.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.impl.redirector.ConfigGetUrlRedirector;
import org.aquarius.cicada.core.spi.AbstractUrlRedirector;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.action.ExternalAnalyserAction;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.menu.ActionListMenuCreator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.slf4j.Logger;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class EditorActionFactory {

	private static final EditorActionFactory instance = new EditorActionFactory();

	private List<ConfigGetUrlRedirector> externalParsers = new ArrayList<>();

	private Logger log = LogUtil.getLogger(getClass());

	private class DefaultAction extends Action {

		/**
		 * @param text
		 */
		public DefaultAction(String text) {
			super(text);
		}

	}

	public static EditorActionFactory getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private EditorActionFactory() {
		super();
	}

	public IAction createExternalAnalyserAction(String label) {

		List<AbstractUrlRedirector> urlRedirectors = RuntimeManager.getInstance().getAllUrlRedirectors();

		if (CollectionUtils.isEmpty(urlRedirectors)) {
			return null;
		}

		List<Action> actionList = new ArrayList<>();

		for (AbstractUrlRedirector urlRedirector : urlRedirectors) {
			ExternalAnalyserAction action = new ExternalAnalyserAction(urlRedirector);
			actionList.add(action);
		}

		DefaultAction dropdownAction = new DefaultAction(label);

		dropdownAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/externalAnalyser.png")); //$NON-NLS-1$
		dropdownAction.setMenuCreator(new ActionListMenuCreator(actionList));

		return dropdownAction;

	}

}
