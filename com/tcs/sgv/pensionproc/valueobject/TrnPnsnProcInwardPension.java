/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 31, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.valueobject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Jan 31, 2011
 */
public class TrnPnsnProcInwardPension implements Serializable {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -6437140136804263845L;

	private Long inwardPensionId;
	private Long dbId;
	private Long locationCode;
	private String inwardNo;
	private Date inwardDate;
	private String caseType;
	private String sevaarthId;
	private Long ddoCode;
	private Date ppoDate;
	private String ppoNo;
	private Long documentCount;
	private Long revisionNo;
	private String payCommission;
	private Character commuateFlag;
	private Double commuteVal;
	private String pensionType;
	private String pensionerType;
	private Character manualComputationFlag;
	private String caseStatus;
	private Character draftFlag;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String comments;
	private Date commensionDate;
	private String agOfficePension;
	private String agOfficeAftrFirstPay;
	private Long trsryIdPension;
	private Long trsryIdAftrFirstPay;
	private String otherPnsnrType;
	private String outwardNo;
	private Date outwardDate;

	public TrnPnsnProcInwardPension() {
	}

	/**
	 * 
	 * @param inwardPensionId
	 * @param dbId
	 * @param locationCode
	 * @param inwardNo
	 * @param inwardDate
	 * @param caseType
	 * @param sevaarthId
	 * @param ddoCode
	 * @param ppoDate
	 * @param ppoNo
	 * @param documentCount
	 * @param revisionNo
	 * @param payComission
	 * @param commuateFlag
	 * @param commuteVal
	 * @param pensionType
	 * @param pnsnrTypeLookupId
	 * @param manualComputationFlag
	 * @param statusLookupId
	 * @param draftFlag
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param comments
	 * @param commensionDate
	 * @param agOffice
	 */

