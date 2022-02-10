/**
 *
 */
package org.aquarius.util.validator;

/**
 *
 * Validator interface.
 *
 * @author aquarius.github@gmail.com
 *
 */
public interface IValueValidator<T> {

	public String validate(T value);

}
