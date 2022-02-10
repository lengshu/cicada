/**
 *
 */
package org.aquarius.cicada.workbench.editor.internal;

import org.aquarius.cicada.workbench.editor.FilterSite;
import org.aquarius.cicada.workbench.editor.SiteBrowserEditor;
import org.aquarius.ui.util.SwtUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Outline to Filter.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FilterContentOutlinePage extends Page implements IContentOutlinePage {

	private FilterContentComposite contentComposite;

	private FilterSite filterSite;

	private SiteBrowserEditor siteEditor;

	/**
	 * @param site
	 */
	public FilterContentOutlinePage(SiteBrowserEditor siteEditor, FilterSite filterSite) {
		super();
		this.siteEditor = siteEditor;
		this.filterSite = filterSite;

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public ISelection getSelection() {
		return StructuredSelection.EMPTY;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setSelection(ISelection selection) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void createControl(Composite top) {
		this.contentComposite = new FilterContentComposite(this.siteEditor, this.filterSite, top, SWT.NONE);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Control getControl() {
		return this.contentComposite;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setFocus() {
		if (null != this.contentComposite) {
			this.contentComposite.setFocus();
		}
	}

	public void setVisible(boolean visible) {
		try {
			if (SwtUtil.isValid(this.contentComposite)) {
				this.contentComposite.setVisible(visible);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
