/**
 *
 */
package org.aquarius.downloader.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.downloader.core.model.Segment;
import org.aquarius.downloader.core.spi.AbstractSegmentDownloader;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.net.HttpUtil;
import org.aquarius.util.security.SecretKey;

/**
 * Download task<BR>
 * It use segment to support multi downloading.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class DownloadTask {

	public static final int StateWaiting = 0;

	// public static final int StateStart = 1;

	public static final int StatePause = 2;

	// public static final int StateResume = 4;

	public static final int StateRunning = 8;

	public static final int StateError = 16;

	public static final int StateDelete = 32;

	public static final int StateFinish = 64;

	public static final int MaxRetryCount = 100;

	public static final int CategoryVideo = 1;

	public static final int CategoryMusic = 2;

	public static final int CategorySoftware = 4;

	public static final int CategoryBook = 8;

	public static final int CategoryUnknown = 128;

	private static final Set<Integer> knownCategories = new TreeSet<Integer>();
	{
		knownCategories.add(CategoryVideo);
		knownCategories.add(CategoryMusic);
		knownCategories.add(CategorySoftware);
		knownCategories.add(CategoryBook);
	}

	private static final Set<Integer> canApplyStates = new TreeSet<Integer>();
	{
		canApplyStates.add(StateWaiting);
		// canApplyStates.add(StateStart);
		// canApplyStates.add(StateResume);
		canApplyStates.add(StateRunning);
	}

	private static final Set<Integer> canDownloadStates = new TreeSet<>();
	{
		canDownloadStates.add(DownloadTask.StateWaiting);
		// canDownloadStates.add(DownloadTask.StateStart);
		// canDownloadStates.add(DownloadTask.StateResume);
		canDownloadStates.add(DownloadTask.StateRunning);
	}

	private String fileName;

	private String title;

	private String folder;

	private int state;

	private int category;

	private String pageUrl;

	private String refererUrl;

	private String downloadUrl;

	private String tagId;

	private String errorMessage;

	private transient Exception exception;

	private List<Segment> segmentList;

	private boolean dynamic = false;

	private String memo;

	private String type;

	private String data;

	private String secretKeyString;

	private SecretKey secretKey;

	private String source;

	private long remoteFileLength;

	private Date lastValidTime;

	private Map<String, String> requestHeaders = new HashMap<>();

	private transient int maxThreadCount = 3;

	private transient int currentThreadCount = 0;

	private transient int retryCount;

	private transient int maxRetryCount = 20;

	private transient long finishedLength = 0;

	private transient int speed = 0;

	private transient long lengthForspeed = 0;

	private transient long lastUpdateTime = 0;

	private transient int rejectErrorCount = 0;

	private transient AbstractSegmentDownloader segmentDownloader;

	private transient String downloadHost;

	private transient String sourceHost;

	/**
	 *
	 */
	public DownloadTask() {
		super();
	}

	/**
	 * Set the max thread.
	 */
	public DownloadTask(int maxThreadCount) {
		super();

		this.maxThreadCount = maxThreadCount;
	}

	/**
	 * After the task was loaded,this method should be invoked to keep consistency.
	 */
	public void restore() {

		this.downloadHost = HttpUtil.getDomain(this.downloadUrl);
		this.sourceHost = HttpUtil.getDomain(this.pageUrl);

		if (this.state == StateFinish) {
			this.finishedLength = this.remoteFileLength;
			return;
		}

		if (this.state == StateRunning) {
			this.state = StatePause;
		}

		if (CollectionUtils.isNotEmpty(this.segmentList)) {
			for (Segment segment : this.segmentList) {
				this.finishedLength = this.finishedLength + segment.getDownloadedLength();
			}
		}
	}

	/**
	 * Means this task can apply new segment.<BR>
	 *
	 * @return
	 */
	public boolean isAllowApply() {
		return canApplyStates.contains(this.state);
	}

	/**
	 * Means this task can apply new segment.<BR>
	 *
	 * @return
	 */
	public boolean isAllowDownload() {
		return canDownloadStates.contains(this.state);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * After mission finished,all segments should be cleared.
	 */
	public void clearSegments() {
		this.segmentList.clear();
		this.segmentList = null;
	}

	/**
	 * Return the segment size.
	 *
	 * @return
	 */
	public int getSegmentSize() {
		if (null == this.segmentList) {
			return 0;
		}

		return this.segmentList.size();
	}

	/**
	 * Apple a segment to download.<BR>
	 *
	 * @param owner
	 * @return if the return value is null,means all segments is downloading or
	 *         finished.
	 */
	public synchronized Segment applySegment(Object owner) {

		if (!isAllowApply()) {
			return null;
		}

		int finishedSegmentCount = 0;

		for (Segment segment : this.segmentList) {
			if (segment.isFinished()) {
				finishedSegmentCount++;
				continue;
			}

			if (!segment.isLocked()) {
				segment.lock(owner);

				return segment;
			}
		}

		if (finishedSegmentCount == this.segmentList.size()) {
			this.setState(StateFinish);
		}

		return null;
	}

	/**
	 * Set the segment list.<BR>
	 *
	 * @param segmentList
	 */
	public void setSegmentList(List<Segment> segmentList) {

		if (CollectionUtils.isNotEmpty(this.segmentList)) {
			throw new UnsupportedOperationException("The segment can't be changed.");
		}

		AssertUtil.assertTrue(CollectionUtils.isNotEmpty(segmentList), "the segment should not be empty");

		this.segmentList = new ArrayList<Segment>(segmentList);

	}

	/**
	 *
	 *
	 * @return the fileLength
	 */
	public long getRemoteFileLength() {
		return this.remoteFileLength;
	}

	/**
	 * Return the target file.<BR>
	 * If this task is hls,it should be a folder.<BR>
	 *
	 * @return
	 */
	public File getFile() {
		return new File(this.folder + File.separator + this.fileName);
	}

	/**
	 * @param fileLength the fileLength to set
	 */
	public void setRemoteFileLength(long fileLength) {
		this.remoteFileLength = fileLength;
	}

	/**
	 * @return the finishedLength
	 */
	public long getFinishedLength() {

		if (this.finishedLength > this.remoteFileLength) {
			return this.remoteFileLength - 1000;
		}

		return this.finishedLength;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return this.speed;
	}

	/**
	 * Return the finished percent.
	 *
	 * @return
	 */
	public double getPercent() {
		if (this.state == StateFinish) {
			return 100;
		}
		if (0 == this.remoteFileLength) {
			return 0;
		}

		double percent = (((double) this.finishedLength * 100) / this.remoteFileLength);

		return Double.min(percent, 100);
	}

	/**
	 * Add download length for computing speed.<BR>
	 *
	 * @param downloadedLength if the file is hls,it should be 1.
	 * @param realLength       the real bytes length downloaded.<BR>
	 * @return
	 */
	public long addDownloadedLength(long downloadedLength, long realLength) {

		if (0 == this.lastUpdateTime) {
			this.lastUpdateTime = System.currentTimeMillis();
		} else {
			long timeInterval = System.currentTimeMillis() - this.lastUpdateTime;
			if (timeInterval != 0) {
				this.lengthForspeed = this.lengthForspeed + downloadedLength;

				// 1000 millisTime
				double value = this.lengthForspeed * SystemUtil.NumberThousand;
				this.speed = (int) ((value) / (timeInterval));
			}
		}

		this.finishedLength = this.finishedLength + realLength;
		return Math.min(this.finishedLength, this.remoteFileLength);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String name) {

		String extensionName = FilenameUtils.getExtension(name);
		String prefix = FilenameUtils.getBaseName(name);

		if (StringUtils.isBlank(extensionName)) {
			this.fileName = StringUtil.getTitleForFile(prefix);
		} else {
			this.fileName = StringUtil.getTitleForFile(prefix) + "." + extensionName;
		}
	}

	/**
	 * @return the downloadUrl
	 */
	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	/**
	 * @param downloadUrl the downloadUrl to set
	 */
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
		this.downloadHost = HttpUtil.getDomain(this.downloadUrl);
		this.sourceHost = HttpUtil.getDomain(this.pageUrl);
	}

	/**
	 * @return the pageUrl
	 */
	public String getPageUrl() {
		return this.pageUrl;
	}

	/**
	 * @param getPageUrl the sourceUrl to set
	 */
	public void setPageUrl(String getPageUrl) {
		this.pageUrl = getPageUrl;

		this.sourceHost = HttpUtil.getDomain(this.pageUrl);
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;

		if (state == StateFinish) {
			doFinish();
		}
	}

	/**
	 * Finish the mission
	 */
	private void doFinish() {
		this.finishedLength = this.remoteFileLength;
		this.currentThreadCount = 0;
	}

	/**
	 * Some mission use dynamic urls, so we need to validate the time.
	 *
	 * @return
	 */
	public boolean isOutOfValidTime() {
		if (null == this.lastValidTime) {
			return true;
		}

		return this.lastValidTime.getTime() < System.currentTimeMillis();
	}

	/**
	 * @return the lastValidTime
	 */
	public Date getLastValidTime() {
		return this.lastValidTime;
	}

	/**
	 * @param lastValidTime the lastValidTime to set
	 */
	public void setLastValidTime(Date lastValidTime) {
		this.lastValidTime = lastValidTime;
	}

	/**
	 * @return the location
	 */
	public String getFolder() {
		return this.folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

	/**
	 * @return the category
	 */
	public int getCategory() {
		return this.category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return this.memo;
	}

	/**
	 * @param memo the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return this.data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return this.retryCount;
	}

	/**
	 * Increase retry count
	 *
	 * @return
	 */
	public synchronized int increaseRetryCount() {
		return this.retryCount++;
	}

	/**
	 *
	 */
	public synchronized void clearInfo() {
		this.retryCount = 0;
		this.currentThreadCount = 0;
		this.rejectErrorCount = 0;
		this.speed = 0;
		this.lengthForspeed = 0;
	}

	/**
	 *
	 */
	public synchronized void reset() {
		this.clearInfo();
		this.finishedLength = 0;
		this.remoteFileLength = 0;

		if (null != this.segmentList) {
			this.segmentList.clear();
		}

	}

	public synchronized void increateRejectErrorCount() {
		this.rejectErrorCount++;
	}

	/**
	 * @return the rejectErrorCount
	 */
	public int getRejectErrorCount() {
		return this.rejectErrorCount;
	}

	/**
	 * @return the maxRetryCount
	 */
	public int getMaxRetryCount() {
		return this.maxRetryCount;
	}

	/**
	 * @param maxRetryCount the maxRetryCount to set
	 */
	public synchronized void setMaxRetryCount(int maxRetryCount) {
		// The max retry count should between 1-100.
		if ((maxRetryCount > 0) && (maxRetryCount <= MaxRetryCount)) {
			this.maxRetryCount = maxRetryCount;
		}
	}

	/**
	 * If errors happened, we need to check whether this mission should be
	 * continued.
	 *
	 * @return
	 */
	public boolean shouldRetry() {
		return this.retryCount < this.maxRetryCount && (this.isAllowApply());
	}

	/**
	 * Return how many threads are running.
	 *
	 * @return the currentThreadCount
	 */
	public synchronized int getCurrentThreadCount() {
		if (this.currentThreadCount < 0) {
			this.currentThreadCount = 0;
		}
		return this.currentThreadCount;
	}

	/**
	 * increase the thread count
	 */
	public synchronized void increaseCurrentThreadCount() {
		this.currentThreadCount++;
	}

	/**
	 * decrease the thread count
	 */
	public synchronized void decreaseCurrentThreadCount() {
		this.currentThreadCount--;

		if (this.currentThreadCount < 0) {
			this.currentThreadCount = 0;
		}
	}

	/**
	 * @return the lastUpdateTime
	 */
	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	/**
	 * @return the tagId
	 */
	public String getTagId() {
		return this.tagId;
	}

	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the dynamic
	 */
	public boolean isDynamic() {
		return this.dynamic;
	}

	/**
	 * @param dynamic the dynamic to set
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	/**
	 * @return the segmentDownloader
	 */
	public AbstractSegmentDownloader getSegmentDownloader() {
		return this.segmentDownloader;
	}

	/**
	 * @param segmentDownloader the segmentDownloader to set
	 */
	public void setSegmentDownloader(AbstractSegmentDownloader segmentDownloader) {
		this.segmentDownloader = segmentDownloader;
	}

	/**
	 * @return the secretKeyString
	 */
	public String getSecretKeyString() {
		return this.secretKeyString;
	}

	/**
	 * @param secretKeyString the secretKeyString to set
	 */
	public void setSecretKeyString(String secretKeyString) {
		this.secretKeyString = secretKeyString;
	}

	/**
	 * @return the secretKey
	 */
	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return this.exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;

		if (null == this.exception) {
			this.errorMessage = null;
		} else {
			this.errorMessage = exception.getLocalizedMessage();
			this.state = StateError;
		}
	}

	/**
	 * @return the maxThreadCount
	 */
	public synchronized int getMaxThreadCount() {
		return this.maxThreadCount;
	}

	/**
	 * @param maxThreadCount the maxThreadCount to set
	 */
	public synchronized void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}

	/**
	 * If the return value is <CODE>true</code>.<BR>
	 * No new thread should be created to download segment.<BR>
	 *
	 * @return
	 */
	public synchronized boolean isThreadFull() {
		return this.currentThreadCount >= this.maxThreadCount;
	}

	/**
	 * @return the refererUrl
	 */
	public String getRefererUrl() {
		return this.refererUrl;
	}

	/**
	 * @param refererUrl the refererUrl to set
	 */
	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	/**
	 * @return the requestHeaders
	 */
	public Map<String, String> getRequestHeaders() {
		return this.requestHeaders;
	}

	/**
	 * @param requestHeaders the requestHeaders to set
	 */
	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	/**
	 * Merge the new contents to this.<BR>
	 * 
	 * @param requestHeaders
	 */
	public void mergeRequestHeaders(Map<String, String> requestHeaders) {
		if (null != requestHeaders) {
			this.requestHeaders.putAll(requestHeaders);
		}
	}

	/**
	 * @return the downloadHost
	 */
	public String getDownloadHost() {
		return this.downloadHost;
	}

	/**
	 * @return the sourceHost
	 */
	public String getSourceHost() {
		return this.sourceHost;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "DownloadTask [fileName=" + this.fileName + ", title=" + this.title + ", folder=" + this.folder + ", state=" + this.state + ", category="
				+ this.category + ", pageUrl=" + this.pageUrl + ", refererUrl=" + this.refererUrl + ", downloadUrl=" + this.downloadUrl + ", tagId="
				+ this.tagId + ", errorMessage=" + this.errorMessage + ", segmentList=" + this.segmentList + ", dynamic=" + this.dynamic + ", memo=" + this.memo
				+ ", type=" + this.type + ", data=" + this.data + ", secretKeyString=" + this.secretKeyString + ", secretKey=" + this.secretKey + ", source="
				+ this.source + ", remoteFileLength=" + this.remoteFileLength + ", lastValidTime=" + this.lastValidTime + ", requestHeaders="
				+ this.requestHeaders + "]";
	}

}
