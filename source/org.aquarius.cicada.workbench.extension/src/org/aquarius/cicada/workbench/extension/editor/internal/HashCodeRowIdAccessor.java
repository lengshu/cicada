/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.io.Serializable;

import org.eclipse.nebula.widgets.nattable.data.IRowIdAccessor;

/**
 * use system hashcode as the id.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class HashCodeRowIdAccessor<R> implements IRowIdAccessor<R> {

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Serializable getRowId(R rowObject) {
		if (null == rowObject) {
			return null;
		} else {
			return rowObject.hashCode();
		}
	}

}
