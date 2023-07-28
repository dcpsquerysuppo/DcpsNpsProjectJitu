/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 20, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 May 20, 2011
 */
public class TrnDcpsDeputationContribution {

	private Long dcpsDeptnContriId;
	private String dummyOfficeId;
	private Long dcpsEmpId;
	private Double contribution;
	private Double contributionEmplr;
	private Long finYearId;
	private Long monthId;
	private String challanNo;
	private String challanNoEmplr;
	private Date challanDate;
	private Date challanDateEmplr;
	private Long langId;
	private Long locId;
	private Long dbId;
	//$t 2019
    private Character isArrears;	
    private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String treasury;
	
	private Date startDate;
	private Date endDate;
	private String payCommission;
	private String typeOfPayment;
	private Double basicPay;
	private Double DP;
	private Double DA;
	private String schemeCode;

	public Long getDcpsDeptnContriId() {
		return dcpsDeptnContriId;
	}

	public void setDcpsDeptnContriId(Long dcpsDeptnContriId) {
		this.dcpsDeptnContriId = dcpsDeptnContriId;
	}

	public String getDummyOfficeId() {
		return dummyOfficeId;
	}

	public void setDummyOfficeId(String dummyOfficeId) {
		this.dummyOfficeId = dummyOfficeId;
	}

	public Long getDcpsEmpId() {
		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}
//$t 2019
	public Double getContribution() {
		return contribution;
	}
	public Character getIsArrears() {
		return isArrears;
	}

	public void setIsArrears(Character isArrears) {
		this.isArrears = isArrears;
	}

	public void setContribution(Double contribution) {
		this.contribution = contribution;
	}
	
	public Double getContributionEmplr() {
		return contributionEmplr;
	}

	public void setContributionEmplr(Double contributionEmplr) {
		this.contributionEmplr = contributionEmplr;
	}

	public Long getFinYearId() {
		return finYearId;
	}

	public void setFinYearId(Long finYearId) {
		this.finYearId = finYearId;
	}

	public Long getMonthId() {
		return monthId;
	}

	public void setMonthId(Long monthId) {
		this.monthId = monthId;
	}

	public String getChallanNo() {
		return challanNo;
	}

	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}
	
	public String getChallanNoEmplr() {
		return challanNoEmplr;
	}

	public void setChallanNoEmplr(String challanNoEmplr) {
		this.challanNoEmplr = challanNoEmplr;
	}

	public Date getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(Date challanDate) {
		this.challanDate = challanDate;
	}
	
	public Date getChallanDateEmplr() {
		return challanDateEmplr;
	}

	public void setChallanDateEmplr(Date challanDateEmplr) {
		this.challanDateEmplr = challanDateEmplr;
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
	
	public String getTreasury() {
		return treasury;
	}

	public void setTreasury(String treasury) {
		this.treasury = treasury;
	}
	
	
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getPayCommission() {
		return payCommission;
	}

	public void setPayCommission(String payCommission) {
		this.payCommission = payCommission;
	}
	
	public String getTypeOfPayment() {
		return typeOfPayment;
	}

	public void setTypeOfPayment(String typeOfPayment) {
		this.typeOfPayment = typeOfPayment;
	}
	
	public Double getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(Double basicPay) {
		this.basicPay = basicPay;
	}

	public Double getDP() {
		return DP;
	}

	public void setDP(Double dp) {
		this.DP = dp;
	}

	public Double getDA() {
		return DA;
	}

	public void setDA(Double da) {
		this.DA = da;
	}
	
	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}


}
