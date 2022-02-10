/**
 *
 */
package org.aquarius.ui.table;

import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * Use spinner to edit a table cell.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SpinnerCellEditor extends CellEditor {

	/**
	 * The zero-based index of the selected item.
	 */
	private int selection;

	/**
	 * The custom spinner control.
	 */
	private Spinner spinner;

	/**
	 * Default SpinnerCellEditor style
	 */
	private static final int defaultStyle = SWT.NONE;

	private int minimum = -1;

	private int maximum = -1;

	/**
	 *
	 */
	public SpinnerCellEditor() {
		setStyle(defaultStyle);
	}

	/**
	 *
	 * @param parent
	 */
	public SpinnerCellEditor(Composite parent) {
		super(parent, defaultStyle);
	}

	/**
	 *
	 * @param minimum
	 * @param maximum
	 */
	public SpinnerCellEditor(int minimum, int maximum) {
		setStyle(defaultStyle);

		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 *
	 * @param parent
	 * @param minimum
	 * @param maximum
	 */
	public SpinnerCellEditor(Composite parent, int minimum, int maximum) {
		super(parent, defaultStyle);

		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return the minimum
	 */
	public int getMinimum() {
		return this.minimum;
	}

	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(int minimum) {
		this.minimum = minimum;

		updateRange();
	}

	/**
	 * @return the maximum
	 */
	public int getMaximum() {
		return this.maximum;
	}

	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(int maximum) {
		this.maximum = maximum;

		updateRange();
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected Control createControl(Composite parent) {
		this.spinner = new Spinner(parent, SWT.NONE);

		updateRange();

		this.spinner.setSelection(0);
		this.spinner.setIncrement(1);
		this.spinner.setSize(120, 80);

		this.spinner.addKeyListener(new KeyAdapter() {
			// hook key pressed - see PR 14201
			@Override
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		this.spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				applyEditorValueAndDeactivate();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				SpinnerCellEditor.this.selection = SpinnerCellEditor.this.spinner.getSelection();
			}
		});

		this.spinner.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});

		this.spinner.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				SpinnerCellEditor.this.focusLost();
			}
		});

		return this.spinner;
	}

	/**
	 *
	 */
	private void updateRange() {

		if (SwtUtil.isValid(this.spinner)) {
			if (this.maximum > this.minimum) {
				this.spinner.setMinimum(this.minimum);
				this.spinner.setMaximum(this.maximum);
			}
		}
	}

	public void setRange(int minimum, int maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
		updateRange();
	}

	/**
	 * The <code>SpinnerCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method returns the zero-based index of the
	 * current selection.
	 *
	 * @return the zero-based index of the current selection wrapped as an
	 *         <code>Integer</code>
	 */
	@Override
	protected Object doGetValue() {
		return Integer.valueOf(this.selection);
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected void doSetFocus() {
		this.spinner.setFocus();
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected void doSetValue(Object value) {

		if (value instanceof Integer) {
			this.selection = (int) value;
		} else {
			this.selection = 0;
		}

		this.spinner.setSelection(this.selection);

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public LayoutData getLayoutData() {
		LayoutData layoutData = super.getLayoutData();
		if ((this.spinner == null) || this.spinner.isDisposed())
			layoutData.minimumWidth = 30;
		else {
			// make the spinner 10 characters wide
			GC gc = new GC(this.spinner);
			layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10;
			gc.dispose();
		}

		return layoutData;
	}

	void applyEditorValueAndDeactivate() {
		// must set the selection before getting value
		this.selection = this.spinner.getSelection();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);
		fireApplyEditorValue();
		deactivate();
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected void focusLost() {
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { // Escape character
			fireCancelEditor();
		} else if (keyEvent.character == '\t') { // tab key
			applyEditorValueAndDeactivate();
		}
	}

}
