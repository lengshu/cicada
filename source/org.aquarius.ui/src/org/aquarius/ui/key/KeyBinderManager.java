/**
 *
 */
package org.aquarius.ui.key;

import java.util.Map;

import org.apache.commons.lang.reflect.MethodUtils;
import org.aquarius.log.LogUtil;
import org.aquarius.util.AssertUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;

/**
 * Key binder manager.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class KeyBinderManager {

	private static final Logger logger = LogUtil.getLogger(KeyBinderManager.class);

	/**
	 *
	 */
	private KeyBinderManager() {
		// No instances needed
	}

	/**
	 * 
	 * @param control
	 * @param mapping
	 */
	public static void bind(Control control, Map<KeyBinder, IAction> mapping) {
		AssertUtil.assertNotNull(control);
		AssertUtil.assertNotNull(mapping);

		control.addKeyListener(new KeyAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void keyReleased(KeyEvent event) {

				for (Map.Entry<KeyBinder, IAction> entry : mapping.entrySet()) {

					KeyBinder keyBinder = entry.getKey();

					if (keyBinder.isMatch(event)) {
						try {

							entry.getValue().run();

							event.doit = true;
							return;
						} catch (Exception e) {
							logger.error("execute " + keyBinder.getMethod(), e);
						}
					}
				}

				super.keyReleased(event);
			}
		});
	}

	/**
	 * 
	 * @param control
	 * @param mapping
	 */
	public static void bindKeyStrokes(Control control, Map<KeyStroke, IAction> mapping) {
		AssertUtil.assertNotNull(control);
		AssertUtil.assertNotNull(mapping);

		control.addKeyListener(new KeyAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void keyReleased(KeyEvent event) {

				for (Map.Entry<KeyStroke, IAction> entry : mapping.entrySet()) {

					KeyStroke keyStroke = entry.getKey();

					if (isMatch(keyStroke, event)) {
						try {

							entry.getValue().run();

							event.doit = true;
							return;
						} catch (Exception e) {
							logger.error("execute method run of " + entry.getValue().getClass(), e);
						}
					}
				}

				super.keyReleased(event);
			}
		});
	}

	private static final boolean isMatch(KeyStroke keyStroke, KeyEvent event) {
		return event.keyCode == keyStroke.getNaturalKey() && event.stateMask == keyStroke.getModifierKeys();
	}

	/**
	 *
	 * @param control
	 * @param executor
	 * @param keyBinders
	 */
	public static void bind(Control control, Object executor, KeyBinder... keyBinders) {
		AssertUtil.assertNotNull(control);
		AssertUtil.assertNotNull(keyBinders);

		control.addKeyListener(new KeyAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void keyReleased(KeyEvent event) {

				for (KeyBinder keyBinder : keyBinders) {

					if (keyBinder.isMatch(event)) {
						try {

							Object methodInvoker = executor;
							if (null == methodInvoker) {
								methodInvoker = control;
							}

							MethodUtils.invokeMethod(methodInvoker, keyBinder.getMethod(), null);

							event.doit = true;
							return;
						} catch (Exception e) {
							logger.error("execute " + keyBinder.getMethod(), e);
						}
					}
				}

				super.keyReleased(event);
			}
		});
	}

}
