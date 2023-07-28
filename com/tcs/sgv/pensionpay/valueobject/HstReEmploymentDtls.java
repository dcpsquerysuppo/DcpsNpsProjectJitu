/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 20, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.util.Date;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Aug 20, 2011
 */
public class HstReEmploymentDtls implements Serializable{
	
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -322120344015649471L;
	
	private Long reEmploymentId;
	private Long dbId;
	private Long locationCode;
	private String pensionerCode;   
	private Date fromDate;
	private Date toDate;
	private String daInPensionSalary;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String employeeOrderNo;
	private Date employeeOrderDate;
		
	public HstReEmploymentDtls()
	{}

	/**
	 * 
	 * @param reEmploymentId
	 * @param dbId
	 * @param locationCode
	 * @param pensionerCode
	 * @param fromDate
	 * @param toDate
	 * @param daInPensionSalary
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param employeeOrderNo
	 * @param employeeOrderDate
	 */

	public HstReEmploymentDtls(Long reEmploymentId, Long dbId, Long locationCode, String pensionerCode, Date fromDate, Date toDate, String daInPensionSalary, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate, String employeeOrderNo, Date employeeOrderDate) {

		super();
		this.reEmploymentId = reEmploymentId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerCode = pensionerCode;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.daInPensionSalary = daInPensionSalary;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.employeeOrderNo = employeeOrderNo;
		this.employeeOrderDate = employeeOrderDate;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result + ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((daInPensionSalary == null) ? 0 : daInPensionSalary.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((employeeOrderDate == null) ? 0 : employeeOrderDate.hashCode());
		result = prime * result + ((employeeOrderNo == null) ? 0 : employeeOrderNo.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((pensionerCode == null) ? 0 : pensionerCode.hashCode());
		result = prime * result + ((reEmploymentId == null) ? 0 : reEmploymentId.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
		result = prime * result + ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result + ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result + ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HstReEmploymentDtls other = (HstReEmploymentDtls) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (createdPostId == null) {
			if (other.createdPostId != null)
				return false;
		} else if (!createdPostId.equals(other.createdPostId))
			return false;
		if (createdUserId == null) {
			if (other.createdUserId != null)
				return false;
		} else if (!createdUserId.equals(other.createdUserId))
			return false;
		if (daInPensionSalary == null) {
			if (other.daInPensionSalary != null)
				return false;
		} else if (!daInPensionSalary.equals(other.daInPensionSalary))
			return false;
		if (dbId == null) {
			if (other.dbId != null)
				return false;
		} else if (!dbId.equals(other.dbId))
			return false;
		if (employeeOrderDate == null) {
			if (other.employeeOrderDate != null)
				return false;
		} else if (!employeeOrderDate.equals(other.employeeOrderDate))
			return false;
		if (employeeOrderNo == null) {
			if (other.employeeOrderNo != null)
				return false;
		} else if (!employeeOrderNo.equals(other.employeeOrderNo))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (locationCode == null) {
			if (other.locationCode != null)
				return false;
		} else if (!locationCode.equals(other.locationCode))
			return false;
		if (pensionerCode == null) {
			if (other.pensionerCode != null)
				return false;
		} else if (!pensionerCode.equals(other.pensionerCode))
			return false;
		if (reEmploymentId == null) {
			if (other.reEmploymentId != null)
				return false;
		} else if (!reEmploymentId.equals(other.reEmploymentId))
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		if (updatedDate == null) {
			if (other.updatedDate != null)
				return false;
		} else if (!updatedDate.equals(other.updatedDate))
			return false;
		if (updatedPostId == null) {
			if (other.updatedPostId != null)
				return false;
		} else if (!updatedPostId.equals(other.updatedPostId))
			return false;
		if (updatedUserId == null) {
			if (other.updatedUserId != null)
				return false;
		} else if (!updatedUserId.equals(other.updatedUserId))
			return false;
		return true;
	}

	public Long getReEmploymentId() {
	
		return reEmploymentId;
	}

	
	public void setReEmploymentId(Long reEmploymentId) {
	
		this.reEmploymentId = reEmploymentId;
	}

	
	public Long getDbId() {
	
		return dbId;
	}

	
	public void setDbId(Long dbId) {
	
		this.dbId = dbId;
	}

	
	public Long getLocationCode() {
	
		return locationCode;
	}

	
	public void setLocationCode(Long locationCode) {
	
		this.locationCode = locationCode;
	}

	
	public String getPensionerCode() {
	
		return pensionerCode;
	}

	
	public void setPensionerCode(String pensionerCode) {
	
		this.pensionerCode = pensionerCode;
	}

	
	public Date getFromDate() {
	
		return fromDate;
	}

	
	public void setFromDate(Date fromDate) {
	
		this.fromDate = fromDate;
	}

	
	public Date getToDate() {
	
		return toDate;
	}

	
	public void setToDate(Date toDate) {
	
		this.toDate = toDate;
	}

	
	public String getDaInPensionSalary() {
	
		return daInPensionSalary;
	}

	
	public void setDaInPensionSalary(String daInPensionSalary) {
	
		this.daInPensionSalary = daInPensionSalary;
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

	
	public String getEmployeeOrderNo() {
	
		return employeeOrderNo;
	}

	
	public void setEmployeeOrderNo(String employeeOrderNo) {
	
		this.employeeOrderNo = employeeOrderNo;
	}

	
	public Date getEmployeeOrderDate() {
	
		return employeeOrderDate;
	}

	
	public void setEmployeeOrderDate(Date employeeOrderDate) {
	
		this.employeeOrderDate = employeeOrderDate;
	}
		
	
}
