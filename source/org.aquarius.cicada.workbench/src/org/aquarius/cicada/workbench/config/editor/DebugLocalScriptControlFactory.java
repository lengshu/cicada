/**
 *
 */
package org.aquarius.cicada.workbench.config.editor;

import java.text.MessageFormat;

import javax.script.ScriptEngine;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aquarius.cicada.workbench.Messages;
import org.aquarius.ui.json.JSonTreeBuilder;
import org.aquarius.ui.tree.TreeNodeContentProvider;
import org.aquarius.ui.util.ClipboardUtil;
import org.aquarius.ui.util.SwtUtil;
import org.aquarius.util.js.JavaScriptEngineManageer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Create control to debug script in a browser.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class DebugLocalScriptControlFactory {

	private String stringResult;

	private Class<?> targetClass;

	private JSONObject jsonObject;

	private StyledText scriptText;

	private TreeViewer jsonTreeViewer;

	private StyledText jsonResultText;

	private CTabFolder jsonFolder;

	/**
	 * @param parent
	 * @param style
	 */
	public DebugLocalScriptControlFactory() {

	}

	/**
	 *
	 * @param parent
	 */
	public Control createControl(Composite parent) {

		SashForm sashForm = new SashForm(parent, SWT.BORDER);
		sashForm.setSashWidth(6);

		createScriptEditor(sashForm);
		createJSonViewer(sashForm);

		sashForm.setWeights(new int[] { 60, 40 });

		return sashForm;
	}

	/**
	 * execute script in a browser.<BR>
	 */
	protected void executeScript() {

		this.stringResult = null;
		this.jsonObject = null;

		doExecuteScript();
		this.jsonResultText.setText(this.stringResult);

		if (null != this.jsonObject) {
			DefaultMutableTreeNode rootNode = JSonTreeBuilder.buildRootTree(this.jsonObject);
			this.jsonTreeViewer.setInput(rootNode);
			this.jsonFolder.setSelection(0);
		} else {
			this.jsonTreeViewer.setInput(null);
			this.jsonFolder.setSelection(1);
		}

		this.jsonTreeViewer.expandAll();
	}

	/**
	 * execute script in a browser.<BR>
	 */
	private boolean doExecuteScript() {

		try {
			ScriptEngine scriptEngine = JavaScriptEngineManageer.getInstance().createEngine();

			Object resultObject = scriptEngine.eval(this.scriptText.getText());

			this.stringResult = ObjectUtils.toString(resultObject, ""); //$NON-NLS-1$

		} catch (Exception e) {
			this.stringResult = ExceptionUtils.getFullStackTrace(e);
			return false;
		}

		try {
			this.jsonObject = JSON.parseObject(this.stringResult);
			this.stringResult = JSON.toJSONString(this.jsonObject, true);
		} catch (Exception e) {
			this.stringResult = Messages.DebugScriptEditorPage_ResultNotJsonErrorMessage + this.stringResult;
			return true;
		}

		if (null != this.targetClass && (String.class != this.targetClass)) {
			try {
				JSON.parseObject(this.stringResult, this.targetClass);
			} catch (Exception e) {
				String message = Messages.DebugScriptEditorPage_ResultJsonIsNotTargetClassErrorMessage;
				this.stringResult = MessageFormat.format(message, this.targetClass.getName());
				return false;
			}
		}

		return true;
	}

	/**
	 * Create the script editor control.<BR>
	 *
	 * @param sashForm
	 */
	private void createScriptEditor(SashForm sashForm) {

		Composite pane = new Composite(sashForm, SWT.BORDER);
		{
			GridLayout layout = new GridLayout(1, false);
			layout.horizontalSpacing = 8;
			pane.setLayout(layout);
		}

		this.scriptText = SwtUtil.createStyledTextWithLine(pane, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		{
			GridData gridData = new GridData(GridData.FILL_BOTH);
			this.scriptText.setLayoutData(gridData);
		}

		Button button = new Button(pane, SWT.BORDER);
		button.setText(Messages.DebugScriptEditorPage_ExecuteScript);

		{
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			button.setLayoutData(gridData);
		}

		button.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeScript();
			}
		});

	}

	/**
	 * Create a tree to show json content.<BR>
	 *
	 * @param sashForm
	 */
	private void createJSonViewer(SashForm sashForm) {

		Composite parent = new Composite(sashForm, SWT.FLAT);
		parent.setLayout(new GridLayout());

		this.jsonFolder = new CTabFolder(parent, SWT.BORDER);

		CTabItem treeTabItem = new CTabItem(this.jsonFolder, SWT.BORDER);
		treeTabItem.setText(Messages.DebugScriptEditorPage_JSonTree);

		this.jsonTreeViewer = new TreeViewer(this.jsonFolder);
		treeTabItem.setControl(this.jsonTreeViewer.getTree());

		CTabItem textTabItem = new CTabItem(this.jsonFolder, SWT.BORDER);
		textTabItem.setText(Messages.DebugScriptEditorPage_SourceText);

		this.jsonResultText = SwtUtil.createStyledTextWithLine(this.jsonFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		textTabItem.setControl(this.jsonResultText);

		this.jsonTreeViewer.setContentProvider(new TreeNodeContentProvider());
		this.jsonTreeViewer.setLabelProvider(new ColumnLabelProvider());

		this.jsonFolder.setSelection(0);

		this.jsonFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button copyButton = new Button(parent, SWT.PUSH);
		copyButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ClipboardUtil.setClipboardString(DebugLocalScriptControlFactory.this.stringResult);
			}
		});

		copyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		copyButton.setText(Messages.DebugLocalScriptControlFactory_CopyToClipboard);
	}

	/**
	 * Validate the script and show result.<BR>
	 *
	 * @return
	 */
	String validate() {
		if (this.doExecuteScript()) {
			return null;
		} else {
			return this.stringResult;
		}
	}

	/**
	 * @return the targetClass
	 */
	public Class<?> getTargetClass() {
		return this.targetClass;
	}

	/**
	 * @param targetClass the targetClass to set
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

}
