package com.tcs.sgv.pensionpay.valueobject;
//com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO
import java.math.BigDecimal;
import java.util.Date;


public class MonthlyPensionBillVO implements Comparable<MonthlyPensionBillVO> {

	private String pensionerCode;

	private String ppoNo;

	private String pensionerName;

	private String accountNo;

	private BigDecimal basicPensionAmount = BigDecimal.ZERO;

	private BigDecimal dpPercent = BigDecimal.ZERO;

	private BigDecimal tiPercent = BigDecimal.ZERO;

	private BigDecimal dpPercentAmount = BigDecimal.ZERO;

	private BigDecimal tiPercentAmount = BigDecimal.ZERO;

	private BigDecimal medicalAllowenceAmount = BigDecimal.ZERO;

	private BigDecimal cvpMonthlyAmount = BigDecimal.ZERO;

	private BigDecimal TIArrearsAmount = BigDecimal.ZERO;

	private BigDecimal otherArrearsAmount = BigDecimal.ZERO;

	private BigDecimal itCutAmount;

	private BigDecimal specialCutAmount;

	private BigDecimal otherBenefit = BigDecimal.ZERO;

	private BigDecimal OMR;

	private BigDecimal recoveryAmount = BigDecimal.ZERO;

	private BigDecimal pensionCutAmount = BigDecimal.ZERO;

	private BigDecimal netPensionAmount = BigDecimal.ZERO;

	private BigDecimal allnBf11156 = BigDecimal.ZERO;

	private BigDecimal allnAf11156 = BigDecimal.ZERO;

	private BigDecimal allnAf10560 = BigDecimal.ZERO;

	private BigDecimal personalPension = BigDecimal.ZERO;

	private BigDecimal ir;

	private BigDecimal MOComm;

	private Integer forMonth;

	private BigDecimal headCode;

	private String billType;

	private BigDecimal adpAmount = BigDecimal.ZERO;

	private Integer noOfDays;

	private Date fromDate;

	private Date toDate;

	private String branchCode;

	private String pageNo;

	private String ledgerNo;

	private BigDecimal PensionBillAmount = BigDecimal.ZERO;

	private BigDecimal ir1Amt = BigDecimal.ZERO;

	private BigDecimal ir2Amt = BigDecimal.ZERO;

	private BigDecimal ir3Amt = BigDecimal.ZERO;

	private BigDecimal grossAmount = BigDecimal.ZERO;

	private BigDecimal allnBf11136 = BigDecimal.ZERO;

	private BigDecimal allnAfZp = BigDecimal.ZERO;

	private BigDecimal reducedPension = BigDecimal.ZERO;

	public Long totalPensioners;

	private String series;

	private Integer mainCatCode;

	private String mainCatDesc;

	private BigDecimal peonAllowanceAmt = BigDecimal.ZERO;

	private BigDecimal gallantryAmt = BigDecimal.ZERO;

	private BigDecimal totalArrearAmt = BigDecimal.ZERO;

	private String ropType;

	private BigDecimal arrear6PC = BigDecimal.ZERO;

	private BigDecimal arrearCommutation = BigDecimal.ZERO;

	private BigDecimal arrearOthrDiff = BigDecimal.ZERO;

	private BigDecimal arrearLC = BigDecimal.ZERO;

	private BigDecimal punishmentCutAmt = BigDecimal.ZERO;

	// Constructors

	/** default constructor */
	public MonthlyPensionBillVO() {

	}

	/** Requierd field Constructer **/

