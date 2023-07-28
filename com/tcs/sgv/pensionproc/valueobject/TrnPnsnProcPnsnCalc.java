/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Bhargav Trivedi								
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
 * Feb 2, 2011
 */
public class TrnPnsnProcPnsnCalc implements Serializable
{
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 7371233972951420660L;
	
	private Long pensionCalcId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private Long pensionerdtlId;
	private Date cvpElgbltyDate;
	private BigDecimal cvpMonthAmnt;
	private BigDecimal cvpRate;
	private BigDecimal cvpAmnt;
	private BigDecimal pensionerReducedAmnt;
	private BigDecimal pensionerTotalAmnt;
	private BigDecimal srvcGratuityAmnt;
	private BigDecimal dcrgAmnt;
	private BigDecimal pvsnlPensionAmnt;
	private Date pvsnlPensionDate;
	private Character dcrgSnctndFlag;
	private BigDecimal pvsnlDcrgAmnt;
	private Character pvsnlPensionFlag;
	private Long documentCount;
	private Character pnsnablePayFlag;
	private BigDecimal fp1Amnt;
	private Date fp1Date;
	private BigDecimal fp2Amnt;
	private Date fp2Date;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String cpoNo;
	private String gpoNo;
	private Date cpoDate;
	private Date gpoDate;
	private String manualEditFlag;
	private Date cvpAppDate;	
	private Long witheldGratuity;
	private Date dcrgAppDate;
	
	public TrnPnsnProcPnsnCalc()
	{}
	
	/**
	 * 
	 * @param pensionCalcId
	 * @param dbId
	 * @param locationCode
	 * @param inwardPensionId
	 * @param pensionerId
	 * @param cvpElgbltyDate
	 * @param cvpMonthAmnt
	 * @param cvpRate
	 * @param cvpAmnt
	 * @param pensionerReducedAmnt
	 * @param pensionerTotalAmnt
	 * @param srvcGratuityAmnt
	 * @param dcrgAmnt
	 * @param pvsnlPensionAmnt
	 * @param pvsnlPensionDate
	 * @param dcrgSnctndFlag
	 * @param pvsnlDcrgAmnt
	 * @param pvsnlPensionFlag
	 * @param documentCount
	 * @param pnsnablePayFlag
	 * @param fp1Amnt
	 * @param fp1Date
	 * @param fp2Amnt
	 * @param fp2Date
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param cpoNo
	 * @param gpoNo
	 */
	
