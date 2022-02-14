/**
 * 
 */
package org.aquarius.downloader.ui.view.table;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.util.SystemUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class RemaingTimeColumnLabelProvider extends ColumnLabelProvider {

	/**
	 *
	 */
	public RemaingTimeColumnLabelProvider() {

		super();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {
		try {
			DownloadTask downloadTask = (DownloadTask) element;

			if (DownloadTask.StateFinish == downloadTask.getState()) {
				return "";
			}

			int speed = downloadTask.getSpeed();

			if (speed <= 0) {
				return "...";
			}

			long totalLength = downloadTask.getRemoteFileLength();
			long finishedLength = downloadTask.getFinishedLength();

			long remaingLength = totalLength - finishedLength;

			if (remaingLength <= 0) {
				return "00:00:52";
			}

			double value = remaingLength / speed;

			if (value > SystemUtil.TimeDay) {
				return "...";
			}

			int second = (int) value;
			long hours = second / 3600;// 转换小时数
			second = second % 3600;// 剩余秒数

			long minutes = second / 60;// 转换分钟
			second = second % 60;// 剩余秒数

			return hours + ":" + minutes + ":" + second;

		} catch (Exception e) {
			return "...";
		}
	}

}
