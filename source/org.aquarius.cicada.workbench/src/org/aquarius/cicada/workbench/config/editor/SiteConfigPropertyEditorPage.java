/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.aquarius.cicada.core.model.MovieChannel;
import org.aquarius.cicada.core.model.SiteConfig;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.ui.binder.CompositeControlBinder;
import org.aquarius.ui.binder.ControlBinderManager;
import org.aquarius.ui.binder.IControlBinder;
import org.aquarius.ui.binder.IValueChangeListener;
import org.aquarius.ui.binder.impl.PropertyValueAccessor;
import org.aquarius.ui.message.LabelMessageShower;
import org.aquarius.ui.table.label.BeanPropertyColumnLableProvider;
import org.aquarius.ui.table.label.IndexLabelProvider;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 *
 * The property page to edit a site parser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SiteConfigPropertyEditorPage extends EditorPart implements IValueChangeListener {
	private LabelMessageShower labelMessageShower;

	private Button channelSupportPagingButton;

	private Text nameText;

	private Text nlsDisplayNameText;

	private Text urlText;

	private Button autoRefreshListButton;

	private Button autoParseDetailButton;

	private TableViewer channelTableViewer;

	private Text cookieText;

	private SiteConfig siteConfig;

	private CompositeControlBinder binderGroup = new CompositeControlBinder();

	private boolean dirty;

	private Button supportPagingButton;

	private List<MovieChannel> editingList = new ArrayList<>();

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		this.binderGroup.save();

		this.siteConfig.getMovieChannelList().clear();
		this.siteConfig.getMovieChannelList().addAll(this.editingList);

		this.dirty = false;

		String validateResult = this.validate();
		this.labelMessageShower.setErrorMessage(validateResult);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void doSaveAs() {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setSite(site);
		this.setInput(input);

		this.siteConfig = input.getAdapter(SiteConfig.class);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDirty() {
		return this.dirty;
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
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout());

		this.labelMessageShower = createMessageLabel(composite);

		SashForm sashForm = new SashForm(composite, SWT.None);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		createForm(sashForm);
		createChannelPane(sashForm);

		for (IControlBinder<?, ?> binder : this.binderGroup.getAllBinders()) {
			binder.setValueChangeListener(this);
		}
	}

	/**
	 * @param sashForm
	 */
	private void createForm(SashForm sashForm) {
		Composite parent = new Composite(sashForm, SWT.FLAT);

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 12;
		parent.setLayout(gridLayout);

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_SiteName);
			Text siteNameText = new Text(parent, SWT.BORDER);
			siteNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			siteNameText.setText(this.siteConfig.getSiteName());
			siteNameText.setEditable(false);
			// Site name can't be changed
		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_MainPage);
			Text mainPageText = new Text(parent, SWT.BORDER);
			mainPageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Text, String> controlBinder = (IControlBinder<Text, String>) ControlBinderManager.bind(mainPageText, PropertyValueAccessor.Instance,
					this.siteConfig, "mainPage", null); //$NON-NLS-1$

			controlBinder.setValidator(new org.aquarius.util.validator.impl.UrlValidator(Messages.SiteConfigPropertyEditorPage_MainPageMustBeUrlErrorMessage));

			this.binderGroup.add(controlBinder);
		}

		{
			Label regexLabel = doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_ParseMovieIdRegex);
			regexLabel.setToolTipText(Messages.SiteConfigPropertyEditorPage_ParseMovieIdRegexTooltip);
			Text regexText = new Text(parent, SWT.BORDER);
			regexText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Text, String> controlBinder = (IControlBinder<Text, String>) ControlBinderManager.bind(regexText, PropertyValueAccessor.Instance,
					this.siteConfig, "parseMovieIdRegex", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);
		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_Enable);
			Button enableButton = new Button(parent, SWT.CHECK);
			enableButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Button, String> controlBinder = (IControlBinder<Button, String>) ControlBinderManager.bind(enableButton,
					PropertyValueAccessor.Instance, this.siteConfig, "enable", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);

		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_UseSourceUrl);
			Button useSourceUrlButton = new Button(parent, SWT.CHECK);
			useSourceUrlButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Button, String> controlBinder = (IControlBinder<Button, String>) ControlBinderManager.bind(useSourceUrlButton,
					PropertyValueAccessor.Instance, this.siteConfig, "useSourceUrl", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);

		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_SupportImport);
			Button supportImportButton = new Button(parent, SWT.CHECK);
			supportImportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Button, String> controlBinder = (IControlBinder<Button, String>) ControlBinderManager.bind(supportImportButton,
					PropertyValueAccessor.Instance, this.siteConfig, "supportImport", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);

		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_SupportJumpToLast);
			Button supportJumpToLastButton = new Button(parent, SWT.CHECK);
			supportJumpToLastButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Button, String> supportJumpToLastControlBinder = (IControlBinder<Button, String>) ControlBinderManager.bind(supportJumpToLastButton,
					PropertyValueAccessor.Instance, this.siteConfig, "supportJumpToLast", //$NON-NLS-1$
					null);

			this.binderGroup.add(supportJumpToLastControlBinder);

		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_SupportPaging);
			this.supportPagingButton = new Button(parent, SWT.CHECK);
			this.supportPagingButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Button, String> supportPagingControlBinder = (IControlBinder<Button, String>) ControlBinderManager.bind(this.supportPagingButton,
					PropertyValueAccessor.Instance, this.siteConfig, "supportPaging", //$NON-NLS-1$
					null);

			this.binderGroup.add(supportPagingControlBinder);
		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_SupportAlbum);
			Button supportAlbumButton = new Button(parent, SWT.CHECK);
			supportAlbumButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Button, String> controlBinder = (IControlBinder<Button, String>) ControlBinderManager.bind(supportAlbumButton,
					PropertyValueAccessor.Instance, this.siteConfig, "supportAlbum", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);
		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_WaitTime);
			Spinner waitTimeSpinner = new Spinner(parent, SWT.BORDER);
			waitTimeSpinner.setMaximum(15);
			waitTimeSpinner.setMinimum(2);
			waitTimeSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Spinner, String> controlBinder = (IControlBinder<Spinner, String>) ControlBinderManager.bind(waitTimeSpinner,
					PropertyValueAccessor.Instance, this.siteConfig, "waitTime", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);
		}

		{
			doCreateLabel(parent, Messages.SiteConfigPropertyEditorPage_ValidPeriod);
			Spinner validPeriodSpinner = new Spinner(parent, SWT.BORDER);
			validPeriodSpinner.setMaximum(24 * 60);
			validPeriodSpinner.setMinimum(2 * 60);
			validPeriodSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			IControlBinder<Spinner, String> controlBinder = (IControlBinder<Spinner, String>) ControlBinderManager.bind(validPeriodSpinner,
					PropertyValueAccessor.Instance, this.siteConfig, "validPeriod", null); //$NON-NLS-1$

			this.binderGroup.add(controlBinder);
		}

		this.binderGroup.load();

		{
			Group group = new Group(parent, SWT.FLAT);
			group.setText(Messages.SiteConfigPropertyEditorPage_CookieTitle);
			group.setLayout(new FillLayout());

			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			group.setLayoutData(gridData);

			this.cookieText = new Text(group, SWT.MULTI | SWT.WRAP);

			String cookie = this.siteConfig.getCookie();
			if (null != cookie) {
				this.cookieText.setText(cookie);
			}

			SwtUtil.createPromptDecoration(this.cookieText, Messages.SiteConfigPropertyEditorPage_CookieTooltip);

			this.cookieText.addModifyListener(event -> {
				this.dirty = true;
			});
		}
	}

	/**
	 * @param parent
	 */
	private Label doCreateLabel(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData());

		return label;
	}

	/**
	 * @param sashForm
	 */
	private void createChannelPane(SashForm sashForm) {
		Composite pane = new Composite(sashForm, SWT.FLAT);
		pane.setLayout(new GridLayout());

		createToolBar(pane);

		Group group = new Group(pane, SWT.FLAT);
		group.setText(Messages.SiteConfigPropertyEditorPage_ChannelInfo);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(group, SWT.None);
		nameLabel.setAlignment(SWT.RIGHT);
		nameLabel.setText(Messages.SiteConfigPropertyEditorPage_Name);

		this.nameText = new Text(group, SWT.BORDER);
		this.nameText.setTextLimit(32);
		this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label displayNameLabel = new Label(group, SWT.None);
		displayNameLabel.setAlignment(SWT.RIGHT);
		displayNameLabel.setText(Messages.SiteConfigPropertyEditorPage_DisplayName);

		this.nlsDisplayNameText = new Text(group, SWT.BORDER);
		this.nlsDisplayNameText.setTextLimit(32);
		this.nlsDisplayNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label urlPatternLabel = new Label(group, SWT.None);
		urlPatternLabel.setAlignment(SWT.RIGHT);
		urlPatternLabel.setText(Messages.SiteConfigPropertyEditorPage_UrlPattern);
		urlPatternLabel.setToolTipText(Messages.SiteConfigPropertyEditorPage_UrlPatternTooltip);

		this.urlText = new Text(group, SWT.BORDER);
		this.urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.channelSupportPagingButton = new Button(group, SWT.CHECK);
		this.channelSupportPagingButton.setText(Messages.SiteConfigPropertyEditorPage_SupportPaging);
		this.channelSupportPagingButton.setLayoutData(gridData);
		this.channelSupportPagingButton.setSelection(true);

		this.autoRefreshListButton = new Button(group, SWT.CHECK);
		this.autoRefreshListButton.setText(Messages.SitePreferencePage_AutoRefreshList);
		this.autoRefreshListButton.setLayoutData(gridData);
		this.autoRefreshListButton.setSelection(true);

		this.autoParseDetailButton = new Button(group, SWT.CHECK);
		this.autoParseDetailButton.setText(Messages.SitePreferencePage_AutoRefreshDetail);
		this.autoParseDetailButton.setLayoutData(gridData);
		this.autoParseDetailButton.setSelection(true);

		this.channelTableViewer = new TableViewer(pane, SWT.FULL_SELECTION | SWT.BORDER);
		this.channelTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		final TableViewerColumn indexTableColumn = new TableViewerColumn(this.channelTableViewer, SWT.NONE);
		indexTableColumn.setLabelProvider(new IndexLabelProvider(this.channelTableViewer));
		indexTableColumn.getColumn().setText(" "); //$NON-NLS-1$
		indexTableColumn.getColumn().setWidth(36);

		final TableViewerColumn nameTableColumn = new TableViewerColumn(this.channelTableViewer, SWT.None);
		nameTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("name")); //$NON-NLS-1$
		nameTableColumn.getColumn().setText(Messages.SiteConfigPropertyEditorPage_ChannelName);
		nameTableColumn.getColumn().setWidth(100);

		final TableViewerColumn displayNameTableColumn = new TableViewerColumn(this.channelTableViewer, SWT.None);
		displayNameTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("displayName")); //$NON-NLS-1$
		displayNameTableColumn.getColumn().setText(Messages.SiteConfigPropertyEditorPage_DisplayName);
		displayNameTableColumn.getColumn().setWidth(160);

		final TableViewerColumn urlPatternTableColumn = new TableViewerColumn(this.channelTableViewer, SWT.FILL);
		urlPatternTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("urlPattern")); //$NON-NLS-1$
		urlPatternTableColumn.getColumn().setText(Messages.SiteConfigPropertyEditorPage_UrlPattern);
		urlPatternTableColumn.getColumn().setWidth(360);

		final TableViewerColumn supportPagingTableColumn = new TableViewerColumn(this.channelTableViewer, SWT.FILL);
		supportPagingTableColumn.setLabelProvider(new BeanPropertyColumnLableProvider("supportPaging")); //$NON-NLS-1$
		supportPagingTableColumn.getColumn().setText(Messages.SiteConfigPropertyEditorPage_SupportPagingColumnLabel);
		supportPagingTableColumn.getColumn().setWidth(120);

		this.channelTableViewer.getTable().setHeaderVisible(true);
		this.channelTableViewer.getTable().setLinesVisible(true);

		this.channelTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		this.editingList.addAll(this.siteConfig.getMovieChannelList());
		this.channelTableViewer.setInput(this.editingList);

	}

	/**
	 * @param pane
	 */
	private void createToolBar(Composite pane) {

		ToolBarManager toolbarManager = new ToolBarManager();

		Action addAction = new Action(Messages.SiteConfigPropertyEditorPage_Add) {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				if (validateChannelInuput()) {

					MovieChannel movieChannel = new MovieChannel();
					movieChannel.setName(SiteConfigPropertyEditorPage.this.nameText.getText());
					movieChannel.setNlsDisplayName(SiteConfigPropertyEditorPage.this.nlsDisplayNameText.getText());
					movieChannel.setUrlPattern(SiteConfigPropertyEditorPage.this.urlText.getText());
					movieChannel.setAutoRefreshList(SiteConfigPropertyEditorPage.this.autoRefreshListButton.getSelection());
					movieChannel.setAutoRefreshDetail(SiteConfigPropertyEditorPage.this.autoParseDetailButton.getSelection());
					movieChannel.setSupportPaging(SiteConfigPropertyEditorPage.this.channelSupportPagingButton.getSelection());
					SiteConfigPropertyEditorPage.this.editingList.add(movieChannel);
					SiteConfigPropertyEditorPage.this.channelTableViewer.refresh();

					SiteConfigPropertyEditorPage.this.dirty = true;

					SiteConfigPropertyEditorPage.this.firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
				}

			}

			private boolean validateChannelInuput() {

				String name = SiteConfigPropertyEditorPage.this.nameText.getText();
				if (StringUtils.isEmpty(name)) {
					showErrorMessage(Messages.SiteConfigPropertyEditorPage_NameCanNotBeEmptyErrorMessage);
					return false;
				}

				String urlPattern = SiteConfigPropertyEditorPage.this.urlText.getText();

				if (SiteConfigPropertyEditorPage.this.supportPagingButton.getSelection()) {
					if ((StringUtils.indexOf(urlPattern, "{0}") < 0)) { //$NON-NLS-1$
						showErrorMessage(Messages.SiteConfigPropertyEditorPage_UrlPatternErrorMessage);
						return false;
					}
				}

				if (StringUtils.contains(urlPattern, "/{0}/")) { //$NON-NLS-1$
					urlPattern = StringUtils.remove(urlPattern, "/{0}/"); //$NON-NLS-1$
				} else {
					urlPattern = StringUtils.remove(urlPattern, "{0}"); //$NON-NLS-1$
				}

				if (UrlValidator.getInstance().isValid(urlPattern)) {
					return true;
				} else {
					showErrorMessage(Messages.SiteConfigPropertyEditorPage_UrlPatternErrorMessage);
					return false;
				}

			}

		};
		addAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("icons/add.png")); //$NON-NLS-1$

		Action removeAction = new Action(Messages.SiteConfigPropertyEditorPage_Remove) {
			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void run() {
				IStructuredSelection selection = SiteConfigPropertyEditorPage.this.channelTableViewer.getStructuredSelection();

				if (selection.isEmpty()) {
					return;
				}

				if (MessageDialog.openConfirm(getSite().getShell(), Messages.ConfirmDialogTitle,
						Messages.SiteConfigPropertyEditorPage_ConfirmDeleteMovieChannel)) {
					List<?> list = selection.toList();
					SiteConfigPropertyEditorPage.this.editingList.removeAll(list);

					SiteConfigPropertyEditorPage.this.channelTableViewer.refresh();

					SiteConfigPropertyEditorPage.this.dirty = true;
					SiteConfigPropertyEditorPage.this.firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
				}

			}
		};
		removeAction.setImageDescriptor(WorkbenchActivator.getImageDescriptor("icons/delete.png")); //$NON-NLS-1$

		toolbarManager.add(addAction);
		toolbarManager.add(removeAction);

		ToolBar toolbar = toolbarManager.createControl(pane);
		toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	/**
	 * Create a message label to show message.<BR>
	 */
	private LabelMessageShower createMessageLabel(Composite parent) {

		CLabel label = new CLabel(parent, SWT.CENTER);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 32;
		label.setLayoutData(gridData);

		return new LabelMessageShower(label);
	}

	/**
	 * Show error message.<BR>
	 *
	 * @param message
	 */
	private void showErrorMessage(String message) {
		this.labelMessageShower.setErrorMessage(message);
	}

	/**
	 * Validate the input contents.<BR>
	 *
	 * @return
	 */
	String validate() {
		return this.binderGroup.validate(true);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void valueChanged(Control source) {
		if (!this.dirty) {
			this.dirty = true;
			this.firePropertyChange(PROP_DIRTY);
		}
	}
}
