/**
 * 
 */
package org.aquarius.cicada.core.spi;

import java.net.HttpURLConnection;

import org.aquarius.service.INameService;
import org.aquarius.util.base.AbstractComparable;
import org.aquarius.util.mark.IUrlAcceptable;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public abstract class AbstractHttpHeaderProcessor extends AbstractComparable<AbstractHttpHeaderProcessor>
		implements IUrlAcceptable, INameService<AbstractHttpHeaderProcessor> {

	/**
	 * 
	 */
	public AbstractHttpHeaderProcessor() {
		// Nothing to do
	}

	public abstract void process(HttpURLConnection httpConnection);

}
