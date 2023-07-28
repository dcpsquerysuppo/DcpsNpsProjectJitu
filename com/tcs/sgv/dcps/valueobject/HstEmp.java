/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 22, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 22, 2011
 */
public class HstEmp implements Serializable {

	private Long hstdcpsId;
	private Long dbId;
	private Long locId;
	private String ddoCode;
	private Long dcpsEmpId;
	private Date startDate;
	private Date endDate;
	private String reasonForDeselection;
	private String remarksForDeselection;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	private String orderNo;
	private Date orderDate;

	public HstEmp() {

	}

	public HstEmp(Long hstdcpsId, Long dbId, Long locId, String ddoCode,
			Long dcpsEmpId, Date startDate, Date endDate,
			String reasonForDeselection, String remarksForDeselection,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate,String orderNo,Date orderDate ) {

		super();
		this.hstdcpsId = hstdcpsId;
		this.dbId = dbId;
		this.locId = locId;
		this.ddoCode = ddoCode;
		this.dcpsEmpId = dcpsEmpId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.reasonForDeselection = reasonForDeselection;
		this.remarksForDeselection = remarksForDeselection;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.orderNo=orderNo;
		this.orderDate=orderDate;
	}
	

	public Long getHstdcpsId() {

		return hstdcpsId;
	}

	public void setHstdcpsId(Long hstdcpsId) {

		this.hstdcpsId = hstdcpsId;
	}

	public Long getDbId() {

		return dbId;
	}

	public void setDbId(Long dbId) {

		this.dbId = dbId;
	}

	public Long getLocId() {

		return locId;
	}

	public void setLocId(Long locId) {

		this.locId = locId;
	}

	public String getDdoCode() {

		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {

		this.ddoCode = ddoCode;
	}

	public Long getDcpsEmpId() {

		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {

		this.dcpsEmpId = dcpsEmpId;
	}

	public Date getStartDate() {

		return startDate;
	}

	public void setStartDate(Date startDate) {

		this.startDate = startDate;
	}

	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	public String getReasonForDeselection() {

		return reasonForDeselection;
	}

	public void setReasonForDeselection(String reasonForDeselection) {

		this.reasonForDeselection = reasonForDeselection;
	}

	public String getRemarksForDeselection() {

		return remarksForDeselection;
	}

	public void setRemarksForDeselection(String remarksForDeselection) {

		this.remarksForDeselection = remarksForDeselection;
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

	public String getOrderNo() {

		return orderNo;
	}

	public void setOrderNo(String orderNo) {

		this.orderNo = orderNo;
	}
	
	public Date getOrderDate() {

		return orderDate;
	}

	public void setOrderDate(Date orderDate) {

		this.orderDate = orderDate;
	}

	
}
