/**
 *
 */
package org.aquarius.downloader.ui.view.tree;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.ObjectUtils;
import org.aquarius.util.collection.Entry;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Tree provider by states.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class TaskStateLabelProvider extends BaseLabelProvider implements ILabelProvider {

	private Map<Object, Image> imageMapping;

	/**
	 * @param imageMapping
	 */
	public TaskStateLabelProvider(Map<Object, Image> imageMapping) {
		super();
		this.imageMapping = imageMapping;

		if (null == this.imageMapping) {
			this.imageMapping = new HashMap<>();
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof DefaultMutableTreeNode) {
			Entry<?, ?> entry = (Entry<?, ?>) ((DefaultMutableTreeNode) element).getUserObject();
			return this.imageMapping.get(entry.getKey());
		}

		return null;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof DefaultMutableTreeNode) {
			Entry<?, ?> entry = (Entry<?, ?>) ((DefaultMutableTreeNode) element).getUserObject();

			return ObjectUtils.toString(entry.getValue());
		}

		return null;
	}

}
