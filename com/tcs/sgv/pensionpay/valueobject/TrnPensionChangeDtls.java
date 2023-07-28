/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 31, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 May 31, 2011
 */
public class TrnPensionChangeDtls implements java.io.Serializable {

	private Long trnPensionChangeDtlsId;

	private Long trnPensionChangeHdrId;

	private String ppoNo;

	private String pensionerCode;

	private String accountNo;

	private String pensionerName;

	private BigDecimal pensionAmount = BigDecimal.ZERO;

	private BigDecimal dpAmount = BigDecimal.ZERO;

	private BigDecimal adpAmount = BigDecimal.ZERO;

	private BigDecimal tiAmount = BigDecimal.ZERO;

	private BigDecimal ir1Amount = BigDecimal.ZERO;

	private BigDecimal ir2Amount = BigDecimal.ZERO;

	private BigDecimal ir3Amount = BigDecimal.ZERO;

	private BigDecimal arrearAmount = BigDecimal.ZERO;

	private BigDecimal tiArrearAmount = BigDecimal.ZERO;

	private BigDecimal cvpMonthAmount = BigDecimal.ZERO;

	private BigDecimal recoveryAmount = BigDecimal.ZERO;

	private BigDecimal reducedPension = BigDecimal.ZERO;

	private BigDecimal allcationBf1436 = BigDecimal.ZERO;

	private BigDecimal allcationBf11156 = BigDecimal.ZERO;

	private BigDecimal allcationAf11156 = BigDecimal.ZERO;

	private BigDecimal allcationAf10560 = BigDecimal.ZERO;

	private BigDecimal allcationAfZp = BigDecimal.ZERO;

	private BigDecimal personalPensionAmount = BigDecimal.ZERO;

	private Integer payForMonth;

	private BigDecimal grossAmount = BigDecimal.ZERO;

	private BigDecimal netAmount = BigDecimal.ZERO;

	private Integer headCode;

	private BigDecimal daRate = BigDecimal.ZERO;;

	private String ledgerNo;

	private String pageNo;

	private BigDecimal peonAllowance = BigDecimal.ZERO;

	private BigDecimal medicalAllowenceAmount = BigDecimal.ZERO;

	private BigDecimal other1 = BigDecimal.ZERO;

	private BigDecimal otherBenefits = BigDecimal.ZERO;

	private BigDecimal totalArrearAmt = BigDecimal.ZERO;

	private BigDecimal pensionCutAmount = BigDecimal.ZERO;

	private String ropType;

	private BigDecimal arrear6PC = BigDecimal.ZERO;

	private BigDecimal arrearCommutation = BigDecimal.ZERO;

	private BigDecimal arrearOthrDiff = BigDecimal.ZERO;

	private BigDecimal arrearLC = BigDecimal.ZERO;

	private BigDecimal punishmentCutAmt = BigDecimal.ZERO;

	public TrnPensionChangeDtls() {

	}

