/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.log.LogUtil;
import org.aquarius.service.IHttpCacheService;
import org.aquarius.service.IHttpCacheService.LoadFinishListener;
import org.aquarius.ui.util.ImageUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.net.HttpUtil;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;

/**
 * @author aquarius.github@gmail.com
 *
 */
class NatTableMovieToolTip extends NatTableContentTooltip implements LoadFinishListener<Serializable> {

	private Image internalImage;

	private DataLayer dataLayer;

	private IRowDataProvider<Movie> dataProvider;

	private Font font;

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 *
	 * @param dataLayer
	 * @param natTable
	 * @param tooltipRegions
	 */
	public NatTableMovieToolTip(DataLayer dataLayer, NatTable natTable, String... tooltipRegions) {
		super(natTable, tooltipRegions);

		this.setPopupDelay(200);

		this.dataLayer = dataLayer;
		this.dataProvider = (IRowDataProvider<Movie>) this.dataLayer.getDataProvider();

		this.font = GUIHelper.getFont(new FontData("Arial", 12, SWT.BOLD));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean shouldCreateToolTip(Event event) {

		if (!WorkbenchActivator.getDefault().getConfiguration().isShowTooltip()) {
			return false;
		}

		return super.shouldCreateToolTip(event);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deactivate() {

		diposeImage();

		super.deactivate();
	}

	/**
	 * 
	 */
	private void diposeImage() {
		SwtUtil.disposeResourceQuietly(this.internalImage);
		this.internalImage = null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void afterHideToolTip(Event event) {
		diposeImage();

		super.afterHideToolTip(event);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Image getImage(Event event) {

		if (WorkbenchActivator.getDefault().getConfiguration().isHideImageTooltip()) {
			return null;
		}

		Movie movie = getMovie(event);
		if (null != movie) {

			try {
				IHttpCacheService cacheService = RuntimeManager.getInstance().getCacheService();
				Map<String, String> headers = new HashMap<String, String>();
				headers.put(HttpUtil.Referer, movie.getPageUrl());

				byte[] bytes = (byte[]) cacheService.getElement(movie.getImageUrl(), headers, true);

				if (ArrayUtils.isNotEmpty(bytes)) {
					ImageData imageData = null;

					try {
						imageData = new ImageData(new ByteArrayInputStream(bytes));
					} catch (Exception e) {
						imageData = ImageUtil.convertQuietly(bytes, ImageUtil.DefaultImageFormat, ImageUtil.ErrorImageData);
					}

					if (null != imageData && imageData.width > 0) {
						int scaleFactor = (480 * 100) / (imageData.width);
						imageData = DPIUtil.autoScaleImageData(event.display, imageData, scaleFactor, 100);
						this.internalImage = new Image(event.display, imageData);
						return this.internalImage;
					} else {
						return null;
					}

				}

			} catch (Exception e) {
				this.logger.error("getImage", e);
			}
		}

		return super.getImage(event);
	}

	/**
	 * @param event
	 */
	private Movie getMovie(Event event) {
		int row = this.natTable.getRowPositionByY(event.y);
		row = LayerUtil.convertRowPosition(this.natTable, row, this.dataLayer);

		int rowCount = this.dataProvider.getRowCount();
		if (row < rowCount && row >= 0) {
			return this.dataProvider.getRowObject(row);
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {

		Composite pane = new Composite(parent, SWT.None);
		pane.setLayout(new GridLayout(1, false));

		Image image = getImage(event);
		Image bgImage = getBackgroundImage(event);
		String text = getText(event);
		Color fgColor = getForegroundColor(event);
		Color bgColor = getBackgroundColor(event);
		Font font = getFont(event);

		int width = -1;

		if (image != null) {
			Label imageLabel = new Label(pane, SWT.WRAP);
			imageLabel.setImage(image);
			GridData data = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
			imageLabel.setLayoutData(data);

			width = image.getImageData().width;
		}

		if (text != null) {
			Label textLabel = new Label(pane, SWT.WRAP);
			textLabel.setText(text);
			doUpateLabel(image, bgImage, fgColor, bgColor, font, textLabel);

			GridData data = new GridData(SWT.HORIZONTAL, SWT.BOTTOM, false, true, 1, 1);

			if (width > 0) {
				Point size = textLabel.computeSize(width, SWT.DEFAULT);

				data.widthHint = size.x;
				data.heightHint = size.y;
			}

			textLabel.setLayoutData(data);

		}

		return pane;
	}

	/**
	 * @param image
	 * @param bgImage
	 * @param fgColor
	 * @param bgColor
	 * @param font
	 * @param textLabel
	 */
	private void doUpateLabel(Image image, Image bgImage, Color fgColor, Color bgColor, Font font, Label textLabel) {
		if (fgColor != null) {
			textLabel.setForeground(fgColor);
		}

		if (bgColor != null) {
			textLabel.setBackground(bgColor);
		}

		if (bgImage != null) {
			textLabel.setBackgroundImage(image);
		}

		if (font != null) {
			textLabel.setFont(font);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected String getText(Event event) {
		Movie movie = this.getMovie(event);
		return MovieHelper.getInfoForTooltip(movie);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Font getFont(Event event) {
		return this.font;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void loadFinished(String urlString, Serializable value) {
		// TODO Auto-generated method stub

	}

}
