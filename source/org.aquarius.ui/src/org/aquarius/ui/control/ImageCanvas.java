/**
 *
 */
package org.aquarius.ui.control;

import java.net.URL;

import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Image Canvas control.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ImageCanvas extends Canvas {

	private static ImageDescriptor checkedImageDescriptor;

	static {
		URL imageUrl = ImageCanvas.class.getResource("/org/aquarius/ui/control/icon/check.png");
		checkedImageDescriptor = ImageDescriptor.createFromURL(imageUrl);
	}

	// The image to display
	private Image image;

	// the zoom for the current image.
	private float zoom = 1f;

	/**
	 * whether the control is checked.<BR>
	 */
	private boolean checked = false;

	/**
	 * whether the control is enabled.<BR>
	 */
	private boolean enabledChecked = false;

	private ImageData imageData;

	/**
	 * @param parent
	 * @param style
	 */
	public ImageCanvas(Composite parent, int style) {
		super(parent, style);

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				// 调用重绘方法
				paintImage(e.gc);
			}
		});

		this.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				if (ImageCanvas.this.enabledChecked) {
					setChecked(!ImageCanvas.this.checked);
				}
			}

		});
	}

	/**
	 * @param image the image to set
	 */
	public void setImageData(ImageData imageData) {

		if (this.imageData != imageData) {

			this.imageData = imageData;

			Image oldImage = this.image;

			updateImage();

			SwtUtil.disposeResourceQuietly(oldImage);
		}

	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return this.checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {

		if (this.checked != checked) {
			this.checked = checked;
			this.updateImage();
		}
	}

	/**
	 *
	 */
	private void updateImage() {

		if (null == this.imageData) {
			this.image = null;
			this.redraw();
			return;
		}
		{
			Image oldImage = this.image;
			this.image = new Image(Display.getDefault(), this.imageData);
			SwtUtil.disposeResourceQuietly(oldImage);
		}

		if (this.checked) {
			DecorationOverlayIcon decorationOverlayIcon = new DecorationOverlayIcon(this.image, checkedImageDescriptor, IDecoration.TOP_LEFT);

			Image oldImage = this.image;
			this.image = decorationOverlayIcon.createImage();
			SwtUtil.disposeResourceQuietly(oldImage);
		}

		this.redraw();
	}

	/**
	 * @return the enabledChecked
	 */
	public boolean isEnabledChecked() {
		return this.enabledChecked;
	}

	/**
	 * @param enabledChecked the enabledChecked to set
	 */
	public void setEnabledChecked(boolean enabledChecked) {

		if (this.enabledChecked != enabledChecked) {

			if (!this.enabledChecked) {
				this.setChecked(false);
			}
		}
	}

	/**
	 * @param enabled the enabled to set
	 */
	@Override
	public void setEnabled(boolean enabled) {

	}

	/**
	 * Redraw the image and recalculate the appropriate display position when the
	 * window area changes, so as to ensure the complete display of the image in the
	 * middle.
	 *
	 * @param gc
	 */
	protected void paintImage(GC gc) {
		if (null == this.image)
			return;
		this.zoom = fitZoom();
		Rectangle rect = getPaintRect();
		gc.drawImage(this.image, 0, 0, this.image.getBounds().width, this.image.getBounds().height, rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Returns the zoom scale of the full display image suitable for the current
	 * window size. When the length and width of the image are less than the display
	 * window, it returns 1.
	 *
	 * @return
	 */
	private float fitZoom() {

		if (SwtUtil.isValid(this.image)) {
			Point size = getSize();
			Rectangle imgSize = this.image.getBounds();
			if (imgSize.width < size.x && imgSize.height < size.y) {
				// return 1f;
			}
			if (imgSize.width * size.y < imgSize.height * size.x) {
				return (float) size.y / imgSize.height;
			}
			return (float) size.x / imgSize.width;
		} else {
			return 1;
		}

	}

	/**
	 * Returns the display area (centered) of the image in the GC according to the
	 * image scale
	 *
	 * @return
	 */
	private Rectangle getPaintRect() {
		Point size = getSize();

		if (SwtUtil.isValid(this.image)) {
			Rectangle imgSize = this.image.getBounds();
			if (this.zoom > 0) {
				imgSize.width *= this.zoom;
				imgSize.height *= this.zoom;
			}
			imgSize.x = (size.x - imgSize.width) / 2;
			imgSize.y = (size.y - imgSize.height) / 2;
			return imgSize;
		} else {
			return new Rectangle(0, 0, size.x, size.y);
		}

	}

	@Override
	public void dispose() {
		super.dispose();
		this.image.dispose();
	}
}