/**
 * 
 */
package org.aquarius.util.update;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.aquarius.util.zip.ZipUtil;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public final class UpdateExecutor {

	private UpdateConfig updateConfig;

	private UpdateRoot updateInfo;

	/**
	 * @param updateConfig
	 * @param updateInfo
	 * @param localCachePath
	 */
	public UpdateExecutor(UpdateConfig updateConfig, UpdateRoot updateInfo) {
		super();
		this.updateConfig = updateConfig;
		this.updateInfo = updateInfo;
	}

	/**
	 * 
	 * @param updateConfig
	 * @throws IOException
	 */
	public void execute() throws IOException {

		this.doDownloadResources();
		this.doDeploy();
	}

	/**
	 * Download resources to local drive.<BR>
	 * 
	 * @throws IOException
	 */
	private void doDownloadResources() throws IOException {

		List<Resource> resources = this.updateInfo.getResources();

		for (Resource resource : resources) {

			String remoteUrl = resource.getRemoteUrl();

			// byte[] bytes = IOUtils.toByteArray(new URL(remoteUrl));

			File file = File.createTempFile("_cicada_", "tmp");

			FileUtils.copyURLToFile(new URL(remoteUrl), file);

			String targetPath = resource.getTargetPath();
			String realTargetPath = targetPath;

			if (!resource.isAbsPath()) {
				realTargetPath = this.updateConfig.getLocalPath() + File.separator + targetPath;
			}

			realTargetPath = FilenameUtils.normalize(realTargetPath);

			resource.setLocalCachePath(file);
			resource.setRealTargetPath(new File(realTargetPath));
		}
	}

	/**
	 * After downloading update resources, deploy them to specified location.<BR>
	 * 
	 * @throws IOException
	 */
	private void doDeploy() throws IOException {

		List<Resource> resources = this.updateInfo.getResources();

		for (Resource resource : resources) {

			File sourceFile = resource.getLocalCachePath();
			File targetFile = resource.getRealTargetPath();

			if (resource.isNeedUnpack()) {

				ZipUtil.unzip(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), this.updateConfig.isOverwrite());

			} else {

				if (targetFile.exists()) {
					if (!this.updateConfig.isOverwrite()) {
						continue;
					}
				}

				FileUtils.copyFile(sourceFile, targetFile);
			}
		}

	}
}
