package com.tcs.sgv.pensionpay.valueobject;
// Generated Apr 2, 2009 5:54:26 PM by Hibernate Tools 3.2.0.beta8

import java.math.BigDecimal;
import java.util.Date;


/**
 * TrnPensionSupplyBillDtls generated by hbm2java
 */
public class TrnPensionSupplyBillDtls {

	// Fields

	private Long pensionSupplyBillId;
	private String billType;
	private String pensionerCode;
	private Long billNo;
	private String partyName;
	private BigDecimal differenceAmount = BigDecimal.ZERO;
	private BigDecimal percentage;
	private BigDecimal grossAmount = BigDecimal.ZERO;
	private BigDecimal deductionA = BigDecimal.ZERO;
	private BigDecimal deductionB = BigDecimal.ZERO;
	private BigDecimal netAmount = BigDecimal.ZERO;
	private BigDecimal createdUserId;
	private BigDecimal createdPostId;
	private Date createdDate;
	private BigDecimal updatedUserId;
	private BigDecimal updatedPostId;
	private Date updatedDate;
	private String ppoNo;
	private BigDecimal headCode;
	private BigDecimal paidAmount;
	private Short status;
	private Long groupId;
	private String branchCode;
	private String accountNo;
	private String locationCode;
	private String cvpOrderNo;
	private Date cvpOrderDate;
	private String gpoNo;
	private Date gpoDate;
	private BigDecimal totalCvpAmount = BigDecimal.ZERO;
	private BigDecimal pensionAmount =  BigDecimal.ZERO;
	private BigDecimal adpAmount =  BigDecimal.ZERO;
	private BigDecimal dpAmount =  BigDecimal.ZERO;
	private BigDecimal ir1Amount =  BigDecimal.ZERO;
	private BigDecimal ir2Amount =  BigDecimal.ZERO;
	private BigDecimal ir3Amount =  BigDecimal.ZERO;
	private BigDecimal daAmount =  BigDecimal.ZERO;
	private BigDecimal peonAllowance =  BigDecimal.ZERO;
	private BigDecimal medicalAllowance =  BigDecimal.ZERO;
	private BigDecimal gallantryAmount =  BigDecimal.ZERO;
	private BigDecimal otherBenefit =  BigDecimal.ZERO;
	private BigDecimal arrearPension =  BigDecimal.ZERO;  
	private BigDecimal arrearDA =  BigDecimal.ZERO;
	private BigDecimal arrearDiffComtPnsn =  BigDecimal.ZERO;  
	private BigDecimal arrearAnyOtherDiff =  BigDecimal.ZERO;  
	private BigDecimal arrear6PC =  BigDecimal.ZERO;
	private String bankCode;
	private String schemeCode;
	private String pensionerName;
	private String requestNo;
	private Long micrCode;
	private String payMode;
	private String arrearDtls;
	private BigDecimal calcArrearAmt = BigDecimal.ZERO;
	private String purpose;
	private String otherPurpose;
	private String ledgerNo;
	private String pageNo;
	
	// Constructors

	/** default constructor */
	public TrnPensionSupplyBillDtls() {

	}

	/** minimal constructor */
	public TrnPensionSupplyBillDtls(Long pensionSupplyBillId, BigDecimal createdUserId, BigDecimal createdPostId, Date createdDate) {

		this.pensionSupplyBillId = pensionSupplyBillId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
	}
	
	public TrnPensionSupplyBillDtls(Long pensionSupplyBillId,String ppoNo,String partyName,BigDecimal grossAmount,BigDecimal differenceAmount,
			BigDecimal netAmount,Short status,Long billNo) {

		this.pensionSupplyBillId = pensionSupplyBillId;
		this.ppoNo = ppoNo;
		this.partyName = partyName;
		this.grossAmount = grossAmount;
		this.differenceAmount = differenceAmount;
		this.netAmount = netAmount;
		this.status = status;
		this.billNo = billNo;
	}


