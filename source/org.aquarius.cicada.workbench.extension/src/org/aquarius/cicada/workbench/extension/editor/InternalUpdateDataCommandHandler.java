/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.aquarius.cicada.core.model.Movie;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;

/**
 *
 *
 * @author aquarius.github@gmail.com
 *
 */
public class InternalUpdateDataCommandHandler extends UpdateDataCommandHandler {

	private final IRowDataProvider<Movie> bodyDataProvider;

	private Set<Movie> modifiedMovieList = new ListOrderedSet<>();

	/**
	 * @param dataLayer
	 */
	public InternalUpdateDataCommandHandler(DataLayer dataLayer) {
		super(dataLayer);

		this.bodyDataProvider = (IRowDataProvider<Movie>) dataLayer.getDataProvider();
	}

	/**
	 * Return whether the modified movie list is empty.<BR>
	 *
	 * @return
	 */
	boolean isDirty() {
		return !this.modifiedMovieList.isEmpty();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected boolean doCommand(UpdateDataCommand command) {
		boolean committed = super.doCommand(command);

		if (committed) {
			int rowIndex = command.getLayer().getRowIndexByPosition(command.getRowPosition());
			Movie movie = this.bodyDataProvider.getRowObject(rowIndex);

			this.modifiedMovieList.add(movie);
		}

		return committed;
	}

	/**
	 * @return the modifiedMovieList
	 */
	public Collection<Movie> getModifiedMovieList() {
		return this.modifiedMovieList;
	}

	/**
	 * Clear the modified movies.<BR>
	 */
	public void clearData() {
		this.modifiedMovieList.clear();
	}

}
