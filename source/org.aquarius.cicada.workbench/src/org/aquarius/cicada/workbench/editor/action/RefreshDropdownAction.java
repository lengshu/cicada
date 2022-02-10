/**
 * 
 */
package org.aquarius.cicada.workbench.editor.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.cicada.workbench.util.WorkbenchUtil;
import org.aquarius.ui.menu.AbstractMenuCreator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class RefreshDropdownAction extends Action {

	private List<Action> actionList;

	private RefreshSiteAction fefreshSiteAction;

	/**
	 * 
	 */
	public RefreshDropdownAction(String label) {
		super(label);

		setImageDescriptor(WorkbenchActivator.getImageDescriptor("/icons/refresh.png")); //$NON-NLS-1$

		this.fefreshSiteAction = new RefreshSiteAction(Messages.SiteEditorActionBarContributor_RefreshSite);

		this.setMenuCreator(new AbstractMenuCreator() {

			@Override
			protected void createEntries(Menu menu) {

				IEditorPart editor = WorkbenchUtil.getActiveEditor();

				if (!(editor instanceof SiteMultiPageEditor)) {
					return;
				}

				SiteMultiPageEditor siteEditor = (SiteMultiPageEditor) editor;
				Site site = siteEditor.getMovieSite();

				if (null == RefreshDropdownAction.this.actionList) {

					RefreshDropdownAction.this.actionList = new ArrayList<>();

					RefreshDropdownAction.this.actionList.add(RefreshDropdownAction.this.fefreshSiteAction);

					SiteConfig siteConfig = SiteConfigManager.getInstance().findSiteConfig(site.getSiteName());

					List<MovieChannel> movieChannels = siteConfig.getMovieChannelList();

					if (CollectionUtils.size(movieChannels) > 1) {
						for (MovieChannel movieChannel : movieChannels) {
							String label = Messages.RefreshDropdownAction_RefreshChannelLabel;
							String message = MessageFormat.format(label, movieChannel.getDisplayName());

							RefreshDropdownAction.this.actionList.add(new RefreshChannelAction(message, movieChannel));
						}
					}

				}

				for (Action action : RefreshDropdownAction.this.actionList) {
					ActionContributionItem item = new ActionContributionItem(action);
					item.fill(menu, -1);
				}

			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		this.fefreshSiteAction.run();
	}

}
