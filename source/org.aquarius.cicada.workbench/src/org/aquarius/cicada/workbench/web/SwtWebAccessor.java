/**
 *
 */
package org.aquarius.cicada.workbench.web;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.spi.IProcessMonitor;
import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.function.common.GetCurrentPageUrlFunction;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.ObjectHolder;
import org.aquarius.util.StringUtil;
import org.aquarius.util.SystemUtil;
import org.aquarius.util.exception.ExceptionUtil;
import org.aquarius.util.io.FileUtil;
import org.aquarius.util.net.HttpUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Display;

/**
 * Use swt browser to access web pages.
 *
 * @author aquarius.github@gmail.com
 *
 */
class SwtWebAccessor implements IWebAccessor {

	private Browser browser;

	private int SleepInterval = 100;

	private class InnerProgressListener implements ProgressListener {

		boolean loaded = false;

		/**
		 * {@inheritDoc}}
		 */
		@Override
		public void changed(ProgressEvent event) {
			// Nothing to do
		}

		/**
		 * {@inheritDoc}}
		 */
		@Override
		public void completed(ProgressEvent event) {
			this.loaded = true;
		}
	};

	private InnerProgressListener progressListener = new InnerProgressListener();

	private SwtWebAccessorService swtWebAccessorService;

	/**
	 *
	 */
	SwtWebAccessor(Browser browser, SwtWebAccessorService swtWebAccessorService) {
		super();

		this.swtWebAccessorService = swtWebAccessorService;
		AssertUtil.assertTrue(SwtUtil.isValid(browser), "The widget is not validate");

		this.browser = browser;
		browser.addProgressListener(this.progressListener);

	}

