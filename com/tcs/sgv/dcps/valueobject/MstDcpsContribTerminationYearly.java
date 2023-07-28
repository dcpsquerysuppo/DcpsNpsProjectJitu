/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 16, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Jun 16, 2011
 */
/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Vihan Khatri								
 *******************************************************************************
 */

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 19, 2011
 */
import java.util.Date;

public class MstDcpsContribTerminationYearly implements java.io.Serializable {

	private Long dcpsContributionYearlyId;
	private String dcpsId;
	private Long yearId;
	private Long curTreasuryCD;
	private String curDdoCD;
	private Double openNet;
	private Double openEmp;
	private Double openEmplr;
	private Double openTier2;
	private Double contribEmp;
	private Double contribEmplr;
	private Double contribTier2;
	private Double intContribEmp;
	private Double intContribEmplr;
	private Double intContribTier2;
	private Double openInt;
	private Double closeEmp;
	private Double closeEmplr;
	private Double closeTier2;
	private Double closeNet;

	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	private Double contribTotalMissingCredit;
	private Double closeMissingCredit;
	private Double openIntEmp;
	private Double openIntEmplr;
	private Double openIntEmpNew;
	private Double openIntEmplrNew;
	
	public Double getOpenIntEmpNew() {
		return openIntEmpNew;
	}

	public void setOpenIntEmpNew(Double openIntEmpNew) {
		this.openIntEmpNew = openIntEmpNew;
	}

	public Double getOpenIntEmplrNew() {
		return openIntEmplrNew;
	}

	public void setOpenIntEmplrNew(Double openIntEmplrNew) {
		this.openIntEmplrNew = openIntEmplrNew;
	}

	public Double getOpenIntEmp() {
		return openIntEmp;
	}

	public void setOpenIntEmp(Double openIntEmp) {
		this.openIntEmp = openIntEmp;
	}

	public Double getOpenIntEmplr() {
		return openIntEmplr;
	}

	public void setOpenIntEmplr(Double openIntEmplr) {
		this.openIntEmplr = openIntEmplr;
	}

	public Long getDcpsContributionYearlyId() {
		return dcpsContributionYearlyId;
	}

	public void setDcpsContributionYearlyId(Long dcpsContributionYearlyId) {
		this.dcpsContributionYearlyId = dcpsContributionYearlyId;
	}

	public String getDcpsId() {
		return dcpsId;
	}

	public void setDcpsId(String dcpsId) {
		this.dcpsId = dcpsId;
	}

	public Long getYearId() {
		return yearId;
	}

	public void setYearId(Long yearId) {
		this.yearId = yearId;
	}

	public Long getCurTreasuryCD() {
		return curTreasuryCD;
	}

	public void setCurTreasuryCD(Long curTreasuryCD) {
		this.curTreasuryCD = curTreasuryCD;
	}

	public String getCurDdoCD() {
		return curDdoCD;
	}

	public void setCurDdoCD(String curDdoCD) {
		this.curDdoCD = curDdoCD;
	}

	public Double getOpenNet() {
		return openNet;
	}

	public void setOpenNet(Double openNet) {
		this.openNet = openNet;
	}

	public Double getOpenEmp() {
		return openEmp;
	}

	public void setOpenEmp(Double openEmp) {
		this.openEmp = openEmp;
	}

	public Double getOpenEmplr() {
		return openEmplr;
	}

	public void setOpenEmplr(Double openEmplr) {
		this.openEmplr = openEmplr;
	}

	public Double getOpenTier2() {
		return openTier2;
	}

	public void setOpenTier2(Double openTier2) {
		this.openTier2 = openTier2;
	}

	public Double getContribEmp() {
		return contribEmp;
	}

	public void setContribEmp(Double contribEmp) {
		this.contribEmp = contribEmp;
	}

	public Double getContribEmplr() {
		return contribEmplr;
	}

	public void setContribEmplr(Double contribEmplr) {
		this.contribEmplr = contribEmplr;
	}

	public Double getContribTier2() {
		return contribTier2;
	}

	public void setContribTier2(Double contribTier2) {
		this.contribTier2 = contribTier2;
	}

	public Double getIntContribEmp() {
		return intContribEmp;
	}

	public void setIntContribEmp(Double intContribEmp) {
		this.intContribEmp = intContribEmp;
	}

	public Double getIntContribEmplr() {
		return intContribEmplr;
	}

	public void setIntContribEmplr(Double intContribEmplr) {
		this.intContribEmplr = intContribEmplr;
	}

	public Double getIntContribTier2() {
		return intContribTier2;
	}

	public void setIntContribTier2(Double intContribTier2) {
		this.intContribTier2 = intContribTier2;
	}

	public Double getOpenInt() {
		return openInt;
	}

	public void setOpenInt(Double openInt) {
		this.openInt = openInt;
	}

	public Double getCloseEmp() {
		return closeEmp;
	}

	public void setCloseEmp(Double closeEmp) {
		this.closeEmp = closeEmp;
	}

	public Double getCloseEmplr() {
		return closeEmplr;
	}

	public void setCloseEmplr(Double closeEmplr) {
		this.closeEmplr = closeEmplr;
	}

	public Double getCloseTier2() {
		return closeTier2;
	}

	public void setCloseTier2(Double closeTier2) {
		this.closeTier2 = closeTier2;
	}

	public Double getCloseNet() {
		return closeNet;
	}

	public void setCloseNet(Double closeNet) {
		this.closeNet = closeNet;
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
	
	public Double getCloseMissingCredit() {
		return closeMissingCredit;
	}

	public void setCloseMissingCredit(Double closeMissingCredit) {
		this.closeMissingCredit = closeMissingCredit;
	}
	
	public Double getContribTotalMissingCredit() {
		return contribTotalMissingCredit;
	}

	public void setContribTotalMissingCredit(Double contribTotalMissingCredit) {
		this.contribTotalMissingCredit = contribTotalMissingCredit;
	}

}
