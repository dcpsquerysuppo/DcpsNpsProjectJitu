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
import java.math.BigDecimal;
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
public class TrnPnsnprocEventdtls implements Serializable{


	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -9002231368875287435L;
	
	private Long pnsnrEventdtlId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private Long pensionerdtlId;
	private Long eventId;
	private Long payScaleId;
	private BigDecimal dp=BigDecimal.ZERO;
	private BigDecimal basic=BigDecimal.ZERO;
	private Date fromDate;
	private Date toDate;
	private String reason;	
	private Long createdUserId;
    private Long createdPostId;
    private Date createdDate;
    private Long updatedUserId;
    private Long updatedPostId;
    private Date updatedDate;
    
    public TrnPnsnprocEventdtls()
    {}
    
    
    
	public TrnPnsnprocEventdtls(Long pnsnrEventdtlId, Long dbId,
			Long locationCode, Long inwardPensionId,
			Long pensionerdtlId, Long eventId, Long payScaleId,
			BigDecimal dp, BigDecimal basic, Date fromDate, Date toDate,
			String reason, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate) {
		super();
		this.pnsnrEventdtlId = pnsnrEventdtlId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.eventId = eventId;
		this.payScaleId = payScaleId;
		this.dp = dp;
		this.basic = basic;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.reason = reason;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((basic == null) ? 0 : basic.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((dp == null) ? 0 : dp.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result
				+ ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result
				+ ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result
				+ ((payScaleId == null) ? 0 : payScaleId.hashCode());
		result = prime * result
				+ ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime * result
				+ ((pnsnrEventdtlId == null) ? 0 : pnsnrEventdtlId.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
		result = prime * result
				+ ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result
				+ ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrnPnsnprocEventdtls other = (TrnPnsnprocEventdtls) obj;
		if (basic == null) {
			if (other.basic != null)
				return false;
		} else if (!basic.equals(other.basic))
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
		if (dp == null) {
			if (other.dp != null)
				return false;
		} else if (!dp.equals(other.dp))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
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
		if (payScaleId == null) {
			if (other.payScaleId != null)
				return false;
		} else if (!payScaleId.equals(other.payScaleId))
			return false;
		if (pensionerdtlId == null) {
			if (other.pensionerdtlId != null)
				return false;
		} else if (!pensionerdtlId.equals(other.pensionerdtlId))
			return false;
		if (pnsnrEventdtlId == null) {
			if (other.pnsnrEventdtlId != null)
				return false;
		} else if (!pnsnrEventdtlId.equals(other.pnsnrEventdtlId))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
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
	
	public Long getPnsnrEventdtlId() {
		return pnsnrEventdtlId;
	}
	public void setPnsnrEventdtlId(Long pnsnrEventdtlId) {
		this.pnsnrEventdtlId = pnsnrEventdtlId;
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
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public Long getPayScaleId() {
		return payScaleId;
	}
	public void setPayScaleId(Long payScaleId) {
		this.payScaleId = payScaleId;
	}
	public BigDecimal getDp() {
		return dp;
	}
	public void setDp(BigDecimal dp) {
		this.dp = dp;
	}
	public BigDecimal getBasic() {
		return basic;
	}
	public void setBasic(BigDecimal basic) {
		this.basic = basic;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
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
