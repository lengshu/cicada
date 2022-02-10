/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.chips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Instances of this class represent a "chips". This is a kind of
 * rounded-shapped button. It can display information, or be used like a check
 * or a push button. You can also add a close button.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.CLOSE</dd>
 * <dd>SWT.CHECK</dd>
 * <dd>SWT.PUSH</dd>
 * <dt><b>Events:</b></dt>
 * <dd>SWT.Close, SWT.Selection</dd>
 * </dl>
 */
public class Chips extends Canvas {

	private static final int CLOSE_CIRCLE_RAY = 7;

	private Color hoverForeground, hoverBackground;
	private Color closeButtonForeground, closeButtonBackground;
	private Color closeButtonHoverForeground, closeButtonHoverBackground;
	private Color pushedStateForeground, pushedStateBackground;
	private Color borderColor, hoverBorderColor, pushedStateBorderColor;
	private Color chipsBackground;
	private String text;
	private Image image, pushImage, hoverImage;
	private boolean selection;
	private final boolean isCheck;
	private final boolean isPush;
	private final boolean isClose;
	private final List<SelectionListener> selectionListeners = new ArrayList<>();
	private final List<CloseListener> closeListeners = new ArrayList<>();
	private boolean cursorInside;
	private Point closeCenter;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *               instance (cannot be null)
	 * @param style  the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 */
	public Chips(final Composite parent, final int style) {
		super(parent, checkStyle(style));
		initDefaultColors();
		this.text = "";
		this.isCheck = (getStyle() & SWT.CHECK) != 0;
		this.isPush = (getStyle() & SWT.PUSH) != 0;
		this.isClose = (getStyle() & SWT.CLOSE) != 0;

		addListener(SWT.Paint, e -> {
			final GC gc = e.gc;
			gc.setFont(getFont());
			gc.setAdvanced(true);
			gc.setTextAntialias(SWT.ON);
			gc.setAntialias(SWT.ON);
			final Color previousForeground = gc.getForeground();
			final Color previousBackground = gc.getBackground();

			int x = drawBackground(gc);
			drawWidgetBorder(gc);
			if (this.isCheck && this.selection) {
				x = drawCheck(gc, x);
			}

			if (this.image != null) {
				x = drawImage(gc, x);
			}

			if (this.text != null) {
				x = drawText(gc, x);
			}

			if (this.isClose) {
				drawClose(gc, x);
			}
			gc.setBackground(previousBackground);
			gc.setForeground(previousForeground);

		});

		addListener(SWT.MouseEnter, e -> {
			this.cursorInside = this.isPush || this.isCheck || this.isClose;
			if (this.cursorInside) {
				setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
			}
			redraw();
		});
		addListener(SWT.MouseExit, e -> {
			this.cursorInside = false;
			if (this.isPush || this.isCheck || this.isClose) {
				setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
			}
			redraw();
		});
		addListener(SWT.MouseUp, e -> {
			if (!this.isClose && !this.isCheck && !this.isPush) {
				return;
			}
			if (this.isClose) {
				final float dist = (float) Math
						.sqrt((e.x - this.closeCenter.x) * (e.x - this.closeCenter.x) + (e.y - this.closeCenter.y) * (e.y - this.closeCenter.y));
				if (dist < CLOSE_CIRCLE_RAY) {
					final CloseEvent event = new CloseEvent(e);
					for (final CloseListener listener : this.closeListeners) {
						listener.onClose(event);
						if (!event.doit) {
							break;
						}
					}
				}

				if (this.isDisposed()) {
					return;
				}
			}
			setSelection(!this.selection);
			final SelectionEvent event = new SelectionEvent(e);
			for (final SelectionListener listener : this.selectionListeners) {
				listener.widgetSelected(event);
				if (!event.doit) {
					break;
				}
			}

		});
	}

	private static int checkStyle(final int style) {
		final int mask = SWT.CLOSE | SWT.CHECK | SWT.PUSH;
		int newStyle = style & mask;
		newStyle |= SWT.DOUBLE_BUFFERED;
		return newStyle;
	}

