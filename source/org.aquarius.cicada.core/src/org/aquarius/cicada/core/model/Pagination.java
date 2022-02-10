/**
 *
 */
package org.aquarius.cicada.core.model;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class Pagination {

	private int currentPageCount;

	private int countPerPage;

	/**
	 * @param currentPageCount
	 * @param countPerPage
	 */
	public Pagination(int currentPageCount, int countPerPage) {
		super();
		this.currentPageCount = currentPageCount;
		this.countPerPage = countPerPage;
	}

	/**
	 *
	 */
	public Pagination() {
		super();
	}

	/**
	 * @return the currentPageCount
	 */
	public int getCurrentPageCount() {
		return this.currentPageCount;
	}

	/**
	 * @param currentPageCount the currentPageCount to set
	 */
	public void setCurrentPageCount(int currentPageCount) {
		this.currentPageCount = currentPageCount;
	}

	/**
	 * @return the countPerPage
	 */
	public int getCountPerPage() {
		return this.countPerPage;
	}

	/**
	 * @param countPerPage the countPerPage to set
	 */
	public void setCountPerPage(int countPerPage) {
		this.countPerPage = countPerPage;
	}

	public int getStart() {
		return this.countPerPage * this.currentPageCount;
	}
}
