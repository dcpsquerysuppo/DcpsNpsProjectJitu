/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Vihan Khatri								
 *******************************************************************************
 */
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 25, 2011
 */
/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Vihan Khatri								
 *******************************************************************************
 */

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 25, 2011
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class HstDcpsOtherChanges implements java.io.Serializable, Cloneable {

	private Long dcpsOtherChangesId;
	private Long dcpsChangesId;
	private Long dcpsEmpId;
	private String bankName;
	private String branchName;
	private String bankAccountNo;
	private String IFSCCode;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	private Character dcpsOrGpf;
	private String acDcpsMaintainedBy;
	private String acNonSRKAEmp;
	private String acMntndByOthers;
	private String acMaintainedBy;
	private String pfSeries;
	private String pfSeriesDesc;
	private String pfAcNo;
	
	public Long getDcpsOtherChangesId() {
		return dcpsOtherChangesId;
	}

	public void setDcpsOtherChangesId(Long dcpsOtherChangesId) {
		this.dcpsOtherChangesId = dcpsOtherChangesId;
	}

	public Long getDcpsChangesId() {
		return dcpsChangesId;
	}

	public void setDcpsChangesId(Long dcpsChangesId) {
		this.dcpsChangesId = dcpsChangesId;
	}

	public Long getDcpsEmpId() {
		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
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

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getIFSCCode() {
		return IFSCCode;
	}

	public void setIFSCCode(String code) {
		IFSCCode = code;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public Long getLocId() {
		return locId;
	}

	public void setLocId(Long locId) {
		this.locId = locId;
	}

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public Long getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Long getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
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
	
	public Character getDcpsOrGpf() {
		
		return dcpsOrGpf;
	}

	public void setDcpsOrGpf(Character dcpsOrGpf) {
		
		this.dcpsOrGpf = dcpsOrGpf;
	}
	
	public String getAcDcpsMaintainedBy() {

		return acDcpsMaintainedBy;
	}

	public void setAcDcpsMaintainedBy(String acDcpsMaintainedBy) {

		this.acDcpsMaintainedBy = acDcpsMaintainedBy;
	}
	
	public String getAcNonSRKAEmp() {

		return acNonSRKAEmp;
	}

	public void setAcNonSRKAEmp(String acNonSRKAEmp) {

		this.acNonSRKAEmp = acNonSRKAEmp;
	}
	
	public String getAcMntndByOthers() {

		return acMntndByOthers;
	}

	public void setAcMntndByOthers(String acMntndByOthers) {

		this.acMntndByOthers = acMntndByOthers;
	}
	
	public String getAcMaintainedBy() {
		return acMaintainedBy;
	}

	public void setAcMaintainedBy(String acMaintainedBy) {
		this.acMaintainedBy = acMaintainedBy;
	}
	
	public String getPfSeries() {
		return pfSeries;
	}

	public void setPfSeries(String pfSeries) {
		this.pfSeries = pfSeries;
	}

	public String getPfSeriesDesc() {
		return pfSeriesDesc;
	}

	public void setPfSeriesDesc(String pfSeriesDesc) {
		this.pfSeriesDesc = pfSeriesDesc;
	}
	
	public String getPfAcNo() {
		return pfAcNo;
	}

	public void setPfAcNo(String pfAcNo) {
		this.pfAcNo = pfAcNo;
	}

	public Object clone() {

		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}
}