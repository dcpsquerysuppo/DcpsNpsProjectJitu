/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 5, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Feb 5, 2011
 */
public class TrnPnsnProcAssesdDues implements Serializable
{
	
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 1754281179952520336L;
	private Long assesdDuesDtlId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private Long pensionerdtlId;
	private Long pnsnrRecoveryId;
    private String assesdDuesDtlTypeId;
	private BigDecimal assesdDuesDtlAmnt;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	public TrnPnsnProcAssesdDues()
	{}

	
	
	public TrnPnsnProcAssesdDues(Long assesdDuesDtlId, Long dbId,
			Long locationCode, Long pnsnrRecoveryId,
			Long inwardPensionId,
			Long pensionerdtlId,
			String assesdDuesDtlTypeId, BigDecimal assesdDuesDtlAmnt,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate) {
		super();
		this.assesdDuesDtlId = assesdDuesDtlId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pnsnrRecoveryId = pnsnrRecoveryId;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.assesdDuesDtlTypeId = assesdDuesDtlTypeId;
		this.assesdDuesDtlAmnt = assesdDuesDtlAmnt;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}



	@Override
	public String toString() {
		return "TrnPnsnProcAssesdDues [assesdDuesDtlAmnt=" + assesdDuesDtlAmnt
				+ ", assesdDuesDtlId=" + assesdDuesDtlId
				+ ", assesdDuesDtlTypeId=" + assesdDuesDtlTypeId
				+ ", createdDate=" + createdDate + ", createdPostId="
				+ createdPostId + ", createdUserId=" + createdUserId
				+ ", dbId=" + dbId + ", inwardPensionId=" + inwardPensionId
				+ ", locationCode=" + locationCode + ", pensionerdtlId="
				+ pensionerdtlId + ", pnsnrRecoveryId=" + pnsnrRecoveryId
				+ ", updatedDate=" + updatedDate + ", updatedPostId="
				+ updatedPostId + ", updatedUserId=" + updatedUserId + "]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((assesdDuesDtlAmnt == null) ? 0 : assesdDuesDtlAmnt
						.hashCode());
		result = prime * result
				+ ((assesdDuesDtlId == null) ? 0 : assesdDuesDtlId.hashCode());
		result = prime
				* result
				+ ((assesdDuesDtlTypeId == null) ? 0 : assesdDuesDtlTypeId
						.hashCode());
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
		result = prime * result
				+ ((pnsnrRecoveryId == null) ? 0 : pnsnrRecoveryId.hashCode());
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
		TrnPnsnProcAssesdDues other = (TrnPnsnProcAssesdDues) obj;
		if (assesdDuesDtlAmnt == null) {
			if (other.assesdDuesDtlAmnt != null)
				return false;
		} else if (!assesdDuesDtlAmnt.equals(other.assesdDuesDtlAmnt))
			return false;
		
		if (assesdDuesDtlId == null) {
			if (other.assesdDuesDtlId != null)
				return false;
		} else if (!assesdDuesDtlId.equals(other.assesdDuesDtlId))
			return false;
		if (assesdDuesDtlTypeId == null) {
			if (other.assesdDuesDtlTypeId != null)
				return false;
		} else if (!assesdDuesDtlTypeId.equals(other.assesdDuesDtlTypeId))
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
		if (pnsnrRecoveryId == null) {
			if (other.pnsnrRecoveryId != null)
				return false;
		} else if (!pnsnrRecoveryId.equals(other.pnsnrRecoveryId))
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



	public Long getAssesdDuesDtlId() {
		return assesdDuesDtlId;
	}



	public void setAssesdDuesDtlId(Long assesdDuesDtlId) {
		this.assesdDuesDtlId = assesdDuesDtlId;
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


	public Long getInwardPensionId() {
		return inwardPensionId;
	}

	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
	}

	public Long getPensionerdtlId() {
		return pensionerdtlId;
	}

	public void setPensionerdtlId(Long pensionerdtlId) {
		this.pensionerdtlId = pensionerdtlId;
	}

	public Long getPnsnrRecoveryId() {
		return pnsnrRecoveryId;
	}



	public void setPnsnrRecoveryId(Long pnsnrRecoveryId) {
		this.pnsnrRecoveryId = pnsnrRecoveryId;
	}



	public String getAssesdDuesDtlTypeId() {
		return assesdDuesDtlTypeId;
	}

	public void setAssesdDuesDtlTypeId(String assesdDuesDtlTypeId) {
		this.assesdDuesDtlTypeId = assesdDuesDtlTypeId;
	}

	public BigDecimal getAssesdDuesDtlAmnt() {
		return assesdDuesDtlAmnt;
	}

	public void setAssesdDuesDtlAmnt(BigDecimal assesdDuesDtlAmnt) {
		this.assesdDuesDtlAmnt = assesdDuesDtlAmnt;
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
	
	
	

}
