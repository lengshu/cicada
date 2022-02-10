/**
 * 
 */
package org.aquarius.cicada.core.internal.js;

import java.lang.reflect.Type;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.serializer.DateCodec;

/**
 * @author aquarius.github@hotmail.com
 *
 */
public class InternalDateCodec extends DateCodec {

	/**
	 * 
	 */
	public InternalDateCodec() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T deserialze(DefaultJSONParser jsonParser, Type type, Object object, String format, int features) {

		try {
			return super.deserialze(jsonParser, type, object, format, features);
		} catch (Exception e) {
			return null;
		}
	}

}
