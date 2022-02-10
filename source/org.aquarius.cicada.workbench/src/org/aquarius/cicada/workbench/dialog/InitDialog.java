/**
 * 
 */
package org.aquarius.cicada.workbench.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.downloader.core.DownloadManager;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Init ffmpeg path and player path.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class InitDialog extends TitleAreaDialog {

	private List<Text> textList = new ArrayList<>();

	/**
	 * @param parentShell
	 */
	public InitDialog(Shell parentShell) {
		super(parentShell);

		parentShell.setSize(960, 640);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);

		this.setTitle(Messages.InitDialog_Title);
		this.setMessage(Messages.InitDialog_Message);

		Composite pane = new Composite(composite, SWT.FLAT);

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginLeft = 8;
		gridLayout.marginBottom = 8;
		gridLayout.marginRight = 8;
		gridLayout.marginTop = 8;

		pane.setLayout(gridLayout);

		pane.setLayoutData(new GridData(GridData.FILL_BOTH));

		createControls(pane);
		return pane;
	}

	/**
	 * @param pane
	 */
	private void createControls(Composite pane) {

		String[] labelNames = new String[] { Messages.InitDialog_FfmpegLabel, Messages.InitDialog_PlayerLabel };
		String[] linkNames = new String[] { Messages.InitDialog_FfmpegLinkMessage, Messages.InitDialog_PlayerLinkMessage };

		for (int i = 0; i < labelNames.length; i++) {
			String labelName = labelNames[i];

			Label label = new Label(pane, SWT.None);
			label.setText(labelName);

			Text text = new Text(pane, SWT.BORDER);
			this.textList.add(text);

			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Button button = new Button(pane, SWT.PUSH);
			button.setText(Messages.InitDialog_SelectFileText);

			button.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {

					FileDialog fileDialog = new FileDialog(pane.getShell());
					String fileName = fileDialog.open();

					if (StringUtils.isNotEmpty(fileName)) {
						text.setText(fileName);
					}

					super.widgetSelected(e);
				}
			});

			String linkName = linkNames[i];
			Link link = SwtUtil.createLink(pane, linkName);

			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			link.setLayoutData(gridData);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		String ffmepgFile = this.textList.get(0).getText();
		if (StringUtils.isNotBlank(ffmepgFile)) {
			DownloadManager.getInstance().getConfiguration().setFfmepgFile(ffmepgFile);

		}

		String playerFile = this.textList.get(1).getText();
		if (StringUtils.isNotBlank(playerFile)) {
			WorkbenchActivator.getDefault().getConfiguration().setMediaPlayerFile(playerFile);

		}

		super.okPressed();
	}

}