	/**
	 * @param trnPensionChangeDtlsId
	 * @param trnPensionChangeHdrId
	 * @param ppoNo
	 * @param pensionerCode
	 * @param accountNo
	 * @param pensionerName
	 * @param pensionAmount
	 * @param dpAmount
	 * @param adpAmount
	 * @param tiAmount
	 * @param ir1Amount
	 * @param ir2Amount
	 * @param ir3Amount
	 * @param arrearAmount
	 * @param tiArrearAmount
	 * @param cvpMonthAmount
	 * @param recoveryAmount
	 * @param reducedPension
	 * @param allcationBf1436
	 * @param allcationBf11156
	 * @param allcationAf11156
	 * @param allcationAf10560
	 * @param allcationAfZp
	 * @param personalPensionAmount
	 * @param payForMonth
	 */
	public TrnPensionChangeDtls(Long trnPensionChangeDtlsId, Long trnPensionChangeHdrId, String ppoNo, String pensionerCode, String accountNo, String pensionerName, BigDecimal pensionAmount,
			BigDecimal dpAmount, BigDecimal adpAmount, BigDecimal tiAmount, BigDecimal ir1Amount, BigDecimal ir2Amount, BigDecimal ir3Amount, BigDecimal arrearAmount, BigDecimal tiArrearAmount,
			BigDecimal cvpMonthAmount, BigDecimal recoveryAmount, BigDecimal reducedPension, BigDecimal allcationBf1436, BigDecimal allcationBf11156, BigDecimal allcationAf11156,
			BigDecimal allcationAf10560, BigDecimal allcationAfZp, BigDecimal personalPensionAmount, Integer payForMonth, BigDecimal grossAmount, BigDecimal netAmount, Integer headCode,
			BigDecimal daRate, String ledgerNo, String pageNo, BigDecimal peonAllowance, BigDecimal medicalAllowenceAmount, BigDecimal other1, BigDecimal otherBenefits, BigDecimal totalArrearAmt,
			BigDecimal pensionCutAmount, String ropType, BigDecimal arrear6PC, BigDecimal arrearCommutation, BigDecimal arrearOthrDiff, BigDecimal arrearLC, BigDecimal punishmentCutAmt) {

		super();
		this.trnPensionChangeDtlsId = trnPensionChangeDtlsId;
		this.trnPensionChangeHdrId = trnPensionChangeHdrId;
		this.ppoNo = ppoNo;
		this.pensionerCode = pensionerCode;
		this.accountNo = accountNo;
		this.pensionerName = pensionerName;
		this.pensionAmount = pensionAmount;
		this.dpAmount = dpAmount;
		this.adpAmount = adpAmount;
		this.tiAmount = tiAmount;
		this.ir1Amount = ir1Amount;
		this.ir2Amount = ir2Amount;
		this.ir3Amount = ir3Amount;
		this.arrearAmount = arrearAmount;
		this.tiArrearAmount = tiArrearAmount;
		this.cvpMonthAmount = cvpMonthAmount;
		this.recoveryAmount = recoveryAmount;
		this.reducedPension = reducedPension;
		this.allcationBf1436 = allcationBf1436;
		this.allcationBf11156 = allcationBf11156;
		this.allcationAf11156 = allcationAf11156;
		this.allcationAf10560 = allcationAf10560;
		this.allcationAfZp = allcationAfZp;
		this.personalPensionAmount = personalPensionAmount;
		this.payForMonth = payForMonth;
		this.grossAmount = grossAmount;
		this.netAmount = netAmount;
		this.headCode = headCode;
		this.daRate = daRate;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
		this.peonAllowance = peonAllowance;
		this.medicalAllowenceAmount = medicalAllowenceAmount;
		this.other1 = other1;
		this.otherBenefits = otherBenefits;
		this.totalArrearAmt = totalArrearAmt;
		this.pensionCutAmount = pensionCutAmount;
		this.ropType = ropType;
		this.arrear6PC = arrear6PC;
		this.arrearCommutation = arrearCommutation;
		this.arrearOthrDiff = arrearOthrDiff;
		this.arrearLC = arrearLC;
		this.punishmentCutAmt = punishmentCutAmt;
	}

	public Long getTrnPensionChangeDtlsId() {

		return trnPensionChangeDtlsId;
	}

	public void setTrnPensionChangeDtlsId(Long trnPensionChangeDtlsId) {

		this.trnPensionChangeDtlsId = trnPensionChangeDtlsId;
	}

	public Long getTrnPensionChangeHdrId() {

		return trnPensionChangeHdrId;
	}

	public void setTrnPensionChangeHdrId(Long trnPensionChangeHdrId) {

		this.trnPensionChangeHdrId = trnPensionChangeHdrId;
	}

