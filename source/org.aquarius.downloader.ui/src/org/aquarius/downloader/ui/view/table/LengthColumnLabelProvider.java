/**
 *
 */
package org.aquarius.downloader.ui.view.table;

import java.text.DecimalFormat;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloaderFactory;
import org.aquarius.util.SystemUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Used to show info for the length of a task.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class LengthColumnLabelProvider extends ColumnLabelProvider {

	private String propertyName;

	private DecimalFormat decimalFormat = new DecimalFormat("# M");

	/**
	 *
	 */
	public LengthColumnLabelProvider(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {
		try {
			Object object = PropertyUtils.getProperty(element, this.propertyName);

			if (!(object instanceof Number)) {
				return ObjectUtils.toString(object);
			}

			Number value = (Number) object;

			DownloadTask downloadTask = (DownloadTask) element;
			if (StringUtils.equals(downloadTask.getType(), AbstractSegmentDownloaderFactory.TypeHls)) {
				return ObjectUtils.toString(object);
			} else {

				double doubleValue = value.longValue() / SystemUtil.DiskSizeInM;

				return this.decimalFormat.format(doubleValue);
			}

		} catch (Exception e) {
			// Nothing to do
			return "error";
		}
	}

}