	public TrnPnsnProcPnsnCalc(Long pensionCalcId, Long dbId,
			Long locationCode, Long inwardPensionId,
			Long pensionerdtlId, Date cvpElgbltyDate,
			BigDecimal cvpMonthAmnt, BigDecimal cvpRate, BigDecimal cvpAmnt,
			BigDecimal pensionerReducedAmnt, BigDecimal pensionerTotalAmnt,
			BigDecimal srvcGratuityAmnt, BigDecimal dcrgAmnt,
			BigDecimal pvsnlPensionAmnt, Date pvsnlPensionDate,
			Character dcrgSnctndFlag, BigDecimal pvsnlDcrgAmnt,
			Character pvsnlPensionFlag, Long documentCount,
			Character pnsnablePayFlag, BigDecimal fp1Amnt, Date fp1Date,
			BigDecimal fp2Amnt, Date fp2Date, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate,String cpoNo,String gpoNo) {
		super();
		this.pensionCalcId = pensionCalcId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.cvpElgbltyDate = cvpElgbltyDate;
		this.cvpMonthAmnt = cvpMonthAmnt;
		this.cvpRate = cvpRate;
		this.cvpAmnt = cvpAmnt;
		this.pensionerReducedAmnt = pensionerReducedAmnt;
		this.pensionerTotalAmnt = pensionerTotalAmnt;
		this.srvcGratuityAmnt = srvcGratuityAmnt;
		this.dcrgAmnt = dcrgAmnt;
		this.pvsnlPensionAmnt = pvsnlPensionAmnt;
		this.pvsnlPensionDate = pvsnlPensionDate;
		this.dcrgSnctndFlag = dcrgSnctndFlag;
		this.pvsnlDcrgAmnt = pvsnlDcrgAmnt;
		this.pvsnlPensionFlag = pvsnlPensionFlag;
		this.documentCount = documentCount;
		this.pnsnablePayFlag = pnsnablePayFlag;
		this.fp1Amnt = fp1Amnt;
		this.fp1Date = fp1Date;
		this.fp2Amnt = fp2Amnt;
		this.fp2Date = fp2Date;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.cpoNo=cpoNo;
		this.gpoNo=gpoNo;
	}



	

	
	@Override
	public String toString() {

		return "TrnPnsnProcPnsnCalc [pensionCalcId=" + pensionCalcId + ", dbId=" + dbId + ", locationCode=" + locationCode + ", inwardPensionId=" + inwardPensionId + ", pensionerdtlId="
				+ pensionerdtlId + ", cvpElgbltyDate=" + cvpElgbltyDate + ", cvpMonthAmnt=" + cvpMonthAmnt + ", cvpRate=" + cvpRate + ", cvpAmnt=" + cvpAmnt + ", pensionerReducedAmnt="
				+ pensionerReducedAmnt + ", pensionerTotalAmnt=" + pensionerTotalAmnt + ", srvcGratuityAmnt=" + srvcGratuityAmnt + ", dcrgAmnt=" + dcrgAmnt + ", pvsnlPensionAmnt=" + pvsnlPensionAmnt
				+ ", pvsnlPensionDate=" + pvsnlPensionDate + ", dcrgSnctndFlag=" + dcrgSnctndFlag + ", pvsnlDcrgAmnt=" + pvsnlDcrgAmnt + ", pvsnlPensionFlag=" + pvsnlPensionFlag + ", documentCount="
				+ documentCount + ", pnsnablePayFlag=" + pnsnablePayFlag + ", fp1Amnt=" + fp1Amnt + ", fp1Date=" + fp1Date + ", fp2Amnt=" + fp2Amnt + ", fp2Date=" + fp2Date + ", createdUserId="
				+ createdUserId + ", createdPostId=" + createdPostId + ", createdDate=" + createdDate + ", updatedUserId=" + updatedUserId + ", updatedPostId=" + updatedPostId + ", updatedDate="
				+ updatedDate + ", cpoNo=" + cpoNo + ", gpoNo=" + gpoNo + ", hashCode()=" + hashCode() + ", getPensionCalcId()=" + getPensionCalcId() + ", getDbId()=" + getDbId()
				+ ", getLocationCode()=" + getLocationCode() + ", getInwardPensionId()=" + getInwardPensionId() + ", getPensionerdtlId()=" + getPensionerdtlId() + ", getCvpElgbltyDate()="
				+ getCvpElgbltyDate() + ", getCvpMonthAmnt()=" + getCvpMonthAmnt() + ", getCvpRate()=" + getCvpRate() + ", getCvpAmnt()=" + getCvpAmnt() + ", getPensionerReducedAmnt()="
				+ getPensionerReducedAmnt() + ", getPensionerTotalAmnt()=" + getPensionerTotalAmnt() + ", getSrvcGratuityAmnt()=" + getSrvcGratuityAmnt() + ", getDcrgAmnt()=" + getDcrgAmnt()
				+ ", getPvsnlPensionAmnt()=" + getPvsnlPensionAmnt() + ", getPvsnlPensionDate()=" + getPvsnlPensionDate() + ", getDcrgSnctndFlag()=" + getDcrgSnctndFlag() + ", getPvsnlDcrgAmnt()="
				+ getPvsnlDcrgAmnt() + ", getPvsnlPensionFlag()=" + getPvsnlPensionFlag() + ", getDocumentCount()=" + getDocumentCount() + ", getPnsnablePayFlag()=" + getPnsnablePayFlag()
				+ ", getFp1Amnt()=" + getFp1Amnt() + ", getFp1Date()=" + getFp1Date() + ", getFp2Amnt()=" + getFp2Amnt() + ", getFp2Date()=" + getFp2Date() + ", getCreatedUserId()="
				+ getCreatedUserId() + ", getCreatedPostId()=" + getCreatedPostId() + ", getCreatedDate()=" + getCreatedDate() + ", getUpdatedUserId()=" + getUpdatedUserId() + ", getUpdatedPostId()="
				+ getUpdatedPostId() + ", getUpdatedDate()=" + getUpdatedDate() + ", getClass()=" + getClass() + ", toString()=" + super.toString() + "]";
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
		result = prime * result + ((cvpAmnt == null) ? 0 : cvpAmnt.hashCode());
		result = prime * result
				+ ((cvpElgbltyDate == null) ? 0 : cvpElgbltyDate.hashCode());
		result = prime * result
				+ ((cvpMonthAmnt == null) ? 0 : cvpMonthAmnt.hashCode());
		result = prime * result + ((cvpRate == null) ? 0 : cvpRate.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result
				+ ((dcrgAmnt == null) ? 0 : dcrgAmnt.hashCode());
		result = prime * result
				+ ((dcrgSnctndFlag == null) ? 0 : dcrgSnctndFlag.hashCode());
		result = prime * result
				+ ((documentCount == null) ? 0 : documentCount.hashCode());
		result = prime * result + ((fp1Amnt == null) ? 0 : fp1Amnt.hashCode());
		result = prime * result + ((fp1Date == null) ? 0 : fp1Date.hashCode());
		result = prime * result + ((fp2Amnt == null) ? 0 : fp2Amnt.hashCode());
		result = prime * result + ((fp2Date == null) ? 0 : fp2Date.hashCode());
		result = prime * result
				+ ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result
				+ ((pensionCalcId == null) ? 0 : pensionCalcId.hashCode());
		result = prime * result
				+ ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime
				* result
				+ ((pensionerReducedAmnt == null) ? 0 : pensionerReducedAmnt
						.hashCode());
		result = prime
				* result
				+ ((pensionerTotalAmnt == null) ? 0 : pensionerTotalAmnt
						.hashCode());
		result = prime * result
				+ ((pnsnablePayFlag == null) ? 0 : pnsnablePayFlag.hashCode());
		result = prime * result
				+ ((pvsnlDcrgAmnt == null) ? 0 : pvsnlDcrgAmnt.hashCode());
		result = prime
				* result
				+ ((pvsnlPensionAmnt == null) ? 0 : pvsnlPensionAmnt.hashCode());
		result = prime
				* result
				+ ((pvsnlPensionDate == null) ? 0 : pvsnlPensionDate.hashCode());
		result = prime
				* result
				+ ((pvsnlPensionFlag == null) ? 0 : pvsnlPensionFlag.hashCode());
		result = prime
				* result
				+ ((srvcGratuityAmnt == null) ? 0 : srvcGratuityAmnt.hashCode());
		result = prime * result
				+ ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result
				+ ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		result = prime * result
			+ ((cpoNo == null) ? 0 : cpoNo.hashCode());
		result = prime * result
			+ ((gpoNo == null) ? 0 : gpoNo.hashCode());
		
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
		TrnPnsnProcPnsnCalc other = (TrnPnsnProcPnsnCalc) obj;
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
		if (cvpAmnt == null) {
			if (other.cvpAmnt != null)
				return false;
		} else if (!cvpAmnt.equals(other.cvpAmnt))
			return false;
		if (cvpElgbltyDate == null) {
			if (other.cvpElgbltyDate != null)
				return false;
		} else if (!cvpElgbltyDate.equals(other.cvpElgbltyDate))
			return false;
		if (cvpMonthAmnt == null) {
			if (other.cvpMonthAmnt != null)
				return false;
		} else if (!cvpMonthAmnt.equals(other.cvpMonthAmnt))
			return false;
		if (cvpRate == null) {
			if (other.cvpRate != null)
				return false;
		} else if (!cvpRate.equals(other.cvpRate))
			return false;
		if (dbId == null) {
			if (other.dbId != null)
				return false;
		} else if (!dbId.equals(other.dbId))
			return false;
		if (dcrgAmnt == null) {
			if (other.dcrgAmnt != null)
				return false;
		} else if (!dcrgAmnt.equals(other.dcrgAmnt))
			return false;
		if (dcrgSnctndFlag == null) {
			if (other.dcrgSnctndFlag != null)
				return false;
		} else if (!dcrgSnctndFlag.equals(other.dcrgSnctndFlag))
			return false;
		if (documentCount == null) {
			if (other.documentCount != null)
				return false;
		} else if (!documentCount.equals(other.documentCount))
			return false;
		if (fp1Amnt == null) {
			if (other.fp1Amnt != null)
				return false;
		} else if (!fp1Amnt.equals(other.fp1Amnt))
			return false;
		if (fp1Date == null) {
			if (other.fp1Date != null)
				return false;
		} else if (!fp1Date.equals(other.fp1Date))
			return false;
		if (fp2Amnt == null) {
			if (other.fp2Amnt != null)
				return false;
		} else if (!fp2Amnt.equals(other.fp2Amnt))
			return false;
		if (fp2Date == null) {
			if (other.fp2Date != null)
				return false;
		} else if (!fp2Date.equals(other.fp2Date))
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
		if (pensionCalcId == null) {
			if (other.pensionCalcId != null)
				return false;
		} else if (!pensionCalcId.equals(other.pensionCalcId))
			return false;
		if (pensionerdtlId == null) {
			if (other.pensionerdtlId != null)
				return false;
		} else if (!pensionerdtlId.equals(other.pensionerdtlId))
			return false;
		if (pensionerReducedAmnt == null) {
			if (other.pensionerReducedAmnt != null)
				return false;
		} else if (!pensionerReducedAmnt.equals(other.pensionerReducedAmnt))
			return false;
		if (pensionerTotalAmnt == null) {
			if (other.pensionerTotalAmnt != null)
				return false;
		} else if (!pensionerTotalAmnt.equals(other.pensionerTotalAmnt))
			return false;
		if (pnsnablePayFlag == null) {
			if (other.pnsnablePayFlag != null)
				return false;
		} else if (!pnsnablePayFlag.equals(other.pnsnablePayFlag))
			return false;
		if (pvsnlDcrgAmnt == null) {
			if (other.pvsnlDcrgAmnt != null)
				return false;
		} else if (!pvsnlDcrgAmnt.equals(other.pvsnlDcrgAmnt))
			return false;
		if (pvsnlPensionAmnt == null) {
			if (other.pvsnlPensionAmnt != null)
				return false;
		} else if (!pvsnlPensionAmnt.equals(other.pvsnlPensionAmnt))
			return false;
		if (pvsnlPensionDate == null) {
			if (other.pvsnlPensionDate != null)
				return false;
		} else if (!pvsnlPensionDate.equals(other.pvsnlPensionDate))
			return false;
		if (pvsnlPensionFlag == null) {
			if (other.pvsnlPensionFlag != null)
				return false;
		} else if (!pvsnlPensionFlag.equals(other.pvsnlPensionFlag))
			return false;
		if (srvcGratuityAmnt == null) {
			if (other.srvcGratuityAmnt != null)
				return false;
		} else if (!srvcGratuityAmnt.equals(other.srvcGratuityAmnt))
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
		if (cpoNo == null) {
			if (other.cpoNo != null)
				return false;
		} else if (!cpoNo.equals(other.cpoNo))
			return false;
		if (gpoNo == null) {
			if (other.gpoNo != null)
				return false;
		} else if (!gpoNo.equals(other.gpoNo))
			return false;
		return true;
	}



	public Long getPensionCalcId() {
		return pensionCalcId;
	}



	public void setPensionCalcId(Long pensionCalcId) {
		this.pensionCalcId = pensionCalcId;
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

	public Date getCvpElgbltyDate() {
		return cvpElgbltyDate;
	}

	public void setCvpElgbltyDate(Date cvpElgbltyDate) {
		this.cvpElgbltyDate = cvpElgbltyDate;
	}



	public BigDecimal getCvpMonthAmnt() {
		return cvpMonthAmnt;
	}



	public void setCvpMonthAmnt(BigDecimal cvpMonthAmnt) {
		this.cvpMonthAmnt = cvpMonthAmnt;
	}



	public BigDecimal getCvpRate() {
		return cvpRate;
	}



	public void setCvpRate(BigDecimal cvpRate) {
		this.cvpRate = cvpRate;
	}



	public BigDecimal getCvpAmnt() {
		return cvpAmnt;
	}



	public void setCvpAmnt(BigDecimal cvpAmnt) {
		this.cvpAmnt = cvpAmnt;
	}



	public BigDecimal getPensionerReducedAmnt() {
		return pensionerReducedAmnt;
	}



	public void setPensionerReducedAmnt(BigDecimal pensionerReducedAmnt) {
		this.pensionerReducedAmnt = pensionerReducedAmnt;
	}



	public BigDecimal getPensionerTotalAmnt() {
		return pensionerTotalAmnt;
	}



	public void setPensionerTotalAmnt(BigDecimal pensionerTotalAmnt) {
		this.pensionerTotalAmnt = pensionerTotalAmnt;
	}



	public BigDecimal getSrvcGratuityAmnt() {
		return srvcGratuityAmnt;
	}



	public void setSrvcGratuityAmnt(BigDecimal srvcGratuityAmnt) {
		this.srvcGratuityAmnt = srvcGratuityAmnt;
	}



	public BigDecimal getDcrgAmnt() {
		return dcrgAmnt;
	}



	public void setDcrgAmnt(BigDecimal dcrgAmnt) {
		this.dcrgAmnt = dcrgAmnt;
	}



	public BigDecimal getPvsnlPensionAmnt() {
		return pvsnlPensionAmnt;
	}



	public void setPvsnlPensionAmnt(BigDecimal pvsnlPensionAmnt) {
		this.pvsnlPensionAmnt = pvsnlPensionAmnt;
	}



	public Date getPvsnlPensionDate() {
		return pvsnlPensionDate;
	}



	public void setPvsnlPensionDate(Date pvsnlPensionDate) {
		this.pvsnlPensionDate = pvsnlPensionDate;
	}



	public Character getDcrgSnctndFlag() {
		return dcrgSnctndFlag;
	}



	public void setDcrgSnctndFlag(Character dcrgSnctndFlag) {
		this.dcrgSnctndFlag = dcrgSnctndFlag;
	}



	public BigDecimal getPvsnlDcrgAmnt() {
		return pvsnlDcrgAmnt;
	}



	public void setPvsnlDcrgAmnt(BigDecimal pvsnlDcrgAmnt) {
		this.pvsnlDcrgAmnt = pvsnlDcrgAmnt;
	}



	public Character getPvsnlPensionFlag() {
		return pvsnlPensionFlag;
	}



	public void setPvsnlPensionFlag(Character pvsnlPensionFlag) {
		this.pvsnlPensionFlag = pvsnlPensionFlag;
	}



	public Long getDocumentCount() {
		return documentCount;
	}



	public void setDocumentCount(Long documentCount) {
		this.documentCount = documentCount;
	}



	public Character getPnsnablePayFlag() {
		return pnsnablePayFlag;
	}



	public void setPnsnablePayFlag(Character pnsnablePayFlag) {
		this.pnsnablePayFlag = pnsnablePayFlag;
	}



	public BigDecimal getFp1Amnt() {
		return fp1Amnt;
	}



	public void setFp1Amnt(BigDecimal fp1Amnt) {
		this.fp1Amnt = fp1Amnt;
	}



	public Date getFp1Date() {
		return fp1Date;
	}



	public void setFp1Date(Date fp1Date) {
		this.fp1Date = fp1Date;
	}



	public BigDecimal getFp2Amnt() {
		return fp2Amnt;
	}



	public void setFp2Amnt(BigDecimal fp2Amnt) {
		this.fp2Amnt = fp2Amnt;
	}



	public Date getFp2Date() {
		return fp2Date;
	}



	public void setFp2Date(Date fp2Date) {
		this.fp2Date = fp2Date;
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

	public String getCpoNo() {
		return cpoNo;
	}

	public void setCpoNo(String cpoNo) {
		this.cpoNo = cpoNo;
	}

	public String getGpoNo() {
		return gpoNo;
	}

	public void setGpoNo(String gpoNo) {
		this.gpoNo = gpoNo;
	}

	public Date getCpoDate() {
		return cpoDate;
	}

	public void setCpoDate(Date cpoDate) {
		this.cpoDate = cpoDate;
	}

	public Date getGpoDate() {
		return gpoDate;
	}

	public void setGpoDate(Date gpoDate) {
		this.gpoDate = gpoDate;
	}

	public void setManualEditFlag(String manualEditFlag) {
		this.manualEditFlag = manualEditFlag;
	}

	public String getManualEditFlag() {
		return manualEditFlag;
	}

	public void setCvpAppDate(Date cvpAppDate) {
		this.cvpAppDate = cvpAppDate;
	}

	public Date getCvpAppDate() {
		return cvpAppDate;
	}

	public void setWitheldGratuity(Long witheldGratuity) {
		this.witheldGratuity = witheldGratuity;
	}

	public Long getWitheldGratuity() {
		return witheldGratuity;
	}

	public void setDcrgAppDate(Date dcrgAppDate) {
		this.dcrgAppDate = dcrgAppDate;
	}

	public Date getDcrgAppDate() {
		return dcrgAppDate;
	}

	
		
}