	public MonthlyPensionBillVO(BigDecimal headCode, String ledgerNo, String pageNo, String ppoNo, String pensionerName, String accountNo, BigDecimal allnBf11136, BigDecimal allnBf11156,
			BigDecimal allnAf11156, BigDecimal allnAf10560, BigDecimal allnAfZp, BigDecimal ir1Amt, BigDecimal dpPercentAmount, BigDecimal ir2Amt, BigDecimal tiPercentAmount, BigDecimal ir3Amt,
			BigDecimal grossAmount, BigDecimal netPensionAmount, BigDecimal TIArrearsAmount, BigDecimal otherArrearsAmount, BigDecimal recoveryAmount, BigDecimal peonAllowanceAmt,
			BigDecimal medicalAllowenceAmount, BigDecimal gallantryAmt, BigDecimal otherBenefit, BigDecimal totalArrearAmt) {

		this.headCode = headCode;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
		this.ppoNo = ppoNo;
		this.pensionerName = pensionerName;
		this.accountNo = accountNo;
		this.allnBf11136 = allnBf11136;
		this.allnAf10560 = allnAf10560;
		this.allnAf11156 = allnAf11156;
		this.allnBf11156 = allnBf11156;
		this.allnAfZp = allnAfZp;
		this.ir1Amt = ir1Amt;
		this.dpPercentAmount = dpPercentAmount;
		this.ir2Amt = ir2Amt;
		this.tiPercentAmount = tiPercentAmount;
		this.ir3Amt = ir3Amt;
		this.grossAmount = grossAmount;
		this.netPensionAmount = netPensionAmount;
		this.TIArrearsAmount = TIArrearsAmount;
		this.otherArrearsAmount = otherArrearsAmount;
		this.recoveryAmount = recoveryAmount;
		this.peonAllowanceAmt = peonAllowanceAmt;
		this.medicalAllowenceAmount = medicalAllowenceAmount;
		this.gallantryAmt = gallantryAmt;
		this.otherBenefit = otherBenefit;
		this.totalArrearAmt = totalArrearAmt;
	}

	/**
	 * Constructer for setting monthlybillvo on generation of monthly bill
	 * report by change statement
	 * 
	 * @param headCode
	 * @param ledgerNo
	 * @param pageNo
	 * @param ppoNo
	 * @param pensionerName
	 * @param accountNo
	 * @param allnBf11136
	 * @param allnBf11156
	 * @param allnAf11156
	 * @param allnAf10560
	 * @param allnAfZp
	 * @param ir1Amt
	 * @param dpPercentAmount
	 * @param ir2Amt
	 * @param tiPercentAmount
	 * @param ir3Amt
	 * @param grossAmount
	 * @param netPensionAmount
	 * @param TIArrearsAmount
	 * @param otherArrearsAmount
	 * @param recoveryAmount
	 * @param peonAllowanceAmt
	 * @param medicalAllowenceAmount
	 * @param gallantryAmt
	 * @param otherBenefit
	 * @param totalArrearAmt
	 */
	public MonthlyPensionBillVO(Integer headCode, String ledgerNo, String pageNo, String ppoNo, String pensionerName, String accountNo, BigDecimal allnBf11136, BigDecimal allnBf11156,
			BigDecimal allnAf11156, BigDecimal allnAf10560, BigDecimal allnAfZp, BigDecimal ir1Amt, BigDecimal dpPercentAmount, BigDecimal ir2Amt, BigDecimal tiPercentAmount, BigDecimal ir3Amt,
			BigDecimal grossAmount, BigDecimal netPensionAmount, BigDecimal TIArrearsAmount, BigDecimal otherArrearsAmount, BigDecimal recoveryAmount, BigDecimal peonAllowanceAmt,
			BigDecimal medicalAllowenceAmount, BigDecimal gallantryAmt, BigDecimal otherBenefit, BigDecimal totalArrearAmt) {

		this.headCode = BigDecimal.valueOf(headCode.longValue());
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
		this.ppoNo = ppoNo;
		this.pensionerName = pensionerName;
		this.accountNo = accountNo;
		this.allnBf11136 = allnBf11136;
		this.allnAf10560 = allnAf10560;
		this.allnAf11156 = allnAf11156;
		this.allnBf11156 = allnBf11156;
		this.allnAfZp = allnAfZp;
		this.ir1Amt = ir1Amt;
		this.dpPercentAmount = dpPercentAmount;
		this.ir2Amt = ir2Amt;
		this.tiPercentAmount = tiPercentAmount;
		this.ir3Amt = ir3Amt;
		this.grossAmount = grossAmount;
		this.netPensionAmount = netPensionAmount;
		this.TIArrearsAmount = TIArrearsAmount;
		this.otherArrearsAmount = otherArrearsAmount;
		this.recoveryAmount = recoveryAmount;
		this.peonAllowanceAmt = peonAllowanceAmt;
		this.medicalAllowenceAmount = medicalAllowenceAmount;
		this.gallantryAmt = gallantryAmt;
		this.otherBenefit = otherBenefit;
		this.totalArrearAmt = totalArrearAmt;
	}

