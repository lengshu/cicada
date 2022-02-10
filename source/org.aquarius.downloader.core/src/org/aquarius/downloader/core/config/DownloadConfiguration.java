/**
 *
 */
package org.aquarius.downloader.core.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.aquarius.downloader.core.SiteConcurrent;
import org.aquarius.log.LogUtil;
import org.aquarius.service.IPropertyStoreService;
import org.aquarius.service.config.AbstractStoreableConfiguration;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.collection.Entry;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * The configuration for download.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class DownloadConfiguration extends AbstractStoreableConfiguration {

	public static final int DoubleClickAction_StartOrPause = 0;

	public static final int DoubleClickAction_OpenUrl = 1;

	public static final int DoubleClickAction_OpenFile = 2;

	public static final int DoubleClickAction_OpenFolder = 3;

	public static final String Key_DefaultDownloadFolder = DownloadConfiguration.class.getName() + "Key.DefaultDownloadFolder";

	public static final String Key_FFmpegFile = DownloadConfiguration.class.getName() + "Key.FfmpegFile";

	public static final String Key_ThreadCountPerTask = DownloadConfiguration.class.getName() + "Key.ThreadCountPerTask";

	public static final String Key_ConcurrentDownloadCount = DownloadConfiguration.class.getName() + "Key.ConcurrentDownloadCount";

	public static final String Key_CacheSize = DownloadConfiguration.class.getName() + "Key.CacheSize";

	public static final String Key_SegmentSize = DownloadConfiguration.class.getName() + "Key.SegmentSize";

	public static final String Key_CheckDiskLeftSpace = DownloadConfiguration.class.getName() + "Key.CheckDiskLeftSpace";

	public static final String Key_DiskLeftSpace = DownloadConfiguration.class.getName() + "Key.DiskLeftSpace";

	public static final String Key_DuplicatedStrategy = DownloadConfiguration.class.getName() + "Key.DuplicatedStrategy";

	public static final String Key_RetryCount = DownloadConfiguration.class.getName() + "Key.RetryCount";

	public static final String Key_AutoSavePeriod = DownloadConfiguration.class.getName() + "Key.AutoSavePeriod";

	public static final String Key_LowerSizeLimit = DownloadConfiguration.class.getName() + "Key.LowerSizeLimit";

	public static final String Key_MergeHls = DownloadConfiguration.class.getName() + "Key.MergeHls";

	public static final String Key_DeleteMergedHls = DownloadConfiguration.class.getName() + "Key.DeleteMergeHls";

	public static final String Key_DeleteToTrash = DownloadConfiguration.class.getName() + "Key.DeleteToTrash";

	public static final String Key_ConfirmDelete = DownloadConfiguration.class.getName() + "Key.ConfirmDelete";

	public static final String Key_FfmpegMergeCommand = DownloadConfiguration.class.getName() + "Key.FfmpegMergeCommand";

	public static final String Key_FfmpegDownloadCommand = DownloadConfiguration.class.getName() + "Key.FfmpegDownloadCommand";

	public static final String Key_ClearCommand = DownloadConfiguration.class.getName() + "Key.ClearCommand";

	public static final String Key_AutoAddSiteConstraint = DownloadConfiguration.class.getName() + "Key.Constraint";

	public static final String Key_SiteConcurrent = DownloadConfiguration.class.getName() + "Key.SiteConcurrent";

	public static final String Key_DoubleClickAction = DownloadConfiguration.class.getName() + "Key.DoubleClickAction";

	public static final String Key_AutoStartDownloading = DownloadConfiguration.class.getName() + "Key.AutoStartDownloading";

	public static final int DuplicatedOverWriteExistFile = 1;

	public static final int DuplicatedAutoRenameFile = 2;

	public static final int DuplicatedAutoBackupFile = 4;

	public static final int MaxThreadCountPerTask = 5;

	public static final int MaxDownloadCount = 10;

	public static final int MaxRetryCount = 50;

	public static final int MaxCacheSize = 16;

	public static final int MaxSegmentSize = 400;

	public static final int MaxDiskLeftSpace = 8;

	public static final int MaxAutoSavePeriod = 300;

	public static final int MaxLowerSizeLimit = 10;

	private List<String> pixels = new ArrayList<>();

	private List<SiteConcurrent> siteConcurrentList = new ArrayList<>();

	private Logger logger = LogUtil.getLogger(getClass());

	/**
	 * @param storeService
	 */
	public DownloadConfiguration(IPropertyStoreService storeService) {
		super(storeService);

	}

	/**
	 * rebuild SiteConcurrent model.<BR>
	 */
	public void rebuildSiteConcurrent() {
		synchronized (this) {
			try {
				String siteConcurrentString = this.getSiteConcurrent();

				if (StringUtils.isNotBlank(siteConcurrentString)) {

					List<Entry> list = JSON.parseArray(siteConcurrentString, Entry.class);
					List<SiteConcurrent> newList = new ArrayList<>();

					for (Entry<String, Integer> entry : list) {
						for (SiteConcurrent siteConcurrent : this.siteConcurrentList) {
							if (StringUtils.equalsIgnoreCase(siteConcurrent.getName(), entry.getKey())) {
								siteConcurrent.setMaxConcurrentCount(entry.getValue());
								continue;
							}
						}

						SiteConcurrent siteConcurrent = new SiteConcurrent(entry.getKey(), entry.getValue());
						newList.add(siteConcurrent);
					}

					this.siteConcurrentList.addAll(newList);
				}

			} catch (Exception e) {
				this.logger.error("rebuildSiteConcurrent", e);
			}
		}
	}

	/**
	 * Add a site concurrent.<BR>
	 * 
	 * @param siteConcurrent
	 */
	public void addSiteConcurrent(SiteConcurrent siteConcurrent) {
		if (null == siteConcurrent) {
			return;
		}

		for (SiteConcurrent oldSiteConcurrent : this.siteConcurrentList) {
			if (StringUtils.equalsIgnoreCase(oldSiteConcurrent.getName(), siteConcurrent.getName())) {
				oldSiteConcurrent.setMaxConcurrentCount(siteConcurrent.getMaxConcurrentCount());
				saveSiteConcurrents();
				return;
			}
		}

		this.siteConcurrentList.add(siteConcurrent);
		saveSiteConcurrents();
	}

	private void saveSiteConcurrents() {
		String json = toJSON(this.siteConcurrentList);
		this.doSetSiteConcurrent(json);
	}

	/**
	 * 
	 * @return
	 */
	public int getDoubleClickAction() {
		return this.getStoreService().getInt(Key_DoubleClickAction);
	}

	/**
	 * @return
	 */
	private static String toJSON(List<SiteConcurrent> siteConcurrentList) {
		List<Entry> list = new ArrayList<>();

		for (SiteConcurrent siteConcurrent : siteConcurrentList) {

			Entry entry = new Entry();

			entry.setKey(siteConcurrent.getName());
			entry.setValue(siteConcurrent.getMaxConcurrentCount());

			list.add(entry);
		}

		String json = JSON.toJSONString(list);
		return json;
	}

	/**
	 * Find a suitable site concurrent model.<BR>
	 *
	 * @param urlString
	 * @return
	 */
	public SiteConcurrent findSiteConcurrent(String urlString) {
		synchronized (this) {

			for (SiteConcurrent siteConcurrent : this.siteConcurrentList) {
				if (siteConcurrent.isAcceptable(urlString)) {
					return siteConcurrent;
				}
			}
		}

		return null;
	}

	/**
	 * @return the siteConcurrent
	 */
	public String getSiteConcurrent() {
		return this.getStoreService().getString(Key_SiteConcurrent);
	}

	/**
	 * @param siteConcurrentString the siteConcurrent to set
	 */
	public void setSiteConcurrent(String siteConcurrentString) {
		this.doSetSiteConcurrent(siteConcurrentString);
		this.rebuildSiteConcurrent();
	}

	/**
	 * @param siteConcurrentString the siteConcurrent to set
	 */
	public void doSetSiteConcurrent(String siteConcurrentString) {
		this.getStoreService().setValue(Key_SiteConcurrent, siteConcurrentString);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void doLoadDefaults(IPropertyStoreService storeService) {

		String ffmepgDownloadCommand = generateFfmpegDownloadCommand("ffmpeg");
		String ffmpegMergeCommand = generateFfmpegMergeCommand("ffmpeg");

		storeService.setDefault(Key_DuplicatedStrategy, DuplicatedOverWriteExistFile);
		storeService.setDefault(Key_ThreadCountPerTask, 2);
		storeService.setDefault(Key_ConcurrentDownloadCount, 3);

		storeService.setDefault(Key_CacheSize, 16);
		storeService.setDefault(Key_SegmentSize, 200);

		storeService.setDefault(Key_AutoSavePeriod, 60);
		storeService.setDefault(Key_RetryCount, 5);

		storeService.setDefault(Key_DiskLeftSpace, 2);
		storeService.setDefault(Key_LowerSizeLimit, 2);

		storeService.setDefault(Key_MergeHls, true);

		storeService.setDefault(Key_DeleteToTrash, false);
		storeService.setDefault(Key_AutoStartDownloading, false);

		// storeService.setDefault(Key_AutoAddSiteConstraint, true);

		storeService.setDefault(Key_FfmpegDownloadCommand, ffmepgDownloadCommand);
		storeService.setDefault(Key_FfmpegMergeCommand, ffmpegMergeCommand);

		if (SystemUtils.IS_OS_WINDOWS) {
			storeService.setDefault(Key_ClearCommand, "rmdir /s/q \"{0}\"");
		} else {
			storeService.setDefault(Key_ClearCommand, "rm -rf \"{0}\"");
		}

		{
			List<SiteConcurrent> list = new ArrayList<>();

			list.add(new SiteConcurrent("mxdcontent.net", 1));
			list.add(new SiteConcurrent("mxdcontent.com", 1));

			list.add(new SiteConcurrent("vidoza.net", 1));
			list.add(new SiteConcurrent("vidoza.com", 1));

			list.add(new SiteConcurrent("doodstream.", 1));

			list.add(new SiteConcurrent("dood.", 1));

			String json = toJSON(list);
			storeService.setDefault(Key_SiteConcurrent, json);
		}

		//
		// mxdcontent

	}

	/**
	 * Set ffmpeg file.<BR>
	 * 
	 * @param ffmpegFile
	 */
	public void setFfmepgFile(String ffmpegFile) {

		String ffmepgDownloadCommand = generateFfmpegDownloadCommand(ffmpegFile);
		String ffmpegMergeCommand = generateFfmpegMergeCommand(ffmpegFile);

		this.getStoreService().setValue(Key_FfmpegDownloadCommand, ffmepgDownloadCommand);
		this.getStoreService().setValue(Key_FfmpegMergeCommand, ffmpegMergeCommand);

		this.getStoreService().setValue(Key_FFmpegFile, ffmpegFile);

	}

	/**
	 * generate ffmpeg merge command with specified ffmpeg file name.
	 * 
	 * @param ffmpegFile
	 * @return
	 */
	public static String generateFfmpegMergeCommand(String ffmpegFile) {
		return ffmpegFile + " -f concat -safe 0 -i \"{0}\" -c copy \"{1}\"";
	}

	/**
	 * generate ffmpeg download command with specified ffmpeg file name.
	 * 
	 * @param ffmpegFile
	 * @return
	 */
	public static String generateFfmpegDownloadCommand(String ffmpegFile) {
		return ffmpegFile + " -i \"{0}\" \"{1}\"";
	}

	/**
	 * Get ffmpeg file.<BR>
	 * 
	 * @return
	 */
	public String getFfmepgFile() {
		return this.getStoreService().getString(Key_FFmpegFile);
	}

	/**
	 * @return the duplicatedStrategy
	 */
	public int getDuplicatedStrategy() {
		return this.getStoreService().getInt(Key_DuplicatedStrategy);
	}

	/**
	 * @param duplicatedStrategy the duplicatedStrategy to set
	 */
	public void setDuplicatedStrategy(int duplicatedStrategy) {

		if (ArrayUtils.contains(new int[] { DuplicatedOverWriteExistFile, DuplicatedAutoRenameFile, DuplicatedAutoBackupFile }, duplicatedStrategy)) {
			this.getStoreService().setValue(Key_DuplicatedStrategy, duplicatedStrategy);
		}
	}

	/**
	 * @return the concurrentDownloadCount
	 */
	public int getConcurrentDownloadCount() {
		return this.getStoreService().getInt(Key_ConcurrentDownloadCount);
	}

	/**
	 * @param concurrentDownloadCount the concurrentDownloadCount to set
	 */
	public void setConcurrentDownloadCount(int concurrentDownloadCount) {
		if ((concurrentDownloadCount > 0) && (concurrentDownloadCount <= MaxDownloadCount)) {
			this.getStoreService().setValue(Key_ConcurrentDownloadCount, concurrentDownloadCount);
		}
	}

	/**
	 * @return the cacheSize
	 */
	public int getCacheSize() {
		return getCacheSizeInM() * SystemUtil.DiskSizeInM;
	}

	/**
	 * @return the cacheSize
	 */
	public int getCacheSizeInM() {
		return this.getStoreService().getInt(Key_CacheSize);
	}

	/**
	 * @param cacheSize the cacheSize to set
	 */
	public void setCacheSizeInM(int cacheSize) {

		if ((cacheSize > 0) && (cacheSize <= MaxCacheSize)) {
			this.getStoreService().setValue(Key_CacheSize, cacheSize);
		}
	}

	/**
	 * @return the segmentSize
	 */
	public long getSegmentSize() {
		long value = this.getSegmentSizeInM();
		return value * SystemUtil.DiskSizeInM;
	}

	/**
	 * @return the segmentSize
	 */
	public int getSegmentSizeInM() {
		return this.getStoreService().getInt(Key_SegmentSize);
	}

	/**
	 * @param segmentSize the segmentSize to set
	 */
	public void setSegmentSizeInM(int segmentSize) {
		if ((segmentSize > 0) && (segmentSize <= MaxSegmentSize)) {
			this.getStoreService().setValue(Key_SegmentSize, segmentSize);
		}
	}

	/**
	 * @return the autoSavePeriod
	 */
	public int getAutoSavePeriod() {
		return this.getStoreService().getInt(Key_AutoSavePeriod);
	}

	/**
	 * @param autoSavePeriod the autoSavePeriod to set
	 */
	public void setAutoSavePeriod(int autoSavePeriod) {
		if ((autoSavePeriod > 0) && (autoSavePeriod <= MaxAutoSavePeriod)) {
			this.getStoreService().setValue(Key_AutoSavePeriod, autoSavePeriod);
		}
	}

	/**
	 * @return the ffmepg merge command
	 */
	public String getFfmpegMergeCommand() {
		return this.getStoreService().getString(Key_FfmpegMergeCommand);
	}

	/**
	 * @param ffmepgMergeCommand the ffmepg merge Command to set
	 */
	public void setFfmpegMergeCommand(String ffmepgMergeCommand) {
		this.getStoreService().setValue(Key_FfmpegMergeCommand, ffmepgMergeCommand);
	}

	/**
	 * @return the ffmepg download command
	 */
	public String getFfmpegDownloadCommand() {
		return this.getStoreService().getString(Key_FfmpegDownloadCommand);
	}

	/**
	 * @param ffmepgDownloadCommand the ffmepg download Command to set
	 */
	public void setFfmpegDownloadCommand(String ffmepgDownloadCommand) {
		this.getStoreService().setValue(Key_FfmpegDownloadCommand, ffmepgDownloadCommand);
	}

	/**
	 * @return the clear command
	 */
	public String getClearCommand() {
		return this.getStoreService().getString(Key_ClearCommand);
	}

	/**
	 * @param clearCommand the clearCommand to set
	 */
	public void setClearCommand(String clearCommand) {
		this.getStoreService().setValue(Key_ClearCommand, clearCommand);
	}

	/**
	 * @return the defaultDownloadFolder
	 */
	public String getDefaultDownloadFolder() {
		return this.getStoreService().getString(Key_DefaultDownloadFolder);
	}

	/**
	 * @param defaultDownloadFolder the defaultDownloadFolder to set
	 */
	public void setDefaultDownloadFolder(String defaultDownloadFolder) {
		this.getStoreService().setValue(Key_DefaultDownloadFolder, defaultDownloadFolder);
	}

	/**
	 * @return the threadCountPerTask
	 */
	public int getThreadCountPerTask() {
		return this.getStoreService().getInt(Key_ThreadCountPerTask);
	}

	/**
	 * @param threadCountPerTask the threadCountPerTask to set
	 */
	public void setThreadCountPerTask(int threadCountPerTask) {
		if ((threadCountPerTask > 0) && (threadCountPerTask <= MaxThreadCountPerTask)) {
			this.getStoreService().setValue(Key_ThreadCountPerTask, threadCountPerTask);
		}
	}

	/**
	 * @return the diskLeftSpace
	 */
	public long getDiskLeftSpace() {
		long value = this.getDiskLeftSpaceInG();
		return value * SystemUtil.DiskSizeInG;
	}

	/**
	 * @return the diskLeftSpace
	 */
	public int getDiskLeftSpaceInG() {
		return this.getStoreService().getInt(Key_DiskLeftSpace);
	}

	/**
	 * @param diskLeftSpace the diskLeftSpace to set
	 */
	public void setDiskLeftSpaceInG(int diskLeftSpace) {
		if ((diskLeftSpace >= 0) && (diskLeftSpace <= MaxDiskLeftSpace)) {
			this.getStoreService().setValue(Key_DiskLeftSpace, diskLeftSpace);
		}
	}

	/**
	 * @return the autoAddSiteConstraint
	 */
	public boolean isAutoAddSiteConstraint() {
		return this.getStoreService().getBoolean(Key_AutoAddSiteConstraint);
	}

	/**
	 * @return the autoAddSiteConstraint
	 */
	public boolean isAutoStartDownloading() {
		return this.getStoreService().getBoolean(Key_AutoStartDownloading);
	}

	/**
	 * @return the diskLeftSpace
	 */
	public boolean isCheckDiskLeftSpace() {
		return this.getStoreService().getBoolean(Key_CheckDiskLeftSpace);
	}

	/**
	 * @param diskLeftSpace the diskLeftSpace to set
	 */
	public void setCheckDiskLeftSpace(boolean checkDiskLeftSpace) {
		this.getStoreService().setValue(Key_CheckDiskLeftSpace, checkDiskLeftSpace);
	}

	/**
	 * @return the deleteToTrash
	 */
	public boolean isDeleteToTrash() {
		return this.getStoreService().getBoolean(Key_DeleteToTrash);
	}

	/**
	 * @param deleteToTrash the deleteToTrash to set
	 */
	public void setDeleteToTrash(boolean deleteToTrash) {
		this.getStoreService().setValue(Key_DeleteToTrash, deleteToTrash);
	}

	/**
	 * @return the pixels
	 */
	public List<String> getPixels() {
		return this.pixels;
	}

	/**
	 * @param pixels the pixels to set
	 */
	public void setPixels(List<String> pixels) {
		this.pixels = pixels;
	}

	/**
	 * @return the mergeHls
	 */
	public boolean isMergeHls() {
		return this.getStoreService().getBoolean(Key_MergeHls);
	}

	/**
	 * @param mergeHls the mergeHls to set
	 */
	public void setMergeHls(boolean mergeHls) {
		this.getStoreService().setValue(Key_MergeHls, mergeHls);
	}

	/**
	 * @return the deleteMergedHls
	 */
	public boolean isDeleteMergedHls() {
		return this.getStoreService().getBoolean(Key_DeleteMergedHls);
	}

	/**
	 * @param deleteMergedHls the deleteMergedHls to set
	 */
	public void setDeleteMergedHls(boolean deleteMergedHls) {
		this.getStoreService().setValue(Key_DeleteMergedHls, deleteMergedHls);
	}

	/**
	 * @return the lowerSizeLimit
	 */
	public int getLowerSizeLimit() {
		return this.getStoreService().getInt(Key_LowerSizeLimit);
	}

	/**
	 * @param lowerSizeLimit the lowerSizeLimit to set
	 */
	public void setLowerSizeLimit(int lowerSizeLimit) {
		if ((lowerSizeLimit >= 0) && (lowerSizeLimit <= MaxLowerSizeLimit)) {
			this.getStoreService().setValue(Key_LowerSizeLimit, lowerSizeLimit);
		}
	}

}
