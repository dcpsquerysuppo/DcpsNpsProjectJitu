package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

public class LifeCertificateVO implements java.io.Serializable
{
	
	/**
	 * serial version uid
	 */
	
	private static final long serialVersionUID = 3419005105712325437L;

	private String pensionerCode;
	
	private Long pensionRqstId;
	
	private String ppoNo;

	private String name;

	private String bankName;

	private String branchName;

	private String accountNo;

	private String panNo;
	
	private String dateOfBirth;
	
	private String ledgerNo = "0";
	
	private String pageNo = "0";
	
	private String familyName;
	
	private BigDecimal headCode;
	
	private Date commencementDate;
	
	private Date retirementDate;
	
	private String aliveFlag;
	
	private String headCodeDesc;
	
	private String treasuryName;
	
	private String treasuryAddr1;
	
	private String treasuryAddr2;
	
	private Date receivedDate;
	
	private String lifeCertFlag;
	
	private String bankCode;
	
	private String branchCode;
	
	private BigDecimal arrearAmount;
	
	private Integer payMonthYear;
	
	public LifeCertificateVO() 
	{
	}
	
	public LifeCertificateVO(String pensionerCode,String headCodeDesc,String ppoNo,String name,String familyName,Date retirementDate,Date commencementDate, String accountNo,String aliveFlag)
	{
		this.pensionerCode=pensionerCode;
		this.headCodeDesc=headCodeDesc;
		this.ppoNo=ppoNo;
		this.name=name;
		this.familyName=familyName;
		this.retirementDate=retirementDate;
		this.commencementDate=commencementDate;
		this.accountNo=accountNo;
		this.aliveFlag=aliveFlag;
	}
	
	public LifeCertificateVO(String pensionerCode,String ppoNo, String name,String familyName,String aliveFlag,String treasuryName,String treasuryAddr1,String treasuryAddr2,String bankName, String branchName) 
	{
		this.pensionerCode=pensionerCode;
		this.ppoNo=ppoNo;
		this.name=name;
		this.familyName=familyName;
		this.aliveFlag=aliveFlag;
		this.treasuryName=treasuryName;
		this.treasuryAddr1=treasuryAddr1;
		this.treasuryAddr2=treasuryAddr2;
		this.bankName=bankName;
		this.branchName=branchName;
	}
	 
	public LifeCertificateVO(String pensionerCode,String ppoNo,String headCodeDesc, String name,String familyName,Date retirementDate,Date commencementDate
			, String accountNo,String aliveFlag,String bankName, String branchName,Date receivedDate) 
	{
			this.pensionerCode=pensionerCode;
			this.ppoNo=ppoNo;
			this.headCodeDesc=headCodeDesc;
			this.name=name;
			this.familyName=familyName;
			this.retirementDate=retirementDate;
			this.commencementDate=commencementDate;
			this.accountNo=accountNo;
			this.aliveFlag=aliveFlag;
			this.bankName=bankName;
			this.branchName=branchName;
			this.receivedDate=receivedDate;
		}
	/**
	 * 
	 * @param pensionerCode
	 * @param ppoNo
	 * @param name
	 * @param bankName
	 * @param branchName
	 * @param accountNo
	 * @param panNo
	 * @param dateOfBirth
	 * @param ledgerNo
	 * @param pageNo
	 * @param familyName
	 * @param headCode
	 * @param commencementDate
	 * @param retirementDate
	 * @param aliveFlag
	 * @param headCodeDesc
	 * @param treasuryName
	 * @param treasuryAddr1
	 * @param treasuryAddr2
	 * @param receivedDate
	 * @param lifeCertFlag
	 */
	public LifeCertificateVO(String pensionerCode, String ppoNo, String name, String bankName, String branchName, String accountNo, String panNo, String dateOfBirth, String ledgerNo, String pageNo,
			String familyName, BigDecimal headCode, Date commencementDate, Date retirementDate, String aliveFlag, String headCodeDesc, String treasuryName, String treasuryAddr1, String treasuryAddr2,
			Date receivedDate, String lifeCertFlag) {

		super();
		this.pensionerCode = pensionerCode;
		this.ppoNo = ppoNo;
		this.name = name;
		this.bankName = bankName;
		this.branchName = branchName;
		this.accountNo = accountNo;
		this.panNo = panNo;
		this.dateOfBirth = dateOfBirth;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
		this.familyName = familyName;
		this.headCode = headCode;
		this.commencementDate = commencementDate;
		this.retirementDate = retirementDate;
		this.aliveFlag = aliveFlag;
		this.headCodeDesc = headCodeDesc;
		this.treasuryName = treasuryName;
		this.treasuryAddr1 = treasuryAddr1;
		this.treasuryAddr2 = treasuryAddr2;
		this.receivedDate = receivedDate;
		this.lifeCertFlag = lifeCertFlag;
	}

	
	
