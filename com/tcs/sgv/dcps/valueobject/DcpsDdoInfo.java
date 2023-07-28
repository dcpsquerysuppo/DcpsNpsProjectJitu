/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
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
 * @since JDK 5.0
 * Mar 19, 2011
 */
public class DcpsDdoInfo  implements Serializable
{
	private Long dcpsDdoDtlId;
	private String ddoCode;
	private String adminDept;
	private String FieldDept;
	private String ddoName;
	private String ddoDesig;
	private Date wefDate;
	private String tanNo;
	private String itAwrdCrcle;
	private String bankName;
	private String branchName;
	private String accNo;
	private String remarks;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	public DcpsDdoInfo() 
	{
		
	}

	
	public DcpsDdoInfo(Long dcpsDdoDtlId, String ddoCode, String adminDept,
			String fieldDept, String ddoName, String ddoDesig, Date wefDate,
			String tanNo, String itAwrdCrcle, String bankName,
			String branchName, String accNo, String remarks,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate) {
		super();
		this.dcpsDdoDtlId = dcpsDdoDtlId;
		this.ddoCode = ddoCode;
		this.adminDept = adminDept;
		FieldDept = fieldDept;
		this.ddoName = ddoName;
		this.ddoDesig = ddoDesig;
		this.wefDate = wefDate;
		this.tanNo = tanNo;
		this.itAwrdCrcle = itAwrdCrcle;
		this.bankName = bankName;
		this.branchName = branchName;
		this.accNo = accNo;
		this.remarks = remarks;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}


	public Long getDcpsDdoDtlId() {
		return dcpsDdoDtlId;
	}


	public void setDcpsDdoDtlId(Long dcpsDdoDtlId) {
		this.dcpsDdoDtlId = dcpsDdoDtlId;
	}


	public String getDdoCode() {
		return ddoCode;
	}


	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}


	public String getAdminDept() {
		return adminDept;
	}


	public void setAdminDept(String adminDept) {
		this.adminDept = adminDept;
	}


	public String getFieldDept() {
		return FieldDept;
	}


	public void setFieldDept(String fieldDept) {
		FieldDept = fieldDept;
	}


	public String getDdoName() {
		return ddoName;
	}


	public void setDdoName(String ddoName) {
		this.ddoName = ddoName;
	}


	public String getDdoDesig() {
		return ddoDesig;
	}


	public void setDdoDesig(String ddoDesig) {
		this.ddoDesig = ddoDesig;
	}


	public Date getWefDate() {
		return wefDate;
	}


	public void setWefDate(Date wefDate) {
		this.wefDate = wefDate;
	}


	public String getTanNo() {
		return tanNo;
	}


	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}


	public String getItAwrdCrcle() {
		return itAwrdCrcle;
	}


	public void setItAwrdCrcle(String itAwrdCrcle) {
		this.itAwrdCrcle = itAwrdCrcle;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getBranchName() {
		return branchName;
	}


	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}


	public String getAccNo() {
		return accNo;
	}


	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
		result = prime * result
				+ ((FieldDept == null) ? 0 : FieldDept.hashCode());
		result = prime * result + ((accNo == null) ? 0 : accNo.hashCode());
		result = prime * result
				+ ((adminDept == null) ? 0 : adminDept.hashCode());
		result = prime * result
				+ ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result
				+ ((branchName == null) ? 0 : branchName.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result
				+ ((dcpsDdoDtlId == null) ? 0 : dcpsDdoDtlId.hashCode());
		result = prime * result + ((ddoCode == null) ? 0 : ddoCode.hashCode());
		result = prime * result
				+ ((ddoDesig == null) ? 0 : ddoDesig.hashCode());
		result = prime * result + ((ddoName == null) ? 0 : ddoName.hashCode());
		result = prime * result
				+ ((itAwrdCrcle == null) ? 0 : itAwrdCrcle.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((tanNo == null) ? 0 : tanNo.hashCode());
		result = prime * result
				+ ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result
				+ ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		result = prime * result + ((wefDate == null) ? 0 : wefDate.hashCode());
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
		DcpsDdoInfo other = (DcpsDdoInfo) obj;
		if (FieldDept == null) {
			if (other.FieldDept != null)
				return false;
		} else if (!FieldDept.equals(other.FieldDept))
			return false;
		if (accNo == null) {
			if (other.accNo != null)
				return false;
		} else if (!accNo.equals(other.accNo))
			return false;
		if (adminDept == null) {
			if (other.adminDept != null)
				return false;
		} else if (!adminDept.equals(other.adminDept))
			return false;
		if (bankName == null) {
			if (other.bankName != null)
				return false;
		} else if (!bankName.equals(other.bankName))
			return false;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
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
		if (dcpsDdoDtlId == null) {
			if (other.dcpsDdoDtlId != null)
				return false;
		} else if (!dcpsDdoDtlId.equals(other.dcpsDdoDtlId))
			return false;
		if (ddoCode == null) {
			if (other.ddoCode != null)
				return false;
		} else if (!ddoCode.equals(other.ddoCode))
			return false;
		if (ddoDesig == null) {
			if (other.ddoDesig != null)
				return false;
		} else if (!ddoDesig.equals(other.ddoDesig))
			return false;
		if (ddoName == null) {
			if (other.ddoName != null)
				return false;
		} else if (!ddoName.equals(other.ddoName))
			return false;
		if (itAwrdCrcle == null) {
			if (other.itAwrdCrcle != null)
				return false;
		} else if (!itAwrdCrcle.equals(other.itAwrdCrcle))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (tanNo == null) {
			if (other.tanNo != null)
				return false;
		} else if (!tanNo.equals(other.tanNo))
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
		if (wefDate == null) {
			if (other.wefDate != null)
				return false;
		} else if (!wefDate.equals(other.wefDate))
			return false;
		return true;
	}

	
	
}
