/**
 *
 */
package org.aquarius.downloader.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.spi.AbstractTaskStoreService;

/**
 * Null implementation.
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NullTaskStoreService extends AbstractTaskStoreService {

	public static final NullTaskStoreService Instance = new NullTaskStoreService();

	/**
	 *
	 */
	private NullTaskStoreService() {
		super();
		// Nothing to init
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSaveTasks(List<DownloadTask> downloadTaskList) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<DownloadTask> loadTasks() {
		return new ArrayList<DownloadTask>();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public List<DownloadTask> parseTasks(InputStream inputStream) throws IOException {
		return new ArrayList<DownloadTask>();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void exportTasks(List<DownloadTask> downloadTaskList, OutputStream outputStream) throws IOException {
		throw new UnsupportedOperationException("This method is not supported.");
	}

}
