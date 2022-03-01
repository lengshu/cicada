/**
 *
 */
package org.aquarius.downloader.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.config.DownloadConfiguration;
import org.aquarius.downloader.core.helper.DownloadHelper;
import org.aquarius.downloader.core.impl.CompositeProgressListener;
import org.aquarius.downloader.core.impl.hls.HlsSegmentDownloaderFactory;
import org.aquarius.downloader.core.impl.http.HttpSegmentDownloaderFactory;
import org.aquarius.downloader.core.spi.AbstractDownloadTypeChecker;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloader;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloaderFactory;
import org.aquarius.downloader.core.spi.AbstractTaskStoreService;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.nls.InternalNlsResource;
import org.aquarius.util.nls.NlsResource;

/**
 * Download manager to control all.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DownloadManager extends CompositeProgressListener implements Closeable {

	public static final String URL = "_URL_";

	private static final int MaxRetryCount = 200;

	private static final int MaxDeleteRetryCount = 10;

	private static final int DefaultMaxThreadCount = 120;

	private static final int CheckMaxThreadCount = 40;

	// Seconds
	// private static final int DefaultWaitTime = 1;

	private static final DownloadManager instance = new DownloadManager();

	private int concurrentCount;

	private DownloadConfiguration downloadConfiguration;

	private AbstractTaskStoreService taskStoreService;

	private List<DownloadTask> downloadTaskList = Collections.synchronizedList(new ArrayList<DownloadTask>());

	private ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, DefaultMaxThreadCount, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	private final static long DefaultInternal = 3 * SystemUtil.TimeSecond;

	// private Logger logger = LogUtil.getLogger(this.getClass());

	private Set<AbstractSegmentDownloaderFactory> downloaderFactories = new TreeSet<AbstractSegmentDownloaderFactory>();

	private boolean started = false;

	private Timer daemonTimer;

	private NlsResource nlsResource;

	private AbstractDownloadTypeChecker downloadTypeChecker;

	/**
	 * @return the instance
	 */
	public static DownloadManager getInstance() {
		return instance;
	}

	/**
	 *
	 */
	private DownloadManager() {
		super();

		ResourceBundle resourceBundle = ResourceBundle.getBundle("org.aquarius.downloader.core.nls/lang");
		this.nlsResource = new InternalNlsResource(resourceBundle);

		this.registerDownloadFactory(new HttpSegmentDownloaderFactory());
		this.registerDownloadFactory(new HlsSegmentDownloaderFactory());
	}

	/**
	 * @return the downloadTypeChecker
	 */
	public AbstractDownloadTypeChecker getDownloadTypeChecker() {
		return this.downloadTypeChecker;
	}

	/**
	 * @param downloadTypeChecker the downloadTypeChecker to set
	 */
	public void setDownloadTypeChecker(AbstractDownloadTypeChecker downloadTypeChecker) {
		this.downloadTypeChecker = downloadTypeChecker;
	}

	/**
	 * @return the nlsResource
	 */
	public NlsResource getNlsResource() {
		return this.nlsResource;
	}

	/**
	 * Register a new downloader factory.<BR>
	 *
	 * @param factory
	 */
	public void registerDownloadFactory(AbstractSegmentDownloaderFactory factory) {
		AssertUtil.assertNotNull(factory, "The downloader factory should not be null.");

		this.downloaderFactories.add(factory);
	}

	/**
	 * Set the download configuration
	 *
	 * @param downloadConfiguration the downloadConfiguration to set
	 */
	public void setDownloadConfiguration(DownloadConfiguration downloadConfiguration) {
		this.downloadConfiguration = downloadConfiguration;
	}

	/**
	 * @return the downloadConfiguration
	 */
	public DownloadConfiguration getConfiguration() {
		return this.downloadConfiguration;
	}

	/**
	 * Set the task store service.<BR>
	 * All tasks will be stored in period.<BR>
	 *
	 * @param taskStoreService the taskStoreService to set
	 */
	public void setTaskStoreService(AbstractTaskStoreService taskStoreService) {
		this.taskStoreService = taskStoreService;
	}

	/**
	 * @return the taskStoreService
	 */
	public AbstractTaskStoreService getTaskStoreService() {
		return this.taskStoreService;
	}

	/**
	 *
	 * @param downloadTask
	 * @return
	 */
	private AbstractSegmentDownloaderFactory findDownloaderFactory(DownloadTask downloadTask) {
		for (AbstractSegmentDownloaderFactory downloaderFactory : this.downloaderFactories) {
			if (StringUtils.equalsIgnoreCase(downloaderFactory.getType(), downloadTask.getType())) {
				return downloaderFactory;
			}
		}

		if (null != this.downloadTypeChecker) {

			try {
				String type = this.downloadTypeChecker.getDownloadType(downloadTask.getDownloadUrl());

				for (AbstractSegmentDownloaderFactory downloaderFactory : this.downloaderFactories) {
					if (StringUtils.equalsIgnoreCase(downloaderFactory.getType(), type)) {
						return downloaderFactory;
					}
				}

			} catch (Exception e) {
				// Nothing to do
			}

		}

		for (AbstractSegmentDownloaderFactory downloaderFactory : this.downloaderFactories) {
			if (downloaderFactory.isAcceptable(downloadTask)) {
				return downloaderFactory;
			}
		}

		return null;
	}

	/**
	 * Call this method to check whether this task is valid or not.
	 *
	 * @param downloadTask
	 * @return
	 */
	public boolean check(DownloadTask downloadTask) {
		AbstractSegmentDownloaderFactory downloaderFactory = this.findDownloaderFactory(downloadTask);

		if (null == downloaderFactory) {
			return false;
		} else {

			return downloaderFactory.check(downloadTask);
		}
	}

	/**
	 * Remove all tasks.<BR
	 */
	public void clearDownloadTasks() {
		this.stopTasks();
		this.downloadTaskList.clear();
		this.saveTasks(true);
	}

	/**
	 *
	 * @param urlString
	 * @return
	 */
	public synchronized boolean increaseCount(DownloadTask downloadTask) {

		if (downloadTask.isThreadFull()) {
			return false;
		}

		String urlString = downloadTask.getDownloadUrl();

		SiteConcurrent siteConcurrent = this.downloadConfiguration.findSiteConcurrent(urlString);

		if (null == siteConcurrent) {
			this.concurrentCount++;
			return true;
		} else {
			if (siteConcurrent.isFull()) {
				return false;
			} else {
				siteConcurrent.increaseConcurrentCount();
				this.concurrentCount++;
				return true;
			}
		}
	}

	/**
	 *
	 * @param urlString
	 */
	public synchronized void decreaseCount(String urlString) {
		SiteConcurrent siteConcurrent = this.downloadConfiguration.findSiteConcurrent(urlString);
		this.concurrentCount--;
		if (null != siteConcurrent) {
			siteConcurrent.decreaseConcurrentCount();
		}
	}

	/**
	 * @return the concurrentCount
	 */
	public int getConcurrentCount() {
		return this.concurrentCount;
	}

	/**
	 * Add tasks to download.<BR>
	 * If the task state is init or resume,running,start,the task will be started
	 * automatically.<BR>
	 * If you don't want the task to be started automatically ,please set the state
	 * to pause before adding a task.<BR>
	 *
	 * @see DownloadTask#StateWaiting
	 * @see DownloadTask#StateResume
	 * @see DownloadTask#StateRunning
	 * @see DownloadTask#StateStart
	 * @see DownloadTask#StateWaiting
	 *
	 * @param downloadTasks
	 */
	public void addDownloadTask(DownloadTask... downloadTasks) {

		List<DownloadTask> resultList = new ArrayList<DownloadTask>();

		for (DownloadTask downloadTask : downloadTasks) {
			AssertUtil.assertNotNull(downloadTask, "The download task should not be null.");

			if (!this.downloadTaskList.contains(downloadTask)) {
				this.downloadTaskList.add(downloadTask);

				resultList.add(downloadTask);
			}
		}

		if (!resultList.isEmpty()) {
			DownloadTask[] tasks = resultList.toArray(new DownloadTask[resultList.size()]);
			onAdd(tasks);
		}
	}

	/**
	 * Resume a task if it is not finished.<BR>
	 *
	 * @param downloadTasks
	 */
	public void resumeDownloadTask(DownloadTask... downloadTasks) {

		List<DownloadTask> resultList = new ArrayList<DownloadTask>();

		for (DownloadTask downloadTask : downloadTasks) {
			AssertUtil.assertNotNull(downloadTask, "The download task should not be null.");

			if (this.downloadTaskList.contains(downloadTask)) {

				if (downloadTask.getState() == DownloadTask.StateFinish) {
					continue;
				}

				downloadTask.setState(DownloadTask.StateWaiting);
				downloadTask.reset();
				resultList.add(downloadTask);
			}
		}

		if (!resultList.isEmpty()) {
			DownloadTask[] tasks = resultList.toArray(new DownloadTask[resultList.size()]);
			onResume(tasks);
		}
	}

	/**
	 * Pause tasks.<BR>
	 *
	 * @param downloadTasks
	 */
	public void pauseDownloadTask(DownloadTask... downloadTasks) {

		List<DownloadTask> resultList = new ArrayList<DownloadTask>();

		for (DownloadTask downloadTask : downloadTasks) {
			if (this.downloadTaskList.contains(downloadTask) && (downloadTask.getState() != DownloadTask.StateFinish)) {
				downloadTask.setState(DownloadTask.StatePause);
				resultList.add(downloadTask);
			}
		}

		if (!resultList.isEmpty()) {
			DownloadTask[] tasks = resultList.toArray(new DownloadTask[resultList.size()]);
			onPause(tasks);
		}
	}

	/**
	 * Delete tasks .<BR>
	 *
	 * @param alsoDeleteFile if the parameter is <code>true</code>,all relevant
	 *                       files will be deleted.<BR>
	 * @param downloadTasks
	 */
	public void deleteDownloadTask(boolean alsoDeleteFile, DownloadTask... downloadTasks) {

		List<DownloadTask> resultList = new ArrayList<DownloadTask>();

		for (DownloadTask downloadTask : downloadTasks) {
			if (this.downloadTaskList.contains(downloadTask)) {
				downloadTask.setState(DownloadTask.StateDelete);

				resultList.add(downloadTask);
			}
		}

		if (!resultList.isEmpty()) {

			this.downloadTaskList.removeAll(resultList);
			DownloadTask[] tasks = resultList.toArray(new DownloadTask[resultList.size()]);

			if (alsoDeleteFile) {
				doDeleteResources(tasks);
			}

			onDelete(tasks);
		}
	}

	/**
	 * Use thread to delete resouces.<BR>
	 *
	 * @param tasks
	 */
	private void doDeleteResources(DownloadTask[] tasks) {

		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				for (DownloadTask downloadTask : tasks) {

					File file = downloadTask.getFile();
					if (!file.exists()) {
						continue;
					}

					try {
						FileUtils.forceDeleteOnExit(file);
					} catch (IOException e1) {
						// Nothing to do
					}

					SystemUtil.sleepQuietly(500);

					for (int i = 0; i <= MaxDeleteRetryCount; i++) {
						try {

							try {
								FileUtils.forceDeleteOnExit(file);
							} catch (IOException e1) {
								// Nothing to do
							}

							if (DownloadManager.this.downloadConfiguration.isDeleteToTrash() && SystemUtil.isSupportDeleteToTrash()) {
								java.awt.Desktop.getDesktop().moveToTrash(file);
							} else {
								FileUtils.forceDelete(file);
							}

							SystemUtil.sleepQuietly(200);

							if (!file.exists()) {
								break;
							}

						} catch (Exception e) {

							SystemUtil.sleepQuietly(300);

							if (i == (MaxDeleteRetryCount)) {
								DownloadManager.this.logger.info("delete directory " + file.getAbsolutePath(), e);
							}
						}
					}

				}
			}
		};

		this.executorService.submit(task);

	}

	/**
	 * Return all taskss.<BR>
	 *
	 * @return
	 */
	public List<DownloadTask> getAllTasks() {
		return new ArrayList<DownloadTask>(this.downloadTaskList);
	}

	/**
	 * Use category to find tasks<BR>
	 *
	 * @param category
	 * @see DownloadTask#CategoryVideo
	 * @see DownloadTask#CategoryMusic
	 * @see DownloadTask#CategorySoftware
	 * @see DownloadTask#CategoryBook
	 * @see DownloadTask#CategoryUnknown
	 *
	 * @return
	 */
	public List<DownloadTask> findTasksByCategory(int category) {
		return this.downloadTaskList.stream().filter(task -> category == task.getCategory()).collect(Collectors.toList());
	}

	/**
	 * Use a referer as condition to find tasks.<BR>
	 *
	 * @param refererUrl
	 * @return
	 */
	public List<DownloadTask> findTasksByReferUrl(String refererUrl) {
		return this.downloadTaskList.stream().filter(task -> StringUtils.equalsIgnoreCase(refererUrl, task.getRefererUrl())).collect(Collectors.toList());
	}

	/**
	 * Use a tag id as condition to find tasks.<BR>
	 *
	 * @param tagId
	 * @return
	 */
	public Optional<DownloadTask> findTaskByTagId(String tagId) {
		return this.downloadTaskList.stream().filter(task -> StringUtils.equalsIgnoreCase(tagId, task.getTagId())).findFirst();

	}

	/**
	 * Use state to find tasks<BR>
	 *
	 * @param state
	 * @return
	 */
	public List<DownloadTask> findTasksByState(int state) {
		return this.downloadTaskList.stream().filter(task -> task.getState() == state).collect(Collectors.toList());
	}

	/**
	 * Get how many running tasks .<BR>
	 *
	 * @return
	 */
	private int computeConcurrentRunningTasks() {
		int count = 0;

		for (DownloadTask downloadTask : this.downloadTaskList) {

			int state = downloadTask.getState();

			if (state == DownloadTask.StateRunning) {
				count++;
			}
		}

		return count;
	}

	/**
	 * The user should invoke this method to start.<BR>
	 * A timer will be created to download tasks.<BR>
	 *
	 * @param delay how many time later,this timer will be started.<BR>
	 */
	public synchronized void start(long delay) {

		AssertUtil.assertNotNull(this.taskStoreService, "Please set store service first.");
		AssertUtil.assertTrue(!this.started, "It is started already.");

		synchronized (this) {

			List<DownloadTask> taskList = this.taskStoreService.loadTasks();
			for (DownloadTask downloadTask : taskList) {
				updateDownloadTask(downloadTask);
			}

			this.downloadTaskList.addAll(taskList);

			this.daemonTimer = new Timer("save download tasks ");

			this.daemonTimer.schedule(new TimerTask() {

				private int saveCounter = 0;

				private boolean canceled = false;

				private int autoSavePeriod = DownloadManager.this.downloadConfiguration.getAutoSavePeriod();

				/**
				 * {@inheritDoc}}
				 */
				@Override
				public boolean cancel() {
					this.canceled = true;
					return super.cancel();
				}

				@Override
				public void run() {

					if (this.canceled) {
						return;
					}

					try {
						doRun();
					} catch (Exception e) {
						DownloadManager.this.logger.error("daemon download thread", e);
					}
				}

				/**
				 *
				 */
				private void doRun() {

					int concurrentTaskCount = computeConcurrentRunningTasks();
					int allowedConcurrentTaskCount = getConcurrentDownloadCount();

					int newThreadCount = allowedConcurrentTaskCount - concurrentTaskCount;
					if (newThreadCount > 0) {
						doStartDownloadTasks(newThreadCount);
					}

					this.saveCounter++;

					if (this.saveCounter > this.autoSavePeriod) {

						this.saveCounter = 0;
						saveTasks(false);
					}
				}

				/**
				 * @return
				 */
				private int getConcurrentDownloadCount() {

					try {
						return DownloadManager.this.downloadConfiguration.getConcurrentDownloadCount();
					} catch (Exception e) {
						return 0;
					}

				}
			}, delay, DefaultInternal);

		}
	}

	/**
	 * Save the tasks in specified way.
	 * 
	 * @param forceSave
	 */
	public void saveTasks(boolean forceSave) {
		DownloadManager.this.taskStoreService.saveTasks(DownloadManager.this.downloadTaskList, forceSave);
	}

	/**
	 *
	 *
	 * @param count the left available thread count
	 */
	protected void doStartDownloadTasks(int count) {

		if (this.executorService.getActiveCount() > CheckMaxThreadCount) {
			return;
		}

		count = Integer.min(count, this.downloadTaskList.size());
		// Recursive all the tasks to start unless the thread count exhausted.

		List<DownloadTask> unmodifiedList = new ArrayList<>(this.downloadTaskList);

		for (DownloadTask downloadTask : unmodifiedList) {

			if (count == 0) {
				return;
			}

			if (downloadTask.isThreadFull()) {
				continue;
			}

			try {
				if (this.doStartDownloadTask(downloadTask)) {
					count--;
				}
			} catch (Exception e) {
				this.logger.error("start task error ", e);
			}
		}
	}

	/**
	 * Start a task.<BR>
	 *
	 * @param downloadTask
	 * @return
	 */
	private boolean doStartDownloadTask(DownloadTask downloadTask) {

		if (downloadTask.isAllowDownload()) {

			File folder = new File(downloadTask.getFolder());
			if (!DownloadHelper.checkFreeSpace(folder)) {
				return false;
			}

			this.onBefore(downloadTask);
			if (!downloadTask.isAllowDownload()) {
				return false;
			}

			AbstractSegmentDownloaderFactory downloaderFactory = this.findDownloaderFactory(downloadTask);
			if (null == downloaderFactory) {
				return false;
			}

			boolean result = downloaderFactory.check(downloadTask);

			if (!result) {
				downloadTask.setState(DownloadTask.StateError);
				return false;
			}

			downloadTask.setErrorMessage(null);
			AbstractSegmentDownloader downloader = downloaderFactory.createDownloader(downloadTask);

			synchronized (downloadTask) {
				int maxRetryCount = Integer.min(downloader.getSuggestMaxRetryCount(), MaxRetryCount);
				downloadTask.setMaxRetryCount(maxRetryCount);

				if (!this.increaseCount(downloadTask)) {
					return false;
				}

				doSubmit(downloader);

				int threadCount = Integer.min(downloader.getSuggestThreadCountPerTask(), downloadTask.getSegmentSize());

				for (int i = 0; i < (threadCount - 1); i++) {
					downloader = downloader.fork();

					if (this.increaseCount(downloadTask)) {
						doSubmit(downloader);
					} else {
						return true;
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * @param downloader
	 */
	private void doSubmit(AbstractSegmentDownloader downloader) {
		// downloader.getDownloadTask().increaseCurrentThreadCount();
		// Increase download thread count
		this.executorService.submit(downloader);
	}

	/**
	 * Stop tasks and release resources.<BR>
	 */
	@Override
	public void close() {

		stopTasks();

		this.saveTasks(true);
	}

	/**
	 * Stop all tasks.<BR>
	 */
	private void stopTasks() {

		this.daemonTimer.cancel();

		for (DownloadTask downloadTask : this.downloadTaskList) {
			if (downloadTask.getState() == DownloadTask.StateRunning) {
				downloadTask.setState(DownloadTask.StatePause);
			}
		}

		this.executorService.shutdownNow();

		for (int i = 0; i < 20; i++) {
			try {
				if (this.executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
					return;
				}
			} catch (Exception e) {
				// Nothing to do
			}
		}

	}

	/**
	 * Return whether there are tasks running.<BR>
	 *
	 * @return
	 */
	public boolean isTaskRunning() {
		return this.executorService.getActiveCount() > 0;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onBefore(DownloadTask... downloadTasks) {
		super.onBefore(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onFinish(DownloadTask... downloadTasks) {
		super.onFinish(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onUpdate(DownloadTask... downloadTasks) {
		super.onUpdate(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onDelete(DownloadTask... downloadTasks) {
		super.onDelete(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onPause(DownloadTask... downloadTasks) {
		super.onPause(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onError(DownloadTask... downloadTasks) {
		super.onError(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onAdd(DownloadTask... downloadTasks) {
		super.onAdd(downloadTasks);
		this.markDirty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onResume(DownloadTask... downloadTasks) {
		super.onResume(downloadTasks);
		this.markDirty();
	}

	/**
	 * Mark dirty to indicate the store service should be invoked.<BR>
	 */
	private void markDirty() {
		if (null != this.taskStoreService) {
			this.taskStoreService.markDirty();
		}
	}

	/**
	 * Create a download task with default configuration.<BR>
	 *
	 * @return
	 */
	public DownloadTask createDownloadTask() {
		DownloadTask downloadTask = new DownloadTask();

		this.updateDownloadTask(downloadTask);

		return downloadTask;
	}

	/**
	 * Update a download task with default configuration.<BR>
	 *
	 * @param downloadTask
	 */
	public void updateDownloadTask(DownloadTask downloadTask) {
		AssertUtil.assertNotNull(downloadTask);

		if (this.downloadConfiguration.isAutoStartDownloading()) {
			if (downloadTask.getState() == DownloadTask.StatePause) {
				downloadTask.setState(DownloadTask.StateWaiting);
			}
		}

		// downloadTask.setMaxRetryCount(this.downloadConfiguration.getRetryCount());
		// downloadTask.setMaxThreadCount(this.downloadConfiguration.getThreadCountPerTask());
	}

	/**
	 * Move the selected tasks up.<BR>
	 *
	 * @param taskList
	 */
	public void moveUp(List<DownloadTask> taskList) {

		synchronized (this.downloadTaskList) {
			for (DownloadTask downloadTask : taskList) {
				int currentIndex = this.downloadTaskList.indexOf(downloadTask);

				if (currentIndex > 0) {
					Collections.swap(this.downloadTaskList, currentIndex, currentIndex - 1);
				}
			}
		}

	}

	/**
	 * Move the selected tasks down.<BR>
	 *
	 * @param taskList
	 */
	public void moveDown(List<DownloadTask> taskList) {

		synchronized (this.downloadTaskList) {
			for (DownloadTask downloadTask : taskList) {
				int currentIndex = this.downloadTaskList.indexOf(downloadTask);

				if ((currentIndex >= 0) && (currentIndex < (this.downloadTaskList.size() - 1))) {
					Collections.swap(this.downloadTaskList, currentIndex, currentIndex + 1);
				}
			}
		}
	}

	/**
	 * Move the selected tasks to top.<BR>
	 *
	 * @param taskList
	 */
	public void moveTop(List<DownloadTask> taskList) {

		synchronized (this.downloadTaskList) {
			this.downloadTaskList.removeAll(taskList);
			this.downloadTaskList.addAll(0, taskList);
		}

	}

	/**
	 * Move the selected tasks to bottom.<BR>
	 *
	 * @param taskList
	 */
	public void moveBottom(List<DownloadTask> taskList) {

		synchronized (this.downloadTaskList) {
			this.downloadTaskList.removeAll(taskList);
			this.downloadTaskList.addAll(taskList);
		}

	}
}