	private int drawBackground(final GC gc) {
		final Rectangle rect = getClientArea();

		final Color color = determineBackgroundColor();
		gc.setBackground(color);
		gc.fillRoundRectangle(0, 0, rect.width, rect.height, rect.height, rect.height);

		return rect.height / 2 + 2;
	}

	private void drawWidgetBorder(final GC gc) {
		final Rectangle rect = getClientArea();
		Color color = this.borderColor;
		if (this.cursorInside) {
			color = this.hoverBorderColor;
		} else if (this.isPush && this.selection) {
			color = this.pushedStateBorderColor;
		}

		if (color == null) {
			// No border
			return;
		}

		gc.setForeground(color);
		gc.drawRoundRectangle(0, 0, rect.width - 2, rect.height - 2, rect.height, rect.height);

	}

	private Color determineBackgroundColor() {
		if (this.cursorInside) {
			return this.hoverBackground == null ? getBackground() : this.hoverBackground;
		}
		if (this.isPush && this.selection) {
			return this.pushedStateBackground == null ? getBackground() : this.pushedStateBackground;
		}
		return getChipsBackground() == null ? getBackground() : getChipsBackground();
	}

	private int drawCheck(final GC gc, final int x) {
		Color foreground = null;
		if (this.cursorInside) {
			foreground = this.hoverForeground;
		} else if (this.isPush && this.selection) {
			foreground = this.pushedStateForeground;
		}
		foreground = foreground == null ? getForeground() : foreground;
		gc.setForeground(foreground);
		gc.setLineWidth(2);

		final Rectangle rect = getClientArea();

		final int centerX = x + 4 + CLOSE_CIRCLE_RAY;
		final int centerY = (rect.height - 2 * CLOSE_CIRCLE_RAY) / 2 + CLOSE_CIRCLE_RAY;
		gc.drawLine(x + 6, centerY, x + 9, centerY + 4);
		gc.drawLine(x + 9, centerY + 4, centerX + 4, centerY - 3);
		return x + 16;
	}

	private int drawImage(final GC gc, final int x) {
		Image img = this.image;
		if (this.cursorInside) {
			img = this.hoverImage == null ? img : this.hoverImage;
		}
		if (this.isPush && this.selection) {
			img = this.pushImage == null ? img : this.pushImage;
		}

		final Rectangle rect = getClientArea();
		gc.drawImage(img, x + 2, (rect.height - img.getBounds().height) / 2);

		return x + 4 + img.getBounds().width;
	}

	private int drawText(final GC gc, final int x) {
		final Point textSize = gc.stringExtent(this.text);
		Color color = null;
		if (this.cursorInside) {
			color = this.hoverForeground;
		} else if (this.isPush && this.selection) {
			color = this.pushedStateForeground;
		}
		color = color == null ? getForeground() : color;
		gc.setForeground(color);

		gc.drawText(this.text, x + 2, (getClientArea().height - textSize.y) / 2, true);

		return x + 2 + textSize.x;
	}

	private void drawClose(final GC gc, final int x) {
		final Color foreground = this.cursorInside ? this.closeButtonHoverForeground : this.closeButtonForeground;
		final Color background = this.cursorInside ? this.closeButtonHoverBackground : this.closeButtonBackground;
		final Rectangle rect = getClientArea();

		gc.setBackground(background);
		gc.setForeground(foreground);
		this.closeCenter = new Point(x + 4 + CLOSE_CIRCLE_RAY, (rect.height - 2 * CLOSE_CIRCLE_RAY) / 2 + CLOSE_CIRCLE_RAY);
		gc.fillOval(x + 4, (rect.height - 2 * CLOSE_CIRCLE_RAY) / 2, 2 * CLOSE_CIRCLE_RAY, 2 * CLOSE_CIRCLE_RAY);

		// Cross
		gc.setLineWidth(2);
		gc.drawLine(this.closeCenter.x - 3, this.closeCenter.y - 3, this.closeCenter.x + 3, this.closeCenter.y + 3);
		gc.drawLine(this.closeCenter.x + 3, this.closeCenter.y - 3, this.closeCenter.x - 3, this.closeCenter.y + 3);
	}

