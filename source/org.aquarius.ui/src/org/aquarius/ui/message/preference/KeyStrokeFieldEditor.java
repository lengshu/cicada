/**
 * 
 */
package org.aquarius.ui.message.preference;

import org.eclipse.jface.bindings.keys.KeySequenceText;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class KeyStrokeFieldEditor extends FieldEditor {

	/**
	 * The text widget where keys are entered. This widget is managed by
	 * <code>textTriggerSequenceManager</code>, which provides its special
	 * behaviour.
	 */
	private Text textTriggerSequence;

	/**
	 * The manager for the text widget that traps incoming key events. This manager
	 * should be used to access the widget, rather than accessing the widget
	 * directly.
	 */
	private KeySequenceText textTriggerSequenceManager;

	/**
	 * 
	 */
	public KeyStrokeFieldEditor() {
		// super();
	}

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public KeyStrokeFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		Label label = this.getLabelControl(parent);
		label.setLayoutData(new GridData());

		// The text widget into which the key strokes will be entered.
		this.textTriggerSequence = new Text(parent, SWT.BORDER);
		// On MacOS X, this font will be changed by KeySequenceText

		// The manager for the key sequence text widget.
		this.textTriggerSequenceManager = new KeySequenceText(this.textTriggerSequence);
		this.textTriggerSequenceManager.setKeyStrokeLimit(4);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		this.textTriggerSequence.setLayoutData(gridData);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoad() {
		String value = this.getPreferenceStore().getString(getPreferenceName());
		this.textTriggerSequence.setText(value);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoadDefault() {
		String value = this.getPreferenceStore().getDefaultString(getPreferenceName());
		this.textTriggerSequence.setText(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStore() {
		this.getPreferenceStore().setValue(this.getPreferenceName(), isValid());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

}