	/**
	 * Return the current browser is valid or not.<BR>
	 * 
	 * @return
	 */
	private boolean isValid() {
		if (!SwtUtil.isValid(this.browser)) {
			return false;
		}

		return !this.swtWebAccessorService.isClosed();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getHtmlSource() {
		if (!this.isValid()) {
			return null;
		}

		SwtUtil.checkNotUThread(this.browser.getDisplay());

		ObjectHolder<String> objectHolder = new ObjectHolder<>();

		this.browser.getDisplay().syncExec(() -> {
			if (this.isValid()) {
				objectHolder.setValue(this.browser.getText());
				return;
			} else {
				objectHolder.setValue(null);
			}
		});

		return objectHolder.getValue();
	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public boolean get(String urlString, IProcessMonitor processMonitor) {

		if (!this.isValid()) {
			return false;
		}

		SwtUtil.checkNotUThread(this.browser.getDisplay());

		ObjectHolder<Boolean> objectHolder = new ObjectHolder<>();

		this.progressListener.loaded = false;
		this.browser.getDisplay().asyncExec(() -> {
			if (!this.isValid()) {
				objectHolder.setValue(false);
				return;
			}

			new GetCurrentPageUrlFunction(this.browser, urlString);
			boolean result = this.browser.setUrl(urlString);
			new GetCurrentPageUrlFunction(this.browser, urlString);

			objectHolder.setValue(result);
		});
		// Use ui thread to load url
		// Then return non-ui thread to wait loading finished.

		long waitTime = 0;

		while ((!objectHolder.isUpdated()) && (!this.progressListener.loaded) && (waitTime < DefaultWaitTimeMills)) {
			waitTime = waitTime + this.SleepInterval;
			SystemUtil.sleepQuietly(this.SleepInterval);
		}
		// Wait default time to finish loading web page

		if (objectHolder.getValue() == null) {
			return true;
		} else {
			return objectHolder.getValue().booleanValue();
		}

	}

	/**
	 *
	 * {@inheritDoc}}
	 */
	@Override
	public String syncExecuteScript(String... scripts) {

		if (ArrayUtils.isEmpty(scripts)) {
			return null;
		}

		if (!isValid()) {
			return null;
		}

		Display display = this.browser.getDisplay();
		SwtUtil.checkNotUThread(display);

		ObjectHolder<Object> objectHolder = new ObjectHolder<>();

		display.syncExec(() -> {

			if (isValid()) {
				try {
					Object result = null;

					for (String script : scripts) {
						result = this.browser.evaluate(script);
					}

					objectHolder.setValue(ObjectUtils.toString(result));
				} catch (Exception e) {
					objectHolder.setValue(e);
				}
			} else {
				objectHolder.setValue(new org.eclipse.swt.SWTException("Widget is disposed"));
			}

		});

		doWait(objectHolder, DefaultWaitTimeMills);

		return ObjectUtils.toString(objectHolder.getValue());
	}

	@Override
	public Object asyncExecuteScript(int timeMillis, String resultName, String... scripts) {

		if (!isValid()) {
			return null;
		}

		Display display = this.browser.getDisplay();
		SwtUtil.checkNotUThread(display);

		ObjectHolder<Object> objectHolder = new ObjectHolder<>();

		display.asyncExec(() -> {

			if (isValid()) {
				try {

					if (null != scripts) {
						for (String script : scripts) {
							this.browser.evaluate(script);
						}
					}

				} catch (Exception e) {
					objectHolder.setValue(e);
				}
			} else {
				objectHolder.setValue(new org.eclipse.swt.SWTException("Widget is disposed"));
			}
		});

		if (timeMillis > 0) {
			SystemUtil.sleepQuietly(timeMillis);
		}

		if (objectHolder.getValue() instanceof Throwable) {
			ExceptionUtil.throwRuntimeException((Throwable) objectHolder.getValue());
		}

		display.asyncExec(() -> {

			if (isValid()) {
				try {

					if (StringUtils.isNotBlank(resultName)) {
						Object result = this.browser.evaluate("return " + resultName + ";");
						objectHolder.setValue(ObjectUtils.toString(result));
					}

				} catch (Exception e) {
					objectHolder.setValue(e);
				}
			} else {
				objectHolder.setValue(new org.eclipse.swt.SWTException("Widget is disposed"));
			}

		});

		if (objectHolder.getValue() instanceof Throwable) {
			ExceptionUtil.throwRuntimeException((Throwable) objectHolder.getValue());
		}

		return ObjectUtils.toString(objectHolder.getValue());
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String post(String urlString, Map<String, String> parameters) {

		if (!isValid()) {
			return "widget disposed";
		}

		SwtUtil.checkNotUThread(this.browser.getDisplay());

		if (null != parameters && parameters.size() > 0) {
			String postParameters = HttpUtil.generateParameters(parameters);
			urlString = urlString + "?&" + postParameters;
		}

		this.progressListener.loaded = false;

		String formHtmlFileName = Starter.getInstance().getWorkingFolder() + FileUtil.getRealFilePath("/config/site/form-xhr.html");

		File file = null;

		try {
			file = File.createTempFile("form_", ".html");
			file.deleteOnExit();
			String htmlTemplate = FileUtils.readFileToString(new File(formHtmlFileName), StringUtil.CODEING_UTF8);

			String htmlContent = StringUtils.replace(htmlTemplate, "{#url}", urlString);
			FileUtils.writeStringToFile(file, htmlContent, StringUtil.CODEING_UTF8);
		} catch (Exception e) {
			ExceptionUtil.throwRuntimeException(e);
		}

		final File externalFile = file;

		ObjectHolder<Object> objectHolder = new ObjectHolder<>();
		this.browser.getDisplay().asyncExec(() -> {

			try {

				if (!isValid()) {
					objectHolder.setValue("widget disposed");
					return;
				}

				this.browser.setUrl(externalFile.toURI().toString());

				new BrowserFunction(this.browser, "invokePostData") {

					/**
					 * {@inheritDoc}}
					 */
					@Override
					public Object function(Object[] arguments) {

						if (ArrayUtils.isNotEmpty(arguments)) {
							objectHolder.setValue(arguments[0]);
						}

						return null;
					}
				};

				return;
			} catch (Exception e) {
				objectHolder.setValue(e);
			}
		});

		doWait(objectHolder, DefaultWaitTimeMills);

		return ObjectUtils.toString(objectHolder.getValue());
	}

	/**
	 * @param objectHolder
	 */
	private void doWait(ObjectHolder<Object> objectHolder, int timeMills) {
		long waitTime = 0;

		while ((!objectHolder.isUpdated()) && (waitTime < timeMills)) {
			waitTime = waitTime + this.SleepInterval;
			SystemUtil.sleepQuietly(this.SleepInterval);
		}

		if (objectHolder.getValue() instanceof Throwable) {
			ExceptionUtil.throwRuntimeException((Throwable) objectHolder.getValue());
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getCookie(String name, String url) {
		return Browser.getCookie(name, url);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean setCookie(String value, String url) {
		return Browser.setCookie(value, url);
	}

	/**
	 * 
	 * @return
	 */
	Browser getBrowser() {
		return this.browser;
	}

}
