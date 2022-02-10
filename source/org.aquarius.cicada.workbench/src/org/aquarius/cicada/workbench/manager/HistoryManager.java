/**
 * 
 */
package org.aquarius.cicada.workbench.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aquarius.cicada.core.util.MovieUtil;
import org.aquarius.cicada.workbench.SearchKeywordModel;
import org.aquarius.cicada.workbench.WorkbenchActivator;
import org.aquarius.log.LogUtil;
import org.aquarius.util.StringUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class HistoryManager {

	private static final int MaxHistoryCount = 10;

	private static final String SearchKeywordModelHistoryKey = HistoryManager.class.getName() + ".SearchKeywordModel.Hisotry";

	private static final String TitleHistoryKey = HistoryManager.class.getName() + ".Title.Hisotry";

	private static final HistoryManager instance = new HistoryManager();

	private List<String> titleHistoryList = new ArrayList<>();

	private List<SearchKeywordModel> searchKeywordModelHistoryList;

	private Logger log = LogUtil.getLogger(HistoryManager.class);

	/**
	 * @return the instance
	 */
	public static HistoryManager getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private HistoryManager() {

		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

		this.loadSearchKeywordHistories(store);
		this.loadTitleHistories(store);

	}

	public void clearHistories() {
		this.searchKeywordModelHistoryList.clear();
		doSaveSearchHistories();
	}

	/**
	 * @param store
	 */
	private void loadSearchKeywordHistories(IPreferenceStore store) {
		try {
			String historyValue = store.getString(SearchKeywordModelHistoryKey);
			if (StringUtils.isNotEmpty(historyValue)) {
				this.searchKeywordModelHistoryList = JSON.parseArray(historyValue, SearchKeywordModel.class);
			}
		} catch (Exception e) {
			this.log.error("init SearchKeywordModelManager", e);
		}

		if (null == this.searchKeywordModelHistoryList) {
			this.searchKeywordModelHistoryList = new ArrayList<>();
		}
	}

	/**
	 * @return the modelList
	 */
	public List<SearchKeywordModel> getSearchKeywordModelHistoryList() {
		return this.searchKeywordModelHistoryList;
	}

	/**
	 * 
	 * @param newModel
	 */
	public synchronized void addSearchKeywordModelHistory(SearchKeywordModel newModel) {
		if (null == newModel) {
			return;
		}

		for (SearchKeywordModel model : this.searchKeywordModelHistoryList) {

			if (StringUtils.equalsIgnoreCase(model.getKeyword(), newModel.getKeyword())) {
				this.searchKeywordModelHistoryList.remove(model);
				break;
			}
		}

		this.searchKeywordModelHistoryList.add(0, newModel);

		if (this.searchKeywordModelHistoryList.size() > MaxHistoryCount) {
			this.searchKeywordModelHistoryList.remove(MaxHistoryCount);
		}

		doSaveSearchHistories();

	}

	/**
	 * 
	 */
	private void doSaveSearchHistories() {
		IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

		try {
			String historyValue = JSON.toJSONString(this.searchKeywordModelHistoryList);
			store.setValue(SearchKeywordModelHistoryKey, historyValue);
		} catch (Exception e) {
			this.log.error("save search histories", e);
		}
	}

	/**
	 *
	 */
	private void loadTitleHistories(IPreferenceStore preferenceStore) {

		String keywordContent = preferenceStore.getString(TitleHistoryKey);
		String[] keywords = MovieUtil.split(keywordContent);

		if (ArrayUtils.isNotEmpty(keywords)) {
			CollectionUtils.addAll(this.titleHistoryList, keywords);
		}

	}

	/**
	 * Add a history keyword.<BR>
	 *
	 * @param titleKeyword
	 */
	public void addTitleHistory(String titleKeyword) {
		if (!this.titleHistoryList.contains(titleKeyword)) {
			this.titleHistoryList.add(0, titleKeyword);
			if (this.titleHistoryList.size() > WorkbenchActivator.getDefault().getConfiguration().getHistoryKeywordCount()) {
				this.titleHistoryList.remove((this.titleHistoryList.size() - 1));
			}

			IPreferenceStore store = WorkbenchActivator.getDefault().getPreferenceStore();

			try {
				store.setValue(TitleHistoryKey, StringUtils.join(this.titleHistoryList, StringUtil.ContentSeparator));
			} catch (Exception e) {
				this.log.error("save title histories", e);
			}
		}
	}

	/**
	 * @return the titleHistoryList
	 */
	public List<String> getTitleHistoryList() {
		return this.titleHistoryList;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getRecentSites() {
		if (CollectionUtils.isEmpty(this.searchKeywordModelHistoryList)) {
			return StringUtil.NullStringList;
		}

		return this.searchKeywordModelHistoryList.get(0).getSites();
	}
}
