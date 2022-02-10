/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.core.model.result.MovieListResult;
import org.aquarius.cicada.core.model.result.MovieResult;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.Starter;
import org.aquarius.cicada.workbench.manager.SiteConfigManager;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.validator.RegexValidator;
import org.aquarius.util.StringUtil;
import org.aquarius.util.io.FileUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * Site parser editor.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteConfigEditor extends MultiPageEditorPart {

	public static final String EditorID = "org.aquarius.cicada.workbench.config.editor.siteConfig"; //$NON-NLS-1$

	private SiteConfig siteConfig;

	private Logger logger = LogUtil.getLogger(getClass());

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createPages() {
		SiteConfigPropertyEditorPage propertyEditorPage = new SiteConfigPropertyEditorPage();
		try {
			this.addPage(propertyEditorPage, getEditorInput());
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}

		DebugBrowserScriptEditorPage listEditorPage = new DebugBrowserScriptEditorPage();
		listEditorPage.setTargetClass(MovieListResult.class);
		listEditorPage.setScriptDefinition(this.siteConfig.getParseListScriptDefinition());

		DebugBrowserScriptEditorPage detailEditorPage = new DebugBrowserScriptEditorPage();
		detailEditorPage.setTargetClass(MovieResult.class);
		detailEditorPage.setScriptDefinition(this.siteConfig.getParseDetailScriptDefinition());

		DebugBrowserScriptEditorPage linkEditorPage = new DebugBrowserScriptEditorPage();
		linkEditorPage.setTargetClass(Link.class);
		linkEditorPage.setScriptDefinition(this.siteConfig.getParseLinkScriptDefinition());

		try {
			this.addPage(listEditorPage, getEditorInput());
			this.addPage(detailEditorPage, getEditorInput());
			this.addPage(linkEditorPage, getEditorInput());

			this.setPageText(0, Messages.SiteConfigEditor_Property);
			this.setPageText(1, Messages.SiteConfigEditor_List);
			this.setPageText(2, Messages.SiteConfigEditor_Detail);
			this.setPageText(3, Messages.SiteConfigEditor_Link);

		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {

		for (int i = 0; i < this.getPageCount(); i++) {
			this.getEditor(i).doSave(monitor);
		}

		try {
			saveToFile();
			SiteConfigManager.getInstance().applySiteConfig(this.siteConfig);
		} catch (IOException e) {
			MessageDialog.openError(this.getEditorSite().getShell(), Messages.SiteConfigEditor_ErrorDialogTitle, e.getLocalizedMessage());
			this.logger.error("saveToFile", e); //$NON-NLS-1$
		}

		this.firePropertyChange(PROP_DIRTY);

	}

	/**
	 * @throws IOException
	 *
	 */
	private void saveToFile() throws IOException {

		String folderString = Starter.getInstance().getParserPath() + File.separator + this.siteConfig.getSiteName(); // $NON-NLS-1$
		File folder = new File(folderString);

		if (folder.isFile()) {
			String message = MessageFormat.format(Messages.SiteConfigEditor_NotFileErrorMessage, folderString);
			MessageDialog.openError(this.getEditorSite().getShell(), Messages.SiteConfigEditor_SaveDialogTitle, message);
			return;
		}

		this.siteConfig.setFolder(folder);

		File configFile = new File(folderString + File.separator + "config.json"); //$NON-NLS-1$
		String configJson = JSON.toJSONString(this.siteConfig, true);
		FileUtils.write(configFile, configJson, StringUtil.CODEING_UTF8);

		String script = this.siteConfig.getParseListScriptDefinition().getScript();
		if (StringUtils.isNotBlank(script)) {
			File jsFile = new File(folderString + File.separator + "list.js"); //$NON-NLS-1$
			FileUtils.write(jsFile, script, StringUtil.CODEING_UTF8);
		}

		script = this.siteConfig.getParseDetailScriptDefinition().getScript();
		if (StringUtils.isNotBlank(script)) {
			File jsFile = new File(folderString + File.separator + "detail.js"); //$NON-NLS-1$
			FileUtils.write(jsFile, script, StringUtil.CODEING_UTF8);
		}

		script = this.siteConfig.getParseLinkScriptDefinition().getScript();
		if (StringUtils.isNotBlank(script)) {
			File jsFile = new File(folderString + File.separator + "link.js"); //$NON-NLS-1$
			FileUtils.write(jsFile, script, StringUtil.CODEING_UTF8);
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSaveAs() {
		InputDialog dialog = new InputDialog(this.getSite().getShell(), Messages.SiteConfigEditor_InputDialogTitle, Messages.SiteConfigEditor_InputMessage,
				"New", // $NON-NLS-3$ //$NON-NLS-1$
				new RegexValidator(FileUtil.ValidFileRegex, Messages.SiteConfigEditor_InvalidFileNameErrorMessage));

		if (Dialog.OK == dialog.open()) {

			String newName = dialog.getValue();
			this.siteConfig.setSiteName(newName);

			this.doSave(new NullProgressMonitor());
		}
	}

	/**
	 * @return the siteConfig
	 */
	public SiteConfig getSiteConfig() {
		return this.siteConfig;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.siteConfig = input.getAdapter(SiteConfig.class);
		super.setInput(input);
		super.setSite(site);

		this.setPartName(this.siteConfig.getSiteName());
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		// Nothing to do
	}

}
