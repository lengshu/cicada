/**
 * 
 */
package org.aquarius.cicada.workbench.job;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.job.AbstractCancelableJob;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.update.UpdateConfig;
import org.aquarius.util.update.UpdateExecutor;
import org.aquarius.util.update.UpdateRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Version;

import com.alibaba.fastjson.JSON;

/**
 * The job is to check whether update the config or not.
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class UpdateConfigJob extends AbstractCancelableJob {

	public static final String KeyUpdateInterval = UpdateConfigJob.class.getName() + ".key.UpdateInterval"; //$NON-NLS-1$

	public static final String KeyLastUpdate = UpdateConfigJob.class.getName() + ".key.LastUpdate"; //$NON-NLS-1$

	public static final String KeyLastVersion = UpdateConfigJob.class.getName() + ".key.LastVersion"; //$NON-NLS-1$

	public static final String KeyUpdateUrl = UpdateConfigJob.class.getName() + ".key.UpdateUrl"; //$NON-NLS-1$

	public static final String KeyChangeLog = UpdateConfigJob.class.getName() + ".key.ChangeLog"; //$NON-NLS-1$

	private static final String DefaultUpdateUrlTemplate = "https://github.com/aquariusStudio/cicada/tree/main/update/{0}/update.json"; //$NON-NLS-1$

	private boolean force = false;

	/**
	 * 
	 * @param name
	 * @param force
	 */
	public UpdateConfigJob(String name, boolean force) {
		super(name);

		this.force = force;
	}

	/**
	 * 
	 * @param name
	 */
	public UpdateConfigJob(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			return doRun();
		} catch (Exception e) {
			return new Status(IStatus.ERROR, WorkbenchActivator.PLUGIN_ID, 1, e.getMessage(), e);
		} finally {
			monitor.done();
		}

	}

	/**
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private IStatus doRun() throws IOException {
		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

		long lastUpdate = store.getLong(KeyLastUpdate);
		int updateInterval = store.getInt(KeyUpdateInterval);

		if ((updateInterval <= 0) && (!this.force)) {
			return Status.CANCEL_STATUS;
		}

		long nextTime = lastUpdate + (updateInterval * SystemUtil.TimeDay);
		boolean checkUpdate = nextTime < (new Date().getTime());

		if ((!checkUpdate) && (!this.force)) {
			return Status.CANCEL_STATUS;
		}

		String oldVersionString = store.getString(KeyLastVersion);

		String updateUrl = store.getString(KeyUpdateUrl);

		if (StringUtils.isEmpty(updateUrl)) {
			updateUrl = getDefaultUpdateUrl();
		}
		String content = null;
		try {
			content = IOUtils.toString(new URL(updateUrl));
		} catch (Exception e) {
			return Status.OK_STATUS;
		}

		UpdateRoot updateRoot = JSON.parseObject(content, UpdateRoot.class);

		if (null == updateRoot || (!updateRoot.checkValid())) {
			return Status.CANCEL_STATUS;
		}

		Version oldVersion = null;

		if (StringUtils.isNotEmpty(oldVersionString)) {

			try {
				oldVersion = new Version(oldVersionString);
			} catch (Exception e) {
				// Nothing to do
			}
		}

		if (null == oldVersion) {
			oldVersion = WorkbenchActivator.getDefault().getBundle().getVersion();
		}

		Version newVersion = new Version(updateRoot.getVersion());

		if (newVersion.compareTo(oldVersion) <= 0) {
			return Status.CANCEL_STATUS;
		}

		UpdateConfig updateConfig = new UpdateConfig();

		updateConfig.setCurrentVersion(oldVersionString);
		updateConfig.setOverwrite(true);
		updateConfig.setRemoteUrl(updateUrl);

		updateConfig.setLocalPath(Starter.getInstance().getWorkingFolder());

		new UpdateExecutor(updateConfig, updateRoot).execute();

		store.setValue(KeyLastVersion, updateRoot.getVersion());
		store.setValue(KeyUpdateUrl, updateRoot.getNextUrl());
		store.setValue(KeyChangeLog, updateRoot.getChangeLog());

		store.setValue(KeyLastUpdate, new Date().getTime());

		TooltipUtil.showInfoTip(Messages.SuccessDialogTitle, Messages.UpdateConfigJob_UpdateSuccessMessage);

		return Status.OK_STATUS;

	}

	/**
	 * 
	 * @return
	 */
	public static final String getDefaultUpdateUrl() {
		Version bundleVersion = WorkbenchActivator.getDefault().getBundle().getVersion();

		return MessageFormat.format(DefaultUpdateUrlTemplate, bundleVersion.toString());

	}

}
