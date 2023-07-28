
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
 * Apr 28, 2011
 */
public class TrnProvisionalVoucherDtls implements Serializable{

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -2104039080176541039L;
	
	private Long provVoucherDtlsId;
	private Long dbId;
	private Long locationCode;
	private String pensionerCode;
    private String voucherMonth;
    private Long voucherYear;
    private String voucherNo;
    private Date voucherDate;
    private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate; 
	
	public TrnProvisionalVoucherDtls()
	{	}

	/**
	 * 
	 * @param provVoucherDtlsId
	 * @param dbId
	 * @param locationCode
	 * @param pensionerCode
	 * @param voucherMonth
	 * @param voucherYear
	 * @param vouherNo
	 * @param voucherDate
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 */
	public TrnProvisionalVoucherDtls(Long provVoucherDtlsId, Long dbId,
			Long locationCode, String pensionerCode, String voucherMonth,
			Long voucherYear, String voucherNo, Date voucherDate,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate) {
		super();
		this.provVoucherDtlsId = provVoucherDtlsId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerCode = pensionerCode;
		this.voucherMonth = voucherMonth;
		this.voucherYear = voucherYear;
		this.voucherNo = voucherNo;
		this.voucherDate = voucherDate;
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
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result
				+ ((pensionerCode == null) ? 0 : pensionerCode.hashCode());
		result = prime
				* result
				+ ((provVoucherDtlsId == null) ? 0 : provVoucherDtlsId
						.hashCode());
		result = prime * result
				+ ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result
				+ ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		result = prime * result
				+ ((voucherDate == null) ? 0 : voucherDate.hashCode());
		result = prime * result
				+ ((voucherMonth == null) ? 0 : voucherMonth.hashCode());
		result = prime * result
				+ ((voucherYear == null) ? 0 : voucherYear.hashCode());
		result = prime * result
				+ ((voucherNo == null) ? 0 : voucherNo.hashCode());
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
		TrnProvisionalVoucherDtls other = (TrnProvisionalVoucherDtls) obj;
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
		if (provVoucherDtlsId == null) {
			if (other.provVoucherDtlsId != null)
				return false;
		} else if (!provVoucherDtlsId.equals(other.provVoucherDtlsId))
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
		if (voucherDate == null) {
			if (other.voucherDate != null)
				return false;
		} else if (!voucherDate.equals(other.voucherDate))
			return false;
		if (voucherMonth == null) {
			if (other.voucherMonth != null)
				return false;
		} else if (!voucherMonth.equals(other.voucherMonth))
			return false;
		if (voucherYear == null) {
			if (other.voucherYear != null)
				return false;
		} else if (!voucherYear.equals(other.voucherYear))
			return false;
		if (voucherNo == null) {
			if (other.voucherNo != null)
				return false;
		} else if (!voucherNo.equals(other.voucherNo))
			return false;
		return true;
	}

	public Long getProvVoucherDtlsId() {
		return provVoucherDtlsId;
	}

	public void setProvVoucherDtlsId(Long provVoucherDtlsId) {
		this.provVoucherDtlsId = provVoucherDtlsId;
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

	public String getVoucherMonth() {
		return voucherMonth;
	}

	public void setVoucherMonth(String voucherMonth) {
		this.voucherMonth = voucherMonth;
	}

	public Long getVoucherYear() {
		return voucherYear;
	}

	public void setVoucherYear(Long voucherYear) {
		this.voucherYear = voucherYear;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
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
