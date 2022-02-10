/**
 *
 */
package org.aquarius.ui.control;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * a special label to error or other info.<BR>
 * The info will disappear in specified delay.<BR>
 *
 * FIXME
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MessageLabel extends Composite {

	private int delayTime;

	private Timer timer;

	private long lastUpdateTime;

	private Label label;

	/**
	 * @param parent
	 * @param style
	 */
	public MessageLabel(Composite parent, int style) {
		this(parent, style, 10);

	}

	/**
	 * @param parent
	 * @param style
	 * @param delayTime
	 */
	public MessageLabel(Composite parent, int style, int delayTime) {
		super(parent, style);
		this.setLayout(new FillLayout());

		this.label = new Label(this, SWT.NONE);
		this.delayTime = delayTime;

		// initTimer();
	}

	/**
	 * create a timer to hide self.<BR>
	 */
	private void initTimer() {
		this.timer = new Timer("hide message  ");
		this.timer.schedule(new TimerTask() {

			private boolean lastState;

			@Override
			public void run() {

				if (this.lastState) {

					if ((MessageLabel.this.lastUpdateTime + MessageLabel.this.delayTime * 1000) < System.currentTimeMillis()) {
						// setVisible(false);
					}
				}

				// this.lastState = isVisible();

			}

		}, 0, 500);
	}

	public void setText(String string) {

		this.lastUpdateTime = System.currentTimeMillis();

		if (null == string) {
			string = "";
		}

		this.label.setText(string);

		if (StringUtils.isEmpty(string)) {
			this.setVisible(false);
		}
	}

	/**
	 * @return the delayTime
	 */
	public int getDelayTime() {
		return this.delayTime;
	}

	/**
	 * @param delayTime the delayTime to set
	 */
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.timer.cancel();
	}

}