	/** full constructor */
	
		
	public TrnPensionSupplyBillDtls(Long pensionSupplyBillId, String billType,
			String pensionerCode, Long billNo, String partyName,
			BigDecimal differenceAmount, BigDecimal percentage,
			BigDecimal grossAmount, BigDecimal deductionA,
			BigDecimal deductionB, BigDecimal netAmount,
			BigDecimal createdUserId, BigDecimal createdPostId,
			Date createdDate, BigDecimal updatedUserId,
			BigDecimal updatedPostId, Date updatedDate, String ppoNo,
			BigDecimal headCode, BigDecimal paidAmount, Short status,
			Long groupId, String branchCode, String accountNo,
			String locationCode, String cvpOrderNo, Date cvpOrderDate,
			String gpoNo, Date gpoDate, BigDecimal totalCvpAmount,
			BigDecimal pensionAmount, BigDecimal adpAmount,
			BigDecimal dpAmount, BigDecimal ir1Amount, BigDecimal ir2Amount,
			BigDecimal ir3Amount, BigDecimal daAmount,
			BigDecimal peonAllowance, BigDecimal medicalAllowance,
			BigDecimal gallantryAmount, BigDecimal otherBenefit,
			BigDecimal arrearPension, BigDecimal arrearDA,
			BigDecimal arrearDiffComtPnsn, BigDecimal arrearAnyOtherDiff,
			BigDecimal arrear6pc, String bankCode, String schemeCode,
			String pensionerName, String requestNo, Long micrCode,
			String payMode, String arrearDtls, BigDecimal calcArrearAmt,
			String purpose, String otherPurpose,String ledgerNo,String pageNo) {
		super();
		this.pensionSupplyBillId = pensionSupplyBillId;
		this.billType = billType;
		this.pensionerCode = pensionerCode;
		this.billNo = billNo;
		this.partyName = partyName;
		this.differenceAmount = differenceAmount;
		this.percentage = percentage;
		this.grossAmount = grossAmount;
		this.deductionA = deductionA;
		this.deductionB = deductionB;
		this.netAmount = netAmount;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.ppoNo = ppoNo;
		this.headCode = headCode;
		this.paidAmount = paidAmount;
		this.status = status;
		this.groupId = groupId;
		this.branchCode = branchCode;
		this.accountNo = accountNo;
		this.locationCode = locationCode;
		this.cvpOrderNo = cvpOrderNo;
		this.cvpOrderDate = cvpOrderDate;
		this.gpoNo = gpoNo;
		this.gpoDate = gpoDate;
		this.totalCvpAmount = totalCvpAmount;
		this.pensionAmount = pensionAmount;
		this.adpAmount = adpAmount;
		this.dpAmount = dpAmount;
		this.ir1Amount = ir1Amount;
		this.ir2Amount = ir2Amount;
		this.ir3Amount = ir3Amount;
		this.daAmount = daAmount;
		this.peonAllowance = peonAllowance;
		this.medicalAllowance = medicalAllowance;
		this.gallantryAmount = gallantryAmount;
		this.otherBenefit = otherBenefit;
		this.arrearPension = arrearPension;
		this.arrearDA = arrearDA;
		this.arrearDiffComtPnsn = arrearDiffComtPnsn;
		this.arrearAnyOtherDiff = arrearAnyOtherDiff;
		arrear6PC = arrear6pc;
		this.bankCode = bankCode;
		this.schemeCode = schemeCode;
		this.pensionerName = pensionerName;
		this.requestNo = requestNo;
		this.micrCode = micrCode;
		this.payMode = payMode;
		this.arrearDtls = arrearDtls;
		this.calcArrearAmt = calcArrearAmt;
		this.purpose = purpose;
		this.otherPurpose = otherPurpose;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
	}



	// Property accessors
	public Long getPensionSupplyBillId() {

		return this.pensionSupplyBillId;
	}
	
	public void setPensionSupplyBillId(Long pensionSupplyBillId) {

		this.pensionSupplyBillId = pensionSupplyBillId;
	}

	public String getBillType() {

		return this.billType;
	}

	public void setBillType(String billType) {

		this.billType = billType;
	}

