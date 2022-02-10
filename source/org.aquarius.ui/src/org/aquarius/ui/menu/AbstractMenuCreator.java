/**
 *
 */
package org.aquarius.ui.menu;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * Simply menu creator.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public abstract class AbstractMenuCreator implements IMenuCreator {

	private Menu lastMenu;

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public void dispose() {
		if (this.lastMenu != null) {
			this.lastMenu.dispose();
			this.lastMenu = null;
		}
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Menu getMenu(Control parent) {
		if (this.lastMenu != null) {
			this.lastMenu.dispose();
		}

		this.lastMenu = new Menu(parent);
		createEntries(this.lastMenu);
		return this.lastMenu;
	}

	protected abstract void createEntries(Menu menu);

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public Menu getMenu(Menu parent) {
		if (this.lastMenu != null) {
			this.lastMenu.dispose();
		}
		this.lastMenu = new Menu(parent);
		createEntries(this.lastMenu);
		return this.lastMenu;
	}

}
