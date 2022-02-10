/**
 *
 */
package org.aquarius.cicada.core.helper;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class Range {

	private int start;

	private int end;

	/**
	 *
	 */
	Range() {
		super();
	}

	/**
	 * @param start
	 * @param end
	 */
	Range(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the start
	 */
	int getStart() {
		return this.start;
	}

	/**
	 * @param start the start to set
	 */
	void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	int getEnd() {
		return this.end;
	}

	/**
	 * @param end the end to set
	 */
	void setEnd(int end) {
		this.end = end;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String toString() {
		return "[" + this.start + "-" + this.end + "]";
	}
}
