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
public class TrnPnsnProcPnsnrDtls implements Serializable {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -64733036114676728L;

	private Long pensionerDtlId;
	private Long dbId;
	private Long locationCode;
	private Long inwardPensionId;
	private String pnsnrName;
	private String pnsnrNameInMarathi;
	private Date birthDate;
	private Date joiningDate;
	private Date retirementDate;
	private Date deathDate;
	private Date lostDate;
	private Date demandedRetirementDate;
	private Date firDate;
	private Character genderFlag;
	private Long groupLookupId;
	private String designation;
	private Integer heightFeet;
	private Integer heightInches;
	private String remarks;
	private String pnsnrAddrFlat;
	private String pnsnrAddrRoad;
	private String pnsnrAddrArea;
	private String pnsnrAddrDistCode;
	private Long pnsnrAddrStateCode;
	private Long pnsnrAddrPincode;
	private String pnsnrAddrMobileNo;
	private String pnsnrAddrLandlineNo;
	private String pnsnrAddrEmailAddr;
	private Character pnsnrAddrRetFlag;
	private String pnsnrAddrRetFlat;
	private String pnsnrAddrRetRoad;
	private String pnsnrAddrRetArea;
	private String pnsnrAddrRetDistCode;
	private Long pnsnrAddrRetStateCode;
	private Long pnsnrAddrRetPincode;
	private String pnsnrAddrRetMobileNo;
	private String pnsnrAddrRetLandlineNo;
	private String pnsnrAddrRetEmailAddr;
	private Long departmentId;
	private Long hooId;
	private Character changeTrsryFlag;
	private Long revisedReason;
	private String officeFlat;
	private String officeRoad;
	private String officeArea;
	private String officeDistCode;
	private Long officeStateCode;
	private Long officePincode;
	private String officeMobileNo;
	private String officeLandLineNo;
	private String officeEmailAddr;
	private String bankName;
	private String bankBranchName;
	private String bankAddress;
	private String bankAccountNo;
	private String bankIfscCode;
	private Long photoAttachmentId;
	private Long signatureAttachmentId;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String uId;
	private String eId;
	private String trsryPnsnComt;
	private String bnOrAn;
	private Character efpYear;
	private String panNo;
	private String ddoOfficeName;
	
	public TrnPnsnProcPnsnrDtls() {
	}

