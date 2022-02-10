/**
 *
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

/**
 * It is designed to support duration.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class Range {

	private int start = -1;

	private int end = Integer.MAX_VALUE;

	/**
	 *
	 */
	public Range() {
		super();
	}

	/**
	 * @param start
	 * @param end
	 */
	public Range(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return this.start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return this.end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String toString() {
		if (this.end == Integer.MAX_VALUE) {
			return "[" + this.start + "-∞]";
		} else {
			return "[" + this.start + "-" + this.end + "]";
		}
	}

}