	public String getPensionerCode() {

		return this.pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {

		this.pensionerCode = pensionerCode;
	}

	public Long getBillNo() {

		return this.billNo;
	}

	public void setBillNo(Long billNo) {

		this.billNo = billNo;
	}

	public String getPartyName() {

		return this.partyName;
	}

	public void setPartyName(String partyName) {

		this.partyName = partyName;
	}

	public BigDecimal getDifferenceAmount() {

		return this.differenceAmount;
	}

	public void setDifferenceAmount(BigDecimal differenceAmount) {

		this.differenceAmount = differenceAmount;
	}

	public BigDecimal getPercentage() {

		return this.percentage;
	}

	public void setPercentage(BigDecimal percentage) {

		this.percentage = percentage;
	}

	public BigDecimal getGrossAmount() {

		return this.grossAmount;
	}

	public void setGrossAmount(BigDecimal grossAmount) {

		this.grossAmount = grossAmount;
	}

	public BigDecimal getDeductionA() {
		return deductionA;
	}

	public void setDeductionA(BigDecimal deductionA) {
		this.deductionA = deductionA;
	}

	public BigDecimal getDeductionB() {
		return deductionB;
	}

	public void setDeductionB(BigDecimal deductionB) {
		this.deductionB = deductionB;
	}
	
	public BigDecimal getNetAmount() {

		return this.netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {

		this.netAmount = netAmount;
	}

	public BigDecimal getCreatedUserId() {

		return this.createdUserId;
	}

	public void setCreatedUserId(BigDecimal createdUserId) {

		this.createdUserId = createdUserId;
	}

	public BigDecimal getCreatedPostId() {

		return this.createdPostId;
	}

	public void setCreatedPostId(BigDecimal createdPostId) {

		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {

		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	public BigDecimal getUpdatedUserId() {

		return this.updatedUserId;
	}

	public void setUpdatedUserId(BigDecimal updatedUserId) {

		this.updatedUserId = updatedUserId;
	}

	public BigDecimal getUpdatedPostId() {

		return this.updatedPostId;
	}

	public void setUpdatedPostId(BigDecimal updatedPostId) {

		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {

		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		this.updatedDate = updatedDate;
	}

	public String getPpoNo() {

		return ppoNo;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public BigDecimal getHeadCode() {

		return headCode;
	}
	
	public void setHeadCode(BigDecimal headCode) {

		this.headCode = headCode;
	}

	public void setPaidAmount(BigDecimal paidAmount) {

		this.paidAmount = paidAmount;
	}
	
	public BigDecimal getPaidAmount() {

		return paidAmount;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public void setGroupId(Long groupId) {

		this.groupId = groupId;
	}
	
	public Long getGroupId() {

		return groupId;
	}
	
	public void setBranchCode(String branchCode)
	{
		this.branchCode = branchCode;
	}
	
	public String getBranchCode()
	{
		return branchCode;
	}
	
	public void setAccountNo(String accountNo)
	{
		this.accountNo = accountNo;
	}
	
	public String getAccountNo()
	{
		return accountNo;
	}
	
	public void setLocationCode(String locationCode)
	{
		this.locationCode = locationCode;
	}
	
	public String getLocationCode()
	{
		return locationCode;
	}
	
	public String getCvpOrderNo() {
	
		return cvpOrderNo;
	}
	
	public void setCvpOrderNo(String cvpOrderNo) {
	
		this.cvpOrderNo = cvpOrderNo;
	}
	
	public Date getCvpOrderDate() {
	
		return cvpOrderDate;
	}
	
	public void setCvpOrderDate(Date cvpOrderDate) {
	
		this.cvpOrderDate = cvpOrderDate;
	}
	
	public String getGpoNo() {
	
		return gpoNo;
	}

	public void setGpoNo(String gpoNo) {
	
		this.gpoNo = gpoNo;
	}

	public Date getGpoDate() {
	
		return gpoDate;
	}

	public void setGpoDate(Date gpoDate) {
	
		this.gpoDate = gpoDate;
	}

	public BigDecimal getTotalCvpAmount() {
	
		return totalCvpAmount;
	}

	public void setTotalCvpAmount(BigDecimal totalCvpAmount) {
	
		this.totalCvpAmount = totalCvpAmount;
	}

	public BigDecimal getPensionAmount() {
		return pensionAmount;
	}

	public void setPensionAmount(BigDecimal pensionAmount) {
		this.pensionAmount = pensionAmount;
	}

	public BigDecimal getAdpAmount() {
		return adpAmount;
	}

	public void setAdpAmount(BigDecimal adpAmount) {
		this.adpAmount = adpAmount;
	}

	public BigDecimal getDpAmount() {
		return dpAmount;
	}

	public void setDpAmount(BigDecimal dpAmount) {
		this.dpAmount = dpAmount;
	}

	public BigDecimal getIr1Amount() {
		return ir1Amount;
	}

	public void setIr1Amount(BigDecimal ir1Amount) {
		this.ir1Amount = ir1Amount;
	}

	public BigDecimal getIr2Amount() {
		return ir2Amount;
	}

	public void setIr2Amount(BigDecimal ir2Amount) {
		this.ir2Amount = ir2Amount;
	}

	public BigDecimal getIr3Amount() {
		return ir3Amount;
	}

	public void setIr3Amount(BigDecimal ir3Amount) {
		this.ir3Amount = ir3Amount;
	}

	public BigDecimal getDaAmount() {
		return daAmount;
	}

	public void setDaAmount(BigDecimal daAmount) {
		this.daAmount = daAmount;
	}

	public BigDecimal getPeonAllowance() {
		return peonAllowance;
	}

	public void setPeonAllowance(BigDecimal peonAllowance) {
		this.peonAllowance = peonAllowance;
	}

	public BigDecimal getMedicalAllowance() {
		return medicalAllowance;
	}

	public void setMedicalAllowance(BigDecimal medicalAllowance) {
		this.medicalAllowance = medicalAllowance;
	}

	public BigDecimal getGallantryAmount() {
		return gallantryAmount;
	}

	public void setGallantryAmount(BigDecimal gallantryAmount) {
		this.gallantryAmount = gallantryAmount;
	}

	public BigDecimal getOtherBenefit() {
		return otherBenefit;
	}

	public void setOtherBenefit(BigDecimal otherBenefit) {
		this.otherBenefit = otherBenefit;
	}

	public BigDecimal getArrearPension() {
		return arrearPension;
	}

	public void setArrearPension(BigDecimal arrearPension) {
		this.arrearPension = arrearPension;
	}

	public BigDecimal getArrearDA() {
		return arrearDA;
	}

	public void setArrearDA(BigDecimal arrearDA) {
		this.arrearDA = arrearDA;
	}

	public BigDecimal getArrearDiffComtPnsn() {
		return arrearDiffComtPnsn;
	}

	public void setArrearDiffComtPnsn(BigDecimal arrearDiffComtPnsn) {
		this.arrearDiffComtPnsn = arrearDiffComtPnsn;
	}

	public BigDecimal getArrearAnyOtherDiff() {
		return arrearAnyOtherDiff;
	}

	public void setArrearAnyOtherDiff(BigDecimal arrearAnyOtherDiff) {
		this.arrearAnyOtherDiff = arrearAnyOtherDiff;
	}

	public BigDecimal getArrear6PC() {
		return arrear6PC;
	}

	public void setArrear6PC(BigDecimal arrear6pc) {
		arrear6PC = arrear6pc;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getPensionerName() {
		return pensionerName;
	}

	public void setPensionerName(String pensionerName) {
		this.pensionerName = pensionerName;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public Long getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(Long micrCode) {
		this.micrCode = micrCode;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getArrearDtls() {
		return arrearDtls;
	}

	public void setArrearDtls(String arrearDtls) {
		this.arrearDtls = arrearDtls;
	}

	public BigDecimal getCalcArrearAmt() {
		return calcArrearAmt;
	}

	public void setCalcArrearAmt(BigDecimal calcArrearAmt) {
		this.calcArrearAmt = calcArrearAmt;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getOtherPurpose() {
		return otherPurpose;
	}

	public void setOtherPurpose(String otherPurpose) {
		this.otherPurpose = otherPurpose;
	}

	public String getLedgerNo() {
		return ledgerNo;
	}

	public void setLedgerNo(String ledgerNo) {
		this.ledgerNo = ledgerNo;
	}

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}
		
}