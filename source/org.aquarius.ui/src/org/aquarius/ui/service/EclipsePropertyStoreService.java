/**
 *
 */
package org.aquarius.ui.service;

import java.io.IOException;

import org.aquarius.service.IPropertyStoreService;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class EclipsePropertyStoreService implements IPropertyStoreService {

	private IPreferenceStore preferenceStore;

	/**
	 * @param preferenceStore
	 */
	public EclipsePropertyStoreService(IPreferenceStore preferenceStore) {
		super();
		this.preferenceStore = preferenceStore;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean contains(String name) {
		return this.preferenceStore.contains(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean getBoolean(String name) {
		return this.preferenceStore.getBoolean(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public double getDouble(String name) {
		return this.preferenceStore.getDouble(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public float getFloat(String name) {
		return this.preferenceStore.getFloat(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getInt(String name) {
		return this.preferenceStore.getInt(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public long getLong(String name) {
		return this.preferenceStore.getLong(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getString(String name) {
		return this.preferenceStore.getString(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(String name, double value) {
		this.preferenceStore.setValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(String name, float value) {
		this.preferenceStore.setValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(String name, int value) {
		this.preferenceStore.setValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(String name, long value) {
		this.preferenceStore.setValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(String name, String value) {
		this.preferenceStore.setValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setValue(String name, boolean value) {
		this.preferenceStore.setValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean getDefaultBoolean(String name) {
		return this.preferenceStore.getDefaultBoolean(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public double getDefaultDouble(String name) {
		return this.preferenceStore.getDefaultDouble(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public float getDefaultFloat(String name) {
		return this.preferenceStore.getDefaultFloat(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int getDefaultInt(String name) {
		return this.preferenceStore.getDefaultInt(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public long getDefaultLong(String name) {
		return this.preferenceStore.getDefaultLong(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String getDefaultString(String name) {
		return this.preferenceStore.getDefaultString(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean isDefault(String name) {
		return this.preferenceStore.isDefault(name);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean needsSaving() {
		return this.preferenceStore.needsSaving();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void putValue(String name, String value) {
		this.preferenceStore.putValue(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setDefault(String name, double value) {
		this.preferenceStore.setDefault(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setDefault(String name, float value) {
		this.preferenceStore.setDefault(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setDefault(String name, int value) {
		this.preferenceStore.setDefault(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setDefault(String name, long value) {
		this.preferenceStore.setDefault(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setDefault(String name, String value) {
		this.preferenceStore.setDefault(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setDefault(String name, boolean value) {
		this.preferenceStore.setDefault(name, value);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setToDefault(String name) {
		this.preferenceStore.setToDefault(name);
	}

	/**
	 * {@inheritDoc}}
	 *
	 * @throws IOException
	 */
	@Override
	public void save() throws IOException {

		if (this.preferenceStore instanceof IPersistentPreferenceStore) {
			((IPersistentPreferenceStore) this.preferenceStore).save();
			return;
		}

		throw new UnsupportedOperationException("Not supported yet");

	}

}
