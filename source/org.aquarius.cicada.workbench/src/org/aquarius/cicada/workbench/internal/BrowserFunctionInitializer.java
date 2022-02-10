/**
 * 
 */
package org.aquarius.cicada.workbench.internal;

import org.aquarius.cicada.core.spi.web.IWebAccessorService.Initializer;
import org.aquarius.cicada.workbench.browser.BrowserUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class BrowserFunctionInitializer implements Initializer<Browser> {

	/**
	 * 
	 */
	public BrowserFunctionInitializer() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Browser browser) {
		Display.getDefault().syncExec(() -> {

			if (SwtUtil.isValid(browser)) {
				BrowserUtil.registerCommonFunctions(browser);
			}

		});
	}

}