	//Property accessors....
	public String getPensionerCode() {
		return pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}

	public Long getPensionRqstId() {
		return pensionRqstId;
	}
	
	public void setPensionRqstId(Long pensionRqstId) {
		this.pensionRqstId = pensionRqstId;
	}
	
	public String getPpoNo() {
		return this.ppoNo;
	}
	
	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBankName() {
		return this.bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public String getBranchName() {
		return this.branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPanNo() {
		return this.panNo;
	}
	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
	
	
	public String getDateOfBirth() {
		return this.dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getLedgerNo() {
		return this.ledgerNo;
	}
	public void setLedgerNo(String ledgerNo) {
		this.ledgerNo = ledgerNo;
	}
	
	public String getPageNo() {
		return this.pageNo;
	}
	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}
	
	public String getFamilyName() {
		return this.familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	public BigDecimal getHeadCode() {
		return this.headCode ;
	}
	public void setHeadCode(BigDecimal headCode){
		this.headCode = headCode;
	}

	
	public Date getCommencementDate() {
		return commencementDate;
	}

	public void setCommencementDate(Date commencementDate) {
		this.commencementDate = commencementDate;
	}

	public String getAliveFlag() {
		return aliveFlag;
	}

	public void setAliveFlag(String aliveFlag) {
		this.aliveFlag = aliveFlag;
	}

	public Date getRetirementDate() {
		return retirementDate;
	}

	public void setRetirementDate(Date retirementDate) {
		this.retirementDate = retirementDate;
	}

	public String getHeadCodeDesc() {
		return headCodeDesc;
	}

	public void setHeadCodeDesc(String headCodeDesc) {
		this.headCodeDesc = headCodeDesc;
	}

	public String getTreasuryName() {
		return treasuryName;
	}

	public void setTreasuryName(String treasuryName) {
		this.treasuryName = treasuryName;
	}

	public String getTreasuryAddr1() {
		return treasuryAddr1;
	}

	public void setTreasuryAddr1(String treasuryAddr1) {
		this.treasuryAddr1 = treasuryAddr1;
	}

	public String getTreasuryAddr2() {
		return treasuryAddr2;
	}

	public void setTreasuryAddr2(String treasuryAddr2) {
		this.treasuryAddr2 = treasuryAddr2;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public String getLifeCertFlag() {
		
		return lifeCertFlag;
	}
	public void setLifeCertFlag(String lifeCertFlag) {
	
		this.lifeCertFlag = lifeCertFlag;
	}
	public String getBankCode() {
	
		return bankCode;
	}
	public void setBankCode(String bankCode) {
	
		this.bankCode = bankCode;
	}
	public String getBranchCode() {
	
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
	
		this.branchCode = branchCode;
	}

	public BigDecimal getArrearAmount() {
	
		return arrearAmount;
	}
	
	public void setArrearAmount(BigDecimal arrearAmount) {
	
		this.arrearAmount = arrearAmount;
	}
	
	public Integer getPayMonthYear() {
	
		return payMonthYear;
	}
	
	public void setPayMonthYear(Integer payMonthYear) {
	
		this.payMonthYear = payMonthYear;
	}
	
	


}
