/**
 *
 */
package org.aquarius.cicada.workbench.nebula;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Rewrite this code is to limit the min width.<BR>
 *
 * This code is from eclispe nebula multichoice control.<BR>
 * {@link https://wiki.eclipse.org/Nebula_TextAssist }
 *
 * Instances of this class are selectable user interface objects that allow the
 * user to enter and modify text. The difference with the Text widget is that
 * when the user types something, some propositions are displayed.
 *
 * @see org.eclipse.swt.widgets.Text
 */
public class TextAssistWithFixedWidth extends TextAssist {

	/**
	 * @param parent
	 * @param style
	 * @param contentProvider
	 */
	public TextAssistWithFixedWidth(Composite parent, int style, TextAssistContentProvider contentProvider) {
		super(parent, style, contentProvider);
	}

	private Integer fixedWidth;

	/**
	 * @return the width
	 */
	public Integer getFixedWidth() {
		return this.fixedWidth;
	}

	/**
	 * @param width the width to set
	 */
	public void setFixedWidth(Integer fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {

		Point size = super.computeSize(wHint, hHint, changed);
		if (null != this.fixedWidth) {
			size.x = this.fixedWidth;
		}

		return size;
	}

	public Text getTextControl() {
		Control[] controls = this.getChildren();

		for (Control control : controls) {
			if (control instanceof Text) {
				return (Text) control;
			}
		}

		return null;
	}

}