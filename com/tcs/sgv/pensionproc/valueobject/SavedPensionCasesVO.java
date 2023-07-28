/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 15, 2011		Anjana Suvariya								
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
 * @since JDK 5.0
 * Mar 15, 2011
 */
public class SavedPensionCasesVO implements Serializable {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -4821055760651800322L;

	private Long inwardPensionId;
	private String inwardNo;
	private String sevaarthId;
	private String pnsnrName;
	private String pnsnType;
	private String deptName;
	private Date joiningDate;
	private Date retirementDate;
	private String caseStatus;
	private String outwardNo;
	private Date outwardDate;
	
	public SavedPensionCasesVO() {

	}

	public SavedPensionCasesVO(Long inwardPensionId, String inwardNo, String sevaarthId, String pnsnrName, String pnsnType, String deptName, Date joiningDate, Date retirementDate,
			String caseStatus,String outwardNo,Date outwardDate) {
		super();
		this.inwardPensionId = inwardPensionId;
		this.inwardNo = inwardNo;
		this.sevaarthId = sevaarthId;
		this.pnsnrName = pnsnrName;
		this.pnsnType = pnsnType;
		this.deptName = deptName;
		this.joiningDate = joiningDate;
		this.retirementDate = retirementDate;
		this.caseStatus = caseStatus;
		this.outwardNo = outwardNo;
		this.outwardDate = outwardDate;
	}

	public Long getInwardPensionId() {
		return inwardPensionId;
	}

	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
	}

	public String getInwardNo() {
		return inwardNo;
	}

	public void setInwardNo(String inwardNo) {
		this.inwardNo = inwardNo;
	}

	public String getSevaarthId() {
		return sevaarthId;
	}

	public void setSevaarthId(String sevaarthId) {
		this.sevaarthId = sevaarthId;
	}

	public String getPnsnrName() {
		return pnsnrName;
	}

	public void setPnsnrName(String pnsnrName) {
		this.pnsnrName = pnsnrName;
	}

	public String getPnsnType() {
		return pnsnType;
	}

	public void setPnsnType(String pnsnType) {
		this.pnsnType = pnsnType;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deptName == null) ? 0 : deptName.hashCode());
		result = prime * result + ((inwardNo == null) ? 0 : inwardNo.hashCode());
		result = prime * result + ((inwardPensionId == null) ? 0 : inwardPensionId.hashCode());
		result = prime * result + ((joiningDate == null) ? 0 : joiningDate.hashCode());
		result = prime * result + ((pnsnType == null) ? 0 : pnsnType.hashCode());
		result = prime * result + ((pnsnrName == null) ? 0 : pnsnrName.hashCode());
		result = prime * result + ((retirementDate == null) ? 0 : retirementDate.hashCode());
		result = prime * result + ((sevaarthId == null) ? 0 : sevaarthId.hashCode());
		result = prime * result + ((caseStatus == null) ? 0 : caseStatus.hashCode());
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
		SavedPensionCasesVO other = (SavedPensionCasesVO) obj;
		if (deptName == null) {
			if (other.deptName != null) {
				return false;
			}
		} else if (!deptName.equals(other.deptName)) {
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
		if (joiningDate == null) {
			if (other.joiningDate != null) {
				return false;
			}
		} else if (!joiningDate.equals(other.joiningDate)) {
			return false;
		}
		if (pnsnType == null) {
			if (other.pnsnType != null) {
				return false;
			}
		} else if (!pnsnType.equals(other.pnsnType)) {
			return false;
		}
		if (pnsnrName == null) {
			if (other.pnsnrName != null) {
				return false;
			}
		} else if (!pnsnrName.equals(other.pnsnrName)) {
			return false;
		}
		if (retirementDate == null) {
			if (other.retirementDate != null) {
				return false;
			}
		} else if (!retirementDate.equals(other.retirementDate)) {
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
		return true;
	}

}
