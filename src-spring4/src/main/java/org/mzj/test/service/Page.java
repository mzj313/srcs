package org.mzj.test.service;

import java.util.List;

public class Page {

	/**
	 * 第几页
	 */
	private int pageNum;
	
	/**
	 * 每页大小
	 */
	private int pageSize;
	
	/**
	 * 总页数
	 */
	private int pages;
	
	/**
	 * 总记录数
	 */
	private int total;
	
	/**
	 * 
	 */
	private List<?> list;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int count) {
		this.total = count;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "Page [pageNum=" + pageNum + ", pageSize=" + pageSize
				+ ", pages=" + pages + ", total=" + total + ", list=" + list
				+ "]";
	}
}
