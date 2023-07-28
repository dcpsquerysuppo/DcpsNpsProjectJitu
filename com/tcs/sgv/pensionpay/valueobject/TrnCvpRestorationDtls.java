/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 25, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * May 25, 2011
 */
public class TrnCvpRestorationDtls implements Serializable{

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 4852678794630414963L;
	
	private Long cvpRestorationDtlsId;
	private Long dbId;
	private Long locationCode;
	private String pensionerCode;
    private BigDecimal amount = BigDecimal.ZERO;
    private Date fromDate;
    private Date toDate;
    private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String restnAplnReceivedFlag;
	private Date restnAplnReceivedDate;
	
	public TrnCvpRestorationDtls()
	{}
	
	/**
	 * 
	 * @param cvpRestorationDtlsId
	 * @param dbId
	 * @param locationCode
	 * @param pensionerCode
	 * @param amount
	 * @param fromDate
	 * @param toDate
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param restnAplnReceivedFlag
	 * @param restnAplnReceivedDate
	 */


	

	public TrnCvpRestorationDtls(Long cvpRestorationDtlsId, Long dbId, Long locationCode, String pensionerCode, BigDecimal amount, Date fromDate, Date toDate, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate, String restnAplnReceivedFlag, Date restnAplnReceivedDate) {

		super();
		this.cvpRestorationDtlsId = cvpRestorationDtlsId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerCode = pensionerCode;
		this.amount = amount;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.restnAplnReceivedFlag = restnAplnReceivedFlag;
		this.restnAplnReceivedDate = restnAplnReceivedDate;
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result + ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((cvpRestorationDtlsId == null) ? 0 : cvpRestorationDtlsId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((pensionerCode == null) ? 0 : pensionerCode.hashCode());
		result = prime * result + ((restnAplnReceivedDate == null) ? 0 : restnAplnReceivedDate.hashCode());
		result = prime * result + ((restnAplnReceivedFlag == null) ? 0 : restnAplnReceivedFlag.hashCode());
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
		TrnCvpRestorationDtls other = (TrnCvpRestorationDtls) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
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
		if (cvpRestorationDtlsId == null) {
			if (other.cvpRestorationDtlsId != null)
				return false;
		} else if (!cvpRestorationDtlsId.equals(other.cvpRestorationDtlsId))
			return false;
		if (dbId == null) {
			if (other.dbId != null)
				return false;
		} else if (!dbId.equals(other.dbId))
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
		if (restnAplnReceivedDate == null) {
			if (other.restnAplnReceivedDate != null)
				return false;
		} else if (!restnAplnReceivedDate.equals(other.restnAplnReceivedDate))
			return false;
		if (restnAplnReceivedFlag == null) {
			if (other.restnAplnReceivedFlag != null)
				return false;
		} else if (!restnAplnReceivedFlag.equals(other.restnAplnReceivedFlag))
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

	public Long getCvpRestorationDtlsId() {
		return cvpRestorationDtlsId;
	}
	public void setCvpRestorationDtlsId(Long cvpRestorationDtlsId) {
		this.cvpRestorationDtlsId = cvpRestorationDtlsId;
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	public String getRestnAplnReceivedFlag() {
		return restnAplnReceivedFlag;
	}
	public void setRestnAplnReceivedFlag(String restnAplnReceivedFlag) {
		this.restnAplnReceivedFlag = restnAplnReceivedFlag;
	}
	public Date getRestnAplnReceivedDate() {
		return restnAplnReceivedDate;
	}
	public void setRestnAplnReceivedDate(Date restnAplnReceivedDate) {
		this.restnAplnReceivedDate = restnAplnReceivedDate;
	} 

}
