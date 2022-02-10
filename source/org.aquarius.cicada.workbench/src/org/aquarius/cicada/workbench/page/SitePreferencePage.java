/**
 *
 */
package org.aquarius.cicada.workbench.page;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.cicada.workbench.page.site.SiteContentProvider;
import org.aquarius.cicada.workbench.page.site.SiteLabelProvider;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.StringUtil;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.alibaba.fastjson.JSON;

/**
 * Manage site configs to disable some.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SitePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TreeViewer treeViewer;

	private List<SiteConfig> siteConfigList;

	private Map<MovieChannel, Boolean> oldAutoRefreshListStates = new HashMap<>();

	private Map<MovieChannel, Boolean> oldAutoParseDetailStates = new HashMap<>();

	/**
	 *
	 */
	public SitePreferencePage() {
		// Default constructor
	}

	/**
	 * @param title
	 */
	public SitePreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public SitePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Control createContents(Composite parent) {

		this.siteConfigList = SiteConfigManager.getInstance().getAllSiteConfigs();

		backup();

		this.treeViewer = new TreeViewer(parent, SWT.FLAT | SWT.FULL_SELECTION);
		this.treeViewer.setContentProvider(new SiteContentProvider());

		TreeViewerColumn nameViewerColumn = new TreeViewerColumn(this.treeViewer, SWT.NONE);
		nameViewerColumn.getColumn().setWidth(240);
		nameViewerColumn.getColumn().setText(" "); //$NON-NLS-1$
		nameViewerColumn.setLabelProvider(new SiteLabelProvider(null));

		TreeViewerColumn autoRefreshListViewerColumn = new TreeViewerColumn(this.treeViewer, SWT.NONE);
		autoRefreshListViewerColumn.getColumn().setWidth(160);
		autoRefreshListViewerColumn.getColumn().setText(Messages.SitePreferencePage_AutoRefreshList);
		autoRefreshListViewerColumn.setLabelProvider(new SiteLabelProvider(MovieChannel.PropertyAutoRefreshList));

		TreeViewerColumn autoParseDetailViewerColumn = new TreeViewerColumn(this.treeViewer, SWT.NONE);
		autoParseDetailViewerColumn.getColumn().setWidth(160);
		autoParseDetailViewerColumn.getColumn().setText(Messages.SitePreferencePage_AutoRefreshDetail);
		autoParseDetailViewerColumn.setLabelProvider(new SiteLabelProvider(MovieChannel.PropertyAutoRefreshDetail));

		Tree tree = this.treeViewer.getTree();

		CellEditor[] editors = new CellEditor[3];
		editors[1] = new CheckboxCellEditor(tree);
		editors[2] = new CheckboxCellEditor(tree);
		this.treeViewer.setColumnProperties(new String[] { "", MovieChannel.PropertyAutoRefreshList, MovieChannel.PropertyAutoRefreshDetail }); //$NON-NLS-1$
		this.treeViewer.setCellEditors(editors);

		this.treeViewer.setCellModifier(new ICellModifier() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public boolean canModify(Object element, String property) {
				if (element instanceof MovieChannel) {
					return StringUtils.isNotEmpty(property);
				}

				return false;
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public Object getValue(Object element, String property) {
				if (element instanceof MovieChannel) {

					MovieChannel movieChannel = (MovieChannel) element;

					if (StringUtils.equals(property, MovieChannel.PropertyAutoRefreshList)) {
						return movieChannel.isAutoRefreshList();
					}

					if (StringUtils.equals(property, MovieChannel.PropertyAutoRefreshDetail)) {
						return movieChannel.isAutoRefreshDetail();
					}
				}
				return null;
			}

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void modify(Object element, String property, Object value) {
				MovieChannel movieChannel = null;

				if (element instanceof TreeItem) {
					element = ((TreeItem) element).getData();
				}

				if (element instanceof MovieChannel) {
					movieChannel = (MovieChannel) element;
				}

				if (null != movieChannel) {
					if (StringUtils.equals(property, MovieChannel.PropertyAutoRefreshList)) {
						movieChannel.setAutoRefreshList((Boolean) value);
					}

					if (StringUtils.equals(property, MovieChannel.PropertyAutoRefreshDetail)) {
						movieChannel.setAutoRefreshDetail((Boolean) value);
					}

					SitePreferencePage.this.treeViewer.update(movieChannel, null);
				}
			}
		});

		this.treeViewer.setInput(this.siteConfigList);

		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		return tree;
	}

	/**
	 * 
	 */
	private void backup() {
		for (SiteConfig siteConfig : this.siteConfigList) {
			List<MovieChannel> movieChannelList = siteConfig.getMovieChannelList();

			for (MovieChannel movieChannel : movieChannelList) {
				this.oldAutoRefreshListStates.put(movieChannel, movieChannel.isAutoRefreshList());
				this.oldAutoParseDetailStates.put(movieChannel, movieChannel.isAutoRefreshDetail());
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void performDefaults() {

		for (SiteConfig siteConfig : this.siteConfigList) {
			List<MovieChannel> movieChannelList = siteConfig.getMovieChannelList();

			for (MovieChannel movieChannel : movieChannelList) {

				movieChannel.setAutoRefreshDetail(this.oldAutoParseDetailStates.get(movieChannel));
				movieChannel.setAutoRefreshList(this.oldAutoRefreshListStates.get(movieChannel));
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean performOk() {

		for (SiteConfig siteConfig : this.siteConfigList) {
			try {
				saveSiteConfig(siteConfig);
			} catch (IOException e) {
				SwtUtil.showErrorDialog(this.getShell(), WorkbenchActivator.PLUGIN_ID, Messages.ErrorDialogTitle, e);
				return false;
			}
		}

		backup();

		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do
	}

	/**
	 * 
	 * @param siteConfig
	 * @throws IOException
	 */
	private void saveSiteConfig(SiteConfig siteConfig) throws IOException {
		File configFile = new File(siteConfig.getFolder().getAbsolutePath() + File.separator + "config.json"); //$NON-NLS-1$
		String configJson = JSON.toJSONString(siteConfig, true);
		FileUtils.write(configFile, configJson, StringUtil.CODEING_UTF8);
	}
}
