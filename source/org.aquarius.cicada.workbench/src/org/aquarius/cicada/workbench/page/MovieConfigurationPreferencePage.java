/**
 *
 */
package org.aquarius.cicada.workbench.page;

import java.util.List;

import org.aquarius.cicada.core.RuntimeManager;
import org.aquarius.cicada.core.config.MovieConfiguration;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.cicada.workbench.RuntimeConstant;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.cicada.workbench.page.internal.PixelFieldEditor;
import org.aquarius.downloader.core.DownloadManager;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for movie configuration.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class MovieConfigurationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public MovieConfigurationPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();
		this.setPreferenceStore(store);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = this.getFieldEditorParent();

//		FieldEditor browserVisibleFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_BrowserVisible,
//				Messages.MovieConfigurationPreferencePage_BrowserVisiblity, parent);

		FieldEditor autoRefreshSiteFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_AutoRefreshSite,
				Messages.MovieConfigurationPreferencePage_AutoRefreshSite, parent);

		FieldEditor autoRefreshMovieFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_AutoRefreshMovie,
				Messages.MovieConfigurationPreferencePage_AutoRefreshMovie, parent);

		BooleanFieldEditor useTitleAsFileNameFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_UseTitleAsFileName,
				Messages.MovieConfigurationPreferencePage_UseTitleAsFileName, parent);

		FieldEditor pasteContentToClipboardFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_PasteContentToClipboard,
				Messages.MovieConfigurationPreferencePage_PasteContentToClipboard, parent);

		FieldEditor keywordFrequencySortFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_KeywordFrequencySort,
				Messages.MovieConfigurationPreferencePage_UseFrequencyToSortKeyword, parent);

//		this.addField(browserVisibleFieldEditor);
		this.addField(autoRefreshSiteFieldEditor);
		this.addField(autoRefreshMovieFieldEditor);
		this.addField(useTitleAsFileNameFieldEditor);
		this.addField(pasteContentToClipboardFieldEditor);
		this.addField(keywordFrequencySortFieldEditor);

		BooleanFieldEditor confirmDownloadMovieFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_ConfirmDownloadMovie,
				Messages.MovieConfigurationPreferencePage_ConfirmDownloadMovie, parent);
		this.addField(confirmDownloadMovieFieldEditor);

		BooleanFieldEditor confirmDeleteMovieFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_ConfirmDeleteMovie,
				Messages.MovieConfigurationPreferencePage_ConfirmDeleteMovie, parent);
		this.addField(confirmDeleteMovieFieldEditor);

		BooleanFieldEditor useLooseCheckStrategyMovieFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_StrictCheckStrategy,
				Messages.MovieConfigurationPreferencePage_UseStrictCheckStrategy, parent);
		this.addField(useLooseCheckStrategyMovieFieldEditor);

		if (RuntimeManager.isDebug()) {
			BooleanFieldEditor useDownloadInfoCacheMovieFieldEditor = new BooleanFieldEditor(MovieConfiguration.Key_UseDownloadInfoCache,
					Messages.MovieConfigurationPreferencePage_UseDownloadInfoCache, parent);
			this.addField(useDownloadInfoCacheMovieFieldEditor);
		}

		IntegerFieldEditor clearnPeriodIntegerFieldEditor = new IntegerFieldEditor(MovieConfiguration.Key_CleanPeriod,
				Messages.MovieConfigurationPreferencePage_ClearDatabasePeriod, parent);

		clearnPeriodIntegerFieldEditor.setValidRange(1, 100);

		this.addField(clearnPeriodIntegerFieldEditor);

		IntegerFieldEditor maxErrorCountIntegerFieldEditor = new IntegerFieldEditor(MovieConfiguration.Key_MaxErrorCount,
				Messages.MovieConfigurationPreferencePage_MaxErrorCount, parent);

		maxErrorCountIntegerFieldEditor.setValidRange(5, RuntimeConstant.RefreshBatchCount);

		this.addField(maxErrorCountIntegerFieldEditor);

		FieldEditor dualListFieldEditor = new PixelFieldEditor(MovieConfiguration.Key_Pixels, Messages.MovieConfigurationPreferencePage_Pixels, parent);
		this.addField(dualListFieldEditor);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean performOk() {
		boolean flag = super.performOk();

		if (flag) {
			MovieConfiguration configuration = RuntimeManager.getInstance().getConfiguration();

			List<String> pixelList = configuration.getPixelList();
			DownloadManager.getInstance().getConfiguration().setPixels(pixelList);
		}

		return flag;
	}

}
