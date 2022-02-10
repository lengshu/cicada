/**
 *
 */
package org.aquarius.cicada.workbench.job;

import java.text.MessageFormat;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.helper.MovieParserHelper;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.monitor.ProcessMonitorProxy;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.job.OrderedSchedulingRule;
import org.aquarius.ui.util.TooltipUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;

/**
 * Refresh a movie channel.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class RefreshChannelJob extends AbstractCancelableJob {

	private MovieChannel movieChannel;

	private String siteName;

	private static Logger logger = LogUtil.getLogger(RefreshChannelJob.class);

	/**
	 * @param name
	 * @param movieChannel
	 */
	public RefreshChannelJob(String name, MovieChannel movieChannel) {
		super(name);
		this.movieChannel = movieChannel;
		this.siteName = this.movieChannel.getSiteName();

		this.setRule(new OrderedSchedulingRule(getFamilyName(this.siteName)));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);

		Site site = RuntimeManager.getInstance().loadSite(this.movieChannel.getSiteName());

		try {
			site.setRefreshing(this, true);
			doRun(site, monitor);
		} finally {
			site.setRefreshing(this, false);
		}

		monitor.done();

		return Status.OK_STATUS;
	}

	/**
	 * @param site
	 */
	private void doRun(Site site, IProgressMonitor monitor) {

		int oldSize = site.getMovieSize();

		IProcessMonitor processMonitor = new ProcessMonitorProxy(monitor);

		try {
			MovieParserHelper.parseChannel(site, this.movieChannel, processMonitor);
		} catch (Exception e) {

			if (monitor.isCanceled()) {
				return;
			}

			logger.error("refresh channel", e); //$NON-NLS-1$
		}

		int sizeInterval = site.getMovieSize() - oldSize;

		String title = MessageFormat.format(Messages.RefreshChannelJob_RefreshChannelJobTitle, this.siteName, this.movieChannel.getDisplayName());
		String message = MessageFormat.format(Messages.RefreshChannelJob_RefreshChannelSize, sizeInterval);

		TooltipUtil.showInfoTip(title, message);

		RefreshSiteJob.refreshEditorForSite(site, false);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean belongsTo(Object family) {
		if (family instanceof String) {
			String familyName = (String) family;
			return getFamilyName(this.siteName).equalsIgnoreCase(familyName);
		} else {
			return false;
		}
	}

}
