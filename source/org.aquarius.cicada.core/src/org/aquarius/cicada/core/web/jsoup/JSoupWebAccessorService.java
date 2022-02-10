/**
 *
 */
package org.aquarius.cicada.core.web.jsoup;

import org.aquarius.cicada.core.spi.web.IWebAccessor;
import org.aquarius.cicada.core.spi.web.IWebAccessorService;
import org.aquarius.cicada.core.spi.web.IWebAccessorVisible;
import org.aquarius.util.base.AbstractComparable;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class JSoupWebAccessorService extends AbstractComparable<IWebAccessorService> implements IWebAccessorService {

	private static final String Name = "jsoup";

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public <T> T visit(Visitor<T> visitor) {

		IWebAccessor webAccessor = new JSoupWebAccessor();

		return visitor.visit(webAccessor);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getName() {
		return Name;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void close() {
		// Nothing to do

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public IWebAccessorVisible getVisibleState() {
		return IWebAccessorVisible.none;
	}

}