	private void initDefaultColors() {
		setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		this.chipsBackground = SWTGraphicUtil.getColorSafely(224, 224, 224);

		this.hoverForeground = SWTGraphicUtil.getColorSafely(62, 28, 96);
		this.hoverBackground = SWTGraphicUtil.getColorSafely(214, 214, 214);

		this.closeButtonForeground = SWTGraphicUtil.getColorSafely(229, 229, 229);
		this.closeButtonBackground = SWTGraphicUtil.getColorSafely(100, 100, 100);
		this.closeButtonHoverForeground = SWTGraphicUtil.getColorSafely(214, 214, 214);
		this.closeButtonHoverBackground = SWTGraphicUtil.getColorSafely(64, 64, 64);

		this.pushedStateForeground = SWTGraphicUtil.getColorSafely(224, 224, 224);
		this.pushedStateBackground = getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		int width = 0; // Border
		int height = 20;
		if (this.image != null) {
			final Rectangle imageSize = this.image.getBounds();
			width += 4 + imageSize.width;
			height = Math.max(height, imageSize.height + 4);

		}

		if (this.text != null) {
			final GC gc = new GC(this);
			final Point textSize = gc.stringExtent(this.text);
			width += 4 + textSize.x;
			height = Math.max(height, textSize.y);
			gc.dispose();
		}

		if (this.isCheck && this.selection || this.isClose) {
			width += 20;
		}

		width += Math.max(height, hHint); // Size for left & right half-circle
		return new Point(Math.max(width, wHint), Math.max(height, hHint));
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is closed by the user, by sending it one of the messages defined
	 * in the <code>CodeListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *                 closed by the user,
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see CloseListener
	 * @see #removeCloseListener
	 * @see SelectionEvent
	 */
	public void addCloseListener(final CloseListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.closeListeners.add(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#addListener(int,
	 *      org.eclipse.swt.widgets.Listener)
	 */
	@Override
	public void addListener(final int eventType, final Listener listener) {
		if (eventType == SWT.Selection) {
			this.selectionListeners.add(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					widgetSelection(e);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					widgetSelection(e);
				}

				private void widgetSelection(final SelectionEvent e) {
					final Event event = new Event();
					event.widget = Chips.this;
					event.display = getDisplay();
					event.type = SWT.Selection;
					listener.handleEvent(event);
				}
			});
			return;
		} else if (eventType == SWT.Close) {
			this.closeListeners.add(new CloseListener() {
				@Override
				public void onClose(final CloseEvent e) {
					final Event event = new Event();
					event.widget = Chips.this;
					event.display = getDisplay();
					event.type = SWT.Close;
					listener.handleEvent(event);
				}
			});
			return;
		}
		super.addListener(eventType, listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is selected by the user, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *                 selected by the user,
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.selectionListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is closed by the user.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see CloseListener
	 * @see #addCloseListener
	 */
	public void removeCloseListener(final CloseListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.closeListeners.remove(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is selected by the user.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.selectionListeners.remove(listener);
	}

	// ---- Getters & Setters

	/**
	 * Returns the receiver's background color.
	 *
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getChipsBackground() {
		checkWidget();
		return this.chipsBackground;
	}

	/**
	 * Returns the receiver's foreground color when mouse is hover the widget.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getHoverForeground() {
		checkWidget();
		return this.hoverForeground;
	}

	/**
	 * Returns the receiver's background color when mouse is hover the widget.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getHoverBackground() {
		checkWidget();
		return this.hoverBackground;
	}

	/**
	 * Returns the receiver's close item foreground color.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getCloseButtonForeground() {
		checkWidget();
		return this.closeButtonForeground;
	}

	/**
	 * Returns the receiver's close item background color.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getCloseButtonBackground() {
		checkWidget();
		return this.closeButtonBackground;
	}

	/**
	 * Returns the receiver's close item foreground color when the mouse is hover
	 * the widget.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getCloseButtonHoverForeground() {
		checkWidget();
		return this.closeButtonHoverForeground;
	}

	/**
	 * Returns the receiver's close item background color when the mouse is hover
	 * the widget.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getCloseButtonHoverBackground() {
		checkWidget();
		return this.closeButtonHoverBackground;
	}

	/**
	 * Returns the receiver's foreground color when the widget is "pushed"
	 * (selected).
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getPushedStateForeground() {
		checkWidget();
		return this.pushedStateForeground;
	}

	/**
	 * Returns the receiver's background color when the widget is "pushed"
	 * (selected).
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getPushedStateBackground() {
		checkWidget();
		return this.pushedStateBackground;
	}

	/**
	 * Returns the receiver's color for the border of the widget.
	 *
	 * @return the border color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getBorderColor() {
		checkWidget();
		return this.borderColor;
	}

	/**
	 * Returns the receiver's color for the border when the mouse is hover the
	 * widget
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the border color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getHoverBorderColor() {
		checkWidget();
		return this.hoverBorderColor;
	}

	/**
	 * Returns the receiver's color for the border when the widget is "pushed"
	 * (selected)
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @return the border color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Color getPushedStateBorderColor() {
		checkWidget();
		return this.pushedStateBorderColor;
	}

	/**
	 * Returns the receiver's text, which will be an empty string if it has never
	 * been set.
	 *
	 * @return the receiver's text
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public String getText() {
		checkWidget();
		return this.text;
	}

	/**
	 * Returns the receiver's image if it has one, or null if it does not.
	 *
	 * @return the receiver's image
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Image getImage() {
		checkWidget();
		return this.image;
	}

	/**
	 * Returns the receiver's image when the widget is pushed (selected) if it has
	 * one, or null if it does not.
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @return the receiver's image
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Image getPushImage() {
		checkWidget();
		return this.pushImage;
	}

	/**
	 * Returns the receiver's image when the mouse is hover the widget if it has
	 * one, or null if it does not.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @return the receiver's image
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Image getHoverImage() {
		checkWidget();
		return this.hoverImage;
	}

	/**
	 * Returns <code>true</code> if the receiver is selected, and false otherwise.
	 * <p>
	 * Note: This operation is only available if the SWT.CHECK or the SWT.PUSH flag
	 * is set.
	 * </p>
	 *
	 * @return the selection state
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public boolean getSelection() {
		checkWidget();
		return this.selection;
	}

	/**
	 * Sets the receiver's background color to the color specified by the argument,
	 * or to the default system color for the control if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setChipsBackground(final Color chipsBackground) {
		checkWidget();
		this.chipsBackground = chipsBackground;
	}

	/**
	 * Sets the receiver's foreground color to the color specified by the argument,
	 * or to the default system color for the control if the argument is null.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setHoverForeground(final Color hoverForeground) {
		checkWidget();
		this.hoverForeground = hoverForeground;
	}

	/**
	 * Sets the receiver's background color to the color specified by the argument,
	 * or to the default system color for the control if the argument is null.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setHoverBackground(final Color hoverBackground) {
		checkWidget();
		this.hoverBackground = hoverBackground;
	}

	/**
	 * Sets the receiver's close button foreground color to the color specified by
	 * the argument, or to the default system color for the control if the argument
	 * is null.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setCloseButtonForeground(final Color closeButtonForeground) {
		checkWidget();
		this.closeButtonForeground = closeButtonForeground;
	}

	/**
	 * Sets the receiver's close button background color to the color specified by
	 * the argument, or to the default system color for the control if the argument
	 * is null.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setCloseButtonBackground(final Color closeButtonBackground) {
		checkWidget();
		this.closeButtonBackground = closeButtonBackground;
	}

	/**
	 * Sets the receiver's close button foreground color (when the mouse is hover
	 * the widget) to the color specified by the argument, or to the default system
	 * color for the control if the argument is null.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setCloseButtonHoverForeground(final Color closeButtonHoverForeground) {
		checkWidget();
		this.closeButtonHoverForeground = closeButtonHoverForeground;
	}

	/**
	 * Sets the receiver's close button background color (when the mouse is hover
	 * the widget) to the color specified by the argument, or to the default system
	 * color for the control if the argument is null.
	 * <p>
	 * Note: This operation is only available if the SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setCloseButtonHoverBackground(final Color closeButtonHoverBackground) {
		checkWidget();
		this.closeButtonHoverBackground = closeButtonHoverBackground;
	}

	/**
	 * Sets the receiver's foreground color when the button is "pushed" (=selected)
	 * to the color specified by the argument, or to the default system color for
	 * the control if the argument is null.
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setPushedStateForeground(final Color pushedStateForeground) {
		checkWidget();
		this.pushedStateForeground = pushedStateForeground;
	}

	/**
	 * Sets the receiver's background color when the button is "pushed" (=selected)
	 * to the color specified by the argument, or to the default system color for
	 * the control if the argument is null.
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setPushedStateBackground(final Color pushedStateBackground) {
		checkWidget();
		this.pushedStateBackground = pushedStateBackground;
	}

	/**
	 * Sets the receiver's border color to the color specified by the argument, or
	 * to the default system color for the control if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setBorderColor(final Color borderColor) {
		checkWidget();
		this.borderColor = borderColor;
	}

	/**
	 * Sets the receiver's border color (when the mouse is hover the widget) to the
	 * color specified by the argument, or to the default system color for the
	 * control if the argument is null.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setHoverBorderColor(final Color hoverBorderColor) {
		checkWidget();
		this.hoverBorderColor = hoverBorderColor;
	}

	/**
	 * Sets the receiver's border color when the button is "pushed" (selected) to
	 * the color specified by the argument, or to the default system color for the
	 * control if the argument is null.
	 * <p>
	 * Note: This operation is a hint and may be overridden by the platform.
	 * </p>
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setPushedStateBorderColor(final Color pushedStateBorderColor) {
		checkWidget();
		this.pushedStateBorderColor = pushedStateBorderColor;
	}

	/**
	 * Sets the receiver's text.
	 * <p>
	 * This method sets the widget label. The label may include the mnemonic
	 * character and line delimiters.
	 * </p>
	 *
	 * @param string the new text
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the text is
	 *                                     null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setText(final String text) {
		checkWidget();
		this.text = text;
	}

	/**
	 * Sets the receiver's image to the argument, which may be null indicating that
	 * no image should be displayed.
	 *
	 * @param image the image to display on the receiver (may be null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the image
	 *                                     has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setImage(final Image image) {
		checkWidget();
		this.image = image;
	}

	/**
	 * Sets the receiver's image to the argument when the widget is "pushed"
	 * (=selected), which may be null indicating that no image should be displayed.
	 * <p>
	 * Note: This operation is only available if the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @param image the image to display on the receiver (may be null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the image
	 *                                     has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setPushImage(final Image pushImage) {
		checkWidget();
		this.pushImage = pushImage;
	}

	/**
	 * Sets the receiver's image to the argument when the mouse is hover the widget,
	 * which may be null indicating that no image should be displayed.
	 * <p>
	 * Note: This operation is only available if at least one the SWT.CHECK,
	 * SWT.PUSH and SWT.CLOSE flag is set.
	 * </p>
	 *
	 * @param image the image to display on the receiver (may be null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the image
	 *                                     has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 */
	public void setHoverImage(final Image hoverImage) {
		checkWidget();
		this.hoverImage = hoverImage;
	}

	/**
	 * Sets the selection state of the receiver, if it is of type <code>CHECK</code>
	 * or <code>PUSH</code>.
	 *
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>, it is
	 * selected when it is checked. When it is of type <code>TOGGLE</code>, it is
	 * selected when it is pushed in.
	 *
	 * @param selected the new selection state
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setSelection(final boolean selected) {
		checkWidget();
		this.selection = selected;
		if (this.isCheck) {
			getParent().layout(new Control[] { this });
		}
		redraw();
	}

}
