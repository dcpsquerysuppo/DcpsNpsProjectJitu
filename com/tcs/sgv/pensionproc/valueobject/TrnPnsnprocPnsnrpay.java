package com.tcs.sgv.pensionproc.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class TrnPnsnprocPnsnrpay implements Serializable{
		
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -2806899253447167288L;
	
	private Long pensionerPayId;
	private Long dbId;
	private Long locationCode;
	private Long pensionerdtlId;
	private Long inwardPensionId;
	private Long lastPayScale=0L;
	private BigDecimal gradePay=BigDecimal.ZERO;
	private BigDecimal basicPay=BigDecimal.ZERO;
	private BigDecimal dpRate=BigDecimal.ZERO;            
    private Character srvcRenderdFlag;
    private Character pnsnCntrbtnFlag;
    private Date pnsnCntrbtnFromDate;
    private Date pnsnCntrbtnToDate;
    private BigDecimal pnsnCntrbtnAmount=BigDecimal.ZERO;     
    private Character prvsnlGratuityFlag;
    private Date prvsnlGratuityDate;
    private Long prvsnlGratuityVoucherno; 
    private BigDecimal prvsnlGratuityAmount=BigDecimal.ZERO; 
    private Long totalServiceBreak;
    private Long qualifyingService;
	private Long actualService;
	private Long nonQualifyingService;
	private BigDecimal grandTotal=BigDecimal.ZERO; 
	private BigDecimal averagePay=BigDecimal.ZERO; 
	private Long createdUserId;
    private Long createdPostId;
    private Date createdDate;
    private Long updatedUserId;
    private Long updatedPostId;
    private Date updatedDate;    
    private Character prvsnlPnsnpaidFlag;
    private String prvsnlPnsnpaidAuthNo;
    private String prvsnlPnsnpaidAuthName;
    private Date prvsnlPnsnpaidFromDate;
    private Date prvsnlPnsnpaidToDate;
    private Integer brkYear;
    private Integer brkMonth;
    private Integer brkDay;
    private Integer actualYear;
    private Integer actualMonth;
    private Integer actualDay;
    private Integer qulifyYear;
    private Integer qulifyMonth;
    private Integer qulifyDay;
    private String foreignPayInfo;
    
    public TrnPnsnprocPnsnrpay()
    { }
    
    
    public TrnPnsnprocPnsnrpay(Long pensionerPayId, Long dbId,
			Long locationCode, Long pensionerdtlId,
			Long inwardPensionId, Long lastPayScale,
			BigDecimal gradePay, BigDecimal basicPay, BigDecimal dpRate,
			Character srvcRenderdFlag, Character pnsnCntrbtnFlag,
			Date pnsnCntrbtnFromDate, Date pnsnCntrbtnToDate,
			BigDecimal pnsnCntrbtnAmount,
			Date prvsnlPnsnaidDate, Long prvsnlPnsnpaidVoucherno,
			BigDecimal prvsnlPnsnpaidAmount, Character prvsnlGratuityFlag,
			Date prvsnlGratuityDate, Long prvsnlGratuityVoucherno,
			BigDecimal prvsnlGratuityAmount, Long totalServiceBreak,
			Long qualifyingService, Long actualService,
			Long nonQualifyingService, BigDecimal grandTotal,
			BigDecimal averagePay, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate,String prvsnlPnsnpaidAuthNo,String prvsnlPnsnpaidAuthName) {
		super();
		this.pensionerPayId = pensionerPayId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerdtlId = pensionerdtlId;
		this.inwardPensionId = inwardPensionId;
		this.lastPayScale = lastPayScale;
		this.gradePay = gradePay;
		this.basicPay = basicPay;
		this.dpRate = dpRate;
		this.srvcRenderdFlag = srvcRenderdFlag;
		this.pnsnCntrbtnFlag = pnsnCntrbtnFlag;
		this.pnsnCntrbtnFromDate = pnsnCntrbtnFromDate;
		this.pnsnCntrbtnToDate = pnsnCntrbtnToDate;
		this.pnsnCntrbtnAmount = pnsnCntrbtnAmount;		
		this.prvsnlGratuityFlag = prvsnlGratuityFlag;
		this.prvsnlGratuityDate = prvsnlGratuityDate;
		this.prvsnlGratuityVoucherno = prvsnlGratuityVoucherno;
		this.prvsnlGratuityAmount = prvsnlGratuityAmount;
		this.totalServiceBreak = totalServiceBreak;
		this.qualifyingService = qualifyingService;
		this.actualService = actualService;
		this.nonQualifyingService = nonQualifyingService;
		this.grandTotal = grandTotal;
		this.averagePay = averagePay;
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
				+ ((actualService == null) ? 0 : actualService.hashCode());
		result = prime * result
				+ ((averagePay == null) ? 0 : averagePay.hashCode());
		result = prime * result
				+ ((basicPay == null) ? 0 : basicPay.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((dpRate == null) ? 0 : dpRate.hashCode());
		result = prime * result
				+ ((gradePay == null) ? 0 : gradePay.hashCode());
		result = prime * result
				+ ((grandTotal == null) ? 0 : grandTotal.hashCode());
		result = prime * result
				+ ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result
				+ ((lastPayScale == null) ? 0 : lastPayScale.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime
				* result
				+ ((nonQualifyingService == null) ? 0 : nonQualifyingService
						.hashCode());
		result = prime * result
				+ ((pensionerPayId == null) ? 0 : pensionerPayId.hashCode());
		result = prime * result
				+ ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime
				* result
				+ ((pnsnCntrbtnAmount == null) ? 0 : pnsnCntrbtnAmount
						.hashCode());
		result = prime * result
				+ ((pnsnCntrbtnFlag == null) ? 0 : pnsnCntrbtnFlag.hashCode());
		result = prime
				* result
				+ ((pnsnCntrbtnFromDate == null) ? 0 : pnsnCntrbtnFromDate
						.hashCode());
		result = prime
				* result
				+ ((pnsnCntrbtnToDate == null) ? 0 : pnsnCntrbtnToDate
						.hashCode());
		result = prime
				* result
				+ ((prvsnlGratuityAmount == null) ? 0 : prvsnlGratuityAmount
						.hashCode());
		result = prime
				* result
				+ ((prvsnlGratuityDate == null) ? 0 : prvsnlGratuityDate
						.hashCode());
		result = prime
				* result
				+ ((prvsnlGratuityFlag == null) ? 0 : prvsnlGratuityFlag
						.hashCode());
		result = prime
				* result
				+ ((prvsnlGratuityVoucherno == null) ? 0
						: prvsnlGratuityVoucherno.hashCode());
	
		result = prime
				* result
				+ ((qualifyingService == null) ? 0 : qualifyingService
						.hashCode());
		result = prime * result
				+ ((srvcRenderdFlag == null) ? 0 : srvcRenderdFlag.hashCode());
		result = prime
				* result
				+ ((totalServiceBreak == null) ? 0 : totalServiceBreak
						.hashCode());
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
		TrnPnsnprocPnsnrpay other = (TrnPnsnprocPnsnrpay) obj;
		if (actualService == null) {
			if (other.actualService != null)
				return false;
		} else if (!actualService.equals(other.actualService))
			return false;
		if (averagePay == null) {
			if (other.averagePay != null)
				return false;
		} else if (!averagePay.equals(other.averagePay))
			return false;
		if (basicPay == null) {
			if (other.basicPay != null)
				return false;
		} else if (!basicPay.equals(other.basicPay))
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
		if (dpRate == null) {
			if (other.dpRate != null)
				return false;
		} else if (!dpRate.equals(other.dpRate))
			return false;
		if (gradePay == null) {
			if (other.gradePay != null)
				return false;
		} else if (!gradePay.equals(other.gradePay))
			return false;
		if (grandTotal == null) {
			if (other.grandTotal != null)
				return false;
		} else if (!grandTotal.equals(other.grandTotal))
			return false;
		if (inwardPensionId == null) {
			if (other.inwardPensionId != null)
				return false;
		} else if (!inwardPensionId.equals(other.inwardPensionId))
			return false;
		if (lastPayScale == null) {
			if (other.lastPayScale != null)
				return false;
		} else if (!lastPayScale.equals(other.lastPayScale))
			return false;
		if (locationCode == null) {
			if (other.locationCode != null)
				return false;
		} else if (!locationCode.equals(other.locationCode))
			return false;
		if (nonQualifyingService == null) {
			if (other.nonQualifyingService != null)
				return false;
		} else if (!nonQualifyingService.equals(other.nonQualifyingService))
			return false;
		if (pensionerPayId == null) {
			if (other.pensionerPayId != null)
				return false;
		} else if (!pensionerPayId.equals(other.pensionerPayId))
			return false;
		if (pensionerdtlId == null) {
			if (other.pensionerdtlId != null)
				return false;
		} else if (!pensionerdtlId.equals(other.pensionerdtlId))
			return false;
		if (pnsnCntrbtnAmount == null) {
			if (other.pnsnCntrbtnAmount != null)
				return false;
		} else if (!pnsnCntrbtnAmount.equals(other.pnsnCntrbtnAmount))
			return false;
		if (pnsnCntrbtnFlag == null) {
			if (other.pnsnCntrbtnFlag != null)
				return false;
		} else if (!pnsnCntrbtnFlag.equals(other.pnsnCntrbtnFlag))
			return false;
		if (pnsnCntrbtnFromDate == null) {
			if (other.pnsnCntrbtnFromDate != null)
				return false;
		} else if (!pnsnCntrbtnFromDate.equals(other.pnsnCntrbtnFromDate))
			return false;
		if (pnsnCntrbtnToDate == null) {
			if (other.pnsnCntrbtnToDate != null)
				return false;
		} else if (!pnsnCntrbtnToDate.equals(other.pnsnCntrbtnToDate))
			return false;
		if (prvsnlGratuityAmount == null) {
			if (other.prvsnlGratuityAmount != null)
				return false;
		} else if (!prvsnlGratuityAmount.equals(other.prvsnlGratuityAmount))
			return false;
		if (prvsnlGratuityDate == null) {
			if (other.prvsnlGratuityDate != null)
				return false;
		} else if (!prvsnlGratuityDate.equals(other.prvsnlGratuityDate))
			return false;
		if (prvsnlGratuityFlag == null) {
			if (other.prvsnlGratuityFlag != null)
				return false;
		} else if (!prvsnlGratuityFlag.equals(other.prvsnlGratuityFlag))
			return false;
		if (prvsnlGratuityVoucherno == null) {
			if (other.prvsnlGratuityVoucherno != null)
				return false;
		} else if (!prvsnlGratuityVoucherno
				.equals(other.prvsnlGratuityVoucherno))
			return false;		
		if (qualifyingService == null) {
			if (other.qualifyingService != null)
				return false;
		} else if (!qualifyingService.equals(other.qualifyingService))
			return false;
		if (srvcRenderdFlag == null) {
			if (other.srvcRenderdFlag != null)
				return false;
		} else if (!srvcRenderdFlag.equals(other.srvcRenderdFlag))
			return false;
		if (totalServiceBreak == null) {
			if (other.totalServiceBreak != null)
				return false;
		} else if (!totalServiceBreak.equals(other.totalServiceBreak))
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


	public Long getPensionerPayId() {
		return pensionerPayId;
	}
	public void setPensionerPayId(Long pensionerPayId) {
		this.pensionerPayId = pensionerPayId;
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


	public Long getLastPayScale() {
		return lastPayScale;
	}
	public void setLastPayScale(Long lastPayScale) {
		this.lastPayScale = lastPayScale;
	}
	public BigDecimal getGradePay() {
		return gradePay;
	}
	public void setGradePay(BigDecimal gradePay) {
		this.gradePay = gradePay;
	}
	public BigDecimal getBasicPay() {
		return basicPay;
	}
	public void setBasicPay(BigDecimal basicPay) {
		this.basicPay = basicPay;
	}
	public BigDecimal getDpRate() {
		return dpRate;
	}
	public void setDpRate(BigDecimal dpRate) {
		this.dpRate = dpRate;
	}
	
	public Date getPnsnCntrbtnFromDate() {
		return pnsnCntrbtnFromDate;
	}
	public void setPnsnCntrbtnFromDate(Date pnsnCntrbtnFromDate) {
		this.pnsnCntrbtnFromDate = pnsnCntrbtnFromDate;
	}
	public Date getPnsnCntrbtnToDate() {
		return pnsnCntrbtnToDate;
	}
	public void setPnsnCntrbtnToDate(Date pnsnCntrbtnToDate) {
		this.pnsnCntrbtnToDate = pnsnCntrbtnToDate;
	}
	public BigDecimal getPnsnCntrbtnAmount() {
		return pnsnCntrbtnAmount;
	}
	public void setPnsnCntrbtnAmount(BigDecimal pnsnCntrbtnAmount) {
		this.pnsnCntrbtnAmount = pnsnCntrbtnAmount;
	}
	public Date getPrvsnlGratuityDate() {
		return prvsnlGratuityDate;
	}
	public void setPrvsnlGratuityDate(Date prvsnlGratuityDate) {
		this.prvsnlGratuityDate = prvsnlGratuityDate;
	}
	public Long getPrvsnlGratuityVoucherno() {
		return prvsnlGratuityVoucherno;
	}
	public void setPrvsnlGratuityVoucherno(Long prvsnlGratuityVoucherno) {
		this.prvsnlGratuityVoucherno = prvsnlGratuityVoucherno;
	}
	public BigDecimal getPrvsnlGratuityAmount() {
		return prvsnlGratuityAmount;
	}
	public void setPrvsnlGratuityAmount(BigDecimal prvsnlGratuityAmount) {
		this.prvsnlGratuityAmount = prvsnlGratuityAmount;
	}
	
	public Long getTotalServiceBreak() {
		return totalServiceBreak;
	}
	public void setTotalServiceBreak(Long totalServiceBreak) {
		this.totalServiceBreak = totalServiceBreak;
	}
	public Long getQualifyingService() {
		return qualifyingService;
	}
	public void setQualifyingService(Long qualifyingService) {
		this.qualifyingService = qualifyingService;
	}
	public Long getActualService() {
		return actualService;
	}
	public void setActualService(Long actualService) {
		this.actualService = actualService;
	}
	public Long getNonQualifyingService() {
		return nonQualifyingService;
	}
	public void setNonQualifyingService(Long nonQualifyingService) {
		this.nonQualifyingService = nonQualifyingService;
	}
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}
	public BigDecimal getAveragePay() {
		return averagePay;
	}
	public void setAveragePay(BigDecimal averagePay) {
		this.averagePay = averagePay;
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
	public Character getSrvcRenderdFlag() {
		return srvcRenderdFlag;
	}
	public void setSrvcRenderdFlag(Character srvcRenderdFlag) {
		this.srvcRenderdFlag = srvcRenderdFlag;
	}
	public Character getPnsnCntrbtnFlag() {
		return pnsnCntrbtnFlag;
	}
	public void setPnsnCntrbtnFlag(Character pnsnCntrbtnFlag) {
		this.pnsnCntrbtnFlag = pnsnCntrbtnFlag;
	}	
	public Character getPrvsnlGratuityFlag() {
		return prvsnlGratuityFlag;
	}
	public void setPrvsnlGratuityFlag(Character prvsnlGratuityFlag) {
		this.prvsnlGratuityFlag = prvsnlGratuityFlag;
	}


	public Character getPrvsnlPnsnpaidFlag() {
		return prvsnlPnsnpaidFlag;
	}


	public void setPrvsnlPnsnpaidFlag(Character prvsnlPnsnpaidFlag) {
		this.prvsnlPnsnpaidFlag = prvsnlPnsnpaidFlag;
	}


	public String getPrvsnlPnsnpaidAuthNo() {
		return prvsnlPnsnpaidAuthNo;
	}


	public void setPrvsnlPnsnpaidAuthNo(String prvsnlPnsnpaidAuthNo) {
		this.prvsnlPnsnpaidAuthNo = prvsnlPnsnpaidAuthNo;
	}


	public String getPrvsnlPnsnpaidAuthName() {
		return prvsnlPnsnpaidAuthName;
	}


	public void setPrvsnlPnsnpaidAuthName(String prvsnlPnsnpaidAuthName) {
		this.prvsnlPnsnpaidAuthName = prvsnlPnsnpaidAuthName;
	}


	public Date getPrvsnlPnsnpaidFromDate() {
		return prvsnlPnsnpaidFromDate;
	}


	public void setPrvsnlPnsnpaidFromDate(Date prvsnlPnsnpaidFromDate) {
		this.prvsnlPnsnpaidFromDate = prvsnlPnsnpaidFromDate;
	}


	public Date getPrvsnlPnsnpaidToDate() {
		return prvsnlPnsnpaidToDate;
	}


	public void setPrvsnlPnsnpaidToDate(Date prvsnlPnsnpaidToDate) {
		this.prvsnlPnsnpaidToDate = prvsnlPnsnpaidToDate;
	}
	public Integer getBrkYear() {
		return brkYear;
	}


	public void setBrkYear(Integer brkYear) {
		this.brkYear = brkYear;
	}


	public Integer getBrkMonth() {
		return brkMonth;
	}


	public void setBrkMonth(Integer brkMonth) {
		this.brkMonth = brkMonth;
	}


	public Integer getBrkDay() {
		return brkDay;
	}


	public void setBrkDay(Integer brkDay) {
		this.brkDay = brkDay;
	}


	public Integer getActualYear() {
		return actualYear;
	}


	public void setActualYear(Integer actualYear) {
		this.actualYear = actualYear;
	}


	public Integer getActualMonth() {
		return actualMonth;
	}


	public void setActualMonth(Integer actualMonth) {
		this.actualMonth = actualMonth;
	}


	public Integer getActualDay() {
		return actualDay;
	}


	public void setActualDay(Integer actualDay) {
		this.actualDay = actualDay;
	}


	public Integer getQulifyYear() {
		return qulifyYear;
	}


	public void setQulifyYear(Integer qulifyYear) {
		this.qulifyYear = qulifyYear;
	}


	public Integer getQulifyMonth() {
		return qulifyMonth;
	}


	public void setQulifyMonth(Integer qulifyMonth) {
		this.qulifyMonth = qulifyMonth;
	}


	public Integer getQulifyDay() {
		return qulifyDay;
	}


	public void setQulifyDay(Integer qulifyDay) {
		this.qulifyDay = qulifyDay;
	}


	public void setForeignPayInfo(String foreignPayInfo) {
		this.foreignPayInfo = foreignPayInfo;
	}


	public String getForeignPayInfo() {
		return foreignPayInfo;
	}


}