	/** For Summary **/
	public MonthlyPensionBillVO(Long totalPensioners, BigDecimal allnBf11136, BigDecimal allnBf11156, BigDecimal allnAf11156, BigDecimal allnAf10560, BigDecimal allnAfZp, BigDecimal ir1Amt,
			BigDecimal dpPercentAmount, BigDecimal ir2Amt, BigDecimal tiPercentAmount, BigDecimal ir3Amt, BigDecimal grossAmount, BigDecimal netPensionAmount, BigDecimal TIArrearsAmount,
			BigDecimal otherArrearsAmount, Integer mainCatCode, String mainCatDesc, BigDecimal recoveryAmount, BigDecimal totalArrearAmt) {

		this.totalPensioners = totalPensioners;
		this.allnBf11136 = allnBf11136;
		this.allnAf10560 = allnAf10560;
		this.allnAf11156 = allnAf11156;
		this.allnBf11156 = allnBf11156;
		this.allnAfZp = allnAfZp;
		this.ir1Amt = ir1Amt;
		this.dpPercentAmount = dpPercentAmount;
		this.ir2Amt = ir2Amt;
		this.tiPercentAmount = tiPercentAmount;
		this.ir3Amt = ir3Amt;
		this.grossAmount = grossAmount;
		this.netPensionAmount = netPensionAmount;
		this.TIArrearsAmount = TIArrearsAmount;
		this.otherArrearsAmount = otherArrearsAmount;
		this.mainCatCode = mainCatCode;
		this.mainCatDesc = mainCatDesc;
		this.recoveryAmount = recoveryAmount;
		this.totalArrearAmt = totalArrearAmt;
	}

	/*
	 * public MonthlyPensionBillVO(BigDecimal headCode, Integer totalPensioners,
	 * BigDecimal allnBf11136, BigDecimal allnBf11156, BigDecimal allnAf11156,
	 * BigDecimal allnAf10560, BigDecimal allnAfZp, BigDecimal ir1Amt,
	 * BigDecimal dpPercentAmount, BigDecimal ir2Amt, BigDecimal
	 * tiPercentAmount, BigDecimal ir3Amt, BigDecimal grossAmount, BigDecimal
	 * netPensionAmount, BigDecimal TIArrearsAmount, BigDecimal
	 * otherArrearsAmount) {
	 * 
	 * this.headCode = headCode; this.totalPensioners = totalPensioners;
	 * this.allnBf11136 = allnBf11136; this.allnAf10560 = allnAf10560;
	 * this.allnAf11156 = allnAf11156; this.allnBf11156 = allnBf11156;
	 * this.allnAfZp = allnAfZp; this.ir1Amt = ir1Amt; this.dpPercentAmount =
	 * dpPercentAmount; this.ir2Amt = ir2Amt; this.tiPercentAmount =
	 * tiPercentAmount; this.ir3Amt = ir3Amt; this.grossAmount = grossAmount;
	 * this.netPensionAmount = netPensionAmount; this.TIArrearsAmount =
	 * TIArrearsAmount; this.otherArrearsAmount = otherArrearsAmount; }
	 */

