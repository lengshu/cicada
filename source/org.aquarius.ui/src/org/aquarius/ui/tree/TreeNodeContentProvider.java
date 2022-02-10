/**
 *
 */
package org.aquarius.ui.tree;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.MutableTreeNode;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Tree content provider to support swing TreeNode model.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class TreeNodeContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return this.getChildren(inputElement);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MutableTreeNode) {
			Enumeration<?> enu = ((MutableTreeNode) parentElement).children();
			List<?> list = Collections.list(enu);
			return list.toArray(new Object[list.size()]);
		}
		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Object getParent(Object element) {

		if (element instanceof MutableTreeNode) {
			((MutableTreeNode) element).getParent();
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof MutableTreeNode) {
			return ((MutableTreeNode) element).getChildCount() > 0;
		}
		return false;
	}

}
