/**
 *
 */
package org.aquarius.ui.binder;

import org.aquarius.ui.binder.impl.CheckboxBinder;
import org.aquarius.ui.binder.impl.LabelBinder;
import org.aquarius.ui.binder.impl.SpinnerBinder;
import org.aquarius.ui.binder.impl.StyledTextBinder;
import org.aquarius.ui.binder.impl.TextBinder;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.AssertUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 *
 * Create control binder for different controls.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class ControlBinderManager {

	/**
	 *
	 */
	private ControlBinderManager() {
		// No instanced needed.
	}

	/**
	 * Binde a control.<BR>
	 *
	 * @param control
	 * @param valueAccessor
	 * @param object
	 * @param propertyName
	 * @param prompt
	 * @return
	 */
	public static IControlBinder<?, ?> bind(Control control, IValueAccessor valueAccessor, Object object, String propertyName, String prompt) {

		AssertUtil.assertNotNull(control);
		AssertUtil.assertNotNull(valueAccessor);
		AssertUtil.assertNotNull(object);
		AssertUtil.assertNotNull(propertyName);

		SwtUtil.createPromptDecoration(control, prompt);

		if (control.getClass().isAssignableFrom(Text.class)) {
			return new TextBinder((Text) control, valueAccessor, object, propertyName);
		}

		if (control.getClass().isAssignableFrom(Label.class)) {
			return new LabelBinder((Label) control, valueAccessor, object, propertyName);
		}

		if (control.getClass().isAssignableFrom(StyledText.class)) {
			return new StyledTextBinder((StyledText) control, valueAccessor, object, propertyName);
		}

		if (control.getClass().isAssignableFrom(Spinner.class)) {
			return new SpinnerBinder((Spinner) control, valueAccessor, object, propertyName);
		}

		if (control.getClass().isAssignableFrom(Button.class)) {
			Button button = (Button) control;
			AssertUtil.assertTrue(((button.getStyle() & SWT.CHECK) != 0), "The buton should be a checkbox");
			return new CheckboxBinder(button, valueAccessor, object, propertyName);
		}

		return null;
	}

}
