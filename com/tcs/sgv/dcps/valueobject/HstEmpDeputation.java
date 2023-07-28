/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 24, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 24, 2011
 */
public class HstEmpDeputation implements Serializable {

	private Long hstdcpsEmpDeptnId;
	private Long dbId;
	private Long locId;
	private String officeCode;
	private MstEmp dcpsEmpId;
	private Date attachDate;
	private Date detachDate;
	private String remarks;
	private String reason;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	private String remarksDetach;
	private String reasonDetach;
	


	public HstEmpDeputation() {
	}

	public HstEmpDeputation(Long hstdcpsEmpDeptnId, Long dbId, Long locId,
			String officeCode, MstEmp dcpsEmpId, Date attachDate,
			Date detachDate, String remarks, String reason, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate,String remarksDetach,String reasonDetach) {
		super();
		this.hstdcpsEmpDeptnId = hstdcpsEmpDeptnId;
		this.dbId = dbId;
		this.locId = locId;
		this.officeCode = officeCode;
		this.remarks = remarks;
		this.reason = reason;
		this.dcpsEmpId = dcpsEmpId;
		this.attachDate = attachDate;
		this.detachDate = detachDate;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.remarksDetach = remarksDetach;
		this.reasonDetach = reasonDetach;
	}

	public Long getHstdcpsEmpDeptnId() {
		return hstdcpsEmpDeptnId;
	}

	public void setHstdcpsEmpDeptnId(Long hstdcpsEmpDeptnId) {
		this.hstdcpsEmpDeptnId = hstdcpsEmpDeptnId;
	}

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public Long getLocId() {
		return locId;
	}

	public void setLocId(Long locId) {
		this.locId = locId;
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public MstEmp getDcpsEmpId() {
		return dcpsEmpId;
	}

	public void setDcpsEmpId(MstEmp dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}

	public Date getAttachDate() {
		return attachDate;
	}

	public void setAttachDate(Date attachDate) {
		this.attachDate = attachDate;
	}

	public Date getDetachDate() {
		return detachDate;
	}

	public void setDetachDate(Date detachDate) {
		this.detachDate = detachDate;
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
	
	public String getRemarksDetach() {
		return remarksDetach;
	}

	public void setRemarksDetach(String remarksDetach) {
		this.remarksDetach = remarksDetach;
	}

	public String getReasonDetach() {
		return reasonDetach;
	}

	public void setReasonDetach(String reasonDetach) {
		this.reasonDetach = reasonDetach;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attachDate == null) ? 0 : attachDate.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result
				+ ((dcpsEmpId == null) ? 0 : dcpsEmpId.hashCode());
		result = prime * result
				+ ((detachDate == null) ? 0 : detachDate.hashCode());
		result = prime
				* result
				+ ((hstdcpsEmpDeptnId == null) ? 0 : hstdcpsEmpDeptnId
						.hashCode());
		result = prime * result + ((locId == null) ? 0 : locId.hashCode());
		result = prime * result
				+ ((officeCode == null) ? 0 : officeCode.hashCode());
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HstEmpDeputation other = (HstEmpDeputation) obj;
		if (attachDate == null) {
			if (other.attachDate != null) {
				return false;
			}
		} else if (!attachDate.equals(other.attachDate)) {
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
		if (dcpsEmpId == null) {
			if (other.dcpsEmpId != null) {
				return false;
			}
		} else if (!dcpsEmpId.equals(other.dcpsEmpId)) {
			return false;
		}
		if (detachDate == null) {
			if (other.detachDate != null) {
				return false;
			}
		} else if (!detachDate.equals(other.detachDate)) {
			return false;
		}
		if (hstdcpsEmpDeptnId == null) {
			if (other.hstdcpsEmpDeptnId != null) {
				return false;
			}
		} else if (!hstdcpsEmpDeptnId.equals(other.hstdcpsEmpDeptnId)) {
			return false;
		}
		if (locId == null) {
			if (other.locId != null) {
				return false;
			}
		} else if (!locId.equals(other.locId)) {
			return false;
		}
		if (officeCode == null) {
			if (other.officeCode != null) {
				return false;
			}
		} else if (!officeCode.equals(other.officeCode)) {
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

	@Override
	public String toString() {
		return "HstEmpDeputation [hstdcpsEmpDeptnId=" + hstdcpsEmpDeptnId
				+ ", dbId=" + dbId + ", locId=" + locId + ", officeCode="
				+ officeCode + ", dcpsEmpId=" + dcpsEmpId + ", attachDate="
				+ attachDate + ", detachDate=" + detachDate + ", remarks="
				+ remarks + ", reason=" + reason + ", createdUserId="
				+ createdUserId + ", createdPostId=" + createdPostId
				+ ", createdDate=" + createdDate + ", updatedUserId="
				+ updatedUserId + ", updatedPostId=" + updatedPostId
				+ ", updatedDate=" + updatedDate + ", remarksDetach="
				+ remarksDetach + ", reasonDetach=" + reasonDetach + "]";
	}
	
	

}
