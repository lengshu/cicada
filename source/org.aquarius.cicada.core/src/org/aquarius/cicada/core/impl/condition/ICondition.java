/**
 * 
 */
package org.aquarius.cicada.core.impl.condition;

import org.aquarius.cicada.core.model.result.MovieListResult;

/**
 * To filter movies.<BR>
 * 
 * @author aquarius.github@gmail.com
 *
 */
public interface ICondition {

	public boolean checkShouldContinue(MovieListResult model);
}
