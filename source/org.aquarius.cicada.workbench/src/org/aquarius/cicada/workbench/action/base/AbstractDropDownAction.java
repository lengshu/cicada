/**
 * 
 */
package org.aquarius.cicada.workbench.action.base;

import java.util.List;

import org.aquarius.ui.menu.AbstractMenuCreator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public abstract class AbstractDropDownAction extends Action {

	/**
	 * @param text
	 */
	public AbstractDropDownAction(String text) {
		super(text);

		createMenu();
	}

	/**
	 * @param text
	 * @param image
	 */
	public AbstractDropDownAction(String text, ImageDescriptor image) {
		super(text, image);

		createMenu();
	}

	/**
	 * @param text
	 * @param style
	 */
	public AbstractDropDownAction(String text, int style) {
		super(text, style);

		createMenu();
	}

	/**
	 * 
	 */
	protected void createMenu() {

		this.setMenuCreator(new AbstractMenuCreator() {

			@Override
			protected void createEntries(Menu menu) {
				List<IContributionItem> items = createActionItems();

				for (IContributionItem item : items) {
					item.fill(menu, SWT.DEFAULT);
				}
			}
		});
	}

	public abstract List<IContributionItem> createActionItems();

}