	/**
	 * @param pensionerDtlId
	 * @param dbId
	 * @param locationCode
	 * @param inwardPensionId
	 * @param pnsnrName
	 * @param pnsnrNameInMarathi
	 * @param birthDate
	 * @param joiningDate
	 * @param retirementDate
	 * @param deathDate
	 * @param lostDate
	 * @param demandedRetirementDate
	 * @param firDate
	 * @param genderFlag
	 * @param groupLookupId
	 * @param designation
	 * @param heightFeet
	 * @param heightInches
	 * @param remarks
	 * @param pnsnrAddrFlat
	 * @param pnsnrAddrRoad
	 * @param pnsnrAddrArea
	 * @param pnsnrAddrDistCode
	 * @param pnsnrAddrStateCode
	 * @param pnsnrAddrPincode
	 * @param pnsnrAddrMobileNo
	 * @param pnsnrAddrRetMobileNo;
	 * @param pnsnrAddrRetLandlineNo;
	 * @param pnsnrAddrRetEmailAddr;
	 * @param pnsnrAddrLandlineNo
	 * @param pnsnrAddrEmailAddr
	 * @param pnsnrAddrRetFlag
	 * @param pnsnrAddrRetFlat
	 * @param pnsnrAddrRetRoad
	 * @param pnsnrAddrRetArea
	 * @param pnsnrAddrRetDistCode
	 * @param pnsnrAddrRetStateCode
	 * @param pnsnrAddrRetPincode
	 * @param departmentId
	 * @param hooId
	 * @param trsryPnsnComtId
	 * @param changeTrsryFlag
	 * @param trsryId
	 * @param revisedReason
	 * @param officeFlat
	 * @param officeRoad
	 * @param officeArea
	 * @param officeDistCode
	 * @param officeStateCode
	 * @param officePincode
	 * @param officeMobileNo
	 * @param officeLandLineNo
	 * @param officeEmailAddr
	 * @param bankName
	 * @param bankBranchName
	 * @param bankAddress
	 * @param bankAccountNo
	 * @param bankIfscCode
	 * @param photoAttachmentId
	 * @param signatureAttachmentId
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param uId
	 * @param eId
	 * @param otherPnsnrType
	 */
	public TrnPnsnProcPnsnrDtls(Long pensionerDtlId, Long dbId, Long locationCode, Long inwardPensionId, String pnsnrName, String pnsnrNameInMarathi, Date birthDate,
			Date joiningDate, Date retirementDate, Date deathDate, Date lostDate, Date demandedRetirementDate, Date firDate, Character genderFlag, Long groupLookupId,
			String designation, Integer heightFeet, Integer heightInches, String remarks, String pnsnrAddrFlat, String pnsnrAddrRoad, String pnsnrAddrArea,
			String pnsnrAddrDistCode, Long pnsnrAddrStateCode, Long pnsnrAddrPincode, String pnsnrAddrMobileNo, String pnsnrAddrLandlineNo, String pnsnrAddrEmailAddr,
			Character pnsnrAddrRetFlag, String pnsnrAddrRetFlat, String pnsnrAddrRetRoad, String pnsnrAddrRetArea, String pnsnrAddrRetDistCode, Long pnsnrAddrRetStateCode,
			Long pnsnrAddrRetPincode, Long departmentId, Long hooId, Character changeTrsryFlag, Long revisedReason, String officeFlat, String officeRoad, String officeArea,
			String officeDistCode, Long officeStateCode, Long officePincode, String officeMobileNo, String officeLandLineNo, String officeEmailAddr, String bankName,
			String bankBranchName, String bankAddress, String bankAccountNo, String bankIfscCode, Long photoAttachmentId, Long signatureAttachmentId, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate, String uId, String eId, String pnsnrAddrRetMobileNo,
			String pnsnrAddrRetLandlineNo, String pnsnrAddrRetEmailAddr, String trsryPnsnComt) {
		super();
		this.pensionerDtlId = pensionerDtlId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.inwardPensionId = inwardPensionId;
		this.pnsnrName = pnsnrName;
		this.pnsnrNameInMarathi = pnsnrNameInMarathi;
		this.birthDate = birthDate;
		this.joiningDate = joiningDate;
		this.retirementDate = retirementDate;
		this.deathDate = deathDate;
		this.lostDate = lostDate;
		this.demandedRetirementDate = demandedRetirementDate;
		this.firDate = firDate;
		this.genderFlag = genderFlag;
		this.groupLookupId = groupLookupId;
		this.designation = designation;
		this.heightFeet = heightFeet;
		this.heightInches = heightInches;
		this.remarks = remarks;
		this.pnsnrAddrFlat = pnsnrAddrFlat;
		this.pnsnrAddrRoad = pnsnrAddrRoad;
		this.pnsnrAddrArea = pnsnrAddrArea;
		this.pnsnrAddrDistCode = pnsnrAddrDistCode;
		this.pnsnrAddrStateCode = pnsnrAddrStateCode;
		this.pnsnrAddrPincode = pnsnrAddrPincode;
		this.pnsnrAddrMobileNo = pnsnrAddrMobileNo;
		this.pnsnrAddrLandlineNo = pnsnrAddrLandlineNo;
		this.pnsnrAddrEmailAddr = pnsnrAddrEmailAddr;
		this.pnsnrAddrRetFlag = pnsnrAddrRetFlag;
		this.pnsnrAddrRetFlat = pnsnrAddrRetFlat;
		this.pnsnrAddrRetRoad = pnsnrAddrRetRoad;
		this.pnsnrAddrRetArea = pnsnrAddrRetArea;
		this.pnsnrAddrRetDistCode = pnsnrAddrRetDistCode;
		this.pnsnrAddrRetStateCode = pnsnrAddrRetStateCode;
		this.pnsnrAddrRetPincode = pnsnrAddrRetPincode;
		this.departmentId = departmentId;
		this.hooId = hooId;
		this.changeTrsryFlag = changeTrsryFlag;
		this.revisedReason = revisedReason;
		this.officeFlat = officeFlat;
		this.officeRoad = officeRoad;
		this.officeArea = officeArea;
		this.officeDistCode = officeDistCode;
		this.officeStateCode = officeStateCode;
		this.officePincode = officePincode;
		this.officeMobileNo = officeMobileNo;
		this.officeLandLineNo = officeLandLineNo;
		this.officeEmailAddr = officeEmailAddr;
		this.bankName = bankName;
		this.bankBranchName = bankBranchName;
		this.bankAddress = bankAddress;
		this.bankAccountNo = bankAccountNo;
		this.bankIfscCode = bankIfscCode;
		this.photoAttachmentId = photoAttachmentId;
		this.signatureAttachmentId = signatureAttachmentId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.uId = uId;
		this.eId = eId;
		this.pnsnrAddrRetMobileNo = pnsnrAddrRetMobileNo;
		this.pnsnrAddrRetLandlineNo = pnsnrAddrRetLandlineNo;
		this.pnsnrAddrRetEmailAddr = pnsnrAddrRetEmailAddr;
		this.trsryPnsnComt = trsryPnsnComt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bankAccountNo == null) ? 0 : bankAccountNo.hashCode());
		result = prime * result + ((bankAddress == null) ? 0 : bankAddress.hashCode());
		result = prime * result + ((bankBranchName == null) ? 0 : bankBranchName.hashCode());
		result = prime * result + ((bankIfscCode == null) ? 0 : bankIfscCode.hashCode());
		result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result + ((uId == null) ? 0 : uId.hashCode());
		result = prime * result + ((eId == null) ? 0 : eId.hashCode());
		result = prime * result + ((pnsnrAddrRetMobileNo == null) ? 0 : pnsnrAddrRetMobileNo.hashCode());
		result = prime * result + ((pnsnrAddrRetLandlineNo == null) ? 0 : pnsnrAddrRetLandlineNo.hashCode());
		result = prime * result + ((pnsnrAddrRetEmailAddr == null) ? 0 : pnsnrAddrRetEmailAddr.hashCode());
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((changeTrsryFlag == null) ? 0 : changeTrsryFlag.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result + ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((deathDate == null) ? 0 : deathDate.hashCode());
		result = prime * result + ((demandedRetirementDate == null) ? 0 : demandedRetirementDate.hashCode());
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((designation == null) ? 0 : designation.hashCode());
		result = prime * result + ((firDate == null) ? 0 : firDate.hashCode());
		result = prime * result + ((genderFlag == null) ? 0 : genderFlag.hashCode());
		result = prime * result + ((groupLookupId == null) ? 0 : groupLookupId.hashCode());
		result = prime * result + ((heightFeet == null) ? 0 : heightFeet.hashCode());
		result = prime * result + ((heightInches == null) ? 0 : heightInches.hashCode());
		result = prime * result + ((hooId == null) ? 0 : hooId.hashCode());
		result = prime * result + ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result + ((joiningDate == null) ? 0 : joiningDate.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((lostDate == null) ? 0 : lostDate.hashCode());
		result = prime * result + ((officeArea == null) ? 0 : officeArea.hashCode());
		result = prime * result + ((officeDistCode == null) ? 0 : officeDistCode.hashCode());
		result = prime * result + ((officeEmailAddr == null) ? 0 : officeEmailAddr.hashCode());
		result = prime * result + ((officeFlat == null) ? 0 : officeFlat.hashCode());
		result = prime * result + ((officeLandLineNo == null) ? 0 : officeLandLineNo.hashCode());
		result = prime * result + ((officeMobileNo == null) ? 0 : officeMobileNo.hashCode());
		result = prime * result + ((officePincode == null) ? 0 : officePincode.hashCode());
		result = prime * result + ((officeRoad == null) ? 0 : officeRoad.hashCode());
		result = prime * result + ((officeStateCode == null) ? 0 : officeStateCode.hashCode());
		result = prime * result + ((pensionerDtlId == null) ? 0 : pensionerDtlId.hashCode());
		result = prime * result + ((photoAttachmentId == null) ? 0 : photoAttachmentId.hashCode());
		result = prime * result + ((pnsnrAddrArea == null) ? 0 : pnsnrAddrArea.hashCode());
		result = prime * result + ((pnsnrAddrDistCode == null) ? 0 : pnsnrAddrDistCode.hashCode());
		result = prime * result + ((pnsnrAddrEmailAddr == null) ? 0 : pnsnrAddrEmailAddr.hashCode());
		result = prime * result + ((pnsnrAddrFlat == null) ? 0 : pnsnrAddrFlat.hashCode());
		result = prime * result + ((pnsnrAddrLandlineNo == null) ? 0 : pnsnrAddrLandlineNo.hashCode());
		result = prime * result + ((pnsnrAddrMobileNo == null) ? 0 : pnsnrAddrMobileNo.hashCode());
		result = prime * result + ((pnsnrAddrPincode == null) ? 0 : pnsnrAddrPincode.hashCode());
		result = prime * result + ((pnsnrAddrRetArea == null) ? 0 : pnsnrAddrRetArea.hashCode());
		result = prime * result + ((pnsnrAddrRetDistCode == null) ? 0 : pnsnrAddrRetDistCode.hashCode());
		result = prime * result + ((pnsnrAddrRetFlag == null) ? 0 : pnsnrAddrRetFlag.hashCode());
		result = prime * result + ((pnsnrAddrRetFlat == null) ? 0 : pnsnrAddrRetFlat.hashCode());
		result = prime * result + ((pnsnrAddrRetPincode == null) ? 0 : pnsnrAddrRetPincode.hashCode());
		result = prime * result + ((pnsnrAddrRetRoad == null) ? 0 : pnsnrAddrRetRoad.hashCode());
		result = prime * result + ((pnsnrAddrRetStateCode == null) ? 0 : pnsnrAddrRetStateCode.hashCode());
		result = prime * result + ((pnsnrAddrRoad == null) ? 0 : pnsnrAddrRoad.hashCode());
		result = prime * result + ((pnsnrAddrStateCode == null) ? 0 : pnsnrAddrStateCode.hashCode());
		result = prime * result + ((pnsnrName == null) ? 0 : pnsnrName.hashCode());
		result = prime * result + ((pnsnrNameInMarathi == null) ? 0 : pnsnrNameInMarathi.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((retirementDate == null) ? 0 : retirementDate.hashCode());
		result = prime * result + ((revisedReason == null) ? 0 : revisedReason.hashCode());
		result = prime * result + ((signatureAttachmentId == null) ? 0 : signatureAttachmentId.hashCode());
		result = prime * result + ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result + ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result + ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		TrnPnsnProcPnsnrDtls other = (TrnPnsnProcPnsnrDtls) obj;
		if (bankAccountNo == null) {
			if (other.bankAccountNo != null) {
				return false;
			}
		} else if (!bankAccountNo.equals(other.bankAccountNo)) {
			return false;
		}
		if (bankAddress == null) {
			if (other.bankAddress != null) {
				return false;
			}
		} else if (!bankAddress.equals(other.bankAddress)) {
			return false;
		}
		if (bankBranchName == null) {
			if (other.bankBranchName != null) {
				return false;
			}
		} else if (!bankBranchName.equals(other.bankBranchName)) {
			return false;
		}
		if (bankIfscCode == null) {
			if (other.bankIfscCode != null) {
				return false;
			}
		} else if (!bankIfscCode.equals(other.bankIfscCode)) {
			return false;
		}
		if (bankName == null) {
			if (other.bankName != null) {
				return false;
			}
		} else if (!bankName.equals(other.bankName)) {
			return false;
		}
		if (uId == null) {
			if (other.uId != null) {
				return false;
			}
		} else if (!uId.equals(other.uId)) {
			return false;
		}
		if (eId == null) {
			if (other.eId != null) {
				return false;
			}
		} else if (!eId.equals(other.eId)) {
			return false;
		}
		if (pnsnrAddrRetMobileNo == null) {
			if (other.pnsnrAddrRetMobileNo != null) {
				return false;
			}
		} else if (!pnsnrAddrRetMobileNo.equals(other.pnsnrAddrRetMobileNo)) {
			return false;
		}
		if (pnsnrAddrRetLandlineNo == null) {
			if (other.pnsnrAddrRetLandlineNo != null) {
				return false;
			}
		} else if (!this.pnsnrAddrRetLandlineNo.equals(other.pnsnrAddrRetLandlineNo)) {
			return false;
		}
		if (pnsnrAddrRetEmailAddr == null) {
			if (other.pnsnrAddrRetEmailAddr != null) {
				return false;
			}
		} else if (!pnsnrAddrRetEmailAddr.equals(other.pnsnrAddrRetEmailAddr)) {
			return false;
		}
		if (birthDate == null) {
			if (other.birthDate != null) {
				return false;
			}
		} else if (!birthDate.equals(other.birthDate)) {
			return false;
		}
		if (changeTrsryFlag == null) {
			if (other.changeTrsryFlag != null) {
				return false;
			}
		} else if (!changeTrsryFlag.equals(other.changeTrsryFlag)) {
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
		if (deathDate == null) {
			if (other.deathDate != null) {
				return false;
			}
		} else if (!deathDate.equals(other.deathDate)) {
			return false;
		}
		if (demandedRetirementDate == null) {
			if (other.demandedRetirementDate != null) {
				return false;
			}
		} else if (!demandedRetirementDate.equals(other.demandedRetirementDate)) {
			return false;
		}
		if (departmentId == null) {
			if (other.departmentId != null) {
				return false;
			}
		} else if (!departmentId.equals(other.departmentId)) {
			return false;
		}
		if (designation == null) {
			if (other.designation != null) {
				return false;
			}
		} else if (!designation.equals(other.designation)) {
			return false;
		}
		if (firDate == null) {
			if (other.firDate != null) {
				return false;
			}
		} else if (!firDate.equals(other.firDate)) {
			return false;
		}
		if (genderFlag == null) {
			if (other.genderFlag != null) {
				return false;
			}
		} else if (!genderFlag.equals(other.genderFlag)) {
			return false;
		}
		if (groupLookupId == null) {
			if (other.groupLookupId != null) {
				return false;
			}
		} else if (!groupLookupId.equals(other.groupLookupId)) {
			return false;
		}
		if (heightFeet == null) {
			if (other.heightFeet != null) {
				return false;
			}
		} else if (!heightFeet.equals(other.heightFeet)) {
			return false;
		}
		if (heightInches == null) {
			if (other.heightInches != null) {
				return false;
			}
		} else if (!heightInches.equals(other.heightInches)) {
			return false;
		}
		if (hooId == null) {
			if (other.hooId != null) {
				return false;
			}
		} else if (!hooId.equals(other.hooId)) {
			return false;
		}
		if (inwardPensionId == null) {
			if (other.inwardPensionId != null) {
				return false;
			}
		} else if (!inwardPensionId.equals(other.inwardPensionId)) {
			return false;
		}
		if (joiningDate == null) {
			if (other.joiningDate != null) {
				return false;
			}
		} else if (!joiningDate.equals(other.joiningDate)) {
			return false;
		}
		if (locationCode == null) {
			if (other.locationCode != null) {
				return false;
			}
		} else if (!locationCode.equals(other.locationCode)) {
			return false;
		}
		if (lostDate == null) {
			if (other.lostDate != null) {
				return false;
			}
		} else if (!lostDate.equals(other.lostDate)) {
			return false;
		}
		if (officeArea == null) {
			if (other.officeArea != null) {
				return false;
			}
		} else if (!officeArea.equals(other.officeArea)) {
			return false;
		}
		if (officeDistCode == null) {
			if (other.officeDistCode != null) {
				return false;
			}
		} else if (!officeDistCode.equals(other.officeDistCode)) {
			return false;
		}
		if (officeEmailAddr == null) {
			if (other.officeEmailAddr != null) {
				return false;
			}
		} else if (!officeEmailAddr.equals(other.officeEmailAddr)) {
			return false;
		}
		if (officeFlat == null) {
			if (other.officeFlat != null) {
				return false;
			}
		} else if (!officeFlat.equals(other.officeFlat)) {
			return false;
		}
		if (officeLandLineNo == null) {
			if (other.officeLandLineNo != null) {
				return false;
			}
		} else if (!officeLandLineNo.equals(other.officeLandLineNo)) {
			return false;
		}
		if (officeMobileNo == null) {
			if (other.officeMobileNo != null) {
				return false;
			}
		} else if (!officeMobileNo.equals(other.officeMobileNo)) {
			return false;
		}
		if (officePincode == null) {
			if (other.officePincode != null) {
				return false;
			}
		} else if (!officePincode.equals(other.officePincode)) {
			return false;
		}
		if (officeRoad == null) {
			if (other.officeRoad != null) {
				return false;
			}
		} else if (!officeRoad.equals(other.officeRoad)) {
			return false;
		}
		if (officeStateCode == null) {
			if (other.officeStateCode != null) {
				return false;
			}
		} else if (!officeStateCode.equals(other.officeStateCode)) {
			return false;
		}
		if (pensionerDtlId == null) {
			if (other.pensionerDtlId != null) {
				return false;
			}
		} else if (!pensionerDtlId.equals(other.pensionerDtlId)) {
			return false;
		}
		if (photoAttachmentId == null) {
			if (other.photoAttachmentId != null) {
				return false;
			}
		} else if (!photoAttachmentId.equals(other.photoAttachmentId)) {
			return false;
		}
		if (pnsnrAddrArea == null) {
			if (other.pnsnrAddrArea != null) {
				return false;
			}
		} else if (!pnsnrAddrArea.equals(other.pnsnrAddrArea)) {
			return false;
		}
		if (pnsnrAddrDistCode == null) {
			if (other.pnsnrAddrDistCode != null) {
				return false;
			}
		} else if (!pnsnrAddrDistCode.equals(other.pnsnrAddrDistCode)) {
			return false;
		}
		if (pnsnrAddrEmailAddr == null) {
			if (other.pnsnrAddrEmailAddr != null) {
				return false;
			}
		} else if (!pnsnrAddrEmailAddr.equals(other.pnsnrAddrEmailAddr)) {
			return false;
		}
		if (pnsnrAddrFlat == null) {
			if (other.pnsnrAddrFlat != null) {
				return false;
			}
		} else if (!pnsnrAddrFlat.equals(other.pnsnrAddrFlat)) {
			return false;
		}
		if (pnsnrAddrLandlineNo == null) {
			if (other.pnsnrAddrLandlineNo != null) {
				return false;
			}
		} else if (!pnsnrAddrLandlineNo.equals(other.pnsnrAddrLandlineNo)) {
			return false;
		}
		if (pnsnrAddrMobileNo == null) {
			if (other.pnsnrAddrMobileNo != null) {
				return false;
			}
		} else if (!pnsnrAddrMobileNo.equals(other.pnsnrAddrMobileNo)) {
			return false;
		}
		if (pnsnrAddrPincode == null) {
			if (other.pnsnrAddrPincode != null) {
				return false;
			}
		} else if (!pnsnrAddrPincode.equals(other.pnsnrAddrPincode)) {
			return false;
		}
		if (pnsnrAddrRetArea == null) {
			if (other.pnsnrAddrRetArea != null) {
				return false;
			}
		} else if (!pnsnrAddrRetArea.equals(other.pnsnrAddrRetArea)) {
			return false;
		}
		if (pnsnrAddrRetDistCode == null) {
			if (other.pnsnrAddrRetDistCode != null) {
				return false;
			}
		} else if (!pnsnrAddrRetDistCode.equals(other.pnsnrAddrRetDistCode)) {
			return false;
		}
		if (pnsnrAddrRetFlag == null) {
			if (other.pnsnrAddrRetFlag != null) {
				return false;
			}
		} else if (!pnsnrAddrRetFlag.equals(other.pnsnrAddrRetFlag)) {
			return false;
		}
		if (pnsnrAddrRetFlat == null) {
			if (other.pnsnrAddrRetFlat != null) {
				return false;
			}
		} else if (!pnsnrAddrRetFlat.equals(other.pnsnrAddrRetFlat)) {
			return false;
		}
		if (pnsnrAddrRetPincode == null) {
			if (other.pnsnrAddrRetPincode != null) {
				return false;
			}
		} else if (!pnsnrAddrRetPincode.equals(other.pnsnrAddrRetPincode)) {
			return false;
		}
		if (pnsnrAddrRetRoad == null) {
			if (other.pnsnrAddrRetRoad != null) {
				return false;
			}
		} else if (!pnsnrAddrRetRoad.equals(other.pnsnrAddrRetRoad)) {
			return false;
		}
		if (pnsnrAddrRetStateCode == null) {
			if (other.pnsnrAddrRetStateCode != null) {
				return false;
			}
		} else if (!pnsnrAddrRetStateCode.equals(other.pnsnrAddrRetStateCode)) {
			return false;
		}
		if (pnsnrAddrRoad == null) {
			if (other.pnsnrAddrRoad != null) {
				return false;
			}
		} else if (!pnsnrAddrRoad.equals(other.pnsnrAddrRoad)) {
			return false;
		}
		if (pnsnrAddrStateCode == null) {
			if (other.pnsnrAddrStateCode != null) {
				return false;
			}
		} else if (!pnsnrAddrStateCode.equals(other.pnsnrAddrStateCode)) {
			return false;
		}
		if (pnsnrName == null) {
			if (other.pnsnrName != null) {
				return false;
			}
		} else if (!pnsnrName.equals(other.pnsnrName)) {
			return false;
		}
		if (pnsnrNameInMarathi == null) {
			if (other.pnsnrNameInMarathi != null) {
				return false;
			}
		} else if (!pnsnrNameInMarathi.equals(other.pnsnrNameInMarathi)) {
			return false;
		}
		if (remarks == null) {
			if (other.remarks != null) {
				return false;
			}
		} else if (!remarks.equals(other.remarks)) {
			return false;
		}
		if (retirementDate == null) {
			if (other.retirementDate != null) {
				return false;
			}
		} else if (!retirementDate.equals(other.retirementDate)) {
			return false;
		}
		if (revisedReason == null) {
			if (other.revisedReason != null) {
				return false;
			}
		} else if (!revisedReason.equals(other.revisedReason)) {
			return false;
		}
		if (signatureAttachmentId == null) {
			if (other.signatureAttachmentId != null) {
				return false;
			}
		} else if (!signatureAttachmentId.equals(other.signatureAttachmentId)) {
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

	public Long getPensionerDtlId() {
		return pensionerDtlId;
	}

	public void setPensionerDtlId(Long pensionerDtlId) {
		this.pensionerDtlId = pensionerDtlId;
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

	public String getPnsnrName() {
		return pnsnrName;
	}

	public void setPnsnrName(String pnsnrName) {
		this.pnsnrName = pnsnrName;
	}

	public String getPnsnrNameInMarathi() {
		return pnsnrNameInMarathi;
	}

	public void setPnsnrNameInMarathi(String pnsnrNameInMarathi) {
		this.pnsnrNameInMarathi = pnsnrNameInMarathi;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}

	public Date getRetirementDate() {
		return retirementDate;
	}

	public void setRetirementDate(Date retirementDate) {
		this.retirementDate = retirementDate;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public Date getLostDate() {
		return lostDate;
	}

	public void setLostDate(Date lostDate) {
		this.lostDate = lostDate;
	}

	public Date getDemandedRetirementDate() {
		return demandedRetirementDate;
	}

	public void setDemandedRetirementDate(Date demandedRetirementDate) {
		this.demandedRetirementDate = demandedRetirementDate;
	}

	public Date getFirDate() {
		return firDate;
	}

	public void setFirDate(Date firDate) {
		this.firDate = firDate;
	}

	public Character getGenderFlag() {
		return genderFlag;
	}

	public void setGenderFlag(Character genderFlag) {
		this.genderFlag = genderFlag;
	}

	public Long getGroupLookupId() {
		return groupLookupId;
	}

	public void setGroupLookupId(Long groupLookupId) {
		this.groupLookupId = groupLookupId;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Integer getHeightFeet() {
		return heightFeet;
	}

	public void setHeightFeet(Integer heightFeet) {
		this.heightFeet = heightFeet;
	}

	public Integer getHeightInches() {
		return heightInches;
	}

	public void setHeightInches(Integer heightInches) {
		this.heightInches = heightInches;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPnsnrAddrFlat() {
		return pnsnrAddrFlat;
	}

	public void setPnsnrAddrFlat(String pnsnrAddrFlat) {
		this.pnsnrAddrFlat = pnsnrAddrFlat;
	}

	public String getPnsnrAddrRoad() {
		return pnsnrAddrRoad;
	}

	public void setPnsnrAddrRoad(String pnsnrAddrRoad) {
		this.pnsnrAddrRoad = pnsnrAddrRoad;
	}

	public String getPnsnrAddrArea() {
		return pnsnrAddrArea;
	}

	public void setPnsnrAddrArea(String pnsnrAddrArea) {
		this.pnsnrAddrArea = pnsnrAddrArea;
	}

	public String getPnsnrAddrDistCode() {
		return pnsnrAddrDistCode;
	}

	public void setPnsnrAddrDistCode(String pnsnrAddrDistCode) {
		this.pnsnrAddrDistCode = pnsnrAddrDistCode;
	}

	public Long getPnsnrAddrStateCode() {
		return pnsnrAddrStateCode;
	}

	public void setPnsnrAddrStateCode(Long pnsnrAddrStateCode) {
		this.pnsnrAddrStateCode = pnsnrAddrStateCode;
	}

	public Long getPnsnrAddrPincode() {
		return pnsnrAddrPincode;
	}

	public void setPnsnrAddrPincode(Long pnsnrAddrPincode) {
		this.pnsnrAddrPincode = pnsnrAddrPincode;
	}

	public String getPnsnrAddrMobileNo() {
		return pnsnrAddrMobileNo;
	}

	public void setPnsnrAddrMobileNo(String pnsnrAddrMobileNo) {
		this.pnsnrAddrMobileNo = pnsnrAddrMobileNo;
	}

	public String getPnsnrAddrLandlineNo() {
		return pnsnrAddrLandlineNo;
	}

	public void setPnsnrAddrLandlineNo(String pnsnrAddrLandlineNo) {
		this.pnsnrAddrLandlineNo = pnsnrAddrLandlineNo;
	}

	public String getPnsnrAddrEmailAddr() {
		return pnsnrAddrEmailAddr;
	}

	public void setPnsnrAddrEmailAddr(String pnsnrAddrEmailAddr) {
		this.pnsnrAddrEmailAddr = pnsnrAddrEmailAddr;
	}

	public Character getPnsnrAddrRetFlag() {
		return pnsnrAddrRetFlag;
	}

	public void setPnsnrAddrRetFlag(Character pnsnrAddrRetFlag) {
		this.pnsnrAddrRetFlag = pnsnrAddrRetFlag;
	}

	public String getPnsnrAddrRetFlat() {
		return pnsnrAddrRetFlat;
	}

	public void setPnsnrAddrRetFlat(String pnsnrAddrRetFlat) {
		this.pnsnrAddrRetFlat = pnsnrAddrRetFlat;
	}

	public String getPnsnrAddrRetRoad() {
		return pnsnrAddrRetRoad;
	}

	public void setPnsnrAddrRetRoad(String pnsnrAddrRetRoad) {
		this.pnsnrAddrRetRoad = pnsnrAddrRetRoad;
	}

	public String getPnsnrAddrRetArea() {
		return pnsnrAddrRetArea;
	}

	public void setPnsnrAddrRetArea(String pnsnrAddrRetArea) {
		this.pnsnrAddrRetArea = pnsnrAddrRetArea;
	}

	public String getPnsnrAddrRetDistCode() {
		return pnsnrAddrRetDistCode;
	}

	public void setPnsnrAddrRetDistCode(String pnsnrAddrRetDistCode) {
		this.pnsnrAddrRetDistCode = pnsnrAddrRetDistCode;
	}

	public Long getPnsnrAddrRetStateCode() {
		return pnsnrAddrRetStateCode;
	}

	public void setPnsnrAddrRetStateCode(Long pnsnrAddrRetStateCode) {
		this.pnsnrAddrRetStateCode = pnsnrAddrRetStateCode;
	}

	public Long getPnsnrAddrRetPincode() {
		return pnsnrAddrRetPincode;
	}

	public void setPnsnrAddrRetPincode(Long pnsnrAddrRetPincode) {
		this.pnsnrAddrRetPincode = pnsnrAddrRetPincode;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getHooId() {
		return hooId;
	}

	public void setHooId(Long hooId) {
		this.hooId = hooId;
	}

	public Character getChangeTrsryFlag() {
		return changeTrsryFlag;
	}

	public void setChangeTrsryFlag(Character changeTrsryFlag) {
		this.changeTrsryFlag = changeTrsryFlag;
	}

	public Long getRevisedReason() {
		return revisedReason;
	}

	public void setRevisedReason(Long revisedReason) {
		this.revisedReason = revisedReason;
	}

	public String getOfficeFlat() {
		return officeFlat;
	}

	public void setOfficeFlat(String officeFlat) {
		this.officeFlat = officeFlat;
	}

	public String getOfficeRoad() {
		return officeRoad;
	}

	public void setOfficeRoad(String officeRoad) {
		this.officeRoad = officeRoad;
	}

	public String getOfficeArea() {
		return officeArea;
	}

	public void setOfficeArea(String officeArea) {
		this.officeArea = officeArea;
	}

	public String getOfficeDistCode() {
		return officeDistCode;
	}

	public void setOfficeDistCode(String officeDistCode) {
		this.officeDistCode = officeDistCode;
	}

	public Long getOfficeStateCode() {
		return officeStateCode;
	}

	public void setOfficeStateCode(Long officeStateCode) {
		this.officeStateCode = officeStateCode;
	}

	public Long getOfficePincode() {
		return officePincode;
	}

	public void setOfficePincode(Long officePincode) {
		this.officePincode = officePincode;
	}

	public String getOfficeMobileNo() {
		return officeMobileNo;
	}

	public void setOfficeMobileNo(String officeMobileNo) {
		this.officeMobileNo = officeMobileNo;
	}

	public String getOfficeLandLineNo() {
		return officeLandLineNo;
	}

	public void setOfficeLandLineNo(String officeLandLineNo) {
		this.officeLandLineNo = officeLandLineNo;
	}

	public String getOfficeEmailAddr() {
		return officeEmailAddr;
	}

	public void setOfficeEmailAddr(String officeEmailAddr) {
		this.officeEmailAddr = officeEmailAddr;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getBankIfscCode() {
		return bankIfscCode;
	}

	public void setBankIfscCode(String bankIfscCode) {
		this.bankIfscCode = bankIfscCode;
	}

	public Long getPhotoAttachmentId() {
		return photoAttachmentId;
	}

	public void setPhotoAttachmentId(Long photoAttachmentId) {
		this.photoAttachmentId = photoAttachmentId;
	}

	public Long getSignatureAttachmentId() {
		return signatureAttachmentId;
	}

	public void setSignatureAttachmentId(Long signatureAttachmentId) {
		this.signatureAttachmentId = signatureAttachmentId;
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

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String geteId() {

		return eId;
	}

	public void seteId(String eId) {

		this.eId = eId;
	}

	public String getPnsnrAddrRetMobileNo() {

		return pnsnrAddrRetMobileNo;
	}

	public void setPnsnrAddrRetMobileNo(String pnsnrAddrRetMobileNo) {

		this.pnsnrAddrRetMobileNo = pnsnrAddrRetMobileNo;
	}

	public String getPnsnrAddrRetLandlineNo() {

		return pnsnrAddrRetLandlineNo;
	}

	public void setPnsnrAddrRetLandlineNo(String pnsnrAddrRetLandlineNo) {

		this.pnsnrAddrRetLandlineNo = pnsnrAddrRetLandlineNo;
	}

	public String getPnsnrAddrRetEmailAddr() {

		return pnsnrAddrRetEmailAddr;
	}

	public void setPnsnrAddrRetEmailAddr(String pnsnrAddrRetEmailAddr) {

		this.pnsnrAddrRetEmailAddr = pnsnrAddrRetEmailAddr;
	}

	public String getTrsryPnsnComt() {
		return trsryPnsnComt;
	}

	public void setTrsryPnsnComt(String trsryPnsnComt) {
		this.trsryPnsnComt = trsryPnsnComt;
	}

	public String getBnOrAn() {
		return bnOrAn;
	}

	public void setBnOrAn(String bnOrAn) {
		this.bnOrAn = bnOrAn;
	}

	public void setEfpYear(Character efpYear) {
		this.efpYear = efpYear;
	}

	public Character getEfpYear() {
		return efpYear;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setDdoOfficeName(String ddoOfficeName) {
		this.ddoOfficeName = ddoOfficeName;
	}

	public String getDdoOfficeName() {
		return ddoOfficeName;
	}

}
