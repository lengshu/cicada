/**
 * 
 */
package org.aquarius.cicada.workbench.page.site;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.SiteConfig;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class SiteLabelProvider extends ColumnLabelProvider {

	public static String TrueSymbol = "√";

	public static String FalseSymbol = "×";

	public static String PropertyName = "propertyName";

	private String propertyName;

	/**
	 * 
	 */
	public SiteLabelProvider(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {

		if (element instanceof SiteConfig) {
			SiteConfig siteConfig = (SiteConfig) element;

			if (StringUtils.isEmpty(this.propertyName)) {
				return siteConfig.getSiteName();
			} else {
				return "";
			}

		}

		if (element instanceof MovieChannel) {
			MovieChannel movieChannel = (MovieChannel) element;

			if (StringUtils.isEmpty(this.propertyName)) {
				return movieChannel.getDisplayName();
			}

			boolean flag = false;

			if (StringUtils.equals(this.propertyName, MovieChannel.PropertyAutoRefreshList)) {
				flag = movieChannel.isAutoRefreshList();
			}

			if (StringUtils.equals(this.propertyName, MovieChannel.PropertyAutoRefreshDetail)) {
				flag = movieChannel.isAutoRefreshDetail();
			}

			if (flag) {
				return TrueSymbol;
			} else {
				return FalseSymbol;
			}
		}

		return super.getText(element);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void update(ViewerCell cell) {

		TreeItem treeItem = (TreeItem) cell.getItem();
		treeItem.setData(PropertyName, this.propertyName);

		super.update(cell);
	}

}
