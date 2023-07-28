/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 22, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 22, 2011
 */
public class MstDcpsDesignation {

	private Long desigId;
	private Long orgDesignationId;
	private Long dbId;
	private Long locationCode;
	private Long langId;
	private Long fieldDeptId;
	private Long desigCode;
	private String desigDesc;
	private Long cadreTypeId;
	private Long payComsnId;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public MstDcpsDesignation() {

	}

	public Long getDesigId() {

		return desigId;
	}

	public void setDesigId(Long desigId) {

		this.desigId = desigId;
	}

	/**
	 * @return the orgDesignationId
	 */
	public Long getOrgDesignationId() {

		return orgDesignationId;
	}

	/**
	 * @param orgDesignationId
	 *            the orgDesignationId to set
	 */
	public void setOrgDesignationId(Long orgDesignationId) {

		this.orgDesignationId = orgDesignationId;
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

	public Long getLangId() {

		return langId;
	}

	public void setLangId(Long langId) {

		this.langId = langId;
	}

	public Long getFieldDeptId() {

		return fieldDeptId;
	}

	public void setFieldDeptId(Long fieldDeptId) {

		this.fieldDeptId = fieldDeptId;
	}

	public Long getDesigCode() {

		return desigCode;
	}

	public void setDesigCode(Long desigCode) {

		this.desigCode = desigCode;
	}

	public String getDesigDesc() {

		return desigDesc;
	}

	public void setDesigDesc(String desigDesc) {

		this.desigDesc = desigDesc;
	}

	public Long getCadreTypeId() {

		return cadreTypeId;
	}

	public void setCadreTypeId(Long cadreTypeId) {

		this.cadreTypeId = cadreTypeId;
	}

	public Long getPayComsnId() {

		return payComsnId;
	}

	public void setPayComsnId(Long payComsnId) {

		this.payComsnId = payComsnId;
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
		result = prime * result + ((cadreTypeId == null) ? 0 : cadreTypeId.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result + ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((desigCode == null) ? 0 : desigCode.hashCode());
		result = prime * result + ((desigDesc == null) ? 0 : desigDesc.hashCode());
		result = prime * result + ((desigId == null) ? 0 : desigId.hashCode());
		result = prime * result + ((fieldDeptId == null) ? 0 : fieldDeptId.hashCode());
		result = prime * result + ((langId == null) ? 0 : langId.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((payComsnId == null) ? 0 : payComsnId.hashCode());
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
		MstDcpsDesignation other = (MstDcpsDesignation) obj;
		if (cadreTypeId == null) {
			if (other.cadreTypeId != null) {
				return false;
			}
		} else if (!cadreTypeId.equals(other.cadreTypeId)) {
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
		if (desigCode == null) {
			if (other.desigCode != null) {
				return false;
			}
		} else if (!desigCode.equals(other.desigCode)) {
			return false;
		}
		if (desigDesc == null) {
			if (other.desigDesc != null) {
				return false;
			}
		} else if (!desigDesc.equals(other.desigDesc)) {
			return false;
		}
		if (desigId == null) {
			if (other.desigId != null) {
				return false;
			}
		} else if (!desigId.equals(other.desigId)) {
			return false;
		}
		if (fieldDeptId == null) {
			if (other.fieldDeptId != null) {
				return false;
			}
		} else if (!fieldDeptId.equals(other.fieldDeptId)) {
			return false;
		}
		if (langId == null) {
			if (other.langId != null) {
				return false;
			}
		} else if (!langId.equals(other.langId)) {
			return false;
		}
		if (locationCode == null) {
			if (other.locationCode != null) {
				return false;
			}
		} else if (!locationCode.equals(other.locationCode)) {
			return false;
		}
		if (payComsnId == null) {
			if (other.payComsnId != null) {
				return false;
			}
		} else if (!payComsnId.equals(other.payComsnId)) {
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

}
