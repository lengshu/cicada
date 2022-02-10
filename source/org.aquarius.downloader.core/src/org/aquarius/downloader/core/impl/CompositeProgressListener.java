/**
 *
 */
package org.aquarius.downloader.core.impl;

import java.util.Set;
import java.util.TreeSet;

import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.core.spi.AbstractProgressListener;
import org.aquarius.log.LogUtil;
import org.aquarius.util.AssertUtil;
import org.slf4j.Logger;

/**
 * Composite progress listener.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class CompositeProgressListener extends AbstractProgressListener {

	private Set<AbstractProgressListener> listeners = new TreeSet<AbstractProgressListener>();

	protected final Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * Remove a listener.
	 *
	 * @param listener
	 */
	public void addListener(AbstractProgressListener listener) {

		AssertUtil.assertNotNull(listener, "The listener should not be null.");
		this.listeners.add(listener);
	}

	/**
	 * Add a listener.
	 *
	 * @param listener
	 */
	public void removeListener(AbstractProgressListener listener) {
		AssertUtil.assertNotNull(listener, "The listener should not be null.");
		this.listeners.remove(listener);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onBefore(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onBefore(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onBefore", exception);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onFinish(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onFinish(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onFinish", e);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onUpdate(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onUpdate(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onUpdate", e);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onDelete(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onDelete(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onDelete", e);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onPause(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onPause(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onPause", e);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onError(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onError(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onError", e);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onAdd(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onAdd(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onAdd", e);
			}

		});
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void onResume(DownloadTask... downloadTasks) {
		this.listeners.forEach(e -> {
			try {
				e.onResume(downloadTasks);
			} catch (Exception exception) {
				this.logger.error("onResume", e);
			}

		});
	}

}
