/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Nov 30, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.helper;

/**
 * Class Description -
 * 
 * 
 * @author 365450
 * @version 0.1
 * @since JDK 5.0 Nov 30, 2011
 */
public class ColumnVo {

	String ColumnName = "";
	int alignMent = 1;
	int width = 0;
	//
	int dataType = 1;
	boolean groupingOrder = false;
	int groupIndex = 0;
	int groupValueColumn = -1;
	int displayTotal = 0;
	boolean allowWrap = false;
	boolean currancyFormated = false;
	boolean isStatic = true;
	String totalLabel = null;

	public String getTotalLabel() {

		return totalLabel;
	}

	public void setTotalLabel(String totalLabel) {

		this.totalLabel = totalLabel;
	}

	public ColumnVo(String columnName, int alignMent, int width, boolean groupingOrder, int groupIndex, int displayTotal, boolean allowWrap) {

		super();
		ColumnName = columnName;
		this.alignMent = alignMent;
		this.width = width;
		this.groupingOrder = groupingOrder;
		this.groupIndex = groupIndex;
		this.displayTotal = displayTotal;
		this.allowWrap = allowWrap;

	}

	public ColumnVo(String columnName, int alignMent, int width, int displayTotal, boolean allowWrap, boolean currancyFormated) {

		super();
		ColumnName = columnName;
		this.alignMent = alignMent;
		this.width = width;
		this.displayTotal = displayTotal;
		this.allowWrap = allowWrap;
		this.currancyFormated = currancyFormated;

	}

	public ColumnVo(String columnName, int alignMent, int width, int displayTotal, boolean allowWrap, boolean currancyFormated, boolean isStatic) {

		super();
		ColumnName = columnName;
		this.alignMent = alignMent;
		this.width = width;
		this.displayTotal = displayTotal;
		this.allowWrap = allowWrap;
		this.currancyFormated = currancyFormated;
		this.isStatic = isStatic;

	}

	public ColumnVo(String columnName, int alignMent, int width, int displayTotal, boolean allowWrap, boolean currancyFormated, boolean isStatic, int dataType) {

		super();
		ColumnName = columnName;
		this.alignMent = alignMent;
		this.width = width;
		this.displayTotal = displayTotal;
		this.allowWrap = allowWrap;
		this.currancyFormated = currancyFormated;
		this.isStatic = isStatic;
		this.dataType = dataType;
	}

	public ColumnVo(String columnName, int alignMent, int width, int displayTotal, boolean allowWrap, boolean currancyFormated, boolean isStatic, String totalLabel) {

		super();
		ColumnName = columnName;
		this.alignMent = alignMent;
		this.width = width;
		this.displayTotal = displayTotal;
		this.allowWrap = allowWrap;
		this.currancyFormated = currancyFormated;
		this.isStatic = isStatic;
		this.totalLabel = totalLabel;
	}

	public String getColumnName() {

		return ColumnName;
	}

	public void setColumnName(String columnName) {

		ColumnName = columnName;
	}

	public int getAlignMent() {

		return alignMent;
	}

	public void setAlignMent(int alignMent) {

		this.alignMent = alignMent;
	}

	public int getWidth() {

		return width;
	}

	public int getRealWidth() {

		return width; // changed from width -1 to width
	}

	public void setWidth(int width) {

		this.width = width;
	}

	public int getDisplayTotal() {

		return displayTotal;
	}

	public void setDisplayTotal(int displayTotal) {

		this.displayTotal = displayTotal;
	}

	public boolean isAllowWrap() {

		return allowWrap;
	}

	public void setAllowWrap(boolean allowWrap) {

		this.allowWrap = allowWrap;
	}

	public boolean isGroupingOrder() {

		return groupingOrder;
	}

	public void setGroupingOrder(boolean groupingOrder) {

		this.groupingOrder = groupingOrder;
	}

	public int getGroupIndex() {

		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {

		this.groupIndex = groupIndex;
	}

	public boolean isCurrancyFormated() {

		return currancyFormated;
	}

	public void setCurrancyFormated(boolean currancyFormated) {

		this.currancyFormated = currancyFormated;
	}

	public boolean isStatic() {

		return isStatic;
	}

	public void setStatic(boolean isStatic) {

		this.isStatic = isStatic;
	}

	public int getGroupValueColumn() {

		if (groupValueColumn == -1) {
			return groupIndex;
		} else {
			return groupValueColumn;
		}
	}

	public void setGroupValueColumn(int groupValueColumn) {

		this.groupValueColumn = groupValueColumn;
	}

	public int getDataType() {

		return dataType;
	}

	public void setDataType(int dataType) {

		this.dataType = dataType;
	}

}