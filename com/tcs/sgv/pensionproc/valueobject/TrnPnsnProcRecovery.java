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
public class TrnPnsnProcRecovery implements Serializable
{

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 6412791633716459970L;
	
	private Long pnsnrRecoveryId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private Long pensionerdtlId;
	private String advanceFlag;
	private BigDecimal totalAdvanceAmount;
	private BigDecimal overPaymentAmnt;
	private BigDecimal arrearsAmnt;
	private BigDecimal govtAcmdtnRetAmnt;
	private String assessedDuesdtlFlag;
	private BigDecimal totalAssessedDues;
	private BigDecimal gratuityAmnt;
	private String enquiryProposedFlag;
    private String enquiryPendingFlag;
	/*private Date invalidatingDate;
	private Date lodgingDate;
	private Integer policeCertificateNo;
	private String policeStationName;;
	private Date policeCertificateDate;*/
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
  
	public TrnPnsnProcRecovery()
	{}

	

	public TrnPnsnProcRecovery(Long pnsnrRecoveryId, Long dbId,
			Long locationCode, Long inwardPensionId,
			Long pensionerdtlId, String advanceFlag,
			BigDecimal totalAdvanceAmount, BigDecimal overPaymentAmnt,
			BigDecimal arrearsAmnt, BigDecimal govtAcmdtnRetAmnt,
			String assessedDuesdtlFlag, BigDecimal totalAssessedDues,
			BigDecimal gratuityAmnt, String enquiryProposedFlag,
			String enquiryPendingFlag,Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate) {
		super();
		this.pnsnrRecoveryId = pnsnrRecoveryId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.advanceFlag = advanceFlag;
		this.totalAdvanceAmount = totalAdvanceAmount;
		this.overPaymentAmnt = overPaymentAmnt;
		this.arrearsAmnt = arrearsAmnt;
		this.govtAcmdtnRetAmnt = govtAcmdtnRetAmnt;
		this.assessedDuesdtlFlag = assessedDuesdtlFlag;
		this.totalAssessedDues = totalAssessedDues;
		this.gratuityAmnt = gratuityAmnt;
		this.enquiryProposedFlag = enquiryProposedFlag;
		this.enquiryPendingFlag = enquiryPendingFlag;
		/*this.invalidatingDate = invalidatingDate;
		this.lodgingDate = lodgingDate;
		this.policeCertificateNo = policeCertificateNo;
		this.policeStationName = policeStationName;
		this.policeCertificateDate = policeCertificateDate;*/
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
				+ ((advanceFlag == null) ? 0 : advanceFlag.hashCode());
		result = prime * result
				+ ((arrearsAmnt == null) ? 0 : arrearsAmnt.hashCode());
		result = prime
				* result
				+ ((assessedDuesdtlFlag == null) ? 0 : assessedDuesdtlFlag
						.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime
				* result
				+ ((enquiryPendingFlag == null) ? 0 : enquiryPendingFlag
						.hashCode());
		result = prime
				* result
				+ ((enquiryProposedFlag == null) ? 0 : enquiryProposedFlag
						.hashCode());
		result = prime
				* result
				+ ((govtAcmdtnRetAmnt == null) ? 0 : govtAcmdtnRetAmnt
						.hashCode());
		result = prime * result
				+ ((gratuityAmnt == null) ? 0 : gratuityAmnt.hashCode());
		/*result = prime
				* result
				+ ((invalidatingDate == null) ? 0 : invalidatingDate.hashCode());*/
		result = prime * result
				+ ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		/*result = prime * result
				+ ((lodgingDate == null) ? 0 : lodgingDate.hashCode());*/
		result = prime * result
				+ ((overPaymentAmnt == null) ? 0 : overPaymentAmnt.hashCode());
		result = prime * result
				+ ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime * result
				+ ((pnsnrRecoveryId == null) ? 0 : pnsnrRecoveryId.hashCode());
		/*result = prime
				* result
				+ ((policeCertificateDate == null) ? 0 : policeCertificateDate
						.hashCode());
		result = prime
				* result
				+ ((policeCertificateNo == null) ? 0 : policeCertificateNo
						.hashCode());
		result = prime
				* result
				+ ((policeStationName == null) ? 0 : policeStationName
						.hashCode()); */
		result = prime
				* result
				+ ((totalAdvanceAmount == null) ? 0 : totalAdvanceAmount
						.hashCode());
		result = prime
				* result
				+ ((totalAssessedDues == null) ? 0 : totalAssessedDues
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
		TrnPnsnProcRecovery other = (TrnPnsnProcRecovery) obj;
		if (advanceFlag == null) {
			if (other.advanceFlag != null)
				return false;
		} else if (!advanceFlag.equals(other.advanceFlag))
			return false;
		if (arrearsAmnt == null) {
			if (other.arrearsAmnt != null)
				return false;
		} else if (!arrearsAmnt.equals(other.arrearsAmnt))
			return false;
		if (assessedDuesdtlFlag == null) {
			if (other.assessedDuesdtlFlag != null)
				return false;
		} else if (!assessedDuesdtlFlag.equals(other.assessedDuesdtlFlag))
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
		if (enquiryPendingFlag == null) {
			if (other.enquiryPendingFlag != null)
				return false;
		} else if (!enquiryPendingFlag.equals(other.enquiryPendingFlag))
			return false;
		if (enquiryProposedFlag == null) {
			if (other.enquiryProposedFlag != null)
				return false;
		} else if (!enquiryProposedFlag.equals(other.enquiryProposedFlag))
			return false;
		if (govtAcmdtnRetAmnt == null) {
			if (other.govtAcmdtnRetAmnt != null)
				return false;
		} else if (!govtAcmdtnRetAmnt.equals(other.govtAcmdtnRetAmnt))
			return false;
		if (gratuityAmnt == null) {
			if (other.gratuityAmnt != null)
				return false;
		} else if (!gratuityAmnt.equals(other.gratuityAmnt))
			return false;
		/*if (invalidatingDate == null) {
			if (other.invalidatingDate != null)
				return false;
		} else if (!invalidatingDate.equals(other.invalidatingDate))
			return false;*/
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
		/*if (lodgingDate == null) {
			if (other.lodgingDate != null)
				return false;
		} else if (!lodgingDate.equals(other.lodgingDate))
			return false;*/
		if (overPaymentAmnt == null) {
			if (other.overPaymentAmnt != null)
				return false;
		} else if (!overPaymentAmnt.equals(other.overPaymentAmnt))
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
		/*if (policeCertificateDate == null) {
			if (other.policeCertificateDate != null)
				return false;
		} else if (!policeCertificateDate.equals(other.policeCertificateDate))
			return false;
		if (policeCertificateNo == null) {
			if (other.policeCertificateNo != null)
				return false;
		} else if (!policeCertificateNo.equals(other.policeCertificateNo))
			return false;
		if (policeStationName == null) {
			if (other.policeStationName != null)
				return false;
		} else if (!policeStationName.equals(other.policeStationName))
			return false;*/
		if (totalAdvanceAmount == null) {
			if (other.totalAdvanceAmount != null)
				return false;
		} else if (!totalAdvanceAmount.equals(other.totalAdvanceAmount))
			return false;
		if (totalAssessedDues == null) {
			if (other.totalAssessedDues != null)
				return false;
		} else if (!totalAssessedDues.equals(other.totalAssessedDues))
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

	public String getAdvanceFlag() {
		return advanceFlag;
	}

	public void setAdvanceFlag(String advanceFlag) {
		this.advanceFlag = advanceFlag;
	}

	public BigDecimal getTotalAdvanceAmount() {
		return totalAdvanceAmount;
	}

	public void setTotalAdvanceAmount(BigDecimal totalAdvanceAmount) {
		this.totalAdvanceAmount = totalAdvanceAmount;
	}

	public String getAssessedDuesdtlFlag() {
		return assessedDuesdtlFlag;
	}

	public void setAssessedDuesdtlFlag(String assessedDuesdtlFlag) {
		this.assessedDuesdtlFlag = assessedDuesdtlFlag;
	}

	public BigDecimal getTotalAssessedDues() {
		return totalAssessedDues;
	}

	public void setTotalAssessedDues(BigDecimal totalAssessedDues) {
		this.totalAssessedDues = totalAssessedDues;
	}

	public String getEnquiryProposedFlag() {
		return enquiryProposedFlag;
	}

	public void setEnquiryProposedFlag(String enquiryProposedFlag) {
		this.enquiryProposedFlag = enquiryProposedFlag;
	}

	public String getEnquiryPendingFlag() {
		return enquiryPendingFlag;
	}

	public void setEnquiryPendingFlag(String enquiryPendingFlag) {
		this.enquiryPendingFlag = enquiryPendingFlag;
	}

	/*public void setPoliceCertificateNo(Integer policeCertificateNo) {
		this.policeCertificateNo = policeCertificateNo;
	}
*/
	public Long getPnsnrRecoveryId() {
		return pnsnrRecoveryId;
	}

	public void setPnsnrRecoveryId(Long pnsnrRecoveryId) {
		this.pnsnrRecoveryId = pnsnrRecoveryId;
	}

	
	public BigDecimal getOverPaymentAmnt() {
		return overPaymentAmnt;
	}



	public void setOverPaymentAmnt(BigDecimal overPaymentAmnt) {
		this.overPaymentAmnt = overPaymentAmnt;
	}



	public BigDecimal getArrearsAmnt() {
		return arrearsAmnt;
	}



	public void setArrearsAmnt(BigDecimal arrearsAmnt) {
		this.arrearsAmnt = arrearsAmnt;
	}



	public BigDecimal getGovtAcmdtnRetAmnt() {
		return govtAcmdtnRetAmnt;
	}



	public void setGovtAcmdtnRetAmnt(BigDecimal govtAcmdtnRetAmnt) {
		this.govtAcmdtnRetAmnt = govtAcmdtnRetAmnt;
	}



	public BigDecimal getGratuityAmnt() {
		return gratuityAmnt;
	}



	public void setGratuityAmnt(BigDecimal gratuityAmnt) {
		this.gratuityAmnt = gratuityAmnt;
	}



	public static long getSerialVersionUID() {
		return serialVersionUID;
	}



	/*public Date getInvalidatingDate() {
		return invalidatingDate;
	}

	public void setInvalidatingDate(Date invalidatingDate) {
		this.invalidatingDate = invalidatingDate;
	}

	public Date getLodgingDate() {
		return lodgingDate;
	}

	public void setLodgingDate(Date lodgingDate) {
		this.lodgingDate = lodgingDate;
	}

	public int getPoliceCertificateNo() {
		return policeCertificateNo;
	}

	public void setPoliceCertificateNo(int policeCertificateNo) {
		this.policeCertificateNo = policeCertificateNo;
	}

	public String getPoliceStationName() {
		return policeStationName;
	}

	public void setPoliceStationName(String policeStationName) {
		this.policeStationName = policeStationName;
	}

	public Date getPoliceCertificateDate() {
		return policeCertificateDate;
	}

	public void setPoliceCertificateDate(Date policeCertificateDate) {
		this.policeCertificateDate = policeCertificateDate;
	}*/

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
