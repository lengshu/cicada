/**
 *
 */
package org.aquarius.ui.action;

import org.apache.commons.lang.reflect.MethodUtils;
import org.aquarius.log.LogUtil;
import org.aquarius.util.AssertUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.slf4j.Logger;

/**
 *
 *
 * @author aquarius.github@gmail.com
 *
 */
public class ReflectionAction extends Action {

	private static Logger logger = LogUtil.getLogger(ReflectionAction.class);

	private Object target;

	private String methodName;

	/**
	 * @param target
	 * @param methodName
	 */
	public ReflectionAction(Object target, String methodName) {
		super();
		this.target = target;
		this.methodName = methodName;
	}

	/**
	 *
	 * @param target
	 * @param methodName
	 * @param text
	 * @param image
	 */
	public ReflectionAction(Object target, String methodName, String text, ImageDescriptor image) {
		super(text, image);

		AssertUtil.assertNotNull(target);
		AssertUtil.assertNotNull(methodName);

		this.target = target;
		this.methodName = methodName;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void run() {

		try {
			MethodUtils.invokeMethod(this.target, this.methodName, null);
		} catch (Exception e) {
			logger.error("invoke method of " + this.methodName, e);
		}

	}

}
