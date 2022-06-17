/**
 * 
 */
package org.aquarius.cicada.workbench.dialog;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.model.Site;
import org.aquarius.cicada.core.model.VirtualSite;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.SearchKeywordModel;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.editor.SiteEditorInput;
import org.aquarius.cicada.workbench.editor.SiteMultiPageEditor;
import org.aquarius.cicada.workbench.helper.MovieHelper;
import org.aquarius.cicada.workbench.manager.HistoryManager;
import org.aquarius.ui.table.CheckedTableControlFactory;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.ui.util.TooltipUtil;
import org.aquarius.util.StringUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class SearchMovieByKeywordDialog extends TitleAreaDialog implements IRunnableWithProgress {

	private Button autoPasteButton;

	private Button searchInTitleButton;

	private Button searchInAllButton;

	private Button useRegexButton;

	private Text keywordText;

	private SearchKeywordModel searchKeywordModel;

	private CheckedTableControlFactory<String> checkedTableControlFactory;

	private static final String AutoPaste = SearchMovieByKeywordDialog.class.getName() + ".AutoPaste"; //$NON-NLS-1$

	/**
	 * @param parent
	 */
	public SearchMovieByKeywordDialog(Shell parent, SearchKeywordModel searchKeywordModel) {
		super(parent);

		this.searchKeywordModel = searchKeywordModel;

		if (null == this.searchKeywordModel) {
			this.searchKeywordModel = new SearchKeywordModel();
			this.searchKeywordModel.setSites(HistoryManager.getInstance().getRecentSites());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		this.setTitle(Messages.SearchMovieByKeywordDialog_Title);
		this.setMessage(Messages.SearchMovieByKeywordDialog_Message);

		Composite pane = new Composite(composite, SWT.FLAT);

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginRight = 8;
		gridLayout.marginLeft = 8;
		gridLayout.horizontalSpacing = 24;
		pane.setLayout(gridLayout);

		pane.setLayoutData(new GridData(GridData.FILL_BOTH));

		createControls(pane);

		return composite;
	}

	/**
	 * @param pane
	 */
	private void createControls(Composite pane) {

		Label label = new Label(pane, SWT.None);
		label.setText(Messages.SearchMovieByKeywordDialog_KeywordLabel);

		this.keywordText = new Text(pane, SWT.BORDER);
		SwtUtil.createPromptDecoration(this.keywordText, Messages.SearchMovieByKeywordDialog_KeywordPrompt);

		if (StringUtils.isNotBlank(this.searchKeywordModel.getKeyword())) {
			this.keywordText.setText(this.searchKeywordModel.getKeyword());
		}

		this.keywordText.addModifyListener(e -> {
			validateKeyword();
		});

		this.keywordText.setLayoutData(new GridData(GridData.FILL_BOTH));

		{
			this.autoPasteButton = new Button(pane, SWT.CHECK);
			this.autoPasteButton.setText(Messages.SearchMovieByKeywordDialog_AutoPaste);

			IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
			boolean autoPaste = store.getBoolean(AutoPaste);

			this.autoPasteButton.setSelection(autoPaste);

			this.autoPasteButton.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					doAutoPaste();
				}

			});

			if (StringUtils.isBlank(this.searchKeywordModel.getKeyword())) {
				this.doAutoPaste();
			}

		}

		{

			this.searchInAllButton = new Button(pane, SWT.RADIO);
			this.searchInAllButton.setText(Messages.SearchMovieByKeywordDialog_SearchInAll);

			this.searchInTitleButton = new Button(pane, SWT.RADIO);
			this.searchInTitleButton.setText(Messages.SearchMovieByKeywordDialog_SearchInTitle);

			if (this.searchKeywordModel.isJustSearchInTitle()) {
				this.searchInTitleButton.setSelection(true);
			} else {
				this.searchInAllButton.setSelection(true);
			}

			this.useRegexButton = new Button(pane, SWT.CHECK);
			this.useRegexButton.setText(Messages.SearchMovieByKeywordDialog_UseRegexToSearch);

			this.useRegexButton.setSelection(this.searchKeywordModel.isUseRegex());

			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;

			this.searchInAllButton.setLayoutData(gridData);
			this.searchInTitleButton.setLayoutData(gridData);

		}

		Set<String> siteNames = RuntimeManager.getInstance().getSupportImportSiteNames();

		this.checkedTableControlFactory = new CheckedTableControlFactory<String>(siteNames, null);

		List<String> sites = this.searchKeywordModel.getSites();

		Composite composite = this.checkedTableControlFactory.createControl(pane, SWT.CHECK, false, false);
		this.checkedTableControlFactory.setCheckedElements(sites);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;

		composite.setLayoutData(gridData);

		this.keywordText.setFocus();
	}

	/**
	 * 
	 */
	private void doAutoPaste() {

		if (this.autoPasteButton.getSelection()) {
			this.keywordText.setText(ClipboardUtil.getStringFromClipboard());
		}
	}

	/**
	 * 
	 */
	private boolean validateKeyword() {
		String keyword = this.keywordText.getText();
		if (StringUtils.isBlank(keyword)) {
			this.setErrorMessage(Messages.SearchMovieByKeywordDialog_KeywordNotEmptyErrorMessage);
			return false;
		} else {
			this.setErrorMessage(null);
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		if (!this.validateKeyword()) {
			return;
		}

		List<String> selectedElements = this.checkedTableControlFactory.getCheckedElements();

		if (selectedElements.size() < 2) {
			this.setErrorMessage(Messages.SearchMovieByKeywordDialog_SiteCountShouldBeMoreErrorMessage);
			return;
		}

		if (this.searchInTitleButton.getSelection()) {
			this.searchKeywordModel.setJustSearchInTitle(true);
		} else {
			this.searchKeywordModel.setJustSearchInTitle(false);
		}

		this.searchKeywordModel.setUseRegex(this.useRegexButton.getSelection());
		this.searchKeywordModel.setKeyword(this.keywordText.getText());
		this.searchKeywordModel.setSites(selectedElements);

		HistoryManager.getInstance().addSearchKeywordModelHistory(this.searchKeywordModel);

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		store.setValue(AutoPaste, this.autoPasteButton.getSelection());

		super.okPressed();

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getParentShell());
		try {
			dialog.run(false, true, this);
		} catch (Exception e) {
			TooltipUtil.showInfoTip(Messages.ErrorDialogTitle, e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		List<Movie> resultList = new ArrayList<>();

		boolean justSearchInTitle = this.searchKeywordModel.isJustSearchInTitle();

		Pattern pattern = null;
		if (this.searchKeywordModel.isUseRegex()) {
			pattern = Pattern.compile(this.searchKeywordModel.getKeyword(), Pattern.CASE_INSENSITIVE);
		}

		String mainKeyword = this.searchKeywordModel.getKeyword();
		String[] keywords = MovieUtil.split(mainKeyword);

		monitor.worked(IProgressMonitor.UNKNOWN);

		Map<String, String> channelNameMapping = new HashMap<>();

		for (String siteName : this.searchKeywordModel.getSites()) {
			String message = MessageFormat.format(Messages.SearchMovieByKeywordDialog_SearchingSiteMessage, siteName);
			monitor.beginTask(message, IProgressMonitor.UNKNOWN);
			Site site = RuntimeManager.getInstance().loadSite(siteName);

			List<Movie> movieList = site.getMovieList();

			for (Movie movie : movieList) {

				if (monitor.isCanceled()) {
					return;
				}

				boolean resultFlag = false;

				if (null == pattern) {
					resultFlag = this.searchByKeywords(movie, justSearchInTitle, keywords);
				} else {
					resultFlag = this.searchByRegex(movie, justSearchInTitle, pattern);
				}

				if (resultFlag) {
					resultList.add(movie);
				}
			}

			channelNameMapping.putAll(MovieHelper.buildChannelNameMapping(site, true));
		}

		if (resultList.isEmpty()) {
			TooltipUtil.showErrorTip(Messages.WarnDialogTitle, Messages.SearchMovieByKeywordDialog_NoResultFoundErrorMessage);
		} else {

			SwtUtil.findDisplay().asyncExec(() -> {

				try {
					Site site = new VirtualSite(mainKeyword, resultList, 0, channelNameMapping);

					SiteEditorInput siteEditorInput = new SiteEditorInput(site, true, false);

					IWorkbenchPage workbenchPage = SwtUtil.findActiveWorkbenchPage();

					workbenchPage.openEditor(siteEditorInput, SiteMultiPageEditor.SearchEditorID);

				} catch (PartInitException e) {
					TooltipUtil.showErrorTip(Messages.ErrorDialogTitle, e.getLocalizedMessage());
				}
			});
		}

		monitor.done();

	}

	private boolean searchByRegex(Movie movie, boolean justSearchInTitle, Pattern pattern) {

		if (justSearchInTitle) {
			if (pattern.matcher(movie.getTitle()).matches()) {
				return true;
			}
		} else {

			String[] stringArray = new String[] { movie.getTitle(), movie.getActor(), movie.getCategory(), movie.getTag(), movie.getUniId(),
					movie.getProducer() };

			for (String string : stringArray) {
				if (StringUtils.isNotEmpty(string) && pattern.matcher(string).matches()) {
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * @param movieList
	 * @param justSearchInTitle
	 * @param keywords
	 * @param resultList
	 */
	private boolean searchByKeywords(Movie movie, boolean justSearchInTitle, String[] keywords) {
		for (String keyword : keywords) {
			if (justSearchInTitle) {
				if (StringUtil.findKeywordInRangeIgnoreCase(keyword, movie.getTitle())) {
					return true;
				}
			} else {
				if (MovieUtil.findKeyword(movie, keyword)) {
					return true;
				}
			}
		}

		return false;
	}
}
