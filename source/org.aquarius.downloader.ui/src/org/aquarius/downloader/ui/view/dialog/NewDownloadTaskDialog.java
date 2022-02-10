/**
 *
 */
package org.aquarius.downloader.ui.view.dialog;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.downloader.core.DownloadTask;
import org.aquarius.downloader.ui.Messages;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.util.io.FileUtil;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * To create a new download task.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class NewDownloadTaskDialog extends TitleAreaDialog {

	private Text fileNameText;

	private Text urlText;

	private Text folderText;

	private ProgressMonitorPart progressMonitor;

	private DownloadTask downloadTask = null;

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * @param parentShell
	 */
	public NewDownloadTaskDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite pane = new Composite(parent, SWT.FLAT);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		pane.setLayout(gridLayout);

		{
			Label urlLabel = new Label(pane, SWT.NONE);
			urlLabel.setText(Messages.NewDownloadTaskDialog_Url);
			urlLabel.setLayoutData(new GridData());

			this.urlText = new Text(pane, SWT.FLAT);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			this.urlText.setLayoutData(gridData);

			String url = ClipboardUtil.getStringFromClipboard();
			if (UrlValidator.getInstance().isValid(url)) {
				this.urlText.setText(url);
			}
		}

		{
			Label fileNamelLabel = new Label(pane, SWT.NONE);
			fileNamelLabel.setText(Messages.NewDownloadTaskDialog_FileName);
			fileNamelLabel.setLayoutData(new GridData());

			this.fileNameText = new Text(pane, SWT.FLAT);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			this.fileNameText.setLayoutData(gridData);

		}

		{

			Label folderLabel = new Label(pane, SWT.NONE);
			folderLabel.setText(Messages.NewDownloadTaskDialog_Folder);
			folderLabel.setLayoutData(new GridData());

			this.folderText = new Text(pane, SWT.FLAT);
			this.folderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.folderText.setText(DownloadManager.getInstance().getConfiguration().getDefaultDownloadFolder());

			Button folderButton = new Button(pane, SWT.FLAT);
			folderButton.setText(Messages.NewDownloadTaskDialog_Browse);
			folderButton.setLayoutData(new GridData());

			folderButton.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					String selectedFolderString = dialog.open();

					if (StringUtils.isNotBlank(selectedFolderString)) {
						NewDownloadTaskDialog.this.folderText.setText(selectedFolderString);
					}

					checkState();
				}

			});
		}

		{
			this.progressMonitor = new ProgressMonitorPart(parent, new GridLayout());

			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			this.progressMonitor.setLayoutData(gridData);
		}

		String urlString = this.urlText.getText();

		if (StringUtils.isNotBlank(urlString)) {

			try {
				URI uri = new URI(urlString);
				String fileName = FilenameUtils.getName(uri.getPath());

				if (StringUtils.endsWithIgnoreCase(fileName, ".m3u8")) { //$NON-NLS-1$
					this.fileNameText.setText(FilenameUtils.getBaseName(fileName));
				} else {
					this.fileNameText.setText(fileName);
				}

			} catch (Exception e) {
				String fileName = FilenameUtils.getName(this.urlText.getText());
				this.fileNameText.setText(fileName);
			}

		}

		pane.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pane.setSize(400, SWT.DEFAULT);

		return pane;

	}

	private boolean parseInfo() {

		try {

			this.downloadTask = new DownloadTask();

			if (StringUtils.isEmpty(this.fileNameText.getText())) {
				this.fileNameText.setText(this.downloadTask.getFileName());
			} else {
				this.downloadTask.setFileName(this.fileNameText.getText());
				this.downloadTask.setTitle(this.fileNameText.getText());
			}

			this.downloadTask.setDownloadUrl(this.urlText.getText());
			this.downloadTask.setFolder(this.folderText.getText());

			this.downloadTask.setDownloadUrl(this.urlText.getText());

			boolean flag = DownloadManager.getInstance().check(this.downloadTask);

			if (!flag) {
				this.setErrorMessage(this.downloadTask.getErrorMessage());
				return false;
			}

			return true;

		} catch (Exception e) {
			this.logger.error("parseInfo", e); //$NON-NLS-1$

			return false;
		}

	}

	private boolean checkState() {

		String urlString = this.urlText.getText();
		String folderString = this.folderText.getText();
		String fileNameString = this.fileNameText.getText();

		if (!UrlValidator.getInstance().isValid(urlString)) {
			this.setErrorMessage(Messages.NewDownloadTaskDialog_ErrorUrlMustBeFilled);
			return false;
		}

		if (StringUtils.isBlank(folderString)) {
			this.setErrorMessage(Messages.NewDownloadTaskDialog_ErrorFolderMustBeFilled);
			return false;
		}

		if (!FileUtil.isPathValid(folderString)) {
			this.setErrorMessage(Messages.NewDownloadTaskDialog_ErrorResourceShouldBeFolder);
			return false;
		}

		File folder = new File(folderString);

		if (folder.exists()) {
			if (folder.isFile()) {
				this.setErrorMessage(Messages.NewDownloadTaskDialog_ErrorResourceShouldBeFile);
				return false;
			}
		}

		if (StringUtils.isBlank(fileNameString)) {
			this.setErrorMessage(Messages.NewDownloadTaskDialog_ErrorFileNameShouldBeFolder);

			return false;
		}

		if (!FileUtil.isPathValid(fileNameString)) {
			this.setErrorMessage(Messages.NewDownloadTaskDialog_ErrorResourceIsNotValidFileName);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void okPressed() {

		if (!this.checkState()) {
			return;
		}

		if (!this.parseInfo()) {
			return;
		}

		this.setErrorMessage(null);
		super.okPressed();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void cancelPressed() {
		this.downloadTask = null;
		super.cancelPressed();
	}

	/**
	 * @return the downloadTask
	 */
	public DownloadTask getDownloadTask() {
		return this.downloadTask;
	}

}
