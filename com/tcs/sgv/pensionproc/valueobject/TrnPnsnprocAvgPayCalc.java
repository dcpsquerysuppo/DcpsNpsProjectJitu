/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 18, 2011		Anjana Suvariya								
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
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Mar 18, 2011
 */
public class TrnPnsnprocAvgPayCalc implements Serializable{

	  /**
	   * Serial version uid
	   */
	   private static final long serialVersionUID = -5177946382336343821L;
	
	   private Long avgPayCalcId;
	   private Long dbId;
	   private Long locationCode;
	   private Long inwardPensionId;
	   private Long pensionerdtlId;
	   private Date fromDate;
	   //private Date toDate;
	   private Date emolumentFromDate;
	   private Date emolumentToDate;
	   private BigDecimal basic=BigDecimal.ZERO; 
	   private BigDecimal dp=BigDecimal.ZERO; 
	   private BigDecimal npa=BigDecimal.ZERO; 
	   private BigDecimal total=BigDecimal.ZERO; 
	   private Long createdUserId;
	   private Long createdPostId;
	   private Date createdDate;
	   private Long updatedUserId;
	   private Long updatedPostId;
	   private Date updatedDate;
	  
	   public TrnPnsnprocAvgPayCalc()
	   { }

	   public TrnPnsnprocAvgPayCalc(Long avgPayCalcId, Long dbId,
			Long locationCode, Long inwardPensionId,
			Long pensionerdtlId, Date fromDate,Date emolumentFromDate,Date emolumentToDate, //Date toDate,
			BigDecimal basic, BigDecimal dp, BigDecimal npa, BigDecimal total,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate) {
		super();
		this.avgPayCalcId = avgPayCalcId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.fromDate = fromDate;
		//this.toDate = toDate;
		this.emolumentFromDate=emolumentFromDate;
		this.emolumentToDate=emolumentToDate;
		this.basic = basic;
		this.dp = dp;
		this.npa = npa;
		this.total = total;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}

	public Long getAvgPayCalcId() {
		return avgPayCalcId;
	}

	public void setAvgPayCalcId(Long avgPayCalcId) {
		this.avgPayCalcId = avgPayCalcId;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/*public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}*/
	
	public Date getEmolumentFromDate() {
		
		return emolumentFromDate;
	}

	
	public void setEmolumentFromDate(Date emolumentFromDate) {
	
		this.emolumentFromDate = emolumentFromDate;
	}

	public Date getEmolumentToDate() {
	
		return emolumentToDate;
	}

	public void setEmolumentToDate(Date emolumentToDate) {
	
		this.emolumentToDate = emolumentToDate;
	}
	
	public BigDecimal getBasic() {
		return basic;
	}
	
	public void setBasic(BigDecimal basic) {
		this.basic = basic;
	}

	public BigDecimal getDp() {
		return dp;
	}

	public void setDp(BigDecimal dp) {
		this.dp = dp;
	}

	public BigDecimal getNpa() {
		return npa;
	}

	public void setNpa(BigDecimal npa) {
		this.npa = npa;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
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

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((avgPayCalcId == null) ? 0 : avgPayCalcId.hashCode());
		result = prime * result + ((basic == null) ? 0 : basic.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result + ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((dp == null) ? 0 : dp.hashCode());
		result = prime * result + ((emolumentFromDate == null) ? 0 : emolumentFromDate.hashCode());
		result = prime * result + ((emolumentToDate == null) ? 0 : emolumentToDate.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((npa == null) ? 0 : npa.hashCode());
		result = prime * result + ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
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
		TrnPnsnprocAvgPayCalc other = (TrnPnsnprocAvgPayCalc) obj;
		if (avgPayCalcId == null) {
			if (other.avgPayCalcId != null)
				return false;
		} else if (!avgPayCalcId.equals(other.avgPayCalcId))
			return false;
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
		if (emolumentFromDate == null) {
			if (other.emolumentFromDate != null)
				return false;
		} else if (!emolumentFromDate.equals(other.emolumentFromDate))
			return false;
		if (emolumentToDate == null) {
			if (other.emolumentToDate != null)
				return false;
		} else if (!emolumentToDate.equals(other.emolumentToDate))
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
		if (npa == null) {
			if (other.npa != null)
				return false;
		} else if (!npa.equals(other.npa))
			return false;
		if (pensionerdtlId == null) {
			if (other.pensionerdtlId != null)
				return false;
		} else if (!pensionerdtlId.equals(other.pensionerdtlId))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
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

	

	
	   
	   
}
