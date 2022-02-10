/**
 * 
 */
package org.aquarius.ui.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.aquarius.ui.UiActivator;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class ImageUtil {

	private static final List<String> FormatNames = new ArrayList<>();

	public static final String DefaultImageFormat = "png";

	public static final ImageData ErrorImageData;

	static {

		String[] formatNames = ImageIO.getReaderFormatNames();

		for (String formatName : formatNames) {
			FormatNames.add(formatName.toLowerCase());
		}

		ErrorImageData = UiActivator.getImageDescriptor("/icons/unsupportImageFormat.png").getImageData(100);

		try {
			ImageIO.scanForPlugins();
		} catch (Exception e) {
			// Nothing to do
		}
	}

	/**
	 * 
	 */
	private ImageUtil() {
		// super();
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static ImageData convertToDefaultQuietly(byte[] bytes) {
		return convertQuietly(new ByteArrayInputStream(bytes), DefaultImageFormat, ErrorImageData);
	}

	/**
	 * 
	 * @param bytes
	 * @param format
	 * @return
	 */
	public static ImageData convertQuietly(byte[] bytes, String format) {
		return convertQuietly(new ByteArrayInputStream(bytes), format);

	}

	/**
	 * 
	 * @param bytes
	 * @param format
	 * @param errorImageData
	 * @return
	 */
	public static ImageData convertQuietly(byte[] bytes, String format, ImageData errorImageData) {
		return convertQuietly(new ByteArrayInputStream(bytes), format, errorImageData);

	}

	/**
	 * 
	 * @param bytes
	 * @param format
	 * @return
	 */
	public static ImageData convertQuietly(InputStream inputStream, String format) {
		return convertQuietly(inputStream, format, null);

	}

	/**
	 * 
	 * @param inputStream
	 * @param format
	 * @param errorImageData
	 * @return
	 */
	public static ImageData convertQuietly(InputStream inputStream, String format, ImageData errorImageData) {
		try {
			BufferedImage bufferedImage = ImageIO.read(inputStream);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, format, output);

			ByteArrayInputStream innerInput = new ByteArrayInputStream(output.toByteArray());
			return new ImageData(innerInput);

		} catch (Exception e) {
			return errorImageData;
		}

	}

	/**
	 * 
	 * @param bytes
	 * @param format
	 * @return
	 */
	public static ImageData convertToDefaultQuietly(InputStream inputStream) {
		return convertQuietly(inputStream, DefaultImageFormat, ErrorImageData);
	}

	/**
	 * 
	 * @param format
	 * @return
	 */
	public static boolean isSupportedImageFormat(String format) {
		return FormatNames.contains(format.toLowerCase());
	}
}
