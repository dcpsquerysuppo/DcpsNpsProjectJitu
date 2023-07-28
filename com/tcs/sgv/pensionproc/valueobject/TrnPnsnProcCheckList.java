/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.valueobject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Feb 8, 2011
 */
public class TrnPnsnProcCheckList implements Serializable
{
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 3057549341135199124L;
	
	private Long pnsnsrCheckListId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private Long pensionerdtlId;
	private Date fromDate;
	private Date toDate;
	private String officeName;
	private Long certificateAttachId;
	private Character gvnmntAcmdtnFlag;
	private Character nocFlag;
	private Long certificateTypeId;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	public TrnPnsnProcCheckList()
	{}



	public TrnPnsnProcCheckList(Long pnsnsrCheckListId, Long dbId,
			Long locationCode, Long inwardPensionId,
			Long pensionerdtlId, Date fromDate, Date toDate, String officeName,String certificatePath,Long certificateAttachId,
			Character gvnmntAcmdtnFlag, Character nocFlag,
			Long certificateTypeId, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate) 
	{
		super();
		this.pnsnsrCheckListId = pnsnsrCheckListId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardPensionId = inwardPensionId;
		this.pensionerdtlId = pensionerdtlId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.officeName = officeName;
		this.certificateAttachId=certificateAttachId;
		this.gvnmntAcmdtnFlag = gvnmntAcmdtnFlag;
		this.nocFlag = nocFlag;
		this.certificateTypeId = certificateTypeId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}



	@Override
	public String toString() {
		return "TrnPnsnProcCheckList [certificateTypeId=" + certificateTypeId
				+ ", createdDate=" + createdDate + ", createdPostId="
				+ createdPostId + ", createdUserId=" + createdUserId
				+ ", dbId=" + dbId + ", officeName=" + officeName + ",,certificateAttachId=" + certificateAttachId + "," +
						" fromDate=" + fromDate
				+ ", gvnmntAcmdtnFlag=" + gvnmntAcmdtnFlag
				+ ", inwardPensionId=" + inwardPensionId + ", locationCode="
				+ locationCode + ", nocFlag=" + nocFlag + ", pensionerId="
				+ pensionerdtlId + ", pnsnsrCheckListId=" + pnsnsrCheckListId
				+ ", toDate=" + toDate + ", updatedDate=" + updatedDate
				+ ", updatedPostId=" + updatedPostId + ", updatedUserId="
				+ updatedUserId + "]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((certificateTypeId == null) ? 0 : certificateTypeId
						.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		
		result = prime * result
				+ ((officeName == null) ? 0 : officeName.hashCode());
		
		result = prime * result
		+ ((certificateAttachId == null) ? 0 : certificateAttachId.hashCode());
		result = prime * result
				+ ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime
				* result
				+ ((gvnmntAcmdtnFlag == null) ? 0 : gvnmntAcmdtnFlag.hashCode());
		result = prime * result
				+ ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((nocFlag == null) ? 0 : nocFlag.hashCode());
		result = prime * result
				+ ((pensionerdtlId == null) ? 0 : pensionerdtlId.hashCode());
		result = prime
				* result
				+ ((pnsnsrCheckListId == null) ? 0 : pnsnsrCheckListId
						.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
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
		TrnPnsnProcCheckList other = (TrnPnsnProcCheckList) obj;
		if (certificateTypeId == null) {
			if (other.certificateTypeId != null)
				return false;
		} else if (!certificateTypeId.equals(other.certificateTypeId))
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
		if (officeName == null) {
			if (other.officeName != null)
				return false;
		} else if (!officeName.equals(other.officeName))
			return false;
	
		
		if (certificateAttachId == null) {
			if (other.certificateAttachId != null)
				return false;
		} else if (!certificateAttachId.equals(other.certificateAttachId))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (gvnmntAcmdtnFlag == null) {
			if (other.gvnmntAcmdtnFlag != null)
				return false;
		} else if (!gvnmntAcmdtnFlag.equals(other.gvnmntAcmdtnFlag))
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
		if (nocFlag == null) {
			if (other.nocFlag != null)
				return false;
		} else if (!nocFlag.equals(other.nocFlag))
			return false;
		if (pensionerdtlId == null) {
			if (other.pensionerdtlId != null)
				return false;
		} else if (!pensionerdtlId.equals(other.pensionerdtlId))
			return false;
		if (pnsnsrCheckListId == null) {
			if (other.pnsnsrCheckListId != null)
				return false;
		} else if (!pnsnsrCheckListId.equals(other.pnsnsrCheckListId))
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



	public Long getPnsnsrCheckListId() {
		return pnsnsrCheckListId;
	}



	public void setPnsnsrCheckListId(Long pnsnsrCheckListId) {
		this.pnsnsrCheckListId = pnsnsrCheckListId;
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



	public Date getToDate() {
		return toDate;
	}



	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Character getGvnmntAcmdtnFlag() {
		return gvnmntAcmdtnFlag;
	}



	public void setGvnmntAcmdtnFlag(Character gvnmntAcmdtnFlag) {
		this.gvnmntAcmdtnFlag = gvnmntAcmdtnFlag;
	}



	public Character getNocFlag() {
		return nocFlag;
	}



	public void setNocFlag(Character nocFlag) {
		this.nocFlag = nocFlag;
	}
	public String getOfficeName() {
		return officeName;
	}



	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}



	public Long getCertificateTypeId() {
		return certificateTypeId;
	}



	public void setCertificateTypeId(Long certificateTypeId) {
		this.certificateTypeId = certificateTypeId;
	}

	public Long getCertificateAttachId() {
		return certificateAttachId;
	}



	public void setCertificateAttachId(Long certificateAttachId) {
		this.certificateAttachId = certificateAttachId;
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
