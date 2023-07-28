/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Sneha								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.valueobject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Sneha
 * @version 0.1
 * @since JDK 5.0
 * Feb 2, 2011
 */
public class TrnPnsnprocPnsnrservcbreak implements Serializable{
	
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 993557322470068105L;
	
	private Long pnsnrServcBreakId;
	private Long dbId;
	private Long locationCode;
	private Long pensionerdtlId;
	private Long inwardPensionId;
	private Long serviceBreaktypeLookupId;
	private Date breakFromDate;
	private Date breakToDate;
	private Integer breakPeriod;
	private Long createdUserId;
    private Long createdPostId;
    private Date createdDate;
    private Long updatedUserId;
    private Long updatedPostId;
    private Date updatedDate;
    private String srvcBrkOtherReason;
    
    public TrnPnsnprocPnsnrservcbreak()
    {}
    
    
    
	public TrnPnsnprocPnsnrservcbreak(Long pnsnrServcBreakId, Long dbId,
			Long locationCode, Long pensionerdtlId,
			Long inwardPensionId,
			Long serviceBreaktypeLookupId, Date breakFromDate,
			Date breakToDate, Integer breakPeriod, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate) {
		super();
		this.pnsnrServcBreakId = pnsnrServcBreakId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerdtlId = pensionerdtlId;
		this.inwardPensionId = inwardPensionId;
		this.serviceBreaktypeLookupId = serviceBreaktypeLookupId;
		this.breakFromDate = breakFromDate;
		this.breakToDate = breakToDate;
		this.breakPeriod = breakPeriod;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((breakFromDate == null) ? 0 : breakFromDate.hashCode());
		result = prime * result
				+ ((breakPeriod == null) ? 0 : breakPeriod.hashCode());
		result = prime * result
				+ ((breakToDate == null) ? 0 : breakToDate.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result
				+ ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result
				+ ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime
				* result
				+ ((pnsnrServcBreakId == null) ? 0 : pnsnrServcBreakId
						.hashCode());
		result = prime
				* result
				+ ((serviceBreaktypeLookupId == null) ? 0
						: serviceBreaktypeLookupId.hashCode());
		result = prime * result
				+ ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result
				+ ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
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
		TrnPnsnprocPnsnrservcbreak other = (TrnPnsnprocPnsnrservcbreak) obj;
		if (breakFromDate == null) {
			if (other.breakFromDate != null)
				return false;
		} else if (!breakFromDate.equals(other.breakFromDate))
			return false;
		if (breakPeriod == null) {
			if (other.breakPeriod != null)
				return false;
		} else if (!breakPeriod.equals(other.breakPeriod))
			return false;
		if (breakToDate == null) {
			if (other.breakToDate != null)
				return false;
		} else if (!breakToDate.equals(other.breakToDate))
			return false;
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
		if (dbId == null) {
			if (other.dbId != null)
				return false;
		} else if (!dbId.equals(other.dbId))
			return false;
		if (inwardPensionId == null) {
			if (other.inwardPensionId != null)
				return false;
		} else if (!inwardPensionId.equals(other.inwardPensionId))
			return false;
		if (locationCode == null) {
			if (other.locationCode != null)
				return false;
		} else if (!locationCode.equals(other.locationCode))
			return false;
		if (pensionerdtlId == null) {
			if (other.pensionerdtlId != null)
				return false;
		} else if (!pensionerdtlId.equals(other.pensionerdtlId))
			return false;
		if (pnsnrServcBreakId == null) {
			if (other.pnsnrServcBreakId != null)
				return false;
		} else if (!pnsnrServcBreakId.equals(other.pnsnrServcBreakId))
			return false;
		if (serviceBreaktypeLookupId == null) {
			if (other.serviceBreaktypeLookupId != null)
				return false;
		} else if (!serviceBreaktypeLookupId
				.equals(other.serviceBreaktypeLookupId))
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



	public Long getPnsnrServcBreakId() {
		return pnsnrServcBreakId;
	}
	public void setPnsnrServcBreakId(Long pnsnrServcBreakId) {
		this.pnsnrServcBreakId = pnsnrServcBreakId;
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
			
	public Long getPensionerdtlId() {
		return pensionerdtlId;
	}
	public void setPensionerdtlId(Long pensionerdtlId) {
		this.pensionerdtlId = pensionerdtlId;
	}
	public Long getInwardPensionId() {
		return inwardPensionId;
	}

	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
	}

	public Long getServiceBreaktypeLookupId() {
		return serviceBreaktypeLookupId;
	}
	public void setServiceBreaktypeLookupId(Long serviceBreaktypeLookupId) {
		this.serviceBreaktypeLookupId = serviceBreaktypeLookupId;
	}
	public Date getBreakFromDate() {
		return breakFromDate;
	}
	public void setBreakFromDate(Date breakFromDate) {
		this.breakFromDate = breakFromDate;
	}
	public Date getBreakToDate() {
		return breakToDate;
	}
	public void setBreakToDate(Date breakToDate) {
		this.breakToDate = breakToDate;
	}
	public Integer getBreakPeriod() {
		return breakPeriod;
	}
	public void setBreakPeriod(Integer breakPeriod) {
		this.breakPeriod = breakPeriod;
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

	public void setSrvcBrkOtherReason(String srvcBrkOtherReason) {
		this.srvcBrkOtherReason = srvcBrkOtherReason;
	}
	public String getSrvcBrkOtherReason() {
		return srvcBrkOtherReason;
	}    
    
}
