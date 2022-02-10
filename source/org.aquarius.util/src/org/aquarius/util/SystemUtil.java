/**
 *
 */
package org.aquarius.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * System function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SystemUtil {

	public static final int TimeSecond = 1000;

	public static final int TimeMinute = 60 * TimeSecond;

	public static final int TimeHour = 60 * TimeMinute;

	public static final int TimeDay = 24 * TimeHour;

	public static final int NumberThousand = 1000;

	public static final int NumberHundred = 100;

	public static final int DiskSizeInK = 1024;

	public static final int DiskSizeInM = 1024 * 1024;

	public static final int DiskSizeInG = 1024 * 1024 * 1024;

	// private static final Logger logger = LogUtil.getLogger(SystemUtil.class);

	/**
	 * No instances needed.
	 */
	private SystemUtil() {

	}

	/**
	 * Sleep quietly without exception
	 *
	 * @param sleepTime
	 */
	public static void sleepQuietly(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// Nohting to do
		}
	}

	/**
	 * Generate a shell file ,then execute the shell file.<BR>
	 *
	 * @param commands
	 * @throws IOException
	 */
	public static void executeCommandByFile(String... commands) throws Exception {

		if (ArrayUtils.isEmpty(commands)) {
			return;
		}

		if (commands.length == 1) {
			Runtime.getRuntime().exec(commands[0]);
		}

		File execFile;
		if (SystemUtils.IS_OS_WINDOWS) {
			execFile = File.createTempFile("exe_cicada_", ".bat");
		} else {
			execFile = File.createTempFile("exe_cicada_", ".sh");
		}

		List<String> commandList = new ArrayList<>();

		CollectionUtils.addAll(commandList, commands);

		execFile.deleteOnExit();

		FileUtils.writeLines(execFile, SystemUtil.getOsEncoding(), Arrays.asList(commands), "\r\n");

		if (SystemUtils.IS_OS_WINDOWS) {
			Runtime.getRuntime().exec("cmd /c start " + execFile.getAbsolutePath());
		} else {
			// Runtime.getRuntime().exec("/bin/sh -c " + execFile.getAbsolutePath());

			// Runtime.getRuntime().exec(execFile.getAbsolutePath());
			Runtime.getRuntime().exec(execFile.getAbsolutePath());
		}

	}

	/**
	 * Use a thread to execute commands.<BR>
	 * The commands will be executed one by one.<BR>
	 *
	 * @param commands
	 * @throws Exception
	 */
	public static void asyncExecuteCommand(String... commands) throws Exception {
		if (ArrayUtils.isEmpty(commands)) {
			return;
		}

		new Thread() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {

				for (String command : commands) {
					try {
						Process process = Runtime.getRuntime().exec(command);
						process.waitFor();

						if (process.exitValue() != 0) {
							return;
						}

					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}

				super.run();
			}

		}.start();
	}

	/**
	 * Return os encoding.<BR>
	 *
	 * @return
	 */
	public static String getOsEncoding() {
		return System.getProperty("sun.jnu.encoding");
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isSupportDeleteToTrash() {
		return SystemUtils.isJavaVersionAtLeast(9);
	}
}