	public String getPpoNo() {

		return ppoNo;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public String getPensionerCode() {

		return pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {

		this.pensionerCode = pensionerCode;
	}

	public String getAccountNo() {

		return accountNo;
	}

	public void setAccountNo(String accountNo) {

		this.accountNo = accountNo;
	}

	public String getPensionerName() {

		return pensionerName;
	}

	public void setPensionerName(String pensionerName) {

		this.pensionerName = pensionerName;
	}

	public BigDecimal getPensionAmount() {

		return pensionAmount;
	}

	public void setPensionAmount(BigDecimal pensionAmount) {

		this.pensionAmount = pensionAmount;
	}

	public BigDecimal getDpAmount() {

		return dpAmount;
	}

	public void setDpAmount(BigDecimal dpAmount) {

		this.dpAmount = dpAmount;
	}

	public BigDecimal getAdpAmount() {

		return adpAmount;
	}

	public void setAdpAmount(BigDecimal adpAmount) {

		this.adpAmount = adpAmount;
	}

	public BigDecimal getTiAmount() {

		return tiAmount;
	}

	public void setTiAmount(BigDecimal tiAmount) {

		this.tiAmount = tiAmount;
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

	public BigDecimal getArrearAmount() {

		return arrearAmount;
	}

	public void setArrearAmount(BigDecimal arrearAmount) {

		this.arrearAmount = arrearAmount;
	}

	public BigDecimal getTiArrearAmount() {

		return tiArrearAmount;
	}

	public void setTiArrearAmount(BigDecimal tiArrearAmount) {

		this.tiArrearAmount = tiArrearAmount;
	}

	public BigDecimal getCvpMonthAmount() {

		return cvpMonthAmount;
	}

	public void setCvpMonthAmount(BigDecimal cvpMonthAmount) {

		this.cvpMonthAmount = cvpMonthAmount;
	}

	public BigDecimal getRecoveryAmount() {

		return recoveryAmount;
	}

	public void setRecoveryAmount(BigDecimal recoveryAmount) {

		this.recoveryAmount = recoveryAmount;
	}

	public BigDecimal getReducedPension() {

		return reducedPension;
	}

	public void setReducedPension(BigDecimal reducedPension) {

		this.reducedPension = reducedPension;
	}

	public BigDecimal getAllcationBf1436() {

		return allcationBf1436;
	}

	public void setAllcationBf1436(BigDecimal allcationBf1436) {

		this.allcationBf1436 = allcationBf1436;
	}

	public BigDecimal getAllcationBf11156() {

		return allcationBf11156;
	}

	public void setAllcationBf11156(BigDecimal allcationBf11156) {

		this.allcationBf11156 = allcationBf11156;
	}

	public BigDecimal getAllcationAf11156() {

		return allcationAf11156;
	}

	public void setAllcationAf11156(BigDecimal allcationAf11156) {

		this.allcationAf11156 = allcationAf11156;
	}

	public BigDecimal getAllcationAf10560() {

		return allcationAf10560;
	}

	public void setAllcationAf10560(BigDecimal allcationAf10560) {

		this.allcationAf10560 = allcationAf10560;
	}

	public BigDecimal getAllcationAfZp() {

		return allcationAfZp;
	}

	public void setAllcationAfZp(BigDecimal allcationAfZp) {

		this.allcationAfZp = allcationAfZp;
	}

	public BigDecimal getPersonalPensionAmount() {

		return personalPensionAmount;
	}

	public void setPersonalPensionAmount(BigDecimal personalPensionAmount) {

		this.personalPensionAmount = personalPensionAmount;
	}

	public Integer getPayForMonth() {

		return payForMonth;
	}

	public void setPayForMonth(Integer payForMonth) {

		this.payForMonth = payForMonth;
	}

	public BigDecimal getGrossAmount() {

		return grossAmount;
	}

	public void setGrossAmount(BigDecimal grossAmount) {

		this.grossAmount = grossAmount;
	}

	public BigDecimal getNetAmount() {

		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {

		this.netAmount = netAmount;
	}

	public Integer getHeadCode() {

		return headCode;
	}

	public void setHeadCode(Integer headCode) {

		this.headCode = headCode;
	}

	public BigDecimal getDaRate() {

		return daRate;
	}

	public void setDaRate(BigDecimal daRate) {

		this.daRate = daRate;
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

	public BigDecimal getPeonAllowance() {

		return peonAllowance;
	}

	public void setPeonAllowance(BigDecimal peonAllowance) {

		this.peonAllowance = peonAllowance;
	}

	public BigDecimal getMedicalAllowenceAmount() {

		return medicalAllowenceAmount;
	}

	public void setMedicalAllowenceAmount(BigDecimal medicalAllowenceAmount) {

		this.medicalAllowenceAmount = medicalAllowenceAmount;
	}

	public BigDecimal getOther1() {

		return other1;
	}

	public void setOther1(BigDecimal other1) {

		this.other1 = other1;
	}

	public BigDecimal getOtherBenefits() {

		return otherBenefits;
	}

	public void setOtherBenefits(BigDecimal otherBenefits) {

		this.otherBenefits = otherBenefits;
	}

	public BigDecimal getTotalArrearAmt() {

		return totalArrearAmt;
	}

	public void setTotalArrearAmt(BigDecimal totalArrearAmt) {

		this.totalArrearAmt = totalArrearAmt;
	}

	public BigDecimal getPensionCutAmount() {

		return pensionCutAmount;
	}

	public void setPensionCutAmount(BigDecimal pensionCutAmount) {

		this.pensionCutAmount = pensionCutAmount;
	}

	public String getRopType() {

		return ropType;
	}

	public void setRopType(String ropType) {

		this.ropType = ropType;
	}

	public BigDecimal getArrear6PC() {

		return arrear6PC;
	}

	public void setArrear6PC(BigDecimal arrear6pc) {

		arrear6PC = arrear6pc;
	}

	public BigDecimal getArrearCommutation() {

		return arrearCommutation;
	}

	public void setArrearCommutation(BigDecimal arrearCommutation) {

		this.arrearCommutation = arrearCommutation;
	}

	public BigDecimal getArrearOthrDiff() {

		return arrearOthrDiff;
	}

	public void setArrearOthrDiff(BigDecimal arrearOthrDiff) {

		this.arrearOthrDiff = arrearOthrDiff;
	}

	public BigDecimal getArrearLC() {

		return arrearLC;
	}

	public void setArrearLC(BigDecimal arrearLC) {

		this.arrearLC = arrearLC;
	}

	public BigDecimal getPunishmentCutAmt() {

		return punishmentCutAmt;
	}

	public void setPunishmentCutAmt(BigDecimal punishmentCutAmt) {

		this.punishmentCutAmt = punishmentCutAmt;
	}

}
