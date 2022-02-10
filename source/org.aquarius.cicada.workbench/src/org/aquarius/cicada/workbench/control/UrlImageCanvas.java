/**
 * 
 */
package org.aquarius.cicada.workbench.control;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.service.IHttpCacheService.LoadFinishListener;
import org.aquarius.ui.control.ImageCanvas;
import org.aquarius.ui.util.ImageUtil;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Show url image async.<BR>
 * 
 * @author aquarius.github@gmail.com
 *
 */
public class UrlImageCanvas extends ImageCanvas implements LoadFinishListener<Serializable> {

	/**
	 * @param parent
	 * @param style
	 * @param urlString
	 */
	public UrlImageCanvas(Composite parent, int style, String urlString, Map<String, String> headers) {
		super(parent, style);

		Serializable value = RuntimeManager.getInstance().getCacheService().getElement(urlString, headers, this);
		if (null != value) {
			loadFinished(urlString, value);
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void loadFinished(String urlString, Serializable value) {
		byte[] bytes = (byte[]) value;
		if (SwtUtil.isUThread()) {
			update(bytes);
		} else {
			Display.getDefault().asyncExec(() -> update(bytes));
		}
	}

	/**
	 * @param bytes
	 */
	private void update(byte[] bytes) {

		ImageData imageData = null;

		try {
			imageData = new ImageData(new ByteArrayInputStream(bytes));
		} catch (Exception e) {
			imageData = ImageUtil.convertToDefaultQuietly(bytes);
		}
		this.setImageData(imageData);
	}
}
