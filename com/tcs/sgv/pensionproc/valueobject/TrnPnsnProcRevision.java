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
public class TrnPnsnProcRevision implements Serializable
{
	private Long revisionId;
	private Long locationCode;
	private Long inwardPensionId;	
	private String sevaarthId;
	private String ppoNo;
	private BigDecimal cvpMonthAmnt;
	private BigDecimal diffCvpMonthAmnt;
	private BigDecimal cvpAmnt;
	private BigDecimal diffCvpAmnt;
	private BigDecimal pensionerReducedAmnt;
	private BigDecimal diffPensionerReducedAmnt;
	private BigDecimal pensionerTotalAmnt;
	private BigDecimal diffPensionerTotalAmnt;
	private BigDecimal srvcGratuityAmnt;
	private BigDecimal diffSrvcGratuityAmnt;
	private BigDecimal dcrgAmnt;
	private BigDecimal diffDcrgAmnt;	
	private BigDecimal fp1Amnt;
	private BigDecimal diffFp1Amnt;	
	private BigDecimal fp2Amnt;
	private BigDecimal diffFp2Amnt;
	private Long revisionNo;
	private String activeFlag;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	
	public Long getRevisionId() {
		return revisionId;
	}
	public void setRevisionId(Long revisionId) {
		this.revisionId = revisionId;
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
	public String getSevaarthId() {
		return sevaarthId;
	}
	public void setSevaarthId(String sevaarthId) {
		this.sevaarthId = sevaarthId;
	}
	public String getPpoNo() {
		return ppoNo;
	}
	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}
	public BigDecimal getCvpMonthAmnt() {
		return cvpMonthAmnt;
	}
	public void setCvpMonthAmnt(BigDecimal cvpMonthAmnt) {
		this.cvpMonthAmnt = cvpMonthAmnt;
	}
	public BigDecimal getDiffCvpMonthAmnt() {
		return diffCvpMonthAmnt;
	}
	public void setDiffCvpMonthAmnt(BigDecimal diffCvpMonthAmnt) {
		this.diffCvpMonthAmnt = diffCvpMonthAmnt;
	}
	public BigDecimal getCvpAmnt() {
		return cvpAmnt;
	}
	public void setCvpAmnt(BigDecimal cvpAmnt) {
		this.cvpAmnt = cvpAmnt;
	}
	public BigDecimal getDiffCvpAmnt() {
		return diffCvpAmnt;
	}
	public void setDiffCvpAmnt(BigDecimal diffCvpAmnt) {
		this.diffCvpAmnt = diffCvpAmnt;
	}
	public BigDecimal getPensionerReducedAmnt() {
		return pensionerReducedAmnt;
	}
	public void setPensionerReducedAmnt(BigDecimal pensionerReducedAmnt) {
		this.pensionerReducedAmnt = pensionerReducedAmnt;
	}
	public BigDecimal getDiffPensionerReducedAmnt() {
		return diffPensionerReducedAmnt;
	}
	public void setDiffPensionerReducedAmnt(BigDecimal diffPensionerReducedAmnt) {
		this.diffPensionerReducedAmnt = diffPensionerReducedAmnt;
	}
	public BigDecimal getPensionerTotalAmnt() {
		return pensionerTotalAmnt;
	}
	public void setPensionerTotalAmnt(BigDecimal pensionerTotalAmnt) {
		this.pensionerTotalAmnt = pensionerTotalAmnt;
	}
	public BigDecimal getDiffPensionerTotalAmnt() {
		return diffPensionerTotalAmnt;
	}
	public void setDiffPensionerTotalAmnt(BigDecimal diffPensionerTotalAmnt) {
		this.diffPensionerTotalAmnt = diffPensionerTotalAmnt;
	}
	public BigDecimal getSrvcGratuityAmnt() {
		return srvcGratuityAmnt;
	}
	public void setSrvcGratuityAmnt(BigDecimal srvcGratuityAmnt) {
		this.srvcGratuityAmnt = srvcGratuityAmnt;
	}
	public BigDecimal getDiffSrvcGratuityAmnt() {
		return diffSrvcGratuityAmnt;
	}
	public void setDiffSrvcGratuityAmnt(BigDecimal diffSrvcGratuityAmnt) {
		this.diffSrvcGratuityAmnt = diffSrvcGratuityAmnt;
	}
	public BigDecimal getDcrgAmnt() {
		return dcrgAmnt;
	}
	public void setDcrgAmnt(BigDecimal dcrgAmnt) {
		this.dcrgAmnt = dcrgAmnt;
	}
	public BigDecimal getDiffDcrgAmnt() {
		return diffDcrgAmnt;
	}
	public void setDiffDcrgAmnt(BigDecimal diffDcrgAmnt) {
		this.diffDcrgAmnt = diffDcrgAmnt;
	}
	public BigDecimal getFp1Amnt() {
		return fp1Amnt;
	}
	public void setFp1Amnt(BigDecimal fp1Amnt) {
		this.fp1Amnt = fp1Amnt;
	}
	public BigDecimal getDiffFp1Amnt() {
		return diffFp1Amnt;
	}
	public void setDiffFp1Amnt(BigDecimal diffFp1Amnt) {
		this.diffFp1Amnt = diffFp1Amnt;
	}
	public BigDecimal getFp2Amnt() {
		return fp2Amnt;
	}
	public void setFp2Amnt(BigDecimal fp2Amnt) {
		this.fp2Amnt = fp2Amnt;
	}
	public BigDecimal getDiffFp2Amnt() {
		return diffFp2Amnt;
	}
	public void setDiffFp2Amnt(BigDecimal diffFp2Amnt) {
		this.diffFp2Amnt = diffFp2Amnt;
	}
	public Long getRevisionNo() {
		return revisionNo;
	}
	public void setRevisionNo(Long revisionNo) {
		this.revisionNo = revisionNo;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	public String getActiveFlag() {
		return activeFlag;
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