	public TrnPnsnProcInwardPension(Long inwardPensionId, Long dbId, Long locationCode, String inwardNo, Date inwardDate, String caseType, String sevaarthId, Long ddoCode,
			Date ppoDate, String ppoNo, Long documentCount, Long revisionNo, String payCommission, Character commuateFlag, Double commuteVal, String pensionType,
			String pensionerType, Character manualComputationFlag, String caseStatus, Character draftFlag, Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate, String comments, Date commensionDate, String agOfficePension, String agOfficeAftrFirstPay,
			Long trsryIdPension, Long trsryIdAftrFirstPay, String otherPnsnrType) {
		super();
		this.inwardPensionId = inwardPensionId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardNo = inwardNo;
		this.inwardDate = inwardDate;
		this.caseType = caseType;
		this.sevaarthId = sevaarthId;
		this.ddoCode = ddoCode;
		this.ppoDate = ppoDate;
		this.ppoNo = ppoNo;
		this.documentCount = documentCount;
		this.revisionNo = revisionNo;
		this.payCommission = payCommission;
		this.commuateFlag = commuateFlag;
		this.commuteVal = commuteVal;
		this.pensionType = pensionType;
		this.pensionerType = pensionerType;
		this.manualComputationFlag = manualComputationFlag;
		this.caseStatus = caseStatus;
		this.draftFlag = draftFlag;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.comments = comments;
		this.commensionDate = commensionDate;
		this.agOfficePension = agOfficePension;
		this.agOfficeAftrFirstPay = agOfficeAftrFirstPay;
		this.trsryIdPension = trsryIdPension;
		this.trsryIdAftrFirstPay = trsryIdAftrFirstPay;
		this.otherPnsnrType = otherPnsnrType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agOfficePension == null) ? 0 : agOfficePension.hashCode());
		result = prime * result + ((agOfficeAftrFirstPay == null) ? 0 : agOfficeAftrFirstPay.hashCode());
		result = prime * result + ((trsryIdPension == null) ? 0 : trsryIdPension.hashCode());
		result = prime * result + ((trsryIdAftrFirstPay == null) ? 0 : trsryIdAftrFirstPay.hashCode());
		result = prime * result + ((caseType == null) ? 0 : caseType.hashCode());
		result = prime * result + ((commensionDate == null) ? 0 : commensionDate.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((commuateFlag == null) ? 0 : commuateFlag.hashCode());
		result = prime * result + ((commuteVal == null) ? 0 : commuteVal.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result + ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((ddoCode == null) ? 0 : ddoCode.hashCode());
		result = prime * result + ((documentCount == null) ? 0 : documentCount.hashCode());
		result = prime * result + ((draftFlag == null) ? 0 : draftFlag.hashCode());
		result = prime * result + ((inwardDate == null) ? 0 : inwardDate.hashCode());
		result = prime * result + ((inwardNo == null) ? 0 : inwardNo.hashCode());
		result = prime * result + ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((manualComputationFlag == null) ? 0 : manualComputationFlag.hashCode());
		result = prime * result + ((otherPnsnrType == null) ? 0 : otherPnsnrType.hashCode());
		result = prime * result + ((payCommission == null) ? 0 : payCommission.hashCode());
		result = prime * result + ((pensionType == null) ? 0 : pensionType.hashCode());
		result = prime * result + ((pensionerType == null) ? 0 : pensionerType.hashCode());
		result = prime * result + ((ppoDate == null) ? 0 : ppoDate.hashCode());
		result = prime * result + ((ppoNo == null) ? 0 : ppoNo.hashCode());
		result = prime * result + ((revisionNo == null) ? 0 : revisionNo.hashCode());
		result = prime * result + ((sevaarthId == null) ? 0 : sevaarthId.hashCode());
		result = prime * result + ((caseStatus == null) ? 0 : caseStatus.hashCode());
		result = prime * result + ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result + ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result + ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TrnPnsnProcInwardPension other = (TrnPnsnProcInwardPension) obj;
		if (agOfficePension == null) {
			if (other.agOfficePension != null) {
				return false;
			}
		} else if (!agOfficePension.equals(other.agOfficePension)) {
			return false;
		}
		if (agOfficeAftrFirstPay == null) {
			if (other.agOfficeAftrFirstPay != null) {
				return false;
			}
		} else if (!agOfficeAftrFirstPay.equals(other.agOfficeAftrFirstPay)) {
			return false;
		}
		if (trsryIdPension == null) {
			if (other.trsryIdPension != null) {
				return false;
			}
		} else if (!trsryIdPension.equals(other.trsryIdPension)) {
			return false;
		}
		if (trsryIdAftrFirstPay == null) {
			if (other.agOfficePension != null) {
				return false;
			}
		} else if (!trsryIdAftrFirstPay.equals(other.trsryIdAftrFirstPay)) {
			return false;
		}

		if (caseType == null) {
			if (other.caseType != null) {
				return false;
			}
		} else if (!caseType.equals(other.caseType)) {
			return false;
		}
		if (commensionDate == null) {
			if (other.commensionDate != null) {
				return false;
			}
		} else if (!commensionDate.equals(other.commensionDate)) {
			return false;
		}
		if (comments == null) {
			if (other.comments != null) {
				return false;
			}
		} else if (!comments.equals(other.comments)) {
			return false;
		}
		if (commuateFlag == null) {
			if (other.commuateFlag != null) {
				return false;
			}
		} else if (!commuateFlag.equals(other.commuateFlag)) {
			return false;
		}
		if (commuteVal == null) {
			if (other.commuteVal != null) {
				return false;
			}
		} else if (!commuteVal.equals(other.commuteVal)) {
			return false;
		}
		if (createdDate == null) {
			if (other.createdDate != null) {
				return false;
			}
		} else if (!createdDate.equals(other.createdDate)) {
			return false;
		}
		if (createdPostId == null) {
			if (other.createdPostId != null) {
				return false;
			}
		} else if (!createdPostId.equals(other.createdPostId)) {
			return false;
		}
		if (createdUserId == null) {
			if (other.createdUserId != null) {
				return false;
			}
		} else if (!createdUserId.equals(other.createdUserId)) {
			return false;
		}
		if (dbId == null) {
			if (other.dbId != null) {
				return false;
			}
		} else if (!dbId.equals(other.dbId)) {
			return false;
		}
		if (ddoCode == null) {
			if (other.ddoCode != null) {
				return false;
			}
		} else if (!ddoCode.equals(other.ddoCode)) {
			return false;
		}
		if (documentCount == null) {
			if (other.documentCount != null) {
				return false;
			}
		} else if (!documentCount.equals(other.documentCount)) {
			return false;
		}
		if (draftFlag == null) {
			if (other.draftFlag != null) {
				return false;
			}
		} else if (!draftFlag.equals(other.draftFlag)) {
			return false;
		}
		if (inwardDate == null) {
			if (other.inwardDate != null) {
				return false;
			}
		} else if (!inwardDate.equals(other.inwardDate)) {
			return false;
		}
		if (inwardNo == null) {
			if (other.inwardNo != null) {
				return false;
			}
		} else if (!inwardNo.equals(other.inwardNo)) {
			return false;
		}
		if (inwardPensionId == null) {
			if (other.inwardPensionId != null) {
				return false;
			}
		} else if (!inwardPensionId.equals(other.inwardPensionId)) {
			return false;
		}
		if (locationCode == null) {
			if (other.locationCode != null) {
				return false;
			}
		} else if (!locationCode.equals(other.locationCode)) {
			return false;
		}
		if (manualComputationFlag == null) {
			if (other.manualComputationFlag != null) {
				return false;
			}
		} else if (!manualComputationFlag.equals(other.manualComputationFlag)) {
			return false;
		}
		if (otherPnsnrType == null) {
			if (other.otherPnsnrType != null) {
				return false;
			}
		} else if (!otherPnsnrType.equals(other.otherPnsnrType)) {
			return false;
		}
		if (payCommission == null) {
			if (other.payCommission != null) {
				return false;
			}
		} else if (!payCommission.equals(other.payCommission)) {
			return false;
		}
		if (pensionType == null) {
			if (other.pensionType != null) {
				return false;
			}
		} else if (!pensionType.equals(other.pensionType)) {
			return false;
		}
		if (pensionerType == null) {
			if (other.pensionerType != null) {
				return false;
			}
		} else if (!pensionerType.equals(other.pensionerType)) {
			return false;
		}
		if (ppoDate == null) {
			if (other.ppoDate != null) {
				return false;
			}
		} else if (!ppoDate.equals(other.ppoDate)) {
			return false;
		}
		if (ppoNo == null) {
			if (other.ppoNo != null) {
				return false;
			}
		} else if (!ppoNo.equals(other.ppoNo)) {
			return false;
		}
		if (revisionNo == null) {
			if (other.revisionNo != null) {
				return false;
			}
		} else if (!revisionNo.equals(other.revisionNo)) {
			return false;
		}
		if (sevaarthId == null) {
			if (other.sevaarthId != null) {
				return false;
			}
		} else if (!sevaarthId.equals(other.sevaarthId)) {
			return false;
		}
		if (caseStatus == null) {
			if (other.caseStatus != null) {
				return false;
			}
		} else if (!caseStatus.equals(other.caseStatus)) {
			return false;
		}
		if (updatedDate == null) {
			if (other.updatedDate != null) {
				return false;
			}
		} else if (!updatedDate.equals(other.updatedDate)) {
			return false;
		}
		if (updatedPostId == null) {
			if (other.updatedPostId != null) {
				return false;
			}
		} else if (!updatedPostId.equals(other.updatedPostId)) {
			return false;
		}
		if (updatedUserId == null) {
			if (other.updatedUserId != null) {
				return false;
			}
		} else if (!updatedUserId.equals(other.updatedUserId)) {
			return false;
		}
		return true;
	}

	public Long getInwardPensionId() {
		return inwardPensionId;
	}

	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
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

	public String getInwardNo() {
		return inwardNo;
	}

	public void setInwardNo(String inwardNo) {
		this.inwardNo = inwardNo;
	}

	public Date getInwardDate() {
		return inwardDate;
	}

	public void setInwardDate(Date inwardDate) {
		this.inwardDate = inwardDate;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getSevaarthId() {
		return sevaarthId;
	}

	public void setSevaarthId(String sevaarthId) {
		this.sevaarthId = sevaarthId;
	}

	public Long getDdoCode() {
		return ddoCode;
	}

	public void setDdoCode(Long ddoCode) {
		this.ddoCode = ddoCode;
	}

	public Date getPpoDate() {
		return ppoDate;
	}

	public void setPpoDate(Date ppoDate) {
		this.ppoDate = ppoDate;
	}

	public String getPpoNo() {
		return ppoNo;
	}

	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}

	public Long getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(Long documentCount) {
		this.documentCount = documentCount;
	}

	public Long getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(Long revisionNo) {
		this.revisionNo = revisionNo;
	}

	public String getPayCommission() {
		return payCommission;
	}

	public void setPayCommission(String payCommission) {
		this.payCommission = payCommission;
	}

	public Character getCommuateFlag() {
		return commuateFlag;
	}

	public void setCommuateFlag(Character commuateFlag) {
		this.commuateFlag = commuateFlag;
	}

	public Double getCommuteVal() {
		return commuteVal;
	}

	public void setCommuteVal(Double commuteVal) {
		this.commuteVal = commuteVal;
	}

	public String getPensionType() {

		return pensionType;
	}

	public void setPensionType(String pensionType) {

		this.pensionType = pensionType;
	}

	public String getPensionerType() {
		return pensionerType;
	}

	public void setPensionerType(String pensionerType) {
		this.pensionerType = pensionerType;
	}

	public Character getManualComputationFlag() {
		return manualComputationFlag;
	}

	public void setManualComputationFlag(Character manualComputationFlag) {
		this.manualComputationFlag = manualComputationFlag;
	}

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

	public Character getDraftFlag() {
		return draftFlag;
	}

	public void setDraftFlag(Character draftFlag) {
		this.draftFlag = draftFlag;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCommensionDate() {

		return commensionDate;
	}

	public void setCommensionDate(Date commensionDate) {

		this.commensionDate = commensionDate;
	}

	public String getOtherPnsnrType() {
		return otherPnsnrType;
	}

	public void setOtherPnsnrType(String otherPnsnrType) {
		this.otherPnsnrType = otherPnsnrType;
	}

	public String getAgOfficePension() {
		return agOfficePension;
	}

	public void setAgOfficePension(String agOfficePension) {
		this.agOfficePension = agOfficePension;
	}

	public String getAgOfficeAftrFirstPay() {
		return agOfficeAftrFirstPay;
	}

	public void setAgOfficeAftrFirstPay(String agOfficeAftrFirstPay) {
		this.agOfficeAftrFirstPay = agOfficeAftrFirstPay;
	}

	public Long getTrsryIdPension() {
		return trsryIdPension;
	}

	public void setTrsryIdPension(Long trsryIdPension) {
		this.trsryIdPension = trsryIdPension;
	}

	public Long getTrsryIdAftrFirstPay() {
		return trsryIdAftrFirstPay;
	}

	public void setTrsryIdAftrFirstPay(Long trsryIdAftrFirstPay) {
		this.trsryIdAftrFirstPay = trsryIdAftrFirstPay;
	}

	public String getOutwardNo() {
		return outwardNo;
	}

	public void setOutwardNo(String outwardNo) {
		this.outwardNo = outwardNo;
	}

	public Date getOutwardDate() {
		return outwardDate;
	}

	public void setOutwardDate(Date outwardDate) {
		this.outwardDate = outwardDate;
	}

}
