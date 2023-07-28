/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 5, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Mar 5, 2012
 */
public class TrnBillChangeStmntMpg implements java.io.Serializable {

	private Long billChngStmntId;
	private Long billNo;
	private String chngStmntIdList;
	private String locationCode;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Integer dbId;
	private Integer forMonth;
	private String schemeCode;

	/**
	 * @param billChngStmntId
	 * @param billNo
	 * @param chngStmntIdList
	 * @param locationCode
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param dbId
	 */
	public TrnBillChangeStmntMpg() {

	}

	public TrnBillChangeStmntMpg(Long billChngStmntId, Long billNo, String chngStmntIdList, String locationCode, Long createdUserId, Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate, Integer dbId, Integer forMonth, String schemeCode) {

		super();
		this.billChngStmntId = billChngStmntId;
		this.billNo = billNo;
		this.chngStmntIdList = chngStmntIdList;
		this.locationCode = locationCode;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
		this.forMonth = forMonth;
		this.schemeCode = schemeCode;
	}

	public Long getBillChngStmntId() {

		return billChngStmntId;
	}

	public void setBillChngStmntId(Long billChngStmntId) {

		this.billChngStmntId = billChngStmntId;
	}

	public Long getBillNo() {

		return billNo;
	}

	public void setBillNo(Long billNo) {

		this.billNo = billNo;
	}

	public String getChngStmntIdList() {

		return chngStmntIdList;
	}

	public void setChngStmntIdList(String chngStmntIdList) {

		this.chngStmntIdList = chngStmntIdList;
	}

	public String getLocationCode() {

		return locationCode;
	}

	public void setLocationCode(String locationCode) {

		this.locationCode = locationCode;
	}

	public Long getCreatedUserId() {

		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {

		this.createdUserId = createdUserId;
	}

	public Long getCreatedPostId() {

		return createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {

		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	public Long getUpdatedUserId() {

		return updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {

		this.updatedUserId = updatedUserId;
	}

	public Long getUpdatedPostId() {

		return updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {

		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {

		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		this.updatedDate = updatedDate;
	}

	public Integer getDbId() {

		return dbId;
	}

	public void setDbId(Integer dbId) {

		this.dbId = dbId;
	}

	public Integer getForMonth() {

		return forMonth;
	}

	public void setForMonth(Integer forMonth) {

		this.forMonth = forMonth;
	}

	public String getSchemeCode() {

		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {

		this.schemeCode = schemeCode;
	}

}