	/** full constructor */
	public MonthlyPensionBillVO(String pensionerCode, String ppoNo, String pensionerName, String accountNo, BigDecimal basicPensionAmount, BigDecimal dpPercent, BigDecimal tiPercent,
			BigDecimal dpPercentAmount, BigDecimal tiPercentAmount, BigDecimal medicalAllowenceAmount, BigDecimal cvpMonthlyAmount, BigDecimal TIArrearsAmount, BigDecimal otherArrearsAmount,
			BigDecimal itCutAmount, BigDecimal specialCutAmount, BigDecimal otherBenefit, BigDecimal OMR, BigDecimal recoveryAmount, BigDecimal pensionCutAmount, BigDecimal netPensionAmount,
			BigDecimal allnBf11156, BigDecimal allnAf11156, BigDecimal allnAf10560, BigDecimal personalPension, BigDecimal ir, BigDecimal MOComm, Integer forMonth, BigDecimal headCode,
			String billType, BigDecimal adpAmount, Integer noOfDays, Date fromDate, Date toDate, String branchCode, String pageNo, String ledgerNo, BigDecimal PensionBillAmount, BigDecimal ir1Amt,
			BigDecimal ir2Amt, BigDecimal ir3Amt, BigDecimal grossAmount, BigDecimal allnBf11136, BigDecimal allnAfZp, BigDecimal reducedPension, Long totalPensioners, String series,
			Integer mainCatCode, String mainCatDesc, BigDecimal peonAllowanceAmt, BigDecimal gallantryAmt, BigDecimal totalArrearAmt, String ropType, BigDecimal arrear6PC,
			BigDecimal arrearCommutation, BigDecimal arrearOthrDiff, BigDecimal arrearLC, BigDecimal punishmentCutAmt) {

		this.pensionerCode = pensionerCode;
		this.ppoNo = ppoNo;
		this.pensionerName = pensionerName;
		this.accountNo = accountNo;
		this.basicPensionAmount = basicPensionAmount;
		this.dpPercent = dpPercent;
		this.tiPercent = tiPercent;
		this.dpPercentAmount = dpPercentAmount;
		this.tiPercentAmount = tiPercentAmount;
		this.cvpMonthlyAmount = cvpMonthlyAmount;
		this.medicalAllowenceAmount = medicalAllowenceAmount;
		this.TIArrearsAmount = TIArrearsAmount;
		this.otherArrearsAmount = otherArrearsAmount;
		this.itCutAmount = itCutAmount;
		this.specialCutAmount = specialCutAmount;
		this.otherBenefit = otherBenefit;
		this.OMR = OMR;
		this.recoveryAmount = recoveryAmount;
		this.pensionCutAmount = pensionCutAmount;
		this.netPensionAmount = netPensionAmount;
		this.allnBf11156 = allnBf11156;
		this.allnAf11156 = allnAf11156;
		this.allnAf10560 = allnAf10560;
		this.personalPension = personalPension;
		this.ir = ir;
		this.MOComm = MOComm;
		this.forMonth = forMonth;
		this.headCode = headCode;
		this.billType = billType;
		this.adpAmount = adpAmount;
		this.noOfDays = noOfDays;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.branchCode = branchCode;
		this.pageNo = pageNo;
		this.ledgerNo = ledgerNo;
		this.PensionBillAmount = PensionBillAmount;
		this.ir1Amt = ir1Amt;
		this.ir2Amt = ir2Amt;
		this.ir3Amt = ir3Amt;
		this.grossAmount = grossAmount;
		this.allnBf11136 = allnBf11136;
		this.allnAfZp = allnAfZp;
		this.reducedPension = reducedPension;
		this.totalPensioners = totalPensioners;
		this.series = series;
		this.mainCatCode = mainCatCode;
		this.mainCatDesc = mainCatDesc;
		this.peonAllowanceAmt = peonAllowanceAmt;
		this.gallantryAmt = gallantryAmt;
		this.totalArrearAmt = totalArrearAmt;
		this.ropType = ropType;
		this.arrear6PC = arrear6PC;
		this.arrearCommutation = arrearCommutation;
		this.arrearOthrDiff = arrearOthrDiff;
		this.arrearLC = arrearLC;
		this.punishmentCutAmt = punishmentCutAmt;
	}

