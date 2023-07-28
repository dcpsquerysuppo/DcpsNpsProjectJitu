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
public class TrnPnsnProcAdvnceBal implements Serializable
{
	
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 3984669989980514208L;
	
	private Long advanceBalId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private Long pensionerdtlId;
	private Long pnsnrRecoveryId;
	private String advanceBalTypeId;
	private BigDecimal advanceAmnt;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String advanceSchemeCode;
	
	public TrnPnsnProcAdvnceBal()
	{}

	public TrnPnsnProcAdvnceBal(Long advanceBalId, Long dbId,
			Long locationCode, Long pnsnrRecoveryId,
			Long inwardPensionId,
			Long pensionerdtlId, 
			String advanceBalTypeId, BigDecimal advanceAmnt, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate,String advanceSchemeCode) {
		super();
		this.advanceBalId = advanceBalId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pnsnrRecoveryId = pnsnrRecoveryId;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.advanceBalTypeId = advanceBalTypeId;
		this.advanceAmnt = advanceAmnt;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.advanceSchemeCode=advanceSchemeCode;
	}
	
	
	

	@Override
	public String toString() {

		return "TrnPnsnProcAdvnceBal [advanceBalId=" + advanceBalId + ", dbId=" + dbId + ", locationCode=" + locationCode + ", inwardPensionId=" + inwardPensionId + ", pensionerdtlId="
				+ pensionerdtlId + ", pnsnrRecoveryId=" + pnsnrRecoveryId + ", advanceBalTypeId=" + advanceBalTypeId + ", advanceAmnt=" + advanceAmnt + ", createdUserId=" + createdUserId
				+ ", createdPostId=" + createdPostId + ", createdDate=" + createdDate + ", updatedUserId=" + updatedUserId + ", updatedPostId=" + updatedPostId + ", updatedDate=" + updatedDate
				+ ", advanceSchemeCode=" + advanceSchemeCode + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((advanceBalTypeId == null) ? 0 : advanceBalTypeId.hashCode());
		result = prime * result
				+ ((advanceAmnt == null) ? 0 : advanceAmnt.hashCode());
		result = prime * result
				+ ((advanceBalId == null) ? 0 : advanceBalId.hashCode());
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
		result = prime * result
			+ ((advanceSchemeCode == null) ? 0 : advanceSchemeCode.hashCode());
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
		TrnPnsnProcAdvnceBal other = (TrnPnsnProcAdvnceBal) obj;
		if (advanceBalTypeId == null) {
			if (other.advanceBalTypeId != null)
				return false;
		} else if (!advanceBalTypeId.equals(other.advanceBalTypeId))
			return false;
		if (advanceAmnt == null) {
			if (other.advanceAmnt != null)
				return false;
		} else if (!advanceAmnt.equals(other.advanceAmnt))
			return false;
		if (advanceBalId == null) {
			if (other.advanceBalId != null)
				return false;
		} else if (!advanceBalId.equals(other.advanceBalId))
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
		if (advanceSchemeCode == null) {
			if (other.advanceSchemeCode != null)
				return false;
		} else if (!advanceSchemeCode.equals(other.advanceSchemeCode))
			return false;
		return true;
	}

	public Long getAdvanceBalId() {
		return advanceBalId;
	}

	public void setAdvanceBalId(Long advanceBalId) {
		this.advanceBalId = advanceBalId;
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

	public String getAdvanceBalTypeId() {
		return advanceBalTypeId;
	}

	public void setAdvanceBalTypeId(String advanceBalTypeId) {
		this.advanceBalTypeId = advanceBalTypeId;
	}

	public BigDecimal getAdvanceAmnt() {
		return advanceAmnt;
	}

	public void setAdvanceAmnt(BigDecimal advanceAmnt) {
		this.advanceAmnt = advanceAmnt;
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

	
	public String getAdvanceSchemeCode() {
	
		return advanceSchemeCode;
	}

	
	public void setAdvanceSchemeCode(String advanceSchemeCode) {
	
		this.advanceSchemeCode = advanceSchemeCode;
	}
	
	
	

}