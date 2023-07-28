package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class MstDcpsContributionMonthly implements java.io.Serializable {
	
	private Long dcpsContributionMonthlyId;
	private String dcpsId;
	private Long yearId;
	private Long curTreasuryCD;
	private String curDdoCD;

	private Long vMonth;
	private Long vYear;
	private Long payMonth;
	private Long payYear;
	private String vorcNo;
	private Date vorcDate;
	
	private Double contribEmp;
	private Double contribEmplr;
	private Double contribTier2;
	private Double intContribEmp;
	private Double intContribEmplr;
	private Double intContribTier2;
	
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	private String typeOfPayment;
	
	private Character isMissingCredit;
	private Character isChallan;
	
	private Double closeEmpPrv;
	private Double closeEmplrPrv;
	private Double closeTier2Prv;
	
	public Double getCloseEmpPrv() {
		return closeEmpPrv;
	}
	public void setCloseEmpPrv(Double closeEmpPrv) {
		this.closeEmpPrv = closeEmpPrv;
	}
	public Double getCloseEmplrPrv() {
		return closeEmplrPrv;
	}
	public void setCloseEmplrPrv(Double closeEmplrPrv) {
		this.closeEmplrPrv = closeEmplrPrv;
	}
	public Double getCloseTier2Prv() {
		return closeTier2Prv;
	}
	public void setCloseTier2Prv(Double closeTier2Prv) {
		this.closeTier2Prv = closeTier2Prv;
	}
	public Long getDcpsContributionMonthlyId() {
		return dcpsContributionMonthlyId;
	}
	public void setDcpsContributionMonthlyId(Long dcpsContributionMonthlyId) {
		this.dcpsContributionMonthlyId = dcpsContributionMonthlyId;
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
	public Long getvMonth() {
		return vMonth;
	}
	public void setvMonth(Long vMonth) {
		this.vMonth = vMonth;
	}
	public Long getvYear() {
		return vYear;
	}
	public void setvYear(Long vYear) {
		this.vYear = vYear;
	}
	public Long getPayMonth() {
		return payMonth;
	}
	public void setPayMonth(Long payMonth) {
		this.payMonth = payMonth;
	}
	public Long getPayYear() {
		return payYear;
	}
	public void setPayYear(Long payYear) {
		this.payYear = payYear;
	}
	public String getVorcNo() {
		return vorcNo;
	}
	public void setVorcNo(String vorcNo) {
		this.vorcNo = vorcNo;
	}
	public Date getVorcDate() {
		return vorcDate;
	}
	public void setVorcDate(Date vorcDate) {
		this.vorcDate = vorcDate;
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
	
	public String getTypeOfPayment() {
		return typeOfPayment;
	}
	public void setTypeOfPayment(String typeOfPayment) {
		this.typeOfPayment = typeOfPayment;
	}
	
	public Character getIsMissingCredit() {
		return isMissingCredit;
	}
	public void setIsMissingCredit(Character isMissingCredit) {
		this.isMissingCredit = isMissingCredit;
	}
	
	public Character getIsChallan() {
		return isChallan;
	}
	public void setIsChallan(Character isChallan) {
		this.isChallan = isChallan;
	}
	
	
}