	// Property accessors
	public String getPensionerCode() {

		return this.pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {

		this.pensionerCode = pensionerCode;
	}

	public String getPpoNo() {

		return this.ppoNo;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public String getPensionerName() {

		return this.pensionerName;
	}

	public void setPensionerName(String pensionerName) {

		this.pensionerName = pensionerName;
	}

	public String getAccountNo() {

		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {

		this.accountNo = accountNo;
	}

	public Long getBasicPensionAmount() {

		return this.basicPensionAmount.longValue();
	}

	public void setBasicPensionAmount(BigDecimal basicPensionAmount) {

		this.basicPensionAmount = basicPensionAmount;
	}

	public Long getDpPercent() {

		return this.dpPercent.longValue();
	}

	public void setDpPercent(BigDecimal dpPercent) {

		this.dpPercent = dpPercent;
	}

	public Long getTiPercent() {

		return this.tiPercent.longValue();
	}

	public void setTiPercent(BigDecimal tiPercent) {

		this.tiPercent = tiPercent;
	}

	public Long getDpPercentAmount() {

		return this.dpPercentAmount.longValue();
	}

	public void setDpPercentAmount(BigDecimal dpPercentAmount) {

		this.dpPercentAmount = dpPercentAmount;
	}

	public Long getTiPercentAmount() {

		return this.tiPercentAmount.longValue();
	}

	public void setTiPercentAmount(BigDecimal tiPercentAmount) {

		this.tiPercentAmount = tiPercentAmount;
	}

	public Long getCvpMonthlyAmount() {

		return this.cvpMonthlyAmount.longValue();
	}

	public void setCvpMonthlyAmount(BigDecimal cvpMonthlyAmount) {

		this.cvpMonthlyAmount = cvpMonthlyAmount;
	}

	public Long getMedicalAllowenceAmount() {

		return this.medicalAllowenceAmount.longValue();
	}

	public void setMedicalAllowenceAmount(BigDecimal medicalAllowenceAmount) {

		this.medicalAllowenceAmount = medicalAllowenceAmount;
	}

	public Long getTIArrearsAmount() {

		return this.TIArrearsAmount.longValue();
	}

	public void setTIArrearsAmount(BigDecimal TIArrearsAmount) {

		this.TIArrearsAmount = TIArrearsAmount;
	}

	public Long getOtherArrearsAmount() {

		return this.otherArrearsAmount.longValue();
	}

	public void setOtherArrearsAmount(BigDecimal otherArrearsAmount) {

		this.otherArrearsAmount = otherArrearsAmount;
	}

	public Long getItCutAmount() {

		return this.itCutAmount.longValue();
	}

	public void setItCutAmount(BigDecimal itCutAmount) {

		this.itCutAmount = itCutAmount;
	}

	public Long getSpecialCutAmount() {

		return this.specialCutAmount.longValue();
	}

	public void setSpecialCutAmount(BigDecimal specialCutAmount) {

		this.specialCutAmount = specialCutAmount;
	}

	public Long getOtherBenefit() {

		return this.otherBenefit.longValue();
	}

	public void setOtherBenefit(BigDecimal otherBenefit) {

		this.otherBenefit = otherBenefit;
	}

	public Long getOMR() {

		return this.OMR.longValue();
	}

	public void setOMR(BigDecimal OMR) {

		this.OMR = OMR;
	}

	public Long getRecoveryAmount() {

		return this.recoveryAmount.longValue();
	}

	public void setRecoveryAmount(BigDecimal recoveryAmount) {

		this.recoveryAmount = recoveryAmount;
	}

	public Long getPensionCutAmount() {

		return this.pensionCutAmount.longValue();
	}

	public void setPensionCutAmount(BigDecimal pensionCutAmount) {

		this.pensionCutAmount = pensionCutAmount;
	}

	public Long getNetPensionAmount() {

		return this.netPensionAmount.longValue();
	}

	public void setNetPensionAmount(BigDecimal netPensionAmount) {

		this.netPensionAmount = netPensionAmount;
	}

	public Long getAllnBf11156() {

		return this.allnBf11156.longValue();
	}

	public void setAllnBf11156(BigDecimal allnBf11156) {

		this.allnBf11156 = allnBf11156;
	}

	public Long getAllnAf11156() {

		return this.allnAf11156.longValue();
	}

	public void setAllnAf11156(BigDecimal allnAf11156) {

		this.allnAf11156 = allnAf11156;
	}

	public Long getAllnAf10560() {

		return this.allnAf10560.longValue();
	}

	public void setAllnAf10560(BigDecimal allnAf10560) {

		this.allnAf10560 = allnAf10560;
	}

	public Long getPersonalPension() {

		return this.personalPension.longValue();
	}

	public void setPersonalPension(BigDecimal personalPension) {

		this.personalPension = personalPension;
	}

	public Long getIr() {

		return this.ir.longValue();
	}

	public void setIr(BigDecimal ir) {

		this.ir = ir;
	}

	public Long getMOComm() {

		return this.MOComm.longValue();
	}

	public void setMOComm(BigDecimal MOComm) {

		this.MOComm = MOComm;
	}

	public Integer getForMonth() {

		return forMonth;
	}

	public void setForMonth(Integer forMonth) {

		this.forMonth = forMonth;
	}

	public BigDecimal getHeadCode() {

		return this.headCode;
	}

	public void setHeadCode(BigDecimal headCode) {

		this.headCode = headCode;
	}

	public String getBillType() {

		return billType;
	}

	public void setBillType(String billType) {

		this.billType = billType;
	}

	public Long getAdpAmount() {

		return this.adpAmount.longValue();
	}

	public void setAdpAmount(BigDecimal adpAmount) {

		this.adpAmount = adpAmount;
	}

	public Integer getNoOfDays() {

		return noOfDays;
	}

	public void setNoOfDays(Integer noOfDays) {

		this.noOfDays = noOfDays;
	}

	public Date getFromDate() {

		return fromDate;
	}

	public void setFromDate(Date fromDate) {

		this.fromDate = fromDate;
	}

	public Date getToDate() {

		return toDate;
	}

	public void setToDate(Date toDate) {

		this.toDate = toDate;
	}

	public String getBranchCode() {

		return this.branchCode;
	}

	public void setBranchCode(String branchCode) {

		this.branchCode = branchCode;
	}

	public String getPageNo() {

		return this.pageNo;
	}

	public void setPageNo(String pageNo) {

		this.pageNo = pageNo;
	}

	public String getLedgerNo() {

		return this.ledgerNo;
	}

	public void setLedgerNo(String ledgerNo) {

		this.ledgerNo = ledgerNo;
	}

	public Long getPensionBillAmount() {

		return PensionBillAmount.longValue();
	}

	public void setPensionBillAmount(BigDecimal pensionBillAmount) {

		PensionBillAmount = pensionBillAmount;
	}

	public Long getIr1Amt() {

		return ir1Amt.longValue();
	}

	public void setIr1Amt(BigDecimal ir1Amt) {

		this.ir1Amt = ir1Amt;
	}

	public Long getIr2Amt() {

		return ir2Amt.longValue();
	}

	public void setIr2Amt(BigDecimal ir2Amt) {

		this.ir2Amt = ir2Amt;
	}

	public Long getIr3Amt() {

		return ir3Amt.longValue();
	}

	public void setIr3Amt(BigDecimal ir3Amt) {

		this.ir3Amt = ir3Amt;
	}

	public Long getGrossAmount() {

		return grossAmount.longValue();
	}

	public void setGrossAmount(BigDecimal grossAmount) {

		this.grossAmount = grossAmount;
	}

	public Long getAllnBf11136() {

		return allnBf11136.longValue();
	}

	public void setAllnBf11136(BigDecimal allnBf11136) {

		this.allnBf11136 = allnBf11136;
	}

	public Long getAllnAfZp() {

		return allnAfZp.longValue();
	}

	public void setAllnAfZp(BigDecimal allnAfZp) {

		this.allnAfZp = allnAfZp;
	}

	public BigDecimal getReducedPension() {

		return reducedPension;
	}

	public void setReducedPension(BigDecimal reducedPension) {

		this.reducedPension = reducedPension;
	}

	public Long getTotalPensioners() {

		return totalPensioners;
	}

	public void setTotalPensioners(Long totalPensioners) {

		this.totalPensioners = totalPensioners;
	}

	public String getSeries() {

		return series;
	}

	public void setSeries(String series) {

		this.series = series;
	}

	public Integer getMainCatCode() {

		return mainCatCode;
	}

	public void setMainCatCode(Integer mainCatCode) {

		this.mainCatCode = mainCatCode;
	}

	public String getMainCatDesc() {

		return mainCatDesc;
	}

	public void setMainCatDesc(String mainCatDesc) {

		this.mainCatDesc = mainCatDesc;
	}

	public BigDecimal getPeonAllowanceAmt() {

		return peonAllowanceAmt;
	}

	public void setPeonAllowanceAmt(BigDecimal peonAllowanceAmt) {

		this.peonAllowanceAmt = peonAllowanceAmt;
	}

	public BigDecimal getGallantryAmt() {

		return gallantryAmt;
	}

	public void setGallantryAmt(BigDecimal gallantryAmt) {

		this.gallantryAmt = gallantryAmt;
	}

	public Long getTotalArrearAmt() {

		return totalArrearAmt.longValue();
	}

	public void setTotalArrearAmt(BigDecimal totalArrearAmt) {

		this.totalArrearAmt = totalArrearAmt;
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

	public int compareTo(MonthlyPensionBillVO arg0) {

		return this.headCode.compareTo(arg0.headCode);

	}

}
